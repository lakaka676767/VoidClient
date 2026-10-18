package com.voidclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;

/** Draws the custom crosshair with plain filled rectangles. */
public final class CrosshairRenderer {
    private CrosshairRenderer() {}

    public static void render(GuiGraphics g) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.hideGui || !mc.options.getCameraType().isFirstPerson()) return;

        ClientConfig c = VoidClient.CONFIG;
        int color = 0xFF000000 | (c.crosshairRed << 16) | (c.crosshairGreen << 8) | c.crosshairBlue;
        int t = c.crosshairThickness, gap = c.crosshairGap, len = c.crosshairSize;

        int x0 = g.guiWidth() / 2 - t / 2;   // top-left corner of the centre square
        int y0 = g.guiHeight() / 2 - t / 2;

        g.fill(x0 - gap - len, y0, x0 - gap, y0 + t, color);               // left arm
        g.fill(x0 + t + gap, y0, x0 + t + gap + len, y0 + t, color);       // right arm
        g.fill(x0, y0 - gap - len, x0 + t, y0 - gap, color);               // top arm
        g.fill(x0, y0 + t + gap, x0 + t, y0 + t + gap + len, color);       // bottom arm
        if (c.crosshairDot) g.fill(x0, y0, x0 + t, y0 + t, color);
    }
}
