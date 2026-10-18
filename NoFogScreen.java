package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class NoFogScreen extends ModuleScreen {
    public NoFogScreen(Screen parent) {
        super("No Fog", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(0), rowW(), ROW_H, "Enabled",
                () -> c.noFogEnabled, v -> c.noFogEnabled = v));
        addBackRow(1, "< Back");
        return 2;
    }

    @Override
    protected String footer() {
        return "removes all fog, water and lava too";
    }
}
