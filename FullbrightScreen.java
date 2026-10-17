package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class FullbrightScreen extends ModuleScreen {
    public FullbrightScreen(Screen parent) {
        super("Fullbright", parent);
    }

    @Override
    protected int addControls(int x, int y) {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggle(x, y, W, "Enabled", c.fullbrightEnabled, v -> c.fullbrightEnabled = v));
        addRenderableWidget(Widgets.slider(x, y + GAP, W, "Brightness", 1, 100, "%",
                c.fullbrightPercent, v -> c.fullbrightPercent = v));
        return 2;
    }
}
