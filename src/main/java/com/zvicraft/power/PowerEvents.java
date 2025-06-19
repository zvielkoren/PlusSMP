package com.zvicraft.power;

import com.zvicraft.database.PowerDatabase;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.network.ServerPlayerEntity;

public class PowerEvents {
    public static void register() {
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            ServerPlayerEntity player = handler.player;
            
            // First, initialize the player's power system
            PowerSystem.initializePlayerPowers(player);
            
            // Check if it's their first join
            boolean isFirstJoin = PowerDatabase.isFirstJoin(player.getUuid());
            
            if (isFirstJoin) {
                System.out.println("First join detected for player " + player.getName().getString());
                // Give them their first random power
                PowerSystem.giveRandomPowerOnJoin(player);
                PowerDatabase.markFirstJoin(player.getUuid());
            } else {
                System.out.println("Loading existing powers for player " + player.getName().getString());
                // Load their existing powers from database
                PowerDatabase.loadPlayerPowers(player);
            }
        });
    }
}