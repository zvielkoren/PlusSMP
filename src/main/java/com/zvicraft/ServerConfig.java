package com.zvicraft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ServerConfig {
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "plussmp/config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static JsonObject configData;

    public static void loadConfig() {
        if (!CONFIG_FILE.exists()) {
            createDefaultConfig();
        }

        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            configData = JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveConfig() {
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(configData, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createDefaultConfig() {
        configData = new JsonObject();
        configData.addProperty("serverApiUrl", "https://example.com/check_license");
        configData.addProperty("licenseKey", "your-server-license-key");

        CONFIG_FILE.getParentFile().mkdirs();
        saveConfig();
    }

    public static String getServerApiUrl() {
        return configData.get("serverApiUrl").getAsString();
    }

    public static String getLicenseKey() {
        return configData.get("licenseKey").getAsString();
    }
}
