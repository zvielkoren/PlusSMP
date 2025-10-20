package com.zvicraft;

import com.zvicraft.block.ModBlocks;
import com.zvicraft.command.KeyBindingCommand;
import com.zvicraft.command.PowerCommand;
import com.zvicraft.command.TrustCommand;
import com.zvicraft.database.PowerDatabase;
import com.zvicraft.effect.ActivePowerTracker;
import com.zvicraft.item.ModItems;
import com.zvicraft.item.PlayerDeathDropHandler;
import com.zvicraft.networking.NetworkHandler;
import com.zvicraft.power.PowerEvents;
import com.zvicraft.power.PowerSystem;
import com.zvicraft.item.ModItemGroups;
import com.zvicraft.trust.TrustSystem;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;



public class PlusSMP implements ModInitializer {
    public static final String MOD_ID = "plussmp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



    @Override
    public void onInitialize() {
        LOGGER.info("Initializing PlusSMP Mod");

        // שלב 1: רשום את היישות קודם!

        // שלב 2: רשום פריטים (כולל SpawnEgg שמצביע ל־BOSS)
//        ModItems.registerModItems();

        // Register network handlers
        NetworkHandler.register();

        // שלב 3: רשום תכונות ברירת מחדל

        // Initialize database
        PowerDatabase.initialize();
        ModItems.registerModItems();
        PlayerDeathDropHandler.register();
        // Register blocks
        ModBlocks.registerModBlocks();
        
//        // Only register item groups on client side to prevent server-client conflicts
//        if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
            ModItemGroups.registerItemGroups();
            ModItems.registerCustomInventoryTab();
//        }

        // Register commands
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            PowerCommand.register(dispatcher);
            TrustCommand.register(dispatcher);
            KeyBindingCommand.register(dispatcher);

        });

        // Register server events
        ServerLifecycleEvents.SERVER_STARTING.register(this::onServerStarting);
        ServerLifecycleEvents.SERVER_STOPPED.register(this::onServerStopped);
        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);
    }

    private void onServerStarting(MinecraftServer server) {
        LOGGER.info("Server starting - initializing systems");
        PowerDatabase PowerDatabase = new PowerDatabase();
        PowerSystem.initialize(server, PowerDatabase);
        TrustSystem.initialize();
        PowerEvents.register();
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
        ActivePowerTracker.tick();

    }


} 