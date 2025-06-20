package com.zvicraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.zvicraft.power.PowerSystem;
import com.zvicraft.power.PowerType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.zvicraft.power.PowerSystem.setPowerInNextSlot;

public class PowerCommand {
    // Cooldown in milliseconds (5 seconds)
    private static final long SWAP_COOLDOWN_MS = 5000;
    private static final Map<UUID, Long> lastSwapTime = new HashMap<>();
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register setpower command with optional slot (requires op level 2)
        dispatcher.register(CommandManager.literal("setpower")
            .requires(source -> source.hasPermissionLevel(2))
            .then(CommandManager.argument("player", EntityArgumentType.player())
            .then(CommandManager.argument("powertype", StringArgumentType.word())
                .suggests((context, builder) -> {
                    for (PowerType power : PowerType.values()) {
                        builder.suggest(power.name());
                    }
                    return builder.buildFuture();
                })
                .executes(context -> { // Execute without slot specified
                    return setPower(context, null);
                })
                .then(CommandManager.argument("slot", IntegerArgumentType.integer(1, 2))
                    .executes(context -> { // Execute with slot specified
                        int slot = IntegerArgumentType.getInteger(context, "slot");
                        return setPower(context, slot);
                    })))));
                    
        // Register the swappowers command (available to all players)
        registerSwapCommand(dispatcher);
    }

    private static int setPower(CommandContext<ServerCommandSource> context, Integer specifiedSlot) throws CommandSyntaxException {
        ServerPlayerEntity player = EntityArgumentType.getPlayer(context, "player");
        String powerName = StringArgumentType.getString(context, "powertype");
        
        try {
            PowerType powerType = PowerType.valueOf(powerName.toUpperCase());
            PowerSystem.SetPowerResult result = setPowerInNextSlot(player, powerType, specifiedSlot);
            
            switch (result.status) {
                case SUCCESS:
                    context.getSource().sendMessage(Text.of("Successfully set " + powerType.getName() + 
                        " in slot " + result.slot + " for " + player.getName().getString()));
                    return   1;
                case NO_EMPTY_SLOT:
                    context.getSource().sendMessage(Text.of("No empty slots available for " + 
                        player.getName().getString()));
                    return 0;
                case INVALID_SLOT:
                    context.getSource().sendMessage(Text.of("Invalid slot specified"));
                    return 0;
                default:
                    context.getSource().sendMessage(Text.of("Failed to set power"));
                    return 0;
            }
        } catch (IllegalArgumentException e) {
            context.getSource().sendMessage(Text.of("Invalid power type: " + powerName));
            return 0;
        }
    }
    
    public static void registerSwapCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("swappowers")
            .then(CommandManager.argument("slot", IntegerArgumentType.integer(1, 2))
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    if (player == null) {
                        context.getSource().sendMessage(Text.of("This command can only be used by players"));
                        return 0;
                    }
                    
                    int selectedSlot = IntegerArgumentType.getInteger(context, "slot");
                    int otherSlot = (selectedSlot == 1) ? 2 : 1; // Automatically select the other slot
                    
                    return swapPowers(context, player, selectedSlot, otherSlot);
                })));
        
        // Keep the old two-slot version for backward compatibility
        dispatcher.register(CommandManager.literal("swappowers")
            .then(CommandManager.argument("slot1", IntegerArgumentType.integer(1, 2))
                .then(CommandManager.argument("slot2", IntegerArgumentType.integer(1, 2))
                    .executes(context -> {
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player == null) {
                            context.getSource().sendMessage(Text.of("This command can only be used by players"));
                            return 0;
                        }
                        
                        int slot1 = IntegerArgumentType.getInteger(context, "slot1");
                        int slot2 = IntegerArgumentType.getInteger(context, "slot2");
                        
                        if (slot1 == slot2) {
                            context.getSource().sendMessage(Text.of("Cannot swap a slot with itself"));
                            return 0;
                        }
                        
                        return swapPowers(context, player, slot1, slot2);
                    }))));
    }
    
    private static int swapPowers(CommandContext<ServerCommandSource> context, ServerPlayerEntity player, int slot1, int slot2) {
        // Check cooldown
        long currentTime = System.currentTimeMillis();
        Long lastSwap = lastSwapTime.get(player.getUuid());
        if (lastSwap != null) {
            long timeSinceLastSwap = currentTime - lastSwap;
            if (timeSinceLastSwap < SWAP_COOLDOWN_MS) {
                long remainingTime = (SWAP_COOLDOWN_MS - timeSinceLastSwap) / 1000 + 1;
                context.getSource().sendMessage(Text.of("You must wait " + remainingTime + " seconds before swapping powers again"));
                return 0;
            }
        }
        
        PowerSystem.PowerData power1 = PowerSystem.getPowerInSlot(player, slot1);
        PowerSystem.PowerData power2 = PowerSystem.getPowerInSlot(player, slot2);
        
        if (power1 == null && power2 == null) {
            context.getSource().sendMessage(Text.of("Both slots are empty"));
            return 0;
        }
        
        // Get current powers
        List<PowerSystem.PowerData> powers = PowerSystem.playerPowers.get(player.getUuid());
        if (powers == null) {
            powers = new ArrayList<>();
            PowerSystem.playerPowers.put(player.getUuid(), powers);
        }
        
        // Ensure the list has enough slots
        while (powers.size() < Math.max(slot1, slot2)) {
            powers.add(null);
        }
        
        // Swap the powers
        PowerSystem.PowerData temp = powers.get(slot1 - 1);
        powers.set(slot1 - 1, powers.get(slot2 - 1));
        powers.set(slot2 - 1, temp);
        
        // Save to database
        PowerType power1Type = powers.size() >= 1 ? (powers.get(0) != null ? powers.get(0).powerType : null) : null;
        PowerType power2Type = powers.size() >= 2 ? (powers.get(1) != null ? powers.get(1).powerType : null) : null;
        PowerSystem.PowerDatabase.savePlayerPowers(player, power1Type, power2Type);
        
        // Send confirmation message
        String message = "Swapped powers: ";
        if (power1 != null) message += "[" + slot1 + "]" + power1.powerType.getName();
        else message += "[Empty]";
        message += " ⇌ ";
        if (power2 != null) message += "[" + slot2 + "]" + power2.powerType.getName();
        else message += "[Empty]";
        
        // Update last swap time
        lastSwapTime.put(player.getUuid(), currentTime);
        
        context.getSource().sendMessage(Text.of(message));
        return 1;
    }
}