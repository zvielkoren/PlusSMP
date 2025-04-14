package com.zvicraft.discord;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

public class DiscordIntegration {
    private static final Path GAME_DIR = FabricLoader.getInstance().getGameDir();
    private static final String PRESENCE_FILE = "discord_presence.json";
    private static final File PRESENCE_FILE_PATH = GAME_DIR.resolve(PRESENCE_FILE).toFile();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void initialize() {
        try {
            // Create initial presence file
            PresenceData presence = new PresenceData(
                "Playing S2 Plus Mod",
                "In Game",
                System.currentTimeMillis() / 1000,
                "minecraft",
                "Minecraft",
                "s2plus",
                "S2 Plus Mod"
            );
            writePresence(presence);
        } catch (IOException e) {
            System.err.println("Failed to initialize Discord presence file: " + e.getMessage());
        }
    }

    public static void updateActivity(ServerPlayerEntity player, String activity) {
        try {
            PresenceData presence = new PresenceData(
                "Playing S2 Plus Mod",
                activity,
                System.currentTimeMillis() / 1000,
                "minecraft",
                "Minecraft",
                "s2plus",
                "S2 Plus Mod"
            );
            writePresence(presence);
        } catch (IOException e) {
            System.err.println("Failed to update Discord presence: " + e.getMessage());
        }
    }

    private static void writePresence(PresenceData presence) throws IOException {
        try (FileWriter writer = new FileWriter(PRESENCE_FILE_PATH)) {
            GSON.toJson(presence, writer);
        }
    }

    public static void shutdown() {
        if (PRESENCE_FILE_PATH.exists()) {
            PRESENCE_FILE_PATH.delete();
        }
    }

    private static class PresenceData {
        private final String details;
        private final String state;
        private final long start;
        private final String largeImageKey;
        private final String largeImageText;
        private final String smallImageKey;
        private final String smallImageText;

        public PresenceData(String details, String state, long start, String largeImageKey, 
                          String largeImageText, String smallImageKey, String smallImageText) {
            this.details = details;
            this.state = state;
            this.start = start;
            this.largeImageKey = largeImageKey;
            this.largeImageText = largeImageText;
            this.smallImageKey = smallImageKey;
            this.smallImageText = smallImageText;
        }
    }
} 