package com.zvicraft.item;

import com.zvicraft.item.ModItems;
import com.zvicraft.item.PowerDropItem;
import com.zvicraft.power.PowerSystem;
import com.zvicraft.power.PowerType;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class PlayerDeathDropHandler {
    public static void register() {
        ServerPlayerEvents.ALLOW_DEATH.register((player, damageSource, callbackInfo) -> {
            dropPowerItem(player);
            return true;
        });
    }

    private static void dropPowerItem(ServerPlayerEntity player) {
        // Drop all active powers
        for (int i = 1; i <= 2; i++) {
            PowerSystem.PowerData powerData = PowerSystem.getPowerInSlot(player, i);
            if (powerData != null && powerData.powerType != null) {
                PowerDropItem powerItem = ModItems.getPowerItem(powerData.powerType);
                if (powerItem != null) {
                    ItemStack stack = powerItem.getDefaultStack();
                    var world = player.getWorld();
                    var itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack);
                    world.spawnEntity(itemEntity);
                }
            }
        }
        // Clear all powers from the player
        PowerSystem.clearPlayerPower(player);
    }
}
