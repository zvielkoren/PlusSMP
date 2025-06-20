package com.zvicraft.client.screens;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;
import net.minecraft.util.Identifier;
import net.minecraft.client.render.RenderTickCounter;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.network.PacketByteBuf;
import com.zvicraft.networking.IconChangePayload;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class Overlay {
    private static Identifier currentIcon =  Identifier.tryParse("plussmp", "textures/screens/empty-slot.png");
    public static void register() {
        // Register the payload type
        PayloadTypeRegistry.playS2C().register(IconChangePayload.ID, IconChangePayload.CODEC);
        
        // Register the packet handler
        ClientPlayNetworking.registerGlobalReceiver(IconChangePayload.ID, (payload, context) -> {
            currentIcon = payload.iconTexture();
        });
        
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
            // Draw the icon
            drawContext.drawTexture(currentIcon, baseX, baseY, 0, 0, slotSize, slotSize, slotSize, slotSize);
        });
    }
}
