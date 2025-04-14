package com.zvicraft;

import com.zvicraft.block.ModBlocks;
import com.zvicraft.command.PowerCommand;
import com.zvicraft.command.TrustCommand;
import com.zvicraft.database.PowerDatabase;
import com.zvicraft.item.ModItems;
import com.zvicraft.keybinding.KeyBindings;
import com.zvicraft.power.PowerSystem;
import com.zvicraft.trust.TrustSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlusSMP implements ModInitializer {
    public static final String MOD_ID = "plussmp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        LOGGER.info("Initializing PlusSMP Mod");

        // Initialize database
        PowerDatabase.initialize();

        // Register blocks and items
        ModBlocks.registerModBlocks();
        ModItems.registerModItems();

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PowerCommand.register(dispatcher);
            TrustCommand.register(dispatcher);
        });

        // Register server events
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    private void onServerStarting(MinecraftServer server) {
        LOGGER.info("Server starting - initializing systems");
        PowerSystem.initialize();
        TrustSystem.initialize();
    }

    private void onServerStopped(MinecraftServer server) {
        LOGGER.info("Server stopping - cleaning up systems");
        PowerSystem.cleanup();
        TrustSystem.cleanup();
        PowerDatabase.close();
    }

    private void onServerTick(MinecraftServer server) {
        PowerSystem.tick();
        TrustSystem.tick();
    }
} 