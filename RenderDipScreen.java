package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

/** Render Dip: drops the render distance for a few seconds, then puts it back by itself. */
public class RenderDipScreen extends ModuleScreen {
    public RenderDipScreen(Screen parent) {
        super("Render Dip", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(0), rowW(), ROW_H, "Dip active",
                () -> c.renderDipEnabled, v -> c.renderDipEnabled = v));
        addRenderableWidget(Widgets.slider(rowX(), rowY(1), rowW(), ROW_H, "Lasts", 1, 60, " s",
                c.renderDipSeconds, v -> c.renderDipSeconds = v));
        addRenderableWidget(Widgets.slider(rowX(), rowY(2), rowW(), ROW_H, "Distance", 2, 12, " chunks",
                c.renderDipDistance, v -> c.renderDipDistance = v));
        addKeyRow(3, "Start key", VoidClient.renderDipKey);
        addBackRow(4, "< Back");
        return 5;
    }

    @Override
    protected String footer() {
        return "goes back by itself when time is up";
    }
}
