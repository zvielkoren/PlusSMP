package com.zvicraft;

import com.zvicraft.client.ClientKeyHandler;
import com.zvicraft.client.DiscordRPCHandler;
import com.zvicraft.keybinding.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

@Environment(EnvType.CLIENT)
public class PlusSMPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register key bindings
        KeyBindings.register();
        
        // Register client event handlers
        ClientKeyHandler.register();
        
        // Initialize Discord RPC
        DiscordRPCHandler.initialize();
        
        // Register shutdown hook for Discord RPC
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            DiscordRPCHandler.shutdown();
        });
    }
}
