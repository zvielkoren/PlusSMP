package com.zvicraft.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.zvicraft.trust.TrustSystem;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

public class TrustCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("trust")
            .then(CommandManager.argument("player", StringArgumentType.string())
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    String targetName = StringArgumentType.getString(context, "player");
                    
                    ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(targetName);
                    if (target == null) {
                        source.sendError(Text.of("Player not found"));
                        return 0;
                    }
                    
                    TrustSystem.addTrust(player, target);
                    return 1;
                })));

        dispatcher.register(CommandManager.literal("untrust")
            .then(CommandManager.argument("player", StringArgumentType.string())
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    String targetName = StringArgumentType.getString(context, "player");
                    
                    ServerPlayerEntity target = source.getServer().getPlayerManager().getPlayer(targetName);
                    if (target == null) {
                        source.sendError(Text.of("Player not found"));
                        return 0;
                    }
                    
                    TrustSystem.removeTrust(player, target);
                    return 1;
                })));

        dispatcher.register(CommandManager.literal("trust")
            .then(CommandManager.literal("list")
                .executes(context -> {
                    ServerCommandSource source = context.getSource();
                    ServerPlayerEntity player = source.getPlayer();
                    
                    TrustSystem.listTrusted(player);
                    return 1;
                })));
    }
} 