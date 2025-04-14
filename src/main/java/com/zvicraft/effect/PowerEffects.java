package com.zvicraft.effect;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class PowerEffects {
    public static void applyPowerEffect(PlayerEntity player, String powerName) {
        if (player instanceof ServerPlayerEntity serverPlayer) {
            switch (powerName) {
                case "Emerald Effect" -> applyEmeraldEffect(serverPlayer);
                case "Army of Dogs" -> applyDogEffect(serverPlayer);
                case "Fire Resistance Effect" -> applyFireResistanceEffect(serverPlayer);
                case "Shield Effect" -> applyShieldEffect(serverPlayer);
                case "Totem Effect" -> applyTotemEffect(serverPlayer);
                case "Creeper Effect" -> applyCreeperEffect(serverPlayer);
                case "Poison Effect" -> applyPoisonEffect(serverPlayer);
                case "Vex Summon" -> applyVexEffect(serverPlayer);
                case "Trident Power" -> applyTridentEffect(serverPlayer);
                case "Dolphin Power" -> applyDolphinEffect(serverPlayer);
                case "Grappling Power" -> applyGrapplingEffect(serverPlayer);
                case "Confuse Power" -> applyConfuseEffect(serverPlayer);
                case "Pearl Power" -> applyPearlEffect(serverPlayer);
                case "Lifesteal Power" -> applyLifestealEffect(serverPlayer);
                case "Heart Power" -> applyHeartEffect(serverPlayer);
                case "Thunder Power" -> applyThunderEffect(serverPlayer);
                case "Piercing Power" -> applyPiercingEffect(serverPlayer);
                case "Mace Power" -> applyMaceEffect(serverPlayer);
                case "Trapper Power" -> applyTrapperEffect(serverPlayer);
                case "Underground Power" -> applyUndergroundEffect(serverPlayer);
                case "Invisibility Power" -> applyInvisibilityEffect(serverPlayer);
                case "Shrink Power" -> applyShrinkEffect(serverPlayer);
                case "CobWeb Power" -> applyCobwebEffect(serverPlayer);
            }
        }
    }

    private static void applyEmeraldEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.HERO_OF_THE_VILLAGE, Integer.MAX_VALUE, 2, false, false
        ));
    }

    private static void applyDogEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.RESISTANCE, Integer.MAX_VALUE, 1, false, false
        ));
    }

    private static void applyFireResistanceEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0, false, false
        ));
    }

    private static void applyShieldEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.RESISTANCE, Integer.MAX_VALUE, 2, false, false
        ));
    }

    private static void applyTotemEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.REGENERATION, Integer.MAX_VALUE, 1, false, false
        ));
    }

    private static void applyCreeperEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.STRENGTH, 20 * 7, 1, false, false
        ));
    }

    private static void applyPoisonEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.POISON, 20 * 10, 0, false, false
        ));
    }

    private static void applyVexEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.STRENGTH, 20 * 20, 1, false, false
        ));
    }

    private static void applyTridentEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.CONDUIT_POWER, 20 * 10, 0, false, false
        ));
    }

    private static void applyDolphinEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.DOLPHINS_GRACE, 20 * 10, 0, false, false
        ));
    }

    private static void applyGrapplingEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.SLOW_FALLING, 20 * 5, 0, false, false
        ));
    }

    private static void applyConfuseEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.BLINDNESS, 20 * 5, 0, false, false
        ));
    }

    private static void applyPearlEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.SPEED, 20 * 30, 1, false, false
        ));
    }

    private static void applyLifestealEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.ABSORPTION, 20 * 10, 1, false, false
        ));
    }

    private static void applyHeartEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.REGENERATION, Integer.MAX_VALUE, 0, false, false
        ));
    }

    private static void applyThunderEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.STRENGTH, 20 * 10, 2, false, false
        ));
    }

    private static void applyPiercingEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.STRENGTH, 20 * 10, 1, false, false
        ));
    }

    private static void applyMaceEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.STRENGTH, 20 * 10, 1, false, false
        ));
    }

    private static void applyTrapperEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.SLOWNESS, 20 * 4, 3, false, false
        ));
    }

    private static void applyUndergroundEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.NIGHT_VISION, 20 * 30, 0, false, false
        ));
    }

    private static void applyInvisibilityEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.INVISIBILITY, Integer.MAX_VALUE, 0, false, false
        ));
    }

    private static void applyShrinkEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.SLOWNESS, 20 * 7, 2, false, false
        ));
    }

    private static void applyCobwebEffect(ServerPlayerEntity player) {
        player.addStatusEffect(new StatusEffectInstance(
            StatusEffects.SLOWNESS, 20 * 3, 4, false, false
        ));
    }
} 