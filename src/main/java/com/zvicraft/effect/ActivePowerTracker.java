package com.zvicraft.effect;

import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.world.ServerWorld;
import java.util.*;

public class ActivePowerTracker {
    private static class PowerEffectData {
        final long startTime;
        final List<Entity> spawnedEntities;
        final List<StatusEffectInstance> appliedEffects;
        final ServerPlayerEntity player;

        PowerEffectData(ServerPlayerEntity player) {
            this.startTime = System.currentTimeMillis();
            this.spawnedEntities = new ArrayList<>();
            this.appliedEffects = new ArrayList<>();
            this.player = player;
        }
    }

    private static final Map<UUID, PowerEffectData> activePowerEffects = new HashMap<>();
    private static final long POWER_DURATION = 60 * 1000; // 1 minute in milliseconds

    public static void trackPowerEffect(ServerPlayerEntity player) {
        activePowerEffects.put(player.getUuid(), new PowerEffectData(player));
    }

    public static void trackSpawnedEntity(ServerPlayerEntity player, Entity entity) {
        PowerEffectData data = activePowerEffects.get(player.getUuid());
        if (data != null) {
            data.spawnedEntities.add(entity);
        }
    }

    public static void trackStatusEffect(ServerPlayerEntity player, StatusEffectInstance effect) {
        PowerEffectData data = activePowerEffects.get(player.getUuid());
        if (data != null) {
            data.appliedEffects.add(effect);
        }
    }

    public static void tick() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<UUID, PowerEffectData>> iterator = activePowerEffects.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<UUID, PowerEffectData> entry = iterator.next();
            PowerEffectData data = entry.getValue();

            if (currentTime - data.startTime >= POWER_DURATION) {
                // Remove spawned entities
                for (Entity entity : data.spawnedEntities) {
                    if (entity.isAlive()) {
                        entity.remove(Entity.RemovalReason.DISCARDED);
                    }
                }

                // Remove status effects
                for (StatusEffectInstance effect : data.appliedEffects) {
                    data.player.removeStatusEffect(effect.getEffectType());
                }

                iterator.remove();
            }
        }
    }
}