package com.zvicraft.item;

import com.zvicraft.PlusSMP;
import com.zvicraft.power.PowerType;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.EnumMap;
import java.util.Map;

public class ModItems {
    public static final Map<PowerType, PowerDropItem> POWER_ITEMS = new EnumMap<>(PowerType.class);
    
    // Declare items without initializing them here to avoid static initialization issues
    public static PowerDropItem GENERAL_POWER;
    public static KochavletItem KOCHAVLET;

    public static void registerModItems() {
        try {
//            // Register GENERAL_POWER first
//            GENERAL_POWER = Registry.register(
//                Registries.ITEM,
//                Identifier.of(PlusSMP.MOD_ID, "general"),
//                new PowerDropItem(
//                    new Item.Settings().maxCount(1),
//                    PowerType.KOCHAVLET.name()
//                )
//            );

            // Register power items for each PowerType
            for (PowerType powerType : PowerType.values()) {
                if (powerType != null && powerType.name() != null) {
                    String itemName = powerType.name().toLowerCase() + "_power";
                    PowerDropItem powerItem = new PowerDropItem(
                        new Item.Settings().maxCount(1),
                        powerType.name()
                    );
                    PowerDropItem registeredItem = Registry.register(
                        Registries.ITEM,
                        Identifier.of(PlusSMP.MOD_ID, itemName),
                        powerItem
                    );
                    if (registeredItem != null) {
                        POWER_ITEMS.put(powerType, registeredItem);
                    }
                }
            }

            // Register Kochavlet item
            KOCHAVLET = Registry.register(
                Registries.ITEM, 
                Identifier.of(PlusSMP.MOD_ID, "kochavlet"),
                new KochavletItem(new Item.Settings().maxCount(16))
            );

            // Only add items to creative tabs if they were successfully registered
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
                POWER_ITEMS.values().stream()
                    .filter(item -> item != null)
                    .forEach(entries::add);
                if (KOCHAVLET != null) {
                    entries.add(KOCHAVLET);
                }
            });

            // Add items to custom inventory tab
            ItemGroupEvents.modifyEntriesEvent(ItemGroups.TOOLS).register(entries -> {
                try {
                    // Add all power items to custom tab
                    POWER_ITEMS.values().stream()
                        .filter(item -> item != null)
                        .forEach(entries::add);

                    // Add Kochavlet item to custom tab
                    if (KOCHAVLET != null) {
                        entries.add(KOCHAVLET);
                    }

                    System.out.println("Added " + POWER_ITEMS.size() + " power items and Kochavlet to custom inventory tab");
                } catch (Exception e) {
                    System.err.println("Error adding items to custom inventory tab: " + e.getMessage());
                    e.printStackTrace();
                }
            });

            System.out.println("Registered Mod Items for " + PlusSMP.MOD_ID);
        } catch (Exception e) {
            System.err.println("Error registering mod items: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static PowerDropItem getPowerItem(PowerType powerType) {
        return POWER_ITEMS.get(powerType);
    }

    // Method to register items in custom inventory tab - call this after ModItemGroups.registerItemGroups()
    public static void registerCustomInventoryTab() {
        try {
            if (ModItemGroups.PLUS_SMP_GROUP != null) {
                ItemGroupEvents.modifyEntriesEvent(ModItemGroups.PLUS_SMP_GROUP_KEY).register(entries -> {
                    try {
                        // Add all power items to custom tab
                        POWER_ITEMS.values().stream()
                            .filter(item -> item != null)
                            .forEach(entries::add);

                        // Add Kochavlet item to custom tab
                        if (KOCHAVLET != null) {
                            entries.add(KOCHAVLET);
                        }

                        System.out.println("Added " + POWER_ITEMS.size() + " power items and Kochavlet to custom inventory tab");
                    } catch (Exception e) {
                        System.err.println("Error adding items to custom inventory tab: " + e.getMessage());
                        e.printStackTrace();
                    }
                });
            } else {
                System.err.println("ModItemGroups.PLUS_SMP_GROUP is null, cannot register custom inventory tab");
            }
        } catch (Exception e) {
            System.err.println("Error registering custom inventory tab: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
