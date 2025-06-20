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
        // Register GENERAL_POWER first
        GENERAL_POWER = Registry.register(
            Registries.ITEM,
            Identifier.of(PlusSMP.MOD_ID, "general_power"),
            new PowerDropItem(
                new Item.Settings().maxCount(1),
                PowerType.KOCHAVLET.name()
            )
        );

        // Register power items for each PowerType
        for (PowerType powerType : PowerType.values()) {
            String itemName = powerType.name().toLowerCase() + "_power";
            PowerDropItem powerItem = new PowerDropItem(
                new Item.Settings().maxCount(1),
                powerType.name()
            );
            POWER_ITEMS.put(powerType, Registry.register(
                Registries.ITEM,
                Identifier.of(PlusSMP.MOD_ID, itemName),
                powerItem
            ));
        }


        // Register Kochavlet item
        KOCHAVLET = Registry.register(
            Registries.ITEM, 
            Identifier.of(PlusSMP.MOD_ID, "kochavlet"),
            new KochavletItem(new Item.Settings().maxCount(16))
        );


        // הוספת האייטמים לטאב הקריאטיב INGREDIENTS
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INGREDIENTS).register(entries -> {
            POWER_ITEMS.values().forEach(entries::add);
            entries.add(KOCHAVLET);
        });

        System.out.println("Registered Mod Items for " + PlusSMP.MOD_ID);
    }

    public static PowerDropItem getPowerItem(PowerType powerType) {
        return POWER_ITEMS.get(powerType);
    }
}
