package com.zvicraft.item;

import com.zvicraft.PlusSMP;
import com.zvicraft.entity.ModEntities;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModItems {
    public static Item POWER_TYPE;
    public static Item PLUS_BOSS_SPAWN_EGG;

    public static void registerModItems() {
        POWER_TYPE = registerItem("power_type", new Item(new Item.Settings()));

        PLUS_BOSS_SPAWN_EGG = registerItem("plus_boss_spawn_egg",
                new SpawnEggItem(ModEntities.BOSS, new Item.Settings()));

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.INVENTORY).register(entries -> {
            entries.add(POWER_TYPE);
            entries.add(PLUS_BOSS_SPAWN_EGG);
        });

        System.out.println("Registered Mod Items for " + PlusSMP.MOD_ID);
    }

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registries.ITEM, Identifier.of(PlusSMP.MOD_ID, name), item);
    }
}
