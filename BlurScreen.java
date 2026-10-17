package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class BlurScreen extends ModuleScreen {
    public BlurScreen(Screen parent) {
        super("Blur", parent);
    }

    @Override
    protected int addControls(int x, int y) {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggle(x, y, W, "Enabled", c.blurEnabled, v -> c.blurEnabled = v));
        addRenderableWidget(Widgets.slider(x, y + GAP, W, "Blur", 1, 100, "%",
                c.blurPercent, v -> c.blurPercent = v));
        return 2;
    }
}
