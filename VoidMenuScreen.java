package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Main menu: one window per category. Drag a window by its title bar, right-click the title
 * (or click its arrow) to fold it. Click a module to switch it on/off, right-click it for its settings.
 */
public class VoidMenuScreen extends Screen {
    private static final int WIN_W = 112, HEAD_H = 18, ROW_H = 15;

    private record Mod(String name, BooleanSupplier on, Consumer<Boolean> set, Function<Screen, Screen> settings) {}

    private record Category(String name, List<Mod> mods) {}

    private final Screen parent;
    private final List<Category> categories = new ArrayList<>();
    /** Draw order, bottom to top. The window you click last moves to the top. */
    private final List<Integer> order = new ArrayList<>(List.of(0, 1, 2));
    private int dragWin = -1;
    private double grabX, grabY;

    public VoidMenuScreen(Screen parent) {
        super(Component.literal("Void Client"));
        this.parent = parent;

        categories.add(new Category("Visual", List.of(
                new Mod("FOV Changer", () -> VoidClient.CONFIG.fovEnabled, v -> VoidClient.CONFIG.fovEnabled = v, FovScreen::new),
                new Mod("Fullbright", () -> VoidClient.CONFIG.fullbrightEnabled, v -> VoidClient.CONFIG.fullbrightEnabled = v, FullbrightScreen::new),
                new Mod("Blur", () -> VoidClient.CONFIG.blurEnabled, v -> VoidClient.CONFIG.blurEnabled = v, BlurScreen::new),
                new Mod("Crosshair", () -> VoidClient.CONFIG.crosshairEnabled, v -> VoidClient.CONFIG.crosshairEnabled = v, CrosshairScreen::new),
                new Mod("Time Changer", () -> VoidClient.CONFIG.timeEnabled, v -> VoidClient.CONFIG.timeEnabled = v, TimeScreen::new))));

        categories.add(new Category("Effects", List.of(
                new Mod("Low Fire", () -> VoidClient.CONFIG.lowFireEnabled, v -> VoidClient.CONFIG.lowFireEnabled = v, LowFireScreen::new),
                new Mod("No Fog", () -> VoidClient.CONFIG.noFogEnabled, v -> VoidClient.CONFIG.noFogEnabled = v, NoFogScreen::new),
                new Mod("Low Particles", () -> VoidClient.CONFIG.lowParticlesEnabled, v -> VoidClient.CONFIG.lowParticlesEnabled = v, LowParticlesScreen::new),
                new Mod("Render Dip", () -> VoidClient.CONFIG.renderDipEnabled, v -> VoidClient.CONFIG.renderDipEnabled = v, RenderDipScreen::new))));

        categories.add(new Category("Camera", List.of(
                new Mod("Freecam", () -> VoidClient.CONFIG.freecamEnabled, v -> VoidClient.CONFIG.freecamEnabled = v, FreecamScreen::new),
                new Mod("Freelook", () -> VoidClient.CONFIG.freelookEnabled, v -> VoidClient.CONFIG.freelookEnabled = v, FreelookScreen::new))));
    }

    // ------------------------------------------------------------------ layout

    @Override
    protected void init() {
        ClientConfig c = VoidClient.CONFIG;
        for (int i = 0; i < categories.size(); i++) {
            if (c.windowX[i] < 0) c.windowX[i] = 16 + i * (WIN_W + 12);   // default spots, left to right
            if (c.windowY[i] < 0) c.windowY[i] = 20;
            keepOnScreen(i);
        }
    }

    private int winHeight(int i) {
        return HEAD_H + (VoidClient.CONFIG.windowCollapsed[i] ? 0 : categories.get(i).mods().size() * ROW_H);
    }

    private void keepOnScreen(int i) {
        ClientConfig c = VoidClient.CONFIG;
        c.windowX[i] = Math.max(0, Math.min(c.windowX[i], Math.max(0, this.width - WIN_W)));
        c.windowY[i] = Math.max(0, Math.min(c.windowY[i], Math.max(0, this.height - HEAD_H)));
    }

    private void bringToFront(int i) {
        order.remove(Integer.valueOf(i));
        order.add(i);
    }

    /** The topmost window under the mouse, or -1. */
    private int windowAt(double mx, double my) {
        ClientConfig c = VoidClient.CONFIG;
        for (int k = order.size() - 1; k >= 0; k--) {
            int i = order.get(k);
            if (mx >= c.windowX[i] && mx < c.windowX[i] + WIN_W
                    && my >= c.windowY[i] && my < c.windowY[i] + winHeight(i)) {
                return i;
            }
        }
        return -1;
    }

    // ------------------------------------------------------------------ input

    @Override
    public boolean mouseClicked(MouseButtonEvent event, boolean doubleClick) {
        double mx = event.x(), my = event.y();
        int button = event.button();
        int i = windowAt(mx, my);
        if (i < 0) return super.mouseClicked(event, doubleClick);

        ClientConfig c = VoidClient.CONFIG;
        bringToFront(i);
        int x = c.windowX[i], y = c.windowY[i];

        if (my < y + HEAD_H) { // title bar
            boolean onArrow = mx >= x + WIN_W - 18;
            if (button == 1 || (button == 0 && onArrow)) {
                c.windowCollapsed[i] = !c.windowCollapsed[i];
                keepOnScreen(i);
                c.save();
            } else if (button == 0) {
                dragWin = i;
                grabX = mx - x;
                grabY = my - y;
            }
            return true;
        }

        int row = (int) ((my - y - HEAD_H) / ROW_H);
        List<Mod> mods = categories.get(i).mods();
        if (row >= 0 && row < mods.size()) {
            Mod mod = mods.get(row);
            if (button == 0) {
                mod.set().accept(!mod.on().getAsBoolean());
                c.save();
            } else if (button == 1) {
                this.minecraft.setScreen(mod.settings().apply(this));
            }
        }
        return true;
    }

    @Override
    public boolean mouseDragged(MouseButtonEvent event, double dx, double dy) {
        if (dragWin >= 0) {
            ClientConfig c = VoidClient.CONFIG;
            c.windowX[dragWin] = (int) Math.round(event.x() - grabX);
            c.windowY[dragWin] = (int) Math.round(event.y() - grabY);
            keepOnScreen(dragWin);
            return true;
        }
        return super.mouseDragged(event, dx, dy);
    }

    @Override
    public boolean mouseReleased(MouseButtonEvent event) {
        if (dragWin >= 0) {
            dragWin = -1;
            VoidClient.CONFIG.save();
            return true;
        }
        return super.mouseReleased(event);
    }

    // ------------------------------------------------------------------ drawing

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        for (int i : order) drawWindow(g, i, mouseX, mouseY);

        // hint strip
        g.fill(0, this.height - 14, this.width, this.height, 0x99000000);
        g.drawCenteredString(this.font,
                Component.literal("Left-click: on/off    Right-click: settings    Drag titles to move"),
                this.width / 2, this.height - 11, Theme.TEXT_OFF);
    }

    private void drawWindow(GuiGraphics g, int i, int mouseX, int mouseY) {
        ClientConfig c = VoidClient.CONFIG;
        Category cat = categories.get(i);
        int x = c.windowX[i], y = c.windowY[i], h = winHeight(i);
        boolean collapsed = c.windowCollapsed[i];

        g.fill(x - 1, y - 1, x + WIN_W + 1, y + h + 1, Theme.BORDER);

        // title bar
        g.fill(x, y, x + WIN_W, y + HEAD_H, Theme.ACCENT);
        g.drawCenteredString(this.font, Component.literal(cat.name()), x + WIN_W / 2 - 4, y + (HEAD_H - 9) / 2 + 1, Theme.TEXT);
        drawArrow(g, x + WIN_W - 10, y + HEAD_H / 2 - 1, collapsed);

        if (collapsed) return;

        // module list
        int top = windowAt(mouseX, mouseY);
        for (int j = 0; j < cat.mods().size(); j++) {
            Mod mod = cat.mods().get(j);
            int ry = y + HEAD_H + j * ROW_H;
            boolean on = mod.on().getAsBoolean();

            g.fill(x, ry, x + WIN_W, ry + ROW_H, Theme.PANEL);
            if (on) g.fill(x, ry, x + WIN_W, ry + ROW_H, Theme.ROW_ON);
            if (top == i && mouseX >= x && mouseX < x + WIN_W && mouseY >= ry && mouseY < ry + ROW_H) {
                g.fill(x, ry, x + WIN_W, ry + ROW_H, Theme.ROW_HOVER);
            }
            if (on) g.fill(x, ry, x + 2, ry + ROW_H, Theme.ACCENT);
            g.drawCenteredString(this.font, Component.literal(mod.name()), x + WIN_W / 2,
                    ry + (ROW_H - 9) / 2 + 1, on ? Theme.TEXT : Theme.TEXT_OFF);
        }
    }

    /** Small triangle: pointing down when the window is open, pointing right when folded. */
    private static void drawArrow(GuiGraphics g, int cx, int cy, boolean folded) {
        int white = 0xFFFFFFFF;
        if (!folded) {
            g.fill(cx - 3, cy - 1, cx + 4, cy, white);
            g.fill(cx - 2, cy, cx + 3, cy + 1, white);
            g.fill(cx - 1, cy + 1, cx + 2, cy + 2, white);
            g.fill(cx, cy + 2, cx + 1, cy + 3, white);
        } else {
            g.fill(cx - 1, cy - 3, cx, cy + 4, white);
            g.fill(cx, cy - 2, cx + 1, cy + 3, white);
            g.fill(cx + 1, cy - 1, cx + 2, cy + 2, white);
            g.fill(cx + 2, cy, cx + 3, cy + 1, white);
        }
    }

    /** Keep the world clear so the windows float over it. */
    @Override
    public void renderBackground(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void onClose() {
        VoidClient.CONFIG.save();
        this.minecraft.setScreen(parent);
    }
}
