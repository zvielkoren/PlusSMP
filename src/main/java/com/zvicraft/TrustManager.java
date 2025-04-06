package com.zvicraft;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TrustManager {
    private final Map<UUID, Set<UUID>> trustMap = new HashMap<>();

    public void trust(UUID playerId, UUID trustedPlayerId) {
        trustMap.computeIfAbsent(playerId, k -> new HashSet<>()).add(trustedPlayerId);
    }

    public void untrust(UUID playerId, UUID untrustedPlayerId) {
        Set<UUID> trustedPlayers = trustMap.get(playerId);
        if (trustedPlayers != null) {
            trustedPlayers.remove(untrustedPlayerId);
        }
    }

    public boolean isTrusted(UUID playerId, UUID otherPlayerId) {
        Set<UUID> trustedPlayers = trustMap.get(playerId);
        return trustedPlayers != null && trustedPlayers.contains(otherPlayerId);
    }

    public Set<UUID> getTrustedPlayers(UUID playerId) {
        return trustMap.getOrDefault(playerId, new HashSet<>());
    }
}