package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class FreecamScreen extends ModuleScreen {
    public FreecamScreen(Screen parent) {
        super("Freecam", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(0), rowW(), ROW_H, "Enabled",
                () -> c.freecamEnabled, v -> c.freecamEnabled = v));
        addRenderableWidget(Widgets.slider(rowX(), rowY(1), rowW(), ROW_H, "Speed", 1, 20, " b/s",
                c.freecamSpeed, v -> c.freecamSpeed = v));
        addBackRow(2, "< Back");
        return 3;
    }

    @Override
    protected String footer() {
        return "toggle: Freecam key (F6)";
    }
}
