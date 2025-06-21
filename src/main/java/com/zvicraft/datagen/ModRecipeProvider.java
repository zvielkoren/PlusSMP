package com.zvicraft.datagen;

import com.zvicraft.PlusSMP;
import com.zvicraft.item.ModItems;
import com.zvicraft.power.PowerType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.data.server.recipe.ShapelessRecipeJsonBuilder;
import net.minecraft.item.Items;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.Identifier;

import java.util.concurrent.CompletableFuture;

public class ModRecipeProvider extends FabricRecipeProvider {
    public ModRecipeProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    public void generate(RecipeExporter exporter) {
        // Generate recipes for each power type
        for (PowerType powerType : PowerType.values()) {
            generatePowerRecipe(exporter, powerType);
        }
    }

    private void generatePowerRecipe(RecipeExporter exporter, PowerType powerType) {
        var powerItem = ModItems.getPowerItem(powerType);
        if (powerItem == null) return;

        String recipeName = powerType.name().toLowerCase() + "_power";

        // Option 1: Shaped Recipe (3x3 pattern)
        ShapedRecipeJsonBuilder.create(RecipeCategory.MISC, powerItem, 1)
                .pattern("GRG")
                .pattern("RDR")
                .pattern("GRG")
                .input('G', Items.DIRT)
                .input('R', Items.DIRT)
                .input('D', Items.DIRT)
                .criterion(hasItem(Items.DIRT), conditionsFromItem(Items.DIRT))
                .criterion(hasItem(Items.DIRT), conditionsFromItem(Items.DIRT))
                .criterion(hasItem(Items.DIRT), conditionsFromItem(Items.DIRT))
                .offerTo(exporter, Identifier.of(PlusSMP.MOD_ID, recipeName));

        // Option 2: Shapeless Recipe (uncomment if you prefer this)
        /*
        ShapelessRecipeJsonBuilder.create(RecipeCategory.MISC, powerItem, 1)
                .input(Items.SUGAR)
                .input(Items.REDSTONE)
                .input(Items.DIAMOND)
                .criterion(hasItem(Items.SUGAR), conditionsFromItem(Items.SUGAR))
                .criterion(hasItem(Items.REDSTONE), conditionsFromItem(Items.REDSTONE))
                .criterion(hasItem(Items.DIAMOND), conditionsFromItem(Items.DIAMOND))
                .offerTo(exporter, Identifier.of(PlusSMP.MOD_ID, recipeName));
        */
    }
}