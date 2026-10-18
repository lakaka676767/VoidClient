package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

/** Two pages: shape on page 1, colour on page 2. */
public class CrosshairScreen extends ModuleScreen {
    public CrosshairScreen(Screen parent) {
        super("Crosshair", parent);
        this.pageCount = 2;
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        int x = rowX(), w = rowW();
        if (page == 0) {
            addRenderableWidget(Widgets.toggleRow(x, rowY(0), w, ROW_H, "Enabled",
                    () -> c.crosshairEnabled, v -> c.crosshairEnabled = v));
            addRenderableWidget(Widgets.slider(x, rowY(1), w, ROW_H, "Size", 1, 20, "px",
                    c.crosshairSize, v -> c.crosshairSize = v));
            addRenderableWidget(Widgets.slider(x, rowY(2), w, ROW_H, "Thickness", 1, 4, "px",
                    c.crosshairThickness, v -> c.crosshairThickness = v));
            addRenderableWidget(Widgets.slider(x, rowY(3), w, ROW_H, "Gap", 0, 10, "px",
                    c.crosshairGap, v -> c.crosshairGap = v));
            addRenderableWidget(Widgets.toggleRow(x, rowY(4), w, ROW_H, "Center dot",
                    () -> c.crosshairDot, v -> c.crosshairDot = v));
            addKeyRow(5, "On/off key", VoidClient.crosshairKey);
            addPageRow(6);
            addBackRow(7, "< Back");
            return 8;
        }
        addRenderableWidget(Widgets.colorSlider(x, rowY(0), w, ROW_H, "Red", 0, 255, 0xAAE5484D,
                c.crosshairRed, v -> c.crosshairRed = v));
        addRenderableWidget(Widgets.colorSlider(x, rowY(1), w, ROW_H, "Green", 0, 255, 0xAA3FB950,
                c.crosshairGreen, v -> c.crosshairGreen = v));
        addRenderableWidget(Widgets.colorSlider(x, rowY(2), w, ROW_H, "Blue", 0, 255, 0xAA4C8DFF,
                c.crosshairBlue, v -> c.crosshairBlue = v));
        addPageRow(3);
        addBackRow(4, "< Back");
        return 5;
    }
}
