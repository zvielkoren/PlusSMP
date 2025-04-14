package com.zvicraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.zvicraft.power.PowerSystem;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class PowerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("activatepower")
            .then(CommandManager.argument("slot", IntegerArgumentType.integer(1, 2))
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    int slot = IntegerArgumentType.getInteger(context, "slot");
                    
                    if (player == null) return 0;
                    
                    // Get the power in the specified slot
                    PowerSystem.PowerData powerData = PowerSystem.getPowerInSlot(player.getUuid(), slot);
                    if (powerData == null || !powerData.isActive) {
                        player.sendMessage(Text.of("No active power in slot " + slot), false);
                        return 0;
                    }
                    
                    // Activate the power
                    PowerSystem.activatePower(player, powerData);
                    return 1;
                })));
    }
} 