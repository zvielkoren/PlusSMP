package com.zvicraft.effect;

import com.zvicraft.power.PowerType;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.VexEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.registry.Registries;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.UUID;

public class PowerEffects {
    private static final Map<PlayerEntity, Integer> immunityHits = new HashMap<>();
    private static final Map<PlayerEntity, Vec3d> playerPositions = new HashMap<>();
    private static final Map<PlayerEntity, Long> playerPositionTimes = new HashMap<>();
    private static final Map<PlayerEntity, Boolean> lifestealActive = new HashMap<>();
    private static final Map<PlayerEntity, Boolean> shieldPiercingActive = new HashMap<>();

    private static final Map<UUID, Long> invisCooldowns = new HashMap<>();
    private static final Map<UUID, Long> invisEndTimes = new HashMap<>();
    private static final long INVIS_COOLDOWN = 90 * 1000; // 1.5 minutes in milliseconds
    private static final long INVIS_DURATION = 7 * 1000; // 7 seconds in milliseconds

    private static void applyMedicEffects(ServerPlayerEntity player) {
        // Apply effects to self
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 1, false, false)); // Instant Health II
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 5 * 20, 2, false, false)); // Regeneration III for 5 seconds
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 3 * 20, 0, false, false)); // Resistance I for 3 seconds
        
        // Find trusted players within 5 blocks and apply effects to them
        player.getWorld().getPlayers().stream()
            .filter(otherPlayer -> otherPlayer != player) // Not the player themselves
            .filter(otherPlayer -> otherPlayer.squaredDistanceTo(player) <= 25) // 5 blocks squared
            .filter(otherPlayer -> {
                // Check if players are in the same team or have a trust relationship
                // For now, we'll apply to all players in range
                return true;
            })
            .forEach(ally -> {
                if (ally.isAlive()) {  // Only apply to living players
                    ally.addStatusEffect(new StatusEffectInstance(StatusEffects.INSTANT_HEALTH, 1, 1, false, false));
                    ally.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 5 * 20, 2, false, false));
                    ally.addStatusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 3 * 20, 0, false, false));
                    ally.sendMessage(Text.of("You've been healed by " + player.getName().getString() + "'s Medic power!"), false);
                }
            });
    }

    private static void applyInvisEffect(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        long currentTime = System.currentTimeMillis();
        
        // Check cooldown
        if (invisCooldowns.containsKey(playerId)) {
            long cooldownEnd = invisCooldowns.get(playerId) + INVIS_COOLDOWN;
            if (currentTime < cooldownEnd) {
                long remaining = (cooldownEnd - currentTime) / 1000;
                player.sendMessage(Text.of("Invis is on cooldown! " + remaining + " seconds remaining."), false);
                return;
            }
        }
        
        // Apply medic effects to self and allies
        applyMedicEffects(player);
        
        // Apply invisibility
        StatusEffectInstance invisibility = new StatusEffectInstance(StatusEffects.INVISIBILITY, (int)(INVIS_DURATION / 50), 0, false, false);
        player.addStatusEffect(invisibility);
        
        // Hide armor and items
        player.setInvisible(true);
        player.setGlowing(false);
        
        // Store end time and set cooldown
        invisEndTimes.put(playerId, currentTime + INVIS_DURATION);
        invisCooldowns.put(playerId, currentTime);
        
        // Schedule end of invisibility
        player.getServer().execute(() -> {
            try {
                Thread.sleep(INVIS_DURATION);
                player.getServer().execute(() -> {
                    if (player.isAlive()) {
                        player.setInvisible(false);
                        invisEndTimes.remove(playerId);
                    }
                });
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });
        
        player.sendMessage(Text.of("You are now invisible for 7 seconds and have been healed!"), false);
    }
    
    public static boolean isInvisActive(PlayerEntity player) {
        return invisEndTimes.containsKey(player.getUuid()) && 
               System.currentTimeMillis() < invisEndTimes.get(player.getUuid());
    }

    public static void applyPowerEffect(ServerPlayerEntity target, PowerType powerType) {
        if (target == null || powerType == null) return;

        switch (powerType) {
            // Self-only powers (buffs)
            case SPEED:
                applySpeedEffect(target);
                break;
            case INVISIBILITY:
                applyInvisEffect(target);
                break;
            case STRENGTH:
                applyStrengthEffect(target); // 15 seconds of Strength II
                break;
            case DOLPHIN:
                applyDolphinEffect(target);
                break;
            case GRAPPLING:
                applyGrapplingEffect(target);
                break;
            case LIFESTEAL:
                applyLifestealEffect(target);
                break;
            case SHIELD_PIERCING:
                applyShieldPiercingEffect(target);
                break;
                
            // Targeted powers (can be used on self or others)
            case POISON:
                applyPoisonEffect(target); // 10 seconds of Poison II
                break;
            case WITHER:
                applyWintherEffect(target); // 10 seconds of Poison II
                break;
            case BLINDNESS:
                applyBlindnessEffect(target); // 5 seconds of Blindness I
                break;
            case SLOWNESS:
                applySlownessEffect(target); // 5 seconds of Slowness II
                break;
            case WEAKNESS:
                applyWeaknessEffect(target); // 10 seconds of Weakness I
                break;
            case COBWEB:
                applyCobwebEffect(target);
                break;
            case SHRINK:
                applyShrinkEffect(target);
                break;
                
            // Special powers
            case DOGS:
                applyDogsEffect(target);
                break;
            case CAGE:
                applyCageEffect(target);
                break;
            case EMERALD:
                applyEmeraldEffect(target);
                break;
            case CREEPER:
                applyCreeperEffect(target);
                break;
            case TOTEM:
                applyTotemEffect(target);
                break;
            case VEX:
                applyVexEffect(target);
                break;
            case PEARL:
                applyPearlEffect(target);
                break;
            case HEART:
                applyHeartEffect(target);
                break;
            case MACE:
                applyMaceEffect(target);
                break;
                
            default:
                target.sendMessage(Text.of("Power " + powerType.getName() + " is not implemented yet!"), false);
                break;
        }
    }

    private static void applyWeaknessEffect(ServerPlayerEntity target) {
        StatusEffectInstance weakness = new StatusEffectInstance(StatusEffects.WEAKNESS, 20 * 10, 0);
        target.addStatusEffect(weakness);
    }

    private static void applySlownessEffect(ServerPlayerEntity target) {
        StatusEffectInstance slowness = new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 5, 1);
        target.addStatusEffect(slowness);
    }

    private static void applyWintherEffect(ServerPlayerEntity target) {
        StatusEffectInstance wither = new StatusEffectInstance(StatusEffects.WITHER, 20 * 10, 0);
        target.addStatusEffect(wither);
    }

    private static void applyStrengthEffect(ServerPlayerEntity target) {
        StatusEffectInstance strength = new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 15, 0);
        target.addStatusEffect(strength);
    }
//
//    public static void applyStatusEffect(ServerPlayerEntity target, StatusEffects effect, int duration, int amplifier) {
//        StatusEffectInstance statusEffect = new StatusEffectInstance(effect, duration, amplifier);
//        target.addStatusEffect(statusEffect);
//    }

    private static void applySpeedEffect(ServerPlayerEntity player) {
        // Permanent Speed I
        StatusEffectInstance speed = new StatusEffectInstance(StatusEffects.SPEED, 20 * 15, 0);
        player.addStatusEffect(
                speed);

        // Launch player forward ~15 blocks
        Vec3d lookDir = player.getRotationVector();
        player.addVelocity(lookDir.x * 2.0, 0.3, lookDir.z * 2.0);
        player.velocityModified = true;
    }

    private static void applyDogsEffect(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        for (int i = 0; i < 5; i++) {
            WolfEntity wolf = EntityType.WOLF.create(world);
            if (wolf != null) {
                // Position near player
                double angle = (2 * Math.PI * i) / 5;
                double x = player.getX() + Math.cos(angle) * 2;
                double z = player.getZ() + Math.sin(angle) * 2;
                wolf.setPos(x, player.getY(), z);

                // Tame to player
                wolf.setTamed(true, true);
                wolf.setOwner(player);
                // Equip armor
                equipWolfArmor(wolf);

                world.spawnEntity(wolf);

                // Schedule despawn after 1 minute
                world.getServer().execute(() -> {
                    world.getServer().getOverworld().getServer().execute(() -> {
                        try {
                            Thread.sleep(60000); // 1 minute
                            if (wolf.isAlive()) {
                                wolf.discard();
                            }
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    });
                });
            }
        }
    }

    private static void applyBlindnessEffect(ServerPlayerEntity player) {
        // Apply Darkness and Wither for 15 seconds
        StatusEffectInstance darkness = new StatusEffectInstance(StatusEffects.DARKNESS, 200, 0);
        StatusEffectInstance wither = new StatusEffectInstance( StatusEffects.WITHER, 200, 0);

        // Permanent Speed I

        StatusEffectInstance speedEffect = new StatusEffectInstance( StatusEffects.SPEED, StatusEffectInstance.INFINITE, 0, false, false
        );
        player.addStatusEffect(speedEffect);

        player.addStatusEffect(darkness);
        player.addStatusEffect(wither);
    }

    private static void applyCageEffect(ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        ServerWorld world = player.getServerWorld();

        // Create 3x3x3 cage around player
        for (int x = -1; x <= 1; x++) {
            for (int y = 0; y <= 2; y++) {
                for (int z = -1; z <= 1; z++) {
                    BlockPos cagePos = pos.add(x, y, z);

                    // Only place blocks on the edges, not inside
                    if (x == -1 || x == 1 || z == -1 || z == 1 || y == 0 || y == 2) {
                        world.setBlockState(cagePos, Blocks.OBSIDIAN.getDefaultState());
                    }
                }
            }
        }

        // Fill inside with cobwebs
        world.setBlockState(pos, Blocks.COBWEB.getDefaultState());
        world.setBlockState(pos.up(), Blocks.COBWEB.getDefaultState());
    }

    private static void applyEmeraldEffect(ServerPlayerEntity player) {
        // Permanent Hero of the Village III
        RegistryEntry<StatusEffect> hero = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.HERO_OF_THE_VILLAGE);
        StatusEffectInstance heroEffect = new StatusEffectInstance( hero, StatusEffectInstance.INFINITE, 0, false, false
        );
        player.addStatusEffect(heroEffect);

        // Note: Villager trading for 1 emerald would need custom villager interaction handling
        // This would require additional event listeners and custom trading logic
    }

    private static void applyImmunityEffect(ServerPlayerEntity player) {
        // Set immunity counter to 3 hits
        immunityHits.put(player, 3);

        // Visual effect
        player.playSound(SoundEvents.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
    }

    private static void applyCreeperEffect(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();
        Vec3d pos = player.getPos();

        // Push away nearby players (within 10 blocks)
        List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                PlayerEntity.class,
                player.getBoundingBox().expand(10),
                p -> p != player
        );

        for (PlayerEntity nearbyPlayer : nearbyPlayers) {
            Vec3d direction = nearbyPlayer.getPos().subtract(pos).normalize();
            nearbyPlayer.addVelocity(direction.x * 2, 0.5, direction.z * 2);

            if (nearbyPlayer instanceof ServerPlayerEntity serverNearbyPlayer) {
                serverNearbyPlayer.networkHandler.sendPacket(
                        new EntityVelocityUpdateS2CPacket(nearbyPlayer)
                );
            }
        }

        // Create explosion without damaging the user
        world.createExplosion(null, pos.x, pos.y, pos.z, 3.0f, World.ExplosionSourceType.TNT);

        // Give brief fire resistance to prevent self-damage
        StatusEffectInstance fireRes = new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 5, 0);
        player.addStatusEffect(fireRes);
    }

    private static void applyPoisonEffect(ServerPlayerEntity player) {
        // Apply Poison II for 10 seconds to target
        // Note: This needs target selection logic - applying to self for now
        StatusEffectInstance poison = new StatusEffectInstance(StatusEffects.POISON, 20 * 10, 2);
        player.addStatusEffect(poison);
    }

    private static void applyTotemEffect(ServerPlayerEntity player) {
        // Apply totem effects for 15 seconds
        StatusEffectInstance regen = new StatusEffectInstance(StatusEffects.REGENERATION, 20 * 15, 0);
        StatusEffectInstance fireRes = new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 15, 0);
        StatusEffectInstance absorption = new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 15, 0);

        player.addStatusEffect(regen);
        player.addStatusEffect(fireRes);
        player.addStatusEffect(absorption);

        // Play totem sound
        player.getWorld().playSound(null, player.getBlockPos(),
                SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private static void applyVexEffect(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        // Spawn 5 vexes
        for (int i = 0; i < 5; i++) {
            VexEntity vex = EntityType.VEX.create(world);
            if (vex != null) {
                double angle = (2 * Math.PI * i) / 5;
                double x = player.getX() + Math.cos(angle) * 3;
                double z = player.getZ() + Math.sin(angle) * 3;
                vex.refreshPositionAndAngles(x, player.getY() + 2, z, 0, 0);

                // Set vex to attack nearby players (except owner)
                List<PlayerEntity> nearbyPlayers = world.getEntitiesByClass(
                        PlayerEntity.class,
                        player.getBoundingBox().expand(10),
                        p -> p != player
                );

                for (PlayerEntity nearbyPlayer : nearbyPlayers) {
                    vex.setTarget(nearbyPlayer);
                }
                world.spawnEntity(vex);
            }
        }
    }

    private static void applyDolphinEffect(ServerPlayerEntity player) {
        // Permanent water breathing
        RegistryEntry<StatusEffect> waterBreathing = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.WATER_BREATHING);
        StatusEffectInstance waterBreathingEffect = new StatusEffectInstance(waterBreathing, StatusEffectInstance.INFINITE, 0, false, false);
        player.addStatusEffect(waterBreathingEffect);

        // Dolphin's Grace for 5 seconds when activated
        StatusEffectInstance dolphinGrace = new StatusEffectInstance(StatusEffects.DOLPHINS_GRACE, 20 * 5, 0);

        player.addStatusEffect(dolphinGrace);
    }

    private static void applyGrapplingEffect(ServerPlayerEntity player) {
        // This would need raycast logic to find target within 15 blocks
        // and pull them toward the player
        Vec3d lookDir = player.getRotationVector();
        Vec3d targetPos = player.getPos().add(lookDir.multiply(15));

        // Find nearby players to pull
        List<PlayerEntity> targets = player.getWorld().getEntitiesByClass(
                PlayerEntity.class,
                player.getBoundingBox().expand(15),
                p -> p != player
        );

        if (!targets.isEmpty()) {
            PlayerEntity target = targets.get(0);
            Vec3d pullDirection = player.getPos().subtract(target.getPos()).normalize();
            target.addVelocity(pullDirection.x * 1.5, pullDirection.y * 0.5, pullDirection.z * 1.5);

            if (target instanceof ServerPlayerEntity serverTarget) {
                serverTarget.networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
            }
        }
    }

    private static void applyPearlEffect(ServerPlayerEntity player) {
        // Teleport to position from 20 seconds ago
        Vec3d savedPos = playerPositions.get(player);
        Long savedTime = playerPositionTimes.get(player);

        if (savedPos != null && savedTime != null) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - savedTime >= 20000) { // 20 seconds
                player.setPosition(savedPos.x, savedPos.y, savedPos.z);
                playerPositions.remove(player);
            }
        }
    }

    private static void applyLifestealEffect(ServerPlayerEntity player) {
        // Mark player as having lifesteal active for next hit
        lifestealActive.put(player, true);

        // Visual indicator
        StatusEffectInstance glowing = new StatusEffectInstance(StatusEffects.GLOWING, 20 * 10, 0);
        player.addStatusEffect(glowing);

        // Schedule removal after 10 seconds
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                lifestealActive.remove(player);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private static void applyHeartEffect(ServerPlayerEntity player) {
        // Permanent Regeneration
        StatusEffectInstance regen = new StatusEffectInstance(StatusEffects.REGENERATION, StatusEffectInstance.INFINITE, 0);
        player.addStatusEffect(regen);

        // Add 5 hearts (10 health points) for 20 seconds
        StatusEffectInstance absorption = new StatusEffectInstance(StatusEffects.ABSORPTION, 20 * 20, 5);
        player.addStatusEffect(absorption);
    }

    private static void applyShieldPiercingEffect(ServerPlayerEntity player) {
        // Mark player as having shield piercing for 10 seconds
        shieldPiercingActive.put(player, true);

        // Visual effect
        StatusEffectInstance glowing = new StatusEffectInstance(StatusEffects.GLOWING, 20 * 10, 0);
        player.addStatusEffect(glowing);

        // Schedule removal after 10 seconds
        new Thread(() -> {
            try {
                Thread.sleep(10000);
                shieldPiercingActive.remove(player);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    private static void applyShrinkEffect(ServerPlayerEntity player) {
        // This would need a mod that supports player scaling
        // For vanilla, we can simulate with effects

        StatusEffectInstance slowness = new StatusEffectInstance( StatusEffects.SLOWNESS, 200, 1);
        StatusEffectInstance weakness = new StatusEffectInstance( StatusEffects.WEAKNESS, 200, 1);

        player.addStatusEffect(slowness);
        player.addStatusEffect(weakness) ;

        // Note: Actual shrinking would require additional mod support like Pehkui
    }

    private static void applyCobwebEffect(ServerPlayerEntity player) {
        BlockPos pos = player.getBlockPos();
        ServerWorld world = player.getServerWorld();

        // Create 3x3 cobweb area around target
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y <= 2; y++) {
                    BlockPos webPos = pos.add(x, y, z);
                    world.setBlockState(webPos, Blocks.COBWEB.getDefaultState());
                }
            }
        }
    }

    private static void applyMaceEffect(ServerPlayerEntity player) {
        // Only works if player has mace in inventory
        boolean hasMace = player.getInventory().contains(new ItemStack(Items.MACE));

        if (hasMace) {
            // Apply Levitation I for 14 seconds
            StatusEffectInstance levitation = new StatusEffectInstance(StatusEffects.LEVITATION, 20 * 14, 0);

            player.addStatusEffect(levitation);
        }
    }

    private static void equipWolfArmor(WolfEntity wolf) {
        ItemStack armor = new ItemStack(Items.WOLF_ARMOR);
        wolf.equipStack(EquipmentSlot.BODY, armor);
        wolf.setEquipmentDropChance(EquipmentSlot.BODY, 0.0F);
    }

    // Utility methods for tracking
    public static void trackPlayerPosition(PlayerEntity player) {
        // Store position every second for pearl effect
        playerPositions.put(player, player.getPos());
        playerPositionTimes.put(player, System.currentTimeMillis());
    }

    public static boolean hasImmunity(PlayerEntity player) {
        return immunityHits.containsKey(player) && immunityHits.get(player) > 0;
    }

    public static void useImmunityHit(PlayerEntity player) {
        if (immunityHits.containsKey(player)) {
            int hits = immunityHits.get(player) - 1;
            if (hits <= 0) {
                immunityHits.remove(player);
            } else {
                immunityHits.put(player, hits);
            }
        }
    }

    public static boolean hasLifesteal(PlayerEntity player) {
        return lifestealActive.getOrDefault(player, false);
    }

    public static boolean hasShieldPiercing(PlayerEntity player) {
        return shieldPiercingActive.getOrDefault(player, false);
    }

    public static void handleLifestealHit(PlayerEntity attacker, PlayerEntity target, float damage) {
        if (hasLifesteal(attacker)) {
            // Steal 30% of damage as health, give 20% to attacker
            float stolenHealth = damage * 0.3f;
            float givenHealth = damage * 0.2f;

            // Remove health from target
            target.setHealth(Math.max(1.0f, target.getHealth() - stolenHealth));

            // Add health to attacker (max 20 health/10 hearts)
            float newHealth = Math.min(20.0f, attacker.getHealth() + givenHealth);
            attacker.setHealth(newHealth);

            // Remove lifesteal after use
            lifestealActive.remove(attacker);
        }
    }
}