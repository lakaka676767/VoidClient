package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class LowFireScreen extends ModuleScreen {
    public LowFireScreen(Screen parent) {
        super("Low Fire", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(0), rowW(), ROW_H, "Enabled",
                () -> c.lowFireEnabled, v -> c.lowFireEnabled = v));
        addRenderableWidget(Widgets.slider(rowX(), rowY(1), rowW(), ROW_H, "Lowering", 0, 100, "%",
                c.fireLowering, v -> c.fireLowering = v));
        addKeyRow(2, "On/off key", VoidClient.lowFireKey);
        addBackRow(3, "< Back");
        return 4;
    }

    @Override
    protected String footer() {
        return "only moves the fire on your screen";
    }
}
