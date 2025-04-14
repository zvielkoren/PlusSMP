package com.zvicraft.trust;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;

public class TrustSystem {
    private static final Map<UUID, List<UUID>> trustMap = new HashMap<>();

    public static void initialize() {
        // Initialize any required resources
        trustMap.clear();
    }

    public static void cleanup() {
        // Clean up any resources
        trustMap.clear();
    }

    public static void tick() {
        // Perform any periodic trust-related tasks
        // For now, this is a placeholder for future functionality
    }

    private static final Map<UUID, Set<UUID>> trustRelations = new HashMap<>();

    public static boolean addTrust(ServerPlayerEntity player, ServerPlayerEntity trustedPlayer) {
        UUID playerId = player.getUuid();
        UUID trustedId = trustedPlayer.getUuid();

        trustRelations.computeIfAbsent(playerId, k -> new HashSet<>());
        
        if (trustRelations.get(playerId).add(trustedId)) {
            player.sendMessage(Text.of("Added " + trustedPlayer.getName().getString() + " to your trust list"));
            return true;
        }
        return false;
    }

    public static boolean removeTrust(ServerPlayerEntity player, ServerPlayerEntity untrustedPlayer) {
        UUID playerId = player.getUuid();
        UUID untrustedId = untrustedPlayer.getUuid();

        if (trustRelations.containsKey(playerId) && 
            trustRelations.get(playerId).remove(untrustedId)) {
            player.sendMessage(Text.of("Removed " + untrustedPlayer.getName().getString() + " from your trust list"));
            return true;
        }
        return false;
    }

    public static void listTrusted(ServerPlayerEntity player) {
        UUID playerId = player.getUuid();
        Set<UUID> trusted = trustRelations.getOrDefault(playerId, new HashSet<>());
        
        if (trusted.isEmpty()) {
            player.sendMessage(Text.of("Your trust list is empty"));
            return;
        }

        player.sendMessage(Text.of("Players you trust:"));
        trusted.forEach(trustedId -> {
            ServerPlayerEntity trustedPlayer = getPlayerById(trustedId);
            if (trustedPlayer != null) {
                player.sendMessage(Text.of("- " + trustedPlayer.getName().getString()));
            }
        });
    }

    public static boolean isTrusted(UUID playerId, UUID otherPlayerId) {
        return trustRelations.containsKey(playerId) && 
               trustRelations.get(playerId).contains(otherPlayerId);
    }

    private static ServerPlayerEntity getPlayerById(UUID playerId) {
        // This is a placeholder - you'll need to implement proper player lookup
        return null;
    }
} 