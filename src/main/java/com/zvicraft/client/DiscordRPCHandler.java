package com.zvicraft.client;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.Style;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.util.Formatting;

public class DiscordRPCHandler {
    private static boolean initialized = false;
    private static long startTime;
    private static final String DISCORD_LINK = "https://discord.gg/eGsUAmDBj4";

    public static void initialize() {
        if (initialized) return;
        
        startTime = System.currentTimeMillis() / 1000;
        
        // Register tick event for updating Discord status
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.player.age % 40 == 0) { // Update every 2 seconds (40 ticks)
                updatePresence(client);
            }
        });
        
        initialized = true;
    }

    private static void updatePresence(MinecraftClient client) {
        if (!initialized || client.player == null) return;
        
        ClientPlayerEntity player = client.player;
        long playTimeMinutes = (System.currentTimeMillis() / 1000 - startTime) / 60;
        
        // Create clickable Discord link
        MutableText discordLink = Text.literal("§9[Discord]")
            .setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, DISCORD_LINK))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, 
                    Text.literal("Click to join our Discord server!"))));
        
        // Create player info with playtime
        MutableText playerInfo = Text.literal("§6" + player.getName().getString())
            .append(Text.literal("§7 | "))
            .append(Text.literal("§aPlaytime: " + formatPlayTime(playTimeMinutes)));
        
        // Add operator status if applicable
        if (player.hasPermissionLevel(2)) {
            playerInfo.append(Text.literal("§7 | §c[OP]"));
        }
        
        // Combine all parts
        MutableText finalStatus = discordLink
            .append(Text.literal("§7 | "))
            .append(playerInfo);
        
        // Set the custom name with all the information
        player.setCustomName(finalStatus);
    }

    private static String formatPlayTime(long minutes) {
        if (minutes < 60) {
            return minutes + "m";
        }
        long hours = minutes / 60;
        long remainingMinutes = minutes % 60;
        if (hours < 24) {
            return String.format("%dh %dm", hours, remainingMinutes);
        }
        long days = hours / 24;
        long remainingHours = hours % 24;
        return String.format("%dd %dh %dm", days, remainingHours, remainingMinutes);
    }

    public static void shutdown() {
        if (!initialized) return;
        initialized = false;
    }
} 