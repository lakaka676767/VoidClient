package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.Supplier;

/** Main menu: one row per module. Left button opens that module's own GUI, right button toggles it. */
public class VoidMenuScreen extends Screen {
    private static final int GAP = 24;
    private final Screen parent;

    public VoidMenuScreen(Screen parent) {
        super(Component.literal("Void Client"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        ClientConfig c = VoidClient.CONFIG;
        int x = this.width / 2 - 110;
        int y = this.height / 4;

        row(x, y, "FOV Changer  >", () -> new FovScreen(this), c.fovEnabled, v -> c.fovEnabled = v);
        row(x, y + GAP, "Fullbright  >", () -> new FullbrightScreen(this), c.fullbrightEnabled, v -> c.fullbrightEnabled = v);
        row(x, y + GAP * 2, "Blur  >", () -> new BlurScreen(this), c.blurEnabled, v -> c.blurEnabled = v);

        addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, b -> onClose())
                .bounds(x, y + GAP * 3 + 8, 220, 20).build());
    }

    private void row(int x, int y, String name, Supplier<Screen> screen, boolean enabled, Consumer<Boolean> setter) {
        addRenderableWidget(Button.builder(Component.literal(name),
                b -> this.minecraft.setScreen(screen.get())).bounds(x, y, 150, 20).build());

        boolean[] state = {enabled};
        addRenderableWidget(Button.builder(Widgets.onOff(enabled), b -> {
            state[0] = !state[0];
            setter.accept(state[0]);
            b.setMessage(Widgets.onOff(state[0]));
        }).bounds(x + 154, y, 66, 20).build());
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFFFF);
    }

    @Override
    public void onClose() {
        VoidClient.CONFIG.save();
        this.minecraft.setScreen(parent);
    }
}
