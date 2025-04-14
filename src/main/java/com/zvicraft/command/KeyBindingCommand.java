package com.zvicraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.zvicraft.database.PowerDatabase;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.server.network.ServerPlayerEntity;

public class KeyBindingCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("setpowerkey")
            .requires(source -> source.hasPermissionLevel(2)) // Only operators can use this command
            .then(CommandManager.argument("slot", IntegerArgumentType.integer(1, 2))
                .then(CommandManager.argument("keycode", IntegerArgumentType.integer())
                    .executes(context -> {
                        int slot = IntegerArgumentType.getInteger(context, "slot");
                        int keyCode = IntegerArgumentType.getInteger(context, "keycode");
                        
                        // Save the key binding to database
                        ServerPlayerEntity player = context.getSource().getPlayer();
                        if (player != null) {
                            PowerDatabase.savePlayerKeyBinding(player, slot, keyCode);
                            context.getSource().sendMessage(Text.of("Power key " + slot + " updated to key code: " + keyCode));
                        }
                        return 1;
                    })
                )
            )
        );
    }
} 