package com.zvicraft.effect;

import com.zvicraft.power.PowerType;
import net.minecraft.block.Blocks;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.Enchantments;
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
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import net.minecraft.registry.Registries;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class PowerEffects {
    private static final Map<PlayerEntity, Integer> immunityHits = new HashMap<>();
    private static final Map<PlayerEntity, Vec3d> playerPositions = new HashMap<>();
    private static final Map<PlayerEntity, Long> playerPositionTimes = new HashMap<>();
    private static final Map<PlayerEntity, Boolean> lifestealActive = new HashMap<>();
    private static final Map<PlayerEntity, Boolean> shieldPiercingActive = new HashMap<>();

    public static void applyPowerEffect(PlayerEntity player, PowerType powerType) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            switch (powerType) {
                case SPEED -> applySpeedEffect(serverPlayer);
                case DOGS -> applyDogsEffect(serverPlayer);
                case BLINDNESS -> applyBlindnessEffect(serverPlayer);
                case CAGE -> applyCageEffect(serverPlayer);
                case EMERALD -> applyEmeraldEffect(serverPlayer);
                case IMMUNITY -> applyImmunityEffect(serverPlayer);
                case CREEPER -> applyCreeperEffect(serverPlayer);
                case POISON -> applyPoisonEffect(serverPlayer);
                case TOTEM -> applyTotemEffect(serverPlayer);
                case VEX -> applyVexEffect(serverPlayer);
                case DOLPHIN -> applyDolphinEffect(serverPlayer);
                case GRAPPLING -> applyGrapplingEffect(serverPlayer);
                case PEARL -> applyPearlEffect(serverPlayer);
                case LIFESTEAL -> applyLifestealEffect(serverPlayer);
                case HEART -> applyHeartEffect(serverPlayer);
                case SHIELD_PIERCING -> applyShieldPiercingEffect(serverPlayer);
                case SHRINK -> applyShrinkEffect(serverPlayer);
                case COBWEB -> applyCobwebEffect(serverPlayer);
                case MACE -> applyMaceEffect(serverPlayer);
            }
        }
    }

    private static void applySpeedEffect(ServerPlayerEntity player) {
        // Permanent Speed I
        RegistryEntry<StatusEffect> speed = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.SPEED);
        player.addStatusEffect(new StatusEffectInstance(
                speed, StatusEffectInstance.INFINITE, 0, false, false
        ));

        // Temporary Speed II for 5 seconds when activated
        player.addStatusEffect(new StatusEffectInstance(
                speed, 20 * 5, 1, false, false
        ));

        // Launch player forward ~15 blocks
        Vec3d lookDir = player.getRotationVector();
        player.addVelocity(lookDir.x * 2.0, 0.3, lookDir.z * 2.0);
        player.velocityModified = true;
    }

    private static void applyDogsEffect(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        for (int i = 0; i < 5; i++) {
            WolfEntity wolf = EntityType.WOLF.create(world, null);
            if (wolf != null) {
                // Position near player
                double angle = (2 * Math.PI * i) / 5;
                double x = player.getX() + Math.cos(angle) * 2;
                double z = player.getZ() + Math.sin(angle) * 2;
                wolf.setPos(x, player.getY(), z);

                // Tame to player
                wolf.setTamed(true, true);
                wolf.setOwner(player);

                // Add effects for 1 minute
                    RegistryEntry<StatusEffect> speed = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.SPEED);
                    RegistryEntry<StatusEffect> fireRes = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.FIRE_RESISTANCE);
                    RegistryEntry<StatusEffect> strength = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.STRENGTH);

                wolf.addStatusEffect(new StatusEffectInstance(speed, 20 * 60, 0));
                wolf.addStatusEffect(new StatusEffectInstance(fireRes, 20 * 60, 0));
                wolf.addStatusEffect(new StatusEffectInstance(strength, 20 * 60, 1));

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
        RegistryEntry<StatusEffect> darkness = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.DARKNESS);
        RegistryEntry<StatusEffect> wither = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.WITHER);

        player.addStatusEffect(new StatusEffectInstance(darkness, 20 * 15, 0));
        player.addStatusEffect(new StatusEffectInstance(wither, 20 * 15, 0));
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
        player.addStatusEffect(new StatusEffectInstance(
                hero, StatusEffectInstance.INFINITE, 2, false, false
        ));

        // Note: Villager trading for 1 emerald would need custom villager interaction handling
        // This would require additional event listeners and custom trading logic
    }

    private static void applyImmunityEffect(ServerPlayerEntity player) {
        // Set immunity counter to 3 hits
        immunityHits.put(player, 3);

        // Visual effect
        RegistryEntry<StatusEffect> glowing = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.GLOWING);
        player.addStatusEffect(new StatusEffectInstance(glowing, 20 * 50, 0));
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
        RegistryEntry<StatusEffect> fireRes = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.FIRE_RESISTANCE);
        player.addStatusEffect(new StatusEffectInstance(fireRes, 20 * 5, 0));
    }

    private static void applyPoisonEffect(ServerPlayerEntity player) {
        // Apply Poison II for 10 seconds to target
        // Note: This needs target selection logic - applying to self for now
        RegistryEntry<StatusEffect> poison = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.POISON);
        player.addStatusEffect(new StatusEffectInstance(poison, 20 * 10, 1));
    }

    private static void applyTotemEffect(ServerPlayerEntity player) {
        // Apply totem effects for 15 seconds
        RegistryEntry<StatusEffect> regen = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.REGENERATION);
        RegistryEntry<StatusEffect> fireRes = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.FIRE_RESISTANCE);
        RegistryEntry<StatusEffect> absorption = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.ABSORPTION);

        player.addStatusEffect(new StatusEffectInstance(regen, 20 * 15, 1));
        player.addStatusEffect(new StatusEffectInstance(fireRes, 20 * 15, 0));
        player.addStatusEffect(new StatusEffectInstance(absorption, 20 * 15, 1));

        // Play totem sound
        player.getWorld().playSound(null, player.getBlockPos(),
                SoundEvents.ITEM_TOTEM_USE, SoundCategory.PLAYERS, 1.0f, 1.0f);
    }

    private static void applyVexEffect(ServerPlayerEntity player) {
        ServerWorld world = player.getServerWorld();

        // Spawn 5 vexes
        for (int i = 0; i < 5; i++) {
            VexEntity vex = EntityType.VEX.create(world, null);
            if (vex != null) {
                double angle = (2 * Math.PI * i) / 5;
                double x = player.getX() + Math.cos(angle) * 3;
                double z = player.getZ() + Math.sin(angle) * 3;
                vex.refreshPositionAndAngles(x, player.getY() + 2, z, 0, 0);

                // Set vex to attack nearby players (except owner)

                world.spawnEntity(vex);
            }
        }
    }

    private static void applyDolphinEffect(ServerPlayerEntity player) {
        // Permanent water breathing
        RegistryEntry<StatusEffect> waterBreathing = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.WATER_BREATHING);
        player.addStatusEffect(new StatusEffectInstance(
                waterBreathing, StatusEffectInstance.INFINITE, 0, false, false
        ));

        // Dolphin's Grace for 5 seconds when activated
        RegistryEntry<StatusEffect> dolphinGrace = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.DOLPHINS_GRACE);
        player.addStatusEffect(new StatusEffectInstance(dolphinGrace, 20 * 5, 0));
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
        RegistryEntry<StatusEffect> glowing = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.GLOWING);
        player.addStatusEffect(new StatusEffectInstance(glowing, 20 * 10, 0));

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
        RegistryEntry<StatusEffect> regen = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.REGENERATION);
        player.addStatusEffect(new StatusEffectInstance(
                regen, StatusEffectInstance.INFINITE, 0, false, false
        ));

        // Add 5 hearts (10 health points) for 20 seconds
        RegistryEntry<StatusEffect> absorption = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.ABSORPTION);
        player.addStatusEffect(new StatusEffectInstance(absorption, 20 * 20, 4));
    }

    private static void applyShieldPiercingEffect(ServerPlayerEntity player) {
        // Mark player as having shield piercing for 10 seconds
        shieldPiercingActive.put(player, true);

        // Visual effect
        RegistryEntry<StatusEffect> glowing = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.GLOWING);
        player.addStatusEffect(new StatusEffectInstance(glowing, 20 * 10, 0));

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
        RegistryEntry<StatusEffect> slowness = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.SLOWNESS);
        RegistryEntry<StatusEffect> weakness = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.WEAKNESS);

        player.addStatusEffect(new StatusEffectInstance(slowness, 20 * 7, 1));
        player.addStatusEffect(new StatusEffectInstance(weakness, 20 * 7, 0));

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
            RegistryEntry<StatusEffect> levitation = Registries.STATUS_EFFECT.getEntry((StatusEffect) StatusEffects.LEVITATION);
            player.addStatusEffect(new StatusEffectInstance(levitation, 20 * 14, 0));
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