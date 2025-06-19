package com.zvicraft.effect;

import com.zvicraft.power.PowerType;  // Add this line
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.registry.Registries;

import java.util.HashMap;
import java.util.Map;

public class PowerEffects {
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

    private static void applyMaceEffect(ServerPlayerEntity serverPlayer) {

    }

    private static void applyCobwebEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyShrinkEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyShieldPiercingEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyHeartEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyLifestealEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyPearlEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyGrapplingEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyDolphinEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyVexEffect(ServerPlayerEntity serverPlayer) {

    }

    private static void applyTotemEffect(ServerPlayerEntity serverPlayer) {
        }

    private static void applyPoisonEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyCreeperEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyImmunityEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyEmeraldEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyCageEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applyBlindnessEffect(ServerPlayerEntity serverPlayer) {
    }

    private static void applySpeedEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.SPEED, Integer.MAX_VALUE, 0, false, false
        ));
        // Additional boost effect when activated
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.SPEED, 20 * 5, 1, false, false
        ));
        // Launch player forward
        Vec3d lookDir = player.getRotationVector();
        player.addVelocity(lookDir.x * 1.5, 0.2, lookDir.z * 1.5);
    }

private static void applyDogsEffect(ServerPlayerEntity player) {
    ServerWorld world = player.getServerWorld();
    ActivePowerTracker.trackPowerEffect(player); // Start tracking this power effect

    for (int i = 0; i < 5; i++) {
        WolfEntity wolf = (WolfEntity) EntityType.WOLF.create(world, null);
        if (wolf != null) {
            wolf.setPos(player.getX(), player.getY(), player.getZ());
            wolf.setTamed(true, true);
            wolf.setOwner(player);
            
            StatusEffectInstance speedEffect = new StatusEffectInstance(StatusEffects.SPEED, 20 * 60, 0);
            StatusEffectInstance fireResistanceEffect = new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 20 * 60, 0);
            StatusEffectInstance strengthEffect = new StatusEffectInstance(StatusEffects.STRENGTH, 20 * 60, 1);
            equipWolfArmor(wolf);
            wolf.addStatusEffect(speedEffect);
            wolf.addStatusEffect(fireResistanceEffect);
            wolf.addStatusEffect(strengthEffect);
            
            world.spawnEntity(wolf);
            
            // Track the spawned wolf
            ActivePowerTracker.trackSpawnedEntity(player, wolf);
        }
    }
}
    private static void equipWolfArmor(WolfEntity wolf) {
        // Create diamond armor items
        ItemStack helmet = new ItemStack(Items.WOLF_ARMOR);
        ItemStack chestplate = new ItemStack(Items.WOLF_ARMOR);
        ItemStack leggings = new ItemStack(Items.WOLF_ARMOR);
        ItemStack boots = new ItemStack(Items.WOLF_ARMOR);

        // Enchant the armor (optional)
//        enchantArmor(helmet);
//        enchantArmor(chestplate);
//        enchantArmor(leggings);
//        enchantArmor(boots);

        // Equip the armor to the wolf
        wolf.equipStack(EquipmentSlot.HEAD, helmet);
        wolf.equipStack(EquipmentSlot.CHEST, chestplate);
        wolf.equipStack(EquipmentSlot.LEGS, leggings);
        wolf.equipStack(EquipmentSlot.FEET, boots); 

        // Set equipment drop chances to 0 so the armor doesn't drop when the wolf despawns
        wolf.setEquipmentDropChance(EquipmentSlot.HEAD, 0.0F);
        wolf.setEquipmentDropChance(EquipmentSlot.CHEST, 0.0F);
        wolf.setEquipmentDropChance(EquipmentSlot.LEGS, 0.0F);
        wolf.setEquipmentDropChance(EquipmentSlot.FEET, 0.0F);
    }

//    private static void enchantArmor(ItemStack armor) {
//        armor.addEnchantment(Enchantments.PROTECTION, 4);
//        armor.addEnchantment(Enchantments.UNBREAKING, 3);
//        armor.addEnchantment(Enchantments.THORNS, 3);
//
//        if (armor.isOf(Items.DIAMOND_BOOTS) || armor.isOf(Items.NETHERITE_BOOTS)) {
//            armor.addEnchantment(Enchantments.FEATHER_FALLING, 4);
//            armor.addEnchantment(Enchantments.DEPTH_STRIDER, 3);
//        }
//    }



    // Add implementations for other effects...
}