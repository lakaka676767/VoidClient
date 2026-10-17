package com.voidclient.screen;

import com.voidclient.VoidClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * Base for every Void Client screen: a dark floating panel with a purple top line,
 * a title, and a vertical list of rows. The game world stays clear behind it.
 */
public abstract class ModuleScreen extends Screen {
    protected static final int PANEL_W = 170, PAD = 6, HEADER_H = 28, ROW_H = 18, STEP = 20;

    private final Screen parent;
    private int panelX, panelY, panelH;

    protected ModuleScreen(String title, Screen parent) {
        super(Component.literal(title));
        this.parent = parent;
    }

    /** Add your widgets using rowX()/rowY(i)/rowW(). Return how many rows you used. */
    protected abstract int addRows();

    /** Optional small dim text at the bottom of the panel. */
    protected String footer() {
        return null;
    }

    protected int rowX() {
        return panelX + PAD;
    }

    protected int rowW() {
        return PANEL_W - PAD * 2;
    }

    protected int rowY(int row) {
        return panelY + HEADER_H + row * STEP;
    }

    protected void addBackRow(int row, String label) {
        addRenderableWidget(Widgets.flat(rowX(), rowY(row), rowW(), ROW_H, label, b -> onClose()));
    }

    @Override
    protected void init() {
        panelX = this.width / 2 - PANEL_W / 2;
        panelY = this.height / 6;
        int rows = addRows();
        panelH = HEADER_H + rows * STEP + PAD + (footer() != null ? 12 : 0);
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        int x = panelX, y = panelY, w = PANEL_W, h = panelH;

        g.fill(x - 1, y - 1, x + w + 1, y + h + 1, Theme.BORDER);
        g.fill(x, y, x + w, y + h, Theme.PANEL);
        g.fill(x, y, x + w, y + 2, Theme.ACCENT);

        Component title = Component.literal(this.title.getString().toUpperCase(Locale.ROOT))
                .withStyle(ChatFormatting.BOLD);
        g.drawCenteredString(this.font, title, x + w / 2, y + 10, Theme.ACCENT_TEXT);
        g.fill(x + PAD, y + HEADER_H - 4, x + w - PAD, y + HEADER_H - 3, Theme.BORDER);

        super.render(g, mouseX, mouseY, partialTick);

        String foot = footer();
        if (foot != null) {
            g.drawCenteredString(this.font, Component.literal(foot), x + w / 2, y + h - 12, Theme.TEXT_OFF);
        }
    }

    /** Keep the world clear (no dark or blurred layer) so the panel floats over it. */
    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void onClose() {
        VoidClient.CONFIG.save();
        this.minecraft.setScreen(parent);
    }
}
