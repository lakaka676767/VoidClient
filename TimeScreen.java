package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class TimeScreen extends ModuleScreen {
    public TimeScreen(Screen parent) {
        super("Time Changer", parent);
    }

    /** Minecraft tick 0 is 6:00 AM, 6000 is noon, 12000 is 6:00 PM, 18000 is midnight. */
    private static String clock(int tick) {
        int hours = (tick / 1000 + 6) % 24;
        int minutes = (tick % 1000) * 60 / 1000;
        return String.format("%02d:%02d", hours, minutes);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        int x = rowX(), w = rowW();
        addRenderableWidget(Widgets.toggleRow(x, rowY(0), w, ROW_H, "Enabled",
                () -> c.timeEnabled, v -> c.timeEnabled = v));
        addRenderableWidget(Widgets.slider(x, rowY(1), w, ROW_H, "Time", 0, 23999,
                TimeScreen::clock, c.timeOfDay, v -> c.timeOfDay = v));

        // quick presets
        String[] names = {"Dawn", "Noon", "Dusk", "Night"};
        int[] ticks = {0, 6000, 12000, 18000};
        int bw = (w - 6) / 4;
        for (int i = 0; i < 4; i++) {
            int tick = ticks[i];
            addRenderableWidget(Widgets.flat(x + i * (bw + 2), rowY(2), bw, ROW_H, names[i], b -> {
                c.timeOfDay = tick;
                this.rebuildWidgets();
            }));
        }

        addBackRow(3, "< Back");
        return 4;
    }

    @Override
    protected String footer() {
        return "only changes your own sky";
    }
}
