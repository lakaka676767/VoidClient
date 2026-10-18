package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class FullbrightScreen extends ModuleScreen {
    public FullbrightScreen(Screen parent) {
        super("Fullbright", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(0), rowW(), ROW_H, "Enabled",
                () -> c.fullbrightEnabled, v -> c.fullbrightEnabled = v));
        addRenderableWidget(Widgets.slider(rowX(), rowY(1), rowW(), ROW_H, "Brightness", 1, 100, "%",
                c.fullbrightPercent, v -> c.fullbrightPercent = v));
        addKeyRow(2, "On/off key", VoidClient.fullbrightKey);
        addBackRow(3, "< Back");
        return 4;
    }
}
