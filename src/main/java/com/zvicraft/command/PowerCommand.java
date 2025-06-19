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

import static com.zvicraft.power.PowerSystem.setPowerInNextSlot;

public class PowerCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        // Register setpower command with optional slot
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
}