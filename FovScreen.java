package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class FovScreen extends ModuleScreen {
    public FovScreen(Screen parent) {
        super("FOV Changer", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(0), rowW(), ROW_H, "Enabled",
                () -> c.fovEnabled, v -> c.fovEnabled = v));
        addRenderableWidget(Widgets.slider(rowX(), rowY(1), rowW(), ROW_H, "FOV",
                ClientConfig.FOV_MIN, ClientConfig.FOV_MAX, "", c.fov, v -> c.fov = v));
        addRenderableWidget(Widgets.slider(rowX(), rowY(2), rowW(), ROW_H, "Zoom (hold key)",
                ClientConfig.ZOOM_MIN, ClientConfig.ZOOM_MAX, "x", c.zoomFactor, v -> c.zoomFactor = v));
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(3), rowW(), ROW_H, "Ignore sprint/speed FOV",
                () -> c.ignoreFovEffects, v -> c.ignoreFovEffects = v));
        addBackRow(4, "< Back");
        return 5;
    }
}
