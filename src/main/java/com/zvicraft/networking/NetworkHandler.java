package com.zvicraft.networking;

import com.zvicraft.keybinding.KeyBindingHandler;
import com.zvicraft.power.PowerSystem;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

public class NetworkHandler {
    public static void register() {
        ServerPlayNetworking.registerGlobalReceiver(
                KeyBindingHandler.POWER_ACTIVATION_PACKET,
                (server, player, handler, buf, responseSender) -> {
                    int slot = buf.readInt(); // קרא את הסלוט שהלקוח שלח

                    server.execute(() -> {
                        PowerSystem.PowerData powerData = PowerSystem.getPowerInSlot(player, slot);
                        if (powerData != null && powerData.isActive) {
                            PowerSystem.activatePower(player, powerData);
                        }
                    });
                }
        );
    }
}