package com.zvicraft.item;

import com.zvicraft.power.PowerSystem;
import com.zvicraft.power.PowerType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class PowerDropItem extends Item {
    private static final int MAX_BAR_STEPS = 13;
    private final String powerType;



    public PowerDropItem(Settings settings, String powerType) {
        super(settings);
        // Validate powerType early
        try {
            PowerType.valueOf(powerType);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("Invalid powerType: " + powerType);
        }
        this.powerType = powerType;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        if (!(user instanceof ServerPlayerEntity player)) {
            return TypedActionResult.pass(user.getStackInHand(hand));
        }

        ItemStack stack = user.getStackInHand(hand);

        try {
            PowerType type = PowerType.valueOf(powerType);
            
            // Check if player already has this power
            if (PowerSystem.hasPower(player.getUuid(), type)) {
                player.sendMessage(Text.literal("You already have the " + type.getName() + " power!").formatted(Formatting.RED));
                return TypedActionResult.fail(stack);
            }

            // Check if this power should be targeted
            boolean isTargeted = shouldBeTargeted(type);
            UUID targetId = null;
            
            if (isTargeted) {
                // For targeted powers, find the target entity the player is looking at
                HitResult hit = user.raycast(20.0, 0.0f, true);
                if (hit.getType() == HitResult.Type.ENTITY) {
                    Entity target = ((EntityHitResult) hit).getEntity();
                    targetId = target.getUuid();
                } else {
                    player.sendMessage(Text.literal("Please look at a target to use this power!").formatted(Formatting.RED));
                    return TypedActionResult.fail(stack);
                }
            }

            // Try to add power to next available slot
            PowerSystem.SetPowerResult result = PowerSystem.setPowerInNextSlot(player, type, null, targetId, isTargeted);
            
            if (result.status == PowerSystem.SetPowerStatus.SUCCESS) {
                if (!player.getAbilities().creativeMode) {
                    stack.decrement(1);
                }
                String message = "You've obtained the " + type.getName() + " power in slot " + result.slot + "!";
                if (isTargeted && targetId != null) {
                    message += " (Targeted)";
                }
                player.sendMessage(Text.literal(message).formatted(Formatting.GREEN));
                return TypedActionResult.success(stack, world.isClient());
            } else if (result.status == PowerSystem.SetPowerStatus.NO_EMPTY_SLOT) {
                player.sendMessage(Text.literal("You don't have any empty power slots available!").formatted(Formatting.RED));
                return TypedActionResult.fail(stack);
            } else {
                player.sendMessage(Text.literal("Failed to add power. Please try again.").formatted(Formatting.RED));
                return TypedActionResult.fail(stack);
            }

        } catch (IllegalArgumentException e) {
            player.sendMessage(Text.literal("Invalid power type!").formatted(Formatting.RED));
            return TypedActionResult.fail(stack);
        }
    }
    
    private boolean shouldBeTargeted(PowerType powerType) {
        // Define which powers should be targeted
        // Add or remove power types from this list as needed
        return switch (powerType) {
            case BLINDNESS, POISON, WITHER -> true;  // Example targeted powers
            default -> false;  // Other powers affect the user
        };
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        tooltip.add(Text.translatable("tooltip.plussmp.power_item.use").formatted(Formatting.GRAY));
        tooltip.add(Text.translatable("tooltip.plussmp.power_item.source").formatted(Formatting.DARK_GRAY));
    }

    @Override
    public Text getName(ItemStack stack) {
        try {
            PowerType type = PowerType.valueOf(powerType);
            return Text.literal(type.getName() + " Power");
        } catch (IllegalArgumentException e) {
            return Text.literal("Unknown Power");
        }
    }

    public String getPowerType() {
        return powerType;
    }
}
