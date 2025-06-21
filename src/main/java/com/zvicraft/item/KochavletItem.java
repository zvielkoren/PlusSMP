package com.zvicraft.item;

import com.zvicraft.power.PowerSystem;
import com.zvicraft.power.PowerType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class KochavletItem extends Item {
    public KochavletItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!world.isClient && user instanceof ServerPlayerEntity player) {
            ItemStack stack = user.getStackInHand(hand);
            
            // Check if player has an available power slot
            if (!PowerSystem.hasEmptyPowerSlot(player)) {
                player.sendMessage(Text.translatable("item.plus_smp.kochavlet.no_slots")
                    .formatted(Formatting.RED));
                return TypedActionResult.fail(stack);
            }
            
            // Get a list of all power types except KOCHAVLET and GENERAL
            List<PowerType> availablePowers = Stream.of(PowerType.values())
                    .filter(power -> power != PowerType.KOCHAVLET && power != PowerType.GENERAL)
                    .filter(power -> !PowerSystem.hasPower(player, power)) // Don't give powers the player already has
                    .collect(Collectors.toList());
            
            if (!availablePowers.isEmpty()) {
                // Select a random power
                PowerType randomPower = availablePowers.get(Random.create().nextInt(availablePowers.size()));
                
                try {
                    // Give the random power to the player
                    PowerSystem.givePower(player, randomPower);
                    
                    // Remove one Kochavlet from the stack
                    if (!player.isCreative()) {
                        stack.decrement(1);
                    }
                    
                    // Send a message to the player
                    player.sendMessage(Text.translatable("item.plus_smp.kochavlet.used",
                        randomPower.getHebrewName())
                        .formatted(Formatting.GREEN));
                    
                    return TypedActionResult.success(stack, true);
                } catch (IllegalStateException e) {
                    player.sendMessage(Text.translatable("item.plus_smp.kochavlet.error")
                        .formatted(Formatting.RED));
                    return TypedActionResult.fail(stack);
                }
            } else {
                player.sendMessage(Text.translatable("item.plus_smp.kochavlet.all_powers")
                    .formatted(Formatting.YELLOW));
                return TypedActionResult.fail(stack);
            }
        }
        return TypedActionResult.pass(user.getStackInHand(hand));
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return true; // Make the item have the enchantment glint effect
    }
}
