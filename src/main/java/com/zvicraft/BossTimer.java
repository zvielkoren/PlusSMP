package com.zvicraft;

import net.minecraft.entity.boss.CommandBossBar;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class BossTimer {
    private final MinecraftServer server;
    private CommandBossBar bossBar;
    private int tickCounter;
    private final int durationTicks;
    private boolean active;

    public BossTimer(MinecraftServer server, String title, int durationMinutes) {
        this.server = server;
        this.durationTicks = durationMinutes * 60 * 20;
        this.tickCounter = 0;
        this.active = false;
        this.bossBar = server.getBossBarManager().add(Identifier.of("plussmp", "timer"), Text.literal(title));
    }

    public void start() {
        this.active = true;
        this.bossBar.setPercent(1.0f);
        this.bossBar.setVisible(true);
    }

    public void stop() {
        this.active = false;
        this.bossBar.setVisible(false);
        this.server.getBossBarManager().remove(this.bossBar);
    }

    public void onTick() {
        if (!active) return;

        tickCounter++;
        float progress = 1.0f - (float) tickCounter / durationTicks;
        bossBar.setPercent(progress);

        if (tickCounter >= durationTicks) {
            stop();
            // Trigger end of timer actions
        }
    }
}