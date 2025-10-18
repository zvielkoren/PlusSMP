package com.zvicraft.database;

import com.zvicraft.power.PowerSystem;
import com.zvicraft.power.PowerType;
import com.zvicraft.keybinding.KeyBindings;
import net.minecraft.server.network.ServerPlayerEntity;

import java.sql.*;
import java.util.List;
import java.util.UUID;
import java.util.ArrayList;

public class PowerDatabase {
    private static final String DB_URL = "jdbc:sqlite:plussmp_powers.db";
    private static Connection connection;
    private static boolean isInitialized = false;

    public static void initialize() {
        if (isInitialized) {
            return;
        }

        try {
            // Load SQLite JDBC driver
            Class.forName("org.sqlite.JDBC");

            // Create connection
            connection = DriverManager.getConnection(DB_URL);
            
            // Set initialized flag first
            isInitialized = true;

            // Create tables after initialization flag is set
            createTables();

        System.out.println("Database initialized successfully");
    } catch (ClassNotFoundException | SQLException e) {
        System.err.println("Failed to initialize database: " + e.getMessage());
        e.printStackTrace();
        // Reset state on failure
        connection = null;
        isInitialized = false;
    }
}

private static void createTables() throws SQLException {
    if (!isInitialized || connection == null || connection.isClosed()) {
        throw new SQLException("Database not properly initialized");
    }

    try (Statement stmt = connection.createStatement()) {
        // Create player_powers table
        stmt.execute("CREATE TABLE IF NOT EXISTS player_powers (" +
            "player_uuid TEXT PRIMARY KEY," +
            "power1 TEXT," +
            "power2 TEXT," +
            "key1 INTEGER," +
            "key2 INTEGER" +
            ")");

        // Create first_join table
        stmt.execute("CREATE TABLE IF NOT EXISTS first_join (" +
            "player_uuid TEXT PRIMARY KEY" +

            ")");
    }
}

    public static void savePlayerPowers(ServerPlayerEntity player, PowerType power1, PowerType power2) {
        try {
            verifyConnection();
            try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT OR REPLACE INTO player_powers (player_uuid, power1, power2) VALUES (?, ?, ?)")) {
                stmt.setString(1, player.getUuid().toString());
                stmt.setString(2, power1 != null ? power1.name() : null);
                stmt.setString(3, power2 != null ? power2.name() : null);
                int updated = stmt.executeUpdate();
                System.out.println("Saved powers for player " + player.getName().getString() + 
                                 ": power1=" + (power1 != null ? power1.name() : "null") + 
                                 ", power2=" + (power2 != null ? power2.name() : "null") +
                                 ", rows updated: " + updated);
            }
        } catch (SQLException e) {
            System.err.println("Failed to save player powers: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void verifyConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            initialize();
        }
    }

    public static void savePlayerKeyBinding(ServerPlayerEntity player, int slot, int keyCode) {
        try {
            verifyConnection();
            String column = slot == 1 ? "key1" : "key2";
            try (PreparedStatement stmt = connection.prepareStatement(
                "UPDATE player_powers SET " + column + " = ? WHERE player_uuid = ?")) {
                stmt.setInt(1, keyCode);
                stmt.setString(2, player.getUuid().toString());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Failed to save key binding: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static List<PowerSystem.PowerData> loadPlayerPowers(ServerPlayerEntity player) {
        try {
            verifyConnection();
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT power1, power2, key1, key2 FROM player_powers WHERE player_uuid = ?")) {
                stmt.setString(1, player.getUuid().toString());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    // Load powers
                    String power1Str = rs.getString("power1");
                    String power2Str = rs.getString("power2");

                    // For debugging
                    System.out.println("Loading powers for " + player.getName().getString() + 
                                     ": power1=" + power1Str + ", power2=" + power2Str);

                    List<PowerSystem.PowerData> powers = new ArrayList<>();

                    if (power1Str != null) {
                        PowerType power1 = PowerType.valueOf(power1Str);
                        PowerSystem.setPowerInNextSlot(player, power1, 1);
                    }

                    if (power2Str != null) {
                        PowerType power2 = PowerType.valueOf(power2Str);
                        PowerSystem.setPowerInNextSlot(player, power2, 2);
                    }

                    // Load key bindings
                    int key1 = rs.getInt("key1");
                    int key2 = rs.getInt("key2");

                    if (key1 != 0) {
                        KeyBindings.updateKeyBinding(1, key1);
                    }

                    if (key2 != 0) {
                        KeyBindings.updateKeyBinding(2, key2);
                    }

                    return powers;
                } else {
                    System.out.println("No powers found for player " + player.getName().getString() + 
                                     ", creating new entry");
                    // No existing data, create default entry
                    try (PreparedStatement insertStmt = connection.prepareStatement(
                        "INSERT INTO player_powers (player_uuid) VALUES (?)")) {
                        insertStmt.setString(1, player.getUuid().toString());
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Failed to load player powers: " + e.getMessage());
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                isInitialized = false;
            } catch (SQLException e) {
                System.err.println("Failed to close database connection: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public static boolean isFirstJoin(UUID playerId) {
        try {
            verifyConnection();
            try (PreparedStatement stmt = connection.prepareStatement(
                "SELECT player_uuid FROM first_join WHERE player_uuid = ?")) {
                stmt.setString(1, playerId.toString());
                ResultSet rs = stmt.executeQuery();
                return !rs.next(); // If no record exists, it's first join
            }
        } catch (SQLException e) {
            System.err.println("Failed to check first join status: " + e.getMessage());
            return false;
        }
    }

    public static void markFirstJoin(UUID playerId) {
        try {
            verifyConnection();
            try (PreparedStatement stmt = connection.prepareStatement(
                "INSERT INTO first_join (player_uuid) VALUES (?)")) {
            stmt.setString(1, playerId.toString());
            stmt.executeUpdate();
        }
        } catch (SQLException e) {
            System.err.println("Failed to mark first join: " + e.getMessage());
        }
    }
}