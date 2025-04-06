package com.zvicraft;

import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<PowerScreenHandler> POWER_SCREEN_HANDLER;

    static {
        POWER_SCREEN_HANDLER = Registry.register(
                Registry.ABSENT_RAW_ID,
                Identifier.of("plussmp", "power_screen_handler"),
                new ScreenHandlerType<>(new PowerScreenHandlerFactory())
        );
    }

    public static void register() {
        // This method can be used to trigger the static initialization
        // of the ScreenHandlerType

    }
}