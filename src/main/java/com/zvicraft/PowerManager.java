package com.zvicraft;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PowerManager {
    private final Map<UUID, Power> activePowers = new HashMap<>();
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public void addPower(UUID playerId, Power power) {
        activePowers.put(playerId, power);
        cooldowns.put(playerId, System.currentTimeMillis() + power.getCooldown() * 1000);
    }

    public void removePower(UUID playerId) {
        activePowers.remove(playerId);
        cooldowns.remove(playerId);
    }

    public Power getPower(UUID playerId) {
        return activePowers.get(playerId);
    }

    public boolean hasPower(UUID playerId) {
        return activePowers.containsKey(playerId);
    }

    public boolean isOnCooldown(UUID playerId) {
        Long cooldownEnd = cooldowns.get(playerId);
        return cooldownEnd != null && System.currentTimeMillis() < cooldownEnd;
    }

    public void activatePower(UUID playerId) {
        if (hasPower(playerId) && !isOnCooldown(playerId)) {
            Power power = getPower(playerId);
            power.activate();
            cooldowns.put(playerId, System.currentTimeMillis() + power.getCooldown() * 1000);
        }
    }
}