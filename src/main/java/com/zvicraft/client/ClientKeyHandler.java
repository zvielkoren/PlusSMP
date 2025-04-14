package com.zvicraft.client;

import com.zvicraft.keybinding.KeyBindings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientKeyHandler {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            
            // Check for key binding changes and activate powers
            if (KeyBindings.POWER_1_KEY != null && KeyBindings.POWER_1_KEY.wasPressed()) {
                activatePower(client, 1);
            }
            if (KeyBindings.POWER_2_KEY != null && KeyBindings.POWER_2_KEY.wasPressed()) {
                activatePower(client, 2);
            }
        });
    }
    
    private static void activatePower(MinecraftClient client, int slot) {
        if (client.player != null) {
            client.player.sendMessage(Text.literal("Activating power " + slot), true);
            // Here you can add any client-side effects when activating the power
        }
    }
} 