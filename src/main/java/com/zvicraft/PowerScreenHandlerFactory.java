package com.zvicraft;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;

public class PowerScreenHandlerFactory implements ScreenHandlerType.Factory<PowerScreenHandler> {

    public PowerScreenHandler create(int syncId, PlayerInventory playerInventory, PacketByteBuf buf) {
        return new PowerScreenHandler(syncId, playerInventory);
    }

    @Override
    public PowerScreenHandler create(int syncId, PlayerInventory playerInventory) {
        return null;
    }
}