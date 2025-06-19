package com.zvicraft.power;

import com.zvicraft.animation.AnimationSystem;
import com.zvicraft.database.PowerDatabase;
import com.zvicraft.effect.PowerEffects;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.*;

public class PowerSystem {
    private static final Map<UUID, List<PowerData>> playerPowers = new HashMap<>();
    private static final int MAX_ACTIVE_POWERS = 2;
    private static final int PREPARATION_TIME = 60; // 1 minute in seconds
    private static final Map<UUID, Boolean> firstJoinPlayers = new HashMap<>();

    public static void initializePlayerPowers(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        playerPowers.computeIfAbsent(playerId, k -> new ArrayList<>());
    }

    public static void loadPlayerPowers(ServerPlayerEntity player) {
        // וודא שהשחקן מאותחל לפני טעינה
        if (!isPlayerInitialized(player)) {
            initializePlayerPowers(player);
        }
    }

    private static boolean isPlayerInitialized(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        return playerPowers.containsKey(playerId);
    }

    public enum SetPowerStatus {
    SUCCESS,
    NO_EMPTY_SLOT,
    INVALID_SLOT
}

public static class SetPowerResult {
    public final SetPowerStatus status;
    public final int slot;

    public SetPowerResult(SetPowerStatus status, int slot) {
        this.status = status;
        this.slot = slot;
    }
}

public static SetPowerResult setPowerInNextSlot(ServerPlayerEntity player, PowerType powerType, Integer specifiedSlot) {
    UUID playerId = player.getUuid();
    List<PowerData> powers = playerPowers.computeIfAbsent(playerId, k -> new ArrayList<>());
    
    int targetSlot;
    
    if (specifiedSlot != null) {
        // Use specified slot
        targetSlot = specifiedSlot;
        if (targetSlot < 1 || targetSlot > MAX_ACTIVE_POWERS) {
            return new SetPowerResult(SetPowerStatus.INVALID_SLOT, -1);
        }
    } else {
        // Find first empty slot
        targetSlot = findEmptySlot(powers);
        if (targetSlot == -1) {
            return new SetPowerResult(SetPowerStatus.NO_EMPTY_SLOT, -1);
        }
    }
    
    // Ensure the list has enough slots
    while (powers.size() < targetSlot) {
        powers.add(null);
    }
    
    // Set the power in the target slot
    powers.set(targetSlot - 1, new PowerData(powerType, System.currentTimeMillis()));
    
    // Save to database
    PowerType power1 = powers.size() >= 1 ? (powers.get(0) != null ? powers.get(0).powerType : null) : null;
    PowerType power2 = powers.size() >= 2 ? (powers.get(1) != null ? powers.get(1).powerType : null) : null;
    PowerDatabase.savePlayerPowers(player, power1, power2);
    
    player.sendMessage(Text.of("Started preparing " + powerType.getName() + " power in slot " + targetSlot + "..."));
    AnimationSystem.playPowerPreparationAnimation(player);
    
    return new SetPowerResult(SetPowerStatus.SUCCESS, targetSlot);
}

private static int findEmptySlot(List<PowerData> powers) {
    for (int i = 0; i < MAX_ACTIVE_POWERS; i++) {
        if (powers.size() <= i || powers.get(i) == null) {
            return i + 1;
        }
    }
    return -1;
}
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

public static PowerData getPowerInSlot(ServerPlayerEntity player, int slot) {
    List<PowerData> powers = playerPowers.get(player.getUuid());
    if (powers == null || powers.size() < slot) {
        System.out.println("No active power found in slot " + slot + " for player " + 
                         player.getName().getString());
        return null;
    }
    PowerData power = powers.get(slot - 1);
    if (power == null) {
        System.out.println("Empty power slot " + slot + " for player " + 
                         player.getName().getString());
    } else {
        System.out.println("Found power " + power.powerType.getName() + " in slot " + 
                         slot + " for player " + player.getName().getString());
    }
    return power;
}

    public static void activatePower(ServerPlayerEntity player, PowerData powerData) {
        long currentTime = System.currentTimeMillis();
        long cooldown = powerData.powerType.getCooldownSeconds() * 1000L;
        
        if (currentTime - powerData.lastActivationTime < cooldown) {
            long remaining = (cooldown - (currentTime - powerData.lastActivationTime)) / 1000;
            player.sendMessage(Text.of("Power is on cooldown! " + remaining + " seconds remaining."));
            return;
        }
        
        powerData.lastActivationTime = currentTime;
        AnimationSystem.playPowerActivationAnimation(player, powerData.powerType.getName());
        PowerEffects.applyPowerEffect(player, powerData.powerType);
        
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
                        PowerEffects.applyPowerEffect(player, data.powerType);
                    }
                }
            });
        });
    }

    private static ServerPlayerEntity getPlayerById(UUID playerId) {
        // This is a placeholder - you'll need to implement proper player lookup
        return null;
    }

public static PowerType getRandomPower() {
    PowerType[] powers = PowerType.values();
    int randomIndex = (int) (Math.random() * powers.length);
    return powers[randomIndex];
}

public static void giveRandomPowerOnJoin(ServerPlayerEntity player) {
    UUID playerId = player.getUuid();
    if (!firstJoinPlayers.containsKey(playerId)) {
        PowerType randomPower = getRandomPower();
        setPowerInNextSlot(player, randomPower, 1); // Use slot 1 for first power
        firstJoinPlayers.put(playerId, true);
    }
}
}