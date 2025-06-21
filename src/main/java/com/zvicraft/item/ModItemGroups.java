package com.zvicraft.item;

import com.zvicraft.PlusSMP;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static final ItemGroup PLUS_SMP_GROUP = Registry.register(Registries.ITEM_GROUP,
             Identifier.of(PlusSMP.MOD_ID, "plus_smp_group"),
            FabricItemGroup.builder()
                    .displayName(Text.translatable("itemgroup." + PlusSMP.MOD_ID))
                    .icon(() -> new ItemStack(ModItems.KOCHAVLET))
                    .build()
    );

    public static void registerItemGroups() {
        PlusSMP.LOGGER.info("Registering Mod Item Groups for " + PlusSMP.MOD_ID);
    }
}
