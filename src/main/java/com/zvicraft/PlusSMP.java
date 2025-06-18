package com.zvicraft;

import com.zvicraft.block.ModBlocks;
import com.zvicraft.command.KeyBindingCommand;
import com.zvicraft.command.PowerCommand;
import com.zvicraft.command.TrustCommand;
import com.zvicraft.database.PowerDatabase;
import com.zvicraft.entity.boss.PlusBossEntity;
import com.zvicraft.networking.NetworkHandler;
import com.zvicraft.power.PowerEvents;
import com.zvicraft.power.PowerSystem;
import com.zvicraft.trust.TrustSystem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;

import static com.zvicraft.entity.ModEntities.BOSS;


public class PlusSMP implements ModInitializer {
    public static final String MOD_ID = "plussmp";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);



    @Override
    public void onInitialize() {
        LOGGER.info("Initializing PlusSMP Mod");

        // שלב 1: רשום את היישות קודם!
        com.zvicraft.entity.ModEntities.init();

        // שלב 2: רשום פריטים (כולל SpawnEgg שמצביע ל־BOSS)
//        ModItems.registerModItems();

        // Register network handlers
        NetworkHandler.register();

        // שלב 3: רשום תכונות ברירת מחדל
        FabricDefaultAttributeRegistry.register(BOSS, PlusBossEntity.createBossAttributes());

        // Initialize database
        PowerDatabase.initialize();

        // Register blocks
        ModBlocks.registerModBlocks();
        PowerEvents.register();
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