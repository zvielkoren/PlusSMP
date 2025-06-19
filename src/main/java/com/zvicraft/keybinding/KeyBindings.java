package com.zvicraft.keybinding;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String CATEGORY = "key.categories.plussmp";

    public static KeyBinding POWER_1_KEY;
    public static KeyBinding POWER_2_KEY;

    public static void register() {
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
    public static void updateKeyBinding(int slot, int keyCode) {
        // Proper client-side check
        if (!FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT)) {
            return;
        }

        if (slot == 1 && POWER_1_KEY != null) {
            POWER_1_KEY.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keyCode));
            KeyBinding.updateKeysByCode(); // Update the keybinding mappings
        } else if (slot == 2 && POWER_2_KEY != null) {
            POWER_2_KEY.setBoundKey(InputUtil.Type.KEYSYM.createFromCode(keyCode));
            KeyBinding.updateKeysByCode(); // Update the keybinding mappings
        }
    }
}