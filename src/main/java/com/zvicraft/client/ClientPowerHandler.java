package com.zvicraft.client;

import com.zvicraft.keybinding.KeyBindings;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class ClientPowerHandler {
    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            
            // Handle first power key
            while (KeyBindings.POWER_1_KEY.wasPressed()) {
                activatePower(client, 1);
            }
            
            // Handle second power key
            while (KeyBindings.POWER_2_KEY.wasPressed()) {
                activatePower(client, 2);
            }
        });
    }
    
    private static void activatePower(MinecraftClient client, int powerSlot) {
        if (client.player == null) return;
        
        // Send activation packet to server
        String message = String.format("/activatepower %d", powerSlot);
        client.player.networkHandler.sendCommand(message);
        
        // Show activation message
        client.player.sendMessage(Text.of("Activating power " + powerSlot), true);
    }
} 