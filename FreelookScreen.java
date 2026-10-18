package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class FreelookScreen extends ModuleScreen {
    public FreelookScreen(Screen parent) {
        super("Freelook", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(0), rowW(), ROW_H, "Enabled",
                () -> c.freelookEnabled, v -> c.freelookEnabled = v));
        addKeyRow(1, "Hold key", VoidClient.freelookKey);
        addBackRow(2, "< Back");
        return 3;
    }
}
