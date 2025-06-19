package com.zvicraft.keybinding;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public class KeyBindingHandler {
    public static final Identifier POWER_ACTIVATION_PACKET = Identifier.of("plussmp", "power_activation");

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null) {
                if (KeyBindings.POWER_1_KEY.wasPressed()) {
                    sendPowerActivation(1);
                }
                if (KeyBindings.POWER_2_KEY.wasPressed()) {
                    sendPowerActivation(2);
                }
            }
        });
    }

    private static void sendPowerActivation(int slot) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(slot);

        // Create a proper CustomPayload
        PowerActivationPayload payload = new PowerActivationPayload(slot);
        ClientPlayNetworking.send(payload);
    }

    // Custom payload record for proper networking
    public record PowerActivationPayload(int slot) implements CustomPayload {
        public static final CustomPayload.Id<PowerActivationPayload> ID =
                new CustomPayload.Id<>(POWER_ACTIVATION_PACKET);

        public static final PacketCodec<PacketByteBuf, PowerActivationPayload> CODEC =
                PacketCodec.of(PowerActivationPayload::write, PowerActivationPayload::new);

        public PowerActivationPayload(PacketByteBuf buf) {
            this(buf.readInt());
        }

        public void write(PacketByteBuf buf) {
            buf.writeInt(this.slot);
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }
}