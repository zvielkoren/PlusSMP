package com.zvicraft;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.slot.Slot;

public class PowerScreenHandler extends ScreenHandler {
    private final PowerInventory powerInventory;

    public PowerScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(null, syncId); // Replace null with your custom ScreenHandlerType
        this.powerInventory = new PowerInventory();

        // Add custom slots
        this.addSlot(new Slot(powerInventory, 0, 8, 8)); // Slot 1
        this.addSlot(new Slot(powerInventory, 1, 26, 8)); // Slot 2
        this.addSlot(new Slot(powerInventory, 2, 44, 8)); // Slot 3
        this.addSlot(new Slot(powerInventory, 3, 62, 8)); // Slot 4
        this.addSlot(new Slot(powerInventory, 4, 80, 8)); // Slot 5
        this.addSlot(new Slot(powerInventory, 5, 98, 8)); // Slot 6

        // Add player inventory slots
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Add player hotbar slots
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, 8 + i * 18, 142));
        }
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }
}