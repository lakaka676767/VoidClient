package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class BlurScreen extends ModuleScreen {
    public BlurScreen(Screen parent) {
        super("Blur", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(0), rowW(), ROW_H, "Enabled",
                () -> c.blurEnabled, v -> c.blurEnabled = v));
        addRenderableWidget(Widgets.slider(rowX(), rowY(1), rowW(), ROW_H, "Blur", 1, 100, "%",
                c.blurPercent, v -> c.blurPercent = v));
        addKeyRow(2, "On/off key", VoidClient.blurKey);
        addBackRow(3, "< Back");
        return 4;
    }
}
