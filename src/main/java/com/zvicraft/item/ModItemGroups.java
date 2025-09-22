package com.zvicraft.item;

import com.zvicraft.PlusSMP;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final RegistryKey<ItemGroup> PLUS_SMP_GROUP_KEY = RegistryKey.of(
        RegistryKeys.ITEM_GROUP, 
        Identifier.of(PlusSMP.MOD_ID, "plus_smp_group")
    );

    public static ItemGroup PLUS_SMP_GROUP;

    public static void registerItemGroups() {
        try {
            if (PLUS_SMP_GROUP == null) {
                PLUS_SMP_GROUP = Registry.register(Registries.ITEM_GROUP,
                     Identifier.of(PlusSMP.MOD_ID, "plus_smp_group"), // Use Identifier directly instead of RegistryKey
                    FabricItemGroup.builder()
                            .displayName(Text.translatable("itemgroup." + PlusSMP.MOD_ID))
                            .icon(() -> new ItemStack(Items.DIAMOND))
                            .build());

                if (PLUS_SMP_GROUP != null) {
                    PlusSMP.LOGGER.info("Successfully registered item group: " + PlusSMP.MOD_ID);
                } else {
                    PlusSMP.LOGGER.error("Failed to register item group: " + PlusSMP.MOD_ID);
                }
            }
        } catch (Exception e) {
            PlusSMP.LOGGER.error("Error registering mod item groups: " + e.getMessage(), e);
        }
    }
}
