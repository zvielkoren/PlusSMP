package com.zvicraft;

import net.minecraft.entity.player.PlayerEntity;

public class PowerEffect {
    private final String name;
    private final int duration; // Duration in seconds
    private final String description;

    public PowerEffect(String name, int duration, String description) {
        this.name = name;
        this.duration = duration;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getDuration() {
        return duration;
    }

    public String getDescription() {
        return description;
    }

    public void applyEffect(PlayerEntity player) {
        // Logic to apply the effect to the player
    }

    public void removeEffect(PlayerEntity player) {
        // Logic to remove the effect from the player
    }
}