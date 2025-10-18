package com.zvicraft.power;

import com.zvicraft.animation.AnimationSystem;
import com.zvicraft.database.PowerDatabase;
import com.zvicraft.effect.PowerEffects;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;

import java.util.*;
import java.util.Random;

public class PowerSystem {
    public static final Map<UUID, List<PowerData>> playerPowers = Collections.synchronizedMap(new HashMap<>());
    private static final int MAX_ACTIVE_POWERS = 2;
    private static final int PREPARATION_TIME = 1; // seconds
    private static final Map<UUID, Boolean> firstJoinPlayers = Collections.synchronizedMap(new HashMap<>());
    private static final Object lock = new Object(); // For thread-safe operations
    public static PowerDatabase PowerDatabase;
    private static MinecraftServer server;

    public static void initialize(MinecraftServer srv, PowerDatabase db) {
        server = srv;
        PowerDatabase = db;
        playerPowers.clear();
    }

    public static void cleanup() {
        synchronized (lock) {
            playerPowers.clear();
        }
    }

    public static void initializePlayerPowers(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        synchronized (lock) {
            playerPowers.computeIfAbsent(playerId, k -> Collections.synchronizedList(new ArrayList<>()));
        }
    }

    public static void loadPlayerPowers(ServerPlayerEntity player) {
        if (!isPlayerInitialized(player)) {
            initializePlayerPowers(player);
        }
    }

    private static boolean isPlayerInitialized(ServerPlayerEntity player) {
        return playerPowers.containsKey(player.getUuid());
    }

    public static boolean hasPower(ServerPlayerEntity player) {
        List<PowerData> powers = playerPowers.get(player.getUuid());
        return powers != null && !powers.isEmpty();
    }
    
    public static boolean hasPower(ServerPlayerEntity player, PowerType powerType) {
        List<PowerData> powers = playerPowers.get(player.getUuid());
        if (powers == null) return false;
        return powers.stream().anyMatch(data -> data != null && data.powerType == powerType);
    }
    
    public static PowerType getRandomPower() {
        PowerType[] allPowers = PowerType.values();
        List<PowerType> validPowers = new ArrayList<>();
        
        // Filter out KOCHAVLET and GENERAL powers
        for (PowerType power : allPowers) {
            if (power != PowerType.KOCHAVLET && power != PowerType.GENERAL) {
                validPowers.add(power);
            }
        }
        
        if (validPowers.isEmpty()) {
            return null;
        }
        
        return validPowers.get(new Random().nextInt(validPowers.size()));
    }

    public static PowerData getPlayerPower(ServerPlayerEntity player) {
        List<PowerData> powers = playerPowers.get(player.getUuid());
        if (powers == null || powers.isEmpty()) return null;
        return powers.get(0);
    }

    public static PowerType getPower(ServerPlayerEntity player) {
        PowerData data = getPlayerPower(player);
        return data != null ? data.powerType : null;
    }

    public static boolean hasEmptyPowerSlot(ServerPlayerEntity player) {
        List<PowerData> powers = playerPowers.get(player.getUuid());
        return powers != null && powers.size() < MAX_ACTIVE_POWERS;
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
        return setPowerInNextSlot(player, powerType, specifiedSlot, null, false);
    }

    public static SetPowerResult setPowerInNextSlot(ServerPlayerEntity player, PowerType powerType, Integer specifiedSlot, UUID targetId, boolean isTargeted) {
        UUID playerId = player.getUuid();
        List<PowerData> powers = playerPowers.computeIfAbsent(playerId, k -> new ArrayList<>());

        int targetSlot = specifiedSlot != null ? specifiedSlot : findEmptySlot(powers);
        if (targetSlot < 1 || targetSlot > MAX_ACTIVE_POWERS) {
            return new SetPowerResult(SetPowerStatus.INVALID_SLOT, -1);
        }

        while (powers.size() < targetSlot) {
            powers.add(null);
        }

        PowerData powerData = new PowerData(powerType, System.currentTimeMillis(), targetId, isTargeted);
        powers.set(targetSlot - 1, powerData);

        savePlayerPowers(player, powers);
        player.sendMessage(Text.of("Started preparing " + powerType.getName() + " power in slot " + targetSlot + (isTargeted ? " (Targeted)" : "") + "..."));
        AnimationSystem.playPowerPreparationAnimation(player);

        return new SetPowerResult(SetPowerStatus.SUCCESS, targetSlot);
    }

    private static int findEmptySlot(List<PowerData> powers) {
        for (int i = 0; i < MAX_ACTIVE_POWERS; i++) {
            if (i >= powers.size() || powers.get(i) == null) {
                return i + 1;
            }
        }
        return -1;
    }

    public static void givePower(ServerPlayerEntity player, PowerType powerType) {
        UUID playerId = player.getUuid();
        List<PowerData> powers = playerPowers.computeIfAbsent(playerId, k -> new ArrayList<>());

        for (int i = 0; i < MAX_ACTIVE_POWERS; i++) {
            if (i >= powers.size() || powers.get(i) == null) {
                PowerData powerData = new PowerData(powerType, System.currentTimeMillis());
                if (i < powers.size()) powers.set(i, powerData);
                else powers.add(powerData);

                savePlayerPowers(player, powers);
                return;
            }
        }
        throw new IllegalStateException("No empty power slots available");
    }

    public static void clearPlayerPower(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        List<PowerData> powers = playerPowers.get(playerId);
        if (powers != null) {
            powers.clear();
            PowerDatabase.savePlayerPowers(player, null, null);
        }
    }

    public static boolean startPowerPreparation(ServerPlayerEntity player, PowerType powerType) {
        UUID playerId = player.getUuid();
        if (hasPower(playerId, powerType)) return false;

        if (getActivePowerCount(playerId) >= MAX_ACTIVE_POWERS) {
            player.sendMessage(Text.of("You can only have " + MAX_ACTIVE_POWERS + " active powers!"));
            return false;
        }

        List<PowerData> powers = playerPowers.computeIfAbsent(playerId, k -> new ArrayList<>());
        powers.add(new PowerData(powerType, System.currentTimeMillis()));

        player.sendMessage(Text.of("Started preparing " + powerType.getName() + " power..."));
        AnimationSystem.playPowerPreparationAnimation(player);
        server.getPlayerManager().broadcast(Text.of("The Effect " + powerType.getName() + " was made by " + player.getName().getString()), false);

        return true;
    }

    public static boolean hasPower(UUID playerId, PowerType powerType) {
        List<PowerData> powers = playerPowers.get(playerId);
        return powers != null && powers.stream().anyMatch(data -> data.powerType.equals(powerType));
    }

    private static int getActivePowerCount(UUID playerId) {
        List<PowerData> powers = playerPowers.get(playerId);
        return powers == null ? 0 : (int) powers.stream().filter(data -> data != null && data.isActive).count();
    }

    public static PowerData getPowerInSlot(ServerPlayerEntity player, int slot) {
        List<PowerData> powers = playerPowers.get(player.getUuid());
        if (powers == null || powers.size() < slot) return null;
        return powers.get(slot - 1);
    }

    public static void activatePower(ServerPlayerEntity player, PowerData powerData) {
        if (powerData == null || powerData.powerType == null) return;

        long currentTime = System.currentTimeMillis();
        if (currentTime - powerData.lastActivationTime < 1000) {
            player.sendMessage(Text.of("Power is on cooldown!"));
            return;
        }

        powerData.lastActivationTime = currentTime;

        if (powerData.isTargeted && powerData.targetId != null) {
            ServerWorld world = player.getServerWorld();
            PlayerEntity target = findEntityByUuid(world, powerData.targetId);

            if (target != null) {
                if (target instanceof ServerPlayerEntity targetPlayer) {
                    AnimationSystem.playPowerActivationAnimation(targetPlayer, powerData.powerType.getName());
                    PowerEffects.applyPowerEffect(targetPlayer, powerData.powerType);
                    targetPlayer.sendMessage(Text.of("You've been affected by " + player.getName().getString() + "'s " + powerData.powerType.getName() + "!"));
                } else {
                    AnimationSystem.playPowerActivationAnimation(target, powerData.powerType.getName());
                    // Add mob-specific effect logic here
                }
            } else {
                player.sendMessage(Text.of("Target not found."));
            }
        } else {
            AnimationSystem.playPowerActivationAnimation(player, powerData.powerType.getName());
            PowerEffects.applyPowerEffect(player, powerData.powerType);
        }

        player.sendMessage(Text.of("Activated " + powerData.powerType.getName() + "!"));
    }
    private static PlayerEntity findEntityByUuid(ServerWorld world, UUID uuid) {
        for (PlayerEntity player : world.getPlayers()) {
            if (player.getUuid().equals(uuid)) {
                return player;
            }
        }
        return null;
    }


    public static void tick() {
        long currentTime = System.currentTimeMillis();

        // Create a copy of the keys to avoid ConcurrentModificationException
        List<UUID> playerIds;
        synchronized (lock) {
            playerIds = new ArrayList<>(playerPowers.keySet());
        }

        for (UUID playerId : playerIds) {
            List<PowerData> powers = playerPowers.get(playerId);
            if (powers == null) continue;
            
            // Process each power data safely
            for (int i = 0; i < powers.size(); i++) {
                PowerData data = powers.get(i);
                if (data != null && !data.isActive &&
                        currentTime - data.preparationStartTime >= PREPARATION_TIME * 1000) {
                    data.isActive = true;
                    ServerPlayerEntity player = getPlayerById(playerId);
                    if (player != null) {
                        player.sendMessage(Text.of("Power " + data.powerType.getName() + " is now active!"));
                        AnimationSystem.playPowerActivationAnimation(player, data.powerType.getName());
                        PowerEffects.applyPowerEffect(player, data.powerType);
                    }
                }
            }
        }
    }

    private static ServerPlayerEntity getPlayerById(UUID playerId) {
        return server.getPlayerManager().getPlayer(playerId);
    }

//    public static PowerType getRandomPower() {
//        PowerType[] powers = PowerType.values();
//        return powers[new Random().nextInt(powers.length)];
//    }

    public static void giveRandomPowerOnJoin(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        if (!firstJoinPlayers.containsKey(playerId)) {
            setPowerInNextSlot(player, getRandomPower(), 1);
            firstJoinPlayers.put(playerId, true);
        }
    }

    private static void savePlayerPowers(ServerPlayerEntity player, List<PowerData> powers) {
        PowerType power1 = powers.size() > 0 && powers.get(0) != null ? powers.get(0).powerType : null;
        PowerType power2 = powers.size() > 1 && powers.get(1) != null ? powers.get(1).powerType : null;
        PowerDatabase.savePlayerPowers(player, power1, power2);
    }

    // PowerData definition
    public static class PowerData {
        public final PowerType powerType;
        public final long preparationStartTime;
        public boolean isActive;
        public long lastActivationTime;
        public UUID targetId;
        public boolean isTargeted;

        public PowerData(PowerType powerType, long preparationStartTime) {
            this(powerType, preparationStartTime, null, false);
        }

        public PowerData(PowerType powerType, long preparationStartTime, UUID targetId, boolean isTargeted) {
            this.powerType = powerType;
            this.preparationStartTime = preparationStartTime;
            this.isActive = false;
            this.lastActivationTime = 0;
            this.targetId = targetId;
            this.isTargeted = isTargeted;
        }
    }
}
