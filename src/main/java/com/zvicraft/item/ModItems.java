package com.zvicraft.item;

import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

import static com.zvicraft.PlusSMP.MOD_ID;
import org.slf4j.Logger;

public class ModItems {
    // Example item registration
    // public static final Item EXAMPLE_ITEM = registerItem("example_item",
    //     new Item(new Item.Settings()));
    public static final Item POWER_TYPE = new Item(new Item.Settings());

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(MOD_ID, name), item);
    }

    public static void registerModItems() {
        // Register all items here
        // EXAMPLE_ITEM will be registered when uncommented
        System.out.println("Registering Mod Items for " + MOD_ID);
        registerItem("power_type", POWER_TYPE);
    }   
} 