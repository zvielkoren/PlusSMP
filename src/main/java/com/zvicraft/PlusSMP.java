package com.zvicraft;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.UUID;

public class PlusSMP implements ModInitializer {
    public static final String PLUS_SMP_SERVER = "plussmp";
    private final PowerManager powerManager = new PowerManager();

    @Override
    public void onInitialize() {
        ModScreenRegistry.register(ModScreenHandlers.POWER_SCREEN_HANDLER, PowerScreen::new);
        ModScreenHandlers.register();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> registerCommands(dispatcher));
    }

    private void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("plusS2")
                .executes(context -> {
                    startPlusS2(context.getSource(), 60); // Default to 60 minutes
                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("PlusMidS2")
                .executes(context -> {
                    startPlusMidS2(context.getSource());
                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("PlusEventMidS2")
                .executes(context -> {
                    startPlusEventMidS2(context.getSource());
                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("PlusEndFight")
                .executes(context -> {
                    startPlusEndFight(context.getSource());
                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("activatePower")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) {
                        UUID playerId = player.getUuid();
                        powerManager.activatePower(playerId);
                        player.sendMessage(Text.literal("Power activated!"), false);
                    }
                    return 1;
                })
        );

        dispatcher.register(CommandManager.literal("addPowers")
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player != null) {
                        UUID playerId = player.getUuid();
                        Power speedBoost = new Power("Speed Boost", 60, "Gives the player a speed boost for 10 seconds");
                        Power strengthBoost = new Power("Strength Boost", 120, "Gives the player a strength boost for 10 seconds");
                        Power healing = new Power("Healing", 180, "Heals the player to full health");
                        powerManager.addPower(playerId, speedBoost);
                        powerManager.addPower(playerId, strengthBoost);
                        powerManager.addPower(playerId, healing);
                        player.sendMessage(Text.literal("Powers added!"), false);
                    }
                    return 1;
                })
        );
    }

    private void startPlusS2(ServerCommandSource source, int durationMinutes) {
        // Existing implementation for plusS2 command
    }

    private void startPlusMidS2(ServerCommandSource source) {
        // Existing implementation for PlusMidS2 command
    }

    private void startPlusEventMidS2(ServerCommandSource source) {
        // Existing implementation for PlusEventMidS2 command
    }

    private void startPlusEndFight(ServerCommandSource source) {
        // Existing implementation for PlusEndFight command
    }
}