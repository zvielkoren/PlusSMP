package com.zvicraft.database;

import com.zvicraft.power.PowerType;
import com.zvicraft.keybinding.KeyBindings;
import net.minecraft.server.network.ServerPlayerEntity;
import org.lwjgl.glfw.GLFW;

import java.sql.*;
import java.util.UUID;

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
            
            // Create tables
            createTables();
            
            isInitialized = true;
            System.out.println("Database initialized successfully");
        } catch (ClassNotFoundException | SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
            isInitialized = false;
        }
    }

    private static void verifyConnection() throws SQLException {
        if (!isInitialized) {
            throw new SQLException("Database not initialized");
        }
        if (connection == null || connection.isClosed()) {
            throw new SQLException("Database connection is closed");
        }
    }

    private static void createTables() throws SQLException {
        verifyConnection();
        try (Statement stmt = connection.createStatement()) {
            // Create player_powers table
            stmt.execute("CREATE TABLE IF NOT EXISTS player_powers (" +
                "player_uuid TEXT PRIMARY KEY," +
                "power1 TEXT," +
                "power2 TEXT," +
                "key1 INTEGER," +
                "key2 INTEGER" +
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
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("Failed to save player powers: " + e.getMessage());
            e.printStackTrace();
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

    public static void loadPlayerPowers(ServerPlayerEntity player) {
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
                    
                    if (power1Str != null) {
                        PowerType power1 = PowerType.valueOf(power1Str);
                        // Apply power1 to player
                    }
                    
                    if (power2Str != null) {
                        PowerType power2 = PowerType.valueOf(power2Str);
                        // Apply power2 to player
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
                } else {
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
} 