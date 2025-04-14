package com.zvicraft.client.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderTickCounter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public class Overlay {
    public static void register() {
        // Register HUD rendering callback

        HudRenderCallback.EVENT.register((DrawContext drawContext, RenderTickCounter tickCounter) -> {
            MinecraftClient client = MinecraftClient.getInstance();

            // Check that player and world exist
            if (client.player == null || client.world == null) return;

            // Get screen dimensions
            Window window = client.getWindow();
            int w = window.getScaledWidth();
            int h = window.getScaledHeight();
            int slotSize = 24;
            int spacing = 6; // Space between the slots

            int totalWidth = slotSize * 2 + spacing;
            int baseX = (w - totalWidth) / 2;
            int baseY = h - 70;
            // Resource location for the slot texture
            Identifier texture = Identifier.of("plussmp", "textures/screens/empty-slot.png");

            // Draw two inventory slot icons
            drawContext.drawTexture(texture, baseX,
                    baseY, 0, 0, slotSize, slotSize, slotSize, slotSize);
            drawContext.drawTexture(texture, baseX + slotSize + spacing, baseY, 0, 0, slotSize, slotSize, slotSize, slotSize);
        });
    }
}
