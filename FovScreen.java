package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class FovScreen extends ModuleScreen {
    public FovScreen(Screen parent) {
        super("FOV Changer", parent);
    }

    @Override
    protected int addControls(int x, int y) {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggle(x, y, W, "Enabled", c.fovEnabled, v -> c.fovEnabled = v));
        addRenderableWidget(Widgets.slider(x, y + GAP, W, "FOV", ClientConfig.FOV_MIN, ClientConfig.FOV_MAX,
                "", c.fov, v -> c.fov = v));
        addRenderableWidget(Widgets.slider(x, y + GAP * 2, W, "Zoom (hold key)", ClientConfig.ZOOM_MIN,
                ClientConfig.ZOOM_MAX, "x", c.zoomFactor, v -> c.zoomFactor = v));
        addRenderableWidget(Widgets.toggle(x, y + GAP * 3, W, "Ignore sprint/speed FOV",
                c.ignoreFovEffects, v -> c.ignoreFovEffects = v));
        return 4;
    }
}
