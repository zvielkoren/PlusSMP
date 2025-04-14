package com.zvicraft.power;

import com.zvicraft.animation.AnimationSystem;
import com.zvicraft.effect.PowerEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.registry.Registry;
import net.minecraft.registry.Registries;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class PowerSystem {
    private static final Map<UUID, List<PowerData>> playerPowers = new HashMap<>();
    private static final int MAX_ACTIVE_POWERS = 2;
    private static final int PREPARATION_TIME = 120; // 2 minutes in seconds

    public static void initialize() {
        // Initialize any required resources
        playerPowers.clear();
    }

    public static void cleanup() {
        // Clean up any resources
        playerPowers.clear();
    }

    public static class PowerData {
        public final PowerType powerType;
        public final long preparationStartTime;
        public boolean isActive;
        public long lastActivationTime;

        public PowerData(PowerType powerType, long preparationStartTime) {
            this.powerType = powerType;
            this.preparationStartTime = preparationStartTime;
            this.isActive = false;
            this.lastActivationTime = 0;
        }
    }

    public static boolean startPowerPreparation(ServerPlayerEntity player, PowerType powerType) {
        UUID playerId = player.getUuid();
        
        // Check if player already has this power
        if (hasPower(playerId, powerType)) {
            return false;
        }

        // Check if player has reached power limit
        if (getActivePowerCount(playerId) >= MAX_ACTIVE_POWERS) {
            player.sendMessage(Text.of("You can only have " + MAX_ACTIVE_POWERS + " active powers at a time!"));
            return false;
        }

        // Start power preparation
        List<PowerData> powers = playerPowers.computeIfAbsent(playerId, k -> new ArrayList<>());
        powers.add(new PowerData(powerType, System.currentTimeMillis()));
        
        player.sendMessage(Text.of("Started preparing " + powerType.getName() + " power..."));
        
        // Play preparation animation
        AnimationSystem.playPowerPreparationAnimation(player);
        
        // Broadcast to server
        player.getServer().getPlayerManager().broadcast(
            Text.of("The Effect " + powerType.getName() + " was made by " + player.getName().getString()),
            false
        );

        return true;
    }

    private static boolean hasPower(UUID playerId, PowerType powerType) {
        List<PowerData> powers = playerPowers.get(playerId);
        if (powers == null) return false;
        return powers.stream().anyMatch(data -> data.powerType == powerType);
    }

    private static int getActivePowerCount(UUID playerId) {
        List<PowerData> powers = playerPowers.get(playerId);
        if (powers == null) return 0;
        return (int) powers.stream().filter(data -> data.isActive).count();
    }

    public static PowerData getPowerInSlot(UUID playerId, int slot) {
        List<PowerData> powers = playerPowers.get(playerId);
        if (powers == null || powers.size() < slot) return null;
        return powers.get(slot - 1);
    }

    public static void activatePower(ServerPlayerEntity player, PowerData powerData) {
        long currentTime = System.currentTimeMillis();
        long cooldown = powerData.powerType.getCooldown() * 1000L;
        
        if (currentTime - powerData.lastActivationTime < cooldown) {
            long remaining = (cooldown - (currentTime - powerData.lastActivationTime)) / 1000;
            player.sendMessage(Text.of("Power is on cooldown! " + remaining + " seconds remaining."));
            return;
        }
        
        powerData.lastActivationTime = currentTime;
        AnimationSystem.playPowerActivationAnimation(player, powerData.powerType.getName());
        PowerEffects.applyPowerEffect(player, powerData.powerType.getName());
        
        player.sendMessage(Text.of("Activated " + powerData.powerType.getName() + "!"));
    }

    public static void tick() {
        long currentTime = System.currentTimeMillis();
        
        playerPowers.forEach((playerId, powers) -> {
            powers.forEach(data -> {
                if (!data.isActive && 
                    currentTime - data.preparationStartTime >= PREPARATION_TIME * 1000) {
                    data.isActive = true;
                    ServerPlayerEntity player = getPlayerById(playerId);
                    if (player != null) {
                        player.sendMessage(Text.of("Power " + data.powerType.getName() + " is now active!"));
                        AnimationSystem.playPowerActivationAnimation(player, data.powerType.getName());
                        PowerEffects.applyPowerEffect(player, data.powerType.getName());
                    }
                }
            });
        });
    }

    private static ServerPlayerEntity getPlayerById(UUID playerId) {
        // This is a placeholder - you'll need to implement proper player lookup
        return null;
    }
} 