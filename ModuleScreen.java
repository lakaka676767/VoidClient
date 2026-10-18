package com.voidclient.screen;

import com.mojang.blaze3d.platform.InputConstants;
import com.voidclient.VoidClient;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.Locale;

/**
 * Base for every Void Client screen: a dark floating panel with a purple top line,
 * a title, and a vertical list of rows. Drag the title bar to move it (double-click to re-centre).
 * Also provides page buttons and "press a key" key-bind rows.
 */
public abstract class ModuleScreen extends Screen {
    protected static final int PANEL_W = 170, PAD = 6, HEADER_H = 28, ROW_H = 18, STEP = 20;

    private final Screen parent;
    private int panelX, panelY, panelH;
    private boolean dragging;
    private double grabX, grabY;

    /** Pages: subclasses set pageCount in their constructor and branch on {@code page} in addRows(). */
    protected int page = 0;
    protected int pageCount = 1;
    private int pageRow = -1;

    /** The key mapping waiting for you to press a key (or null). */
    private KeyMapping listening;

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

    /** A row with "<" and ">" buttons and "1 / 2" between them. */
    protected void addPageRow(int row) {
        pageRow = row;
        int w = 30;
        addRenderableWidget(Widgets.flat(rowX(), rowY(row), w, ROW_H, "<", b -> {
            page = (page - 1 + pageCount) % pageCount;
            this.rebuildWidgets();
        }));
        addRenderableWidget(Widgets.flat(rowX() + rowW() - w, rowY(row), w, ROW_H, ">", b -> {
            page = (page + 1) % pageCount;
            this.rebuildWidgets();
        }));
    }

    /** A row that shows the key bound to {@code mapping}. Click it, then press the key you want. */
    protected void addKeyRow(int row, String label, KeyMapping mapping) {
        String keyName = mapping.isUnbound() ? "None" : mapping.getTranslatedKeyMessage().getString();
        String text = listening == mapping ? label + ": Press a key..." : label + ": " + keyName;
        addRenderableWidget(Widgets.flat(rowX(), rowY(row), rowW(), ROW_H, text, b -> {
            listening = (listening == mapping) ? null : mapping;
            this.rebuildWidgets();
        }));
    }

    private void bind(KeyMapping mapping, InputConstants.Key key) {
        mapping.setKey(key);
        KeyMapping.resetMapping();
        this.minecraft.options.save();
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
        pageRow = -1;
        if (page >= pageCount) page = 0;

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

    // ------------------------------------------------------------------ input

    private boolean inTitleBar(double mx, double my) {
        return mx >= panelX && mx < panelX + PANEL_W && my >= panelY && my < panelY + HEADER_H;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (listening != null) {
            KeyMapping mapping = listening;
            listening = null;
            int key = event.key();
            if (key == GLFW.GLFW_KEY_ESCAPE) {
                // cancel, keep the old key
            } else if (key == GLFW.GLFW_KEY_DELETE || key == GLFW.GLFW_KEY_BACKSPACE) {
                bind(mapping, InputConstants.UNKNOWN);
            } else {
                bind(mapping, InputConstants.Type.KEYSYM.getOrCreate(key));
            }
            this.rebuildWidgets();
            return true;
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        if (listening != null) {
            KeyMapping mapping = listening;
            listening = null;
            int button = event.button();
            if (button >= 2) { // middle / side mouse buttons can be bound; left and right just cancel
                bind(mapping, InputConstants.Type.MOUSE.getOrCreate(button));
            }
            this.rebuildWidgets();
            return true;
        }
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

        if (pageRow >= 0) {
            g.drawCenteredString(this.font, Component.literal((page + 1) + " / " + pageCount),
                    x + w / 2, rowY(pageRow) + (ROW_H - 9) / 2 + 1, Theme.TEXT_OFF);
        }
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
