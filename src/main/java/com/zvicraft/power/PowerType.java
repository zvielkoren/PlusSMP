package com.zvicraft.power;

public enum PowerType {
    EMERALD("Emerald Effect", 60),
    ARMY_OF_DOGS("Army of Dogs", 80),
    FIRE_RESISTANCE("Fire Resistance Effect", 40),
    SHIELD("Shield Effect", 50),
    TOTEM("Totem Effect", 60),
    CREEPER("Creeper Effect", 90),
    POISON("Poison Effect", 60),
    VEX_SUMMON("Vex Summon", 60),
    TRIDENT("Trident Power", 90),
    DOLPHIN("Dolphin Power", 60),
    GRAPPLING("Grappling Power", 60),
    CONFUSE("Confuse Power", 60),
    PEARL("Pearl Power", 80),
    LIFESTEAL("Lifesteal Power", 60),
    HEART("Heart Power", 90),
    THUNDER("Thunder Power", 90),
    PIERCING("Piercing Power", 80),
    MACE("Mace Power", 60),
    TRAPPER("Trapper Power", 100),
    UNDERGROUND("Underground Power", 80),
    INVISIBILITY("Invisibility Power", 60),
    SHRINK("Shrink Power", 90),
    COBWEB("CobWeb Power", 40);

    private final String name;
    private final int cooldown;

    PowerType(String name, int cooldown) {
        this.name = name;
        this.cooldown = cooldown;
    }

    public String getName() {
        return name;
    }

    public int getCooldown() {
        return cooldown;
    }
} 