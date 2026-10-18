package com.voidclient.screen;

import com.voidclient.VoidClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.Locale;

/**
 * Base for every Void Client screen: a dark floating panel with a purple top line,
 * a title, and a vertical list of rows. The game world stays clear behind it.
 * Drag the title bar to move the panel; double-click it to put the panel back in the centre.
 */
public abstract class ModuleScreen extends Screen {
    protected static final int PANEL_W = 170, PAD = 6, HEADER_H = 28, ROW_H = 18, STEP = 20;

    private final Screen parent;
    private int panelX, panelY, panelH;
    private boolean dragging;
    private double grabX, grabY;

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

    // ------------------------------------------------------------------ layout

    private int centredX() {
        return this.width / 2 - PANEL_W / 2;
    }

    private int centredY() {
        return Math.max(4, (this.height - panelH) / 2);
    }

    @Override
    protected void init() {
        // pass 1: only to count the rows so we know how tall the panel is
        panelX = centredX();
        panelY = 0;
        int rows = addRows();
        panelH = HEADER_H + rows * STEP + PAD + (footer() != null ? 12 : 0);
        this.clearWidgets();

        // pass 2: place the panel (centre + saved drag offset, kept on screen) and add the real widgets
        panelX = Math.max(0, Math.min(centredX() + VoidClient.CONFIG.menuOffsetX, this.width - PANEL_W));
        panelY = Math.max(0, Math.min(centredY() + VoidClient.CONFIG.menuOffsetY, this.height - panelH));
        addRows();
    }

    private void moveTo(int newX, int newY) {
        int nx = Math.max(0, Math.min(newX, this.width - PANEL_W));
        int ny = Math.max(0, Math.min(newY, this.height - panelH));
        VoidClient.CONFIG.menuOffsetX = nx - centredX();
        VoidClient.CONFIG.menuOffsetY = ny - centredY();
        this.rebuildWidgets();
    }

    // ------------------------------------------------------------------ dragging

    private boolean inTitleBar(double mx, double my) {
        return mx >= panelX && mx < panelX + PANEL_W && my >= panelY && my < panelY + HEADER_H;
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (event.button() == 0 && inTitleBar(event.x(), event.y())) {
            if (doubleClick) {
                VoidClient.CONFIG.menuOffsetX = 0;
                VoidClient.CONFIG.menuOffsetY = 0;
                VoidClient.CONFIG.save();
                this.rebuildWidgets();
                return true;
            }
            dragging = true;
            grabX = event.x() - panelX;
            grabY = event.y() - panelY;
            return true;
        }
        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragging) {
            moveTo((int) Math.round(event.x() - grabX), (int) Math.round(event.y() - grabY));
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragging) {
            dragging = false;
            VoidClient.CONFIG.save();
            return true;
        }
        return super.mouseReleased(event);
    }

    // ------------------------------------------------------------------ drawing

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
