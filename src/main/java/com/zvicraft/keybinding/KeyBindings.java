package com.zvicraft.keybinding;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String CATEGORY = "key.categories.plussmp";
    
    // Power activation keys - dynamic on client side
    public static KeyBinding POWER_1_KEY;
    public static KeyBinding POWER_2_KEY;
    
    public static void register() {
        // Only register if we're on the client side
        if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType().CLIENT) {
            POWER_1_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.plussmp.power1",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_Z,
                CATEGORY
            ));
            
            POWER_2_KEY = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.plussmp.power2",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_X,
                CATEGORY
            ));
        }
    }
    
    public static void updateKeyBinding(int slot, int keyCode) {
        if (net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType() != net.fabricmc.loader.api.FabricLoader.getInstance().getEnvironmentType().CLIENT) {
            return;
        }
        
        if (slot == 1 && POWER_1_KEY != null) {
            POWER_1_KEY.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keyCode));
        } else if (slot == 2 && POWER_2_KEY != null) {
            POWER_2_KEY.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keyCode));
        }
    }
} 