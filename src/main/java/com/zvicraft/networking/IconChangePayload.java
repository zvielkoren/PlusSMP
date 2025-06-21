package com.zvicraft.networking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Identifier;

public record IconChangePayload(Identifier iconTexture) implements CustomPayload {
    public static final CustomPayload.Id<IconChangePayload> ID = new CustomPayload.Id<>( Identifier.of("plussmp", "icon_change"));
    public static final PacketCodec<PacketByteBuf, IconChangePayload> CODEC = PacketCodec.of(IconChangePayload::write, IconChangePayload::new);

    public IconChangePayload(PacketByteBuf buf) {
        this(buf.readIdentifier());
    }

    public void write(PacketByteBuf buf) {
        buf.writeIdentifier(this.iconTexture);
    }

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
