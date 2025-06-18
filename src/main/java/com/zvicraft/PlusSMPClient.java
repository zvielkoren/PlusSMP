package com.zvicraft;

import com.zvicraft.client.ClientKeyHandler;
import com.zvicraft.discord.DiscordIntegration;
import com.zvicraft.entity.boss.PlusBossRenderer;
import com.zvicraft.keybinding.KeyBindingHandler;
import com.zvicraft.keybinding.KeyBindings;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import com.zvicraft.entity.boss.PlusBossRenderer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

@Environment(EnvType.CLIENT)
public class PlusSMPClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        // Register key bindings
        KeyBindings.register();
        KeyBindingHandler.register();

        // Register client event handlers
        ClientKeyHandler.register();
        
        // Initialize Discord RPC
        DiscordIntegration.initialize();
        // Register shutdown hook for Discord RPC
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            DiscordIntegration.shutdown();
        });
    }
}
