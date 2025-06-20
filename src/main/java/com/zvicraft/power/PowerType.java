package com.zvicraft.power;

import com.google.gson.internal.bind.JsonTreeWriter;

public enum PowerType {
    SPEED("Speed Power", 60, "כוח מהירות"),
    DOGS("Dogs Power", 180, "כוח הכלבים"),
    BLINDNESS("Blindness Power", 180, "כוח העיוורון"),
    CAGE("Cage Power", 180, "כוח הכלוב"),
    EMERALD("Emerald Power", 240, "כוח האמרלד"),
    IMMUNITY("Immunity Power", 50, "כוח החסינות"),
    CREEPER("Creeper Power", 120, "כוח הקריפר"),
    POISON("Poison Power", 120, "כוח הפויזן"),
    TOTEM("Totem Power", 180, "כוח הטוטם"),
    VEX("Vex Power", 180, "כוח הווקסים"),
    DOLPHIN("Dolphin Power", 60, "כוח הדולפין"),
    GRAPPLING("Grappling Power", 180, "כוח הגראפלינג"),
    PEARL("Pearl Power", 300, "כוח הפארל"),
    LIFESTEAL("Lifesteal Power", 180, "כוח הלייפסטיל"),
    HEART("Heart Power", -1, "כוח הלב"), // -1 indicates no cooldown
    SHIELD_PIERCING("Shield Piercing Power", 120, "כוח החסין שילד"),
    SHRINK("Shrink Power", 90, "כוח השרינק"),
    COBWEB("Cobweb Power", -1, "כוח הקאבוואב"), // -1 indicates no cooldown
    MACE("Mace Power", 90, "כוח המייס"),
    INVISIBILITY("Invisibility Power", 90, "כוח האינוויז"),
    STRENGTH("Strength Power", 90, "כוח הסטרנג"),
    SLOWNESS("Slowness Power", 90, "כוח הסלובנס"),
    WEAKNESS("Weakness Power", 90, "כוח הוויקנס"),
    WITHER("Wither Power", 90, "כוח הויטר"),
    KOCHAVLET("Kochavlet", 0, "כוחות"),
    GENERAL("General", 0, "כוחות");


//    public static final String GENERAL = "general";
    private final String name;
    private int cooldownSeconds;
    private final String hebrewName;

    PowerType(String name, int cooldownSeconds, String hebrewName) {
        this.name = name;
        this.cooldownSeconds = cooldownSeconds;
        this.hebrewName = hebrewName;
    }

    public String getName() {
        return name;
    }

    public String getHebrewName() {
        return hebrewName;
    }

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }
    
    public void setCooldownSeconds(int seconds) {
        this.cooldownSeconds = seconds;
    }

    public long getCooldown() {
        return 0;
    }
}