package com.zvicraft;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.HashMap;
import java.util.Map;

public class PowerInventory implements Inventory {
    private final DefaultedList<ItemStack> items = DefaultedList.ofSize(6, ItemStack.EMPTY); // Updated size
    private final Map<Integer, PowerEffect> powerEffects = new HashMap<>();

    @Override
    public int size() {
        return items.size();
    }

    @Override
    public boolean isEmpty() {
        return items.stream().allMatch(ItemStack::isEmpty);
    }

    @Override
    public ItemStack getStack(int slot) {
        return items.get(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack removeStack(int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        items.set(slot, stack);
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return true;
    }

    @Override
    public void clear() {
        items.clear();
        powerEffects.clear();
    }

    public void addPowerEffect(int slot, PowerEffect effect) {
        powerEffects.put(slot, effect);
    }

    public void removePowerEffect(int slot) {
        powerEffects.remove(slot);
    }

    public PowerEffect getPowerEffect(int slot) {
        return powerEffects.get(slot);
    }
}