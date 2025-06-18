package com.zvicraft.client;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

public class DiscordRPCHandler {
    private static final String CLIENT_ID = "1371214190659895306";
    private static long startTime;

    public static void start() {
        startTime = System.currentTimeMillis() / 1000;

        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder()
                .setReadyEventHandler(user -> {
                    System.out.println("Discord RPC connected: " + user.username + "#" + user.discriminator);
                })
                .build();

        DiscordRPC.discordInitialize(CLIENT_ID, handlers, true);

        new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                DiscordRPC.discordRunCallbacks();
                updatePresence("In Minecraft", "Playing on ZviCraft");
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException ignored) {}
            }
        }, "Discord-RPC-Callback-Handler").start();
    }

    public static void updatePresence(String details, String state) {
        DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(state)
                .setDetails(details)
                .setStartTimestamps(startTime)
                .setBigImage("minecraft", "Playing Minecraft")
                .setSmallImage("plussmp", "PLus Server");

        DiscordRPC.discordUpdatePresence(presence.build());
    }

    public static void shutdown() {
        DiscordRPC.discordShutdown();
    }
}
