package com.zvicraft;

public class Power {
    private final String name;
    private final int cooldown;
    private final String onActivate;

    public Power(String name, int cooldown, String onActivate) {
        this.name = name;
        this.cooldown = cooldown;
        this.onActivate = onActivate;
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }

    public String getOnActivate() {
        return onActivate;
    }

    public void activate() {
        // Implement the activation logic here
        // For example, apply effects to the player or trigger events

    }
}