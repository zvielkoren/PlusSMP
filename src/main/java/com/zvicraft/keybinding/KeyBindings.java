package com.zvicraft.keybinding;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
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
}
