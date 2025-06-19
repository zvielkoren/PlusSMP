package com.zvicraft.networking;

import com.zvicraft.keybinding.KeyBindingHandler;
import com.zvicraft.power.PowerSystem;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public class NetworkHandler {
    public static void register() {
        System.out.println("NetworkHandler registering..."); // DEBUG

        // Register the payload type
        PayloadTypeRegistry.playC2S().register(
                KeyBindingHandler.PowerActivationPayload.ID,
                KeyBindingHandler.PowerActivationPayload.CODEC
        );
        System.out.println("Payload type registered!"); // DEBUG

        // Register the packet handler
        ServerPlayNetworking.registerGlobalReceiver(
                KeyBindingHandler.PowerActivationPayload.ID,
                (payload, context) -> {
                    System.out.println("Received power activation packet for slot: " + payload.slot()); // DEBUG
                    int slot = payload.slot();

                    context.server().execute(() -> {
                        System.out.println("Executing power activation on server thread..."); // DEBUG

                        try {
                            // וודא שהשחקן מאותחל לפני שמנסה לטעון כוחות
                            PowerSystem.initializePlayerPowers(context.player());

                            // עכשיו נסה לטעון את הכוחות
                            PowerSystem.loadPlayerPowers(context.player());
                            System.out.println("Player powers loaded!"); // DEBUG

                            PowerSystem.PowerData powerData = PowerSystem.getPowerInSlot(context.player(), slot);
                            if (powerData != null && powerData.isActive) {
                                System.out.println("Activating power: " + powerData); // DEBUG
                                PowerSystem.activatePower(context.player(), powerData);
                            } else {
                                System.out.println("No active power found in slot " + slot + " for player " + context.player().getName().getString()); // DEBUG

                                // אם עדיין אין כוח, תן לשחקן כוח רנדומלי
                                System.out.println("Giving random power to player..."); // DEBUG
                                PowerSystem.giveRandomPowerOnJoin(context.player());

                                // נסה שוב לטעון ולהפעיל
                                PowerSystem.loadPlayerPowers(context.player());
                                PowerSystem.PowerData newPowerData = PowerSystem.getPowerInSlot(context.player(), slot);
                                if (newPowerData != null && newPowerData.isActive) {
                                    PowerSystem.activatePower(context.player(), newPowerData);
                                }
                            }
                        } catch (Exception e) {
                            System.err.println("Error handling power activation: " + e.getMessage());
                            e.printStackTrace();
                        }
                    });
                }
        );
        System.out.println("NetworkHandler registration complete!"); // DEBUG
    }
}