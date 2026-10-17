package com.voidclient.screen;

import com.voidclient.VoidClient;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

/** Base for every module's own settings GUI: title, controls, Back button, auto-save. */
public abstract class ModuleScreen extends Screen {
    protected static final int W = 220, GAP = 24;
    private final Screen parent;

    protected ModuleScreen(String title, Screen parent) {
        super(Component.literal(title));
        this.parent = parent;
    }

    /** Add controls starting at (x, y), one row every GAP pixels. Return how many rows were used. */
    protected abstract int addControls(int x, int y);

    @Override
    protected void init() {
        int x = this.width / 2 - W / 2;
        int y = this.height / 4;
        int rows = addControls(x, y);
        addRenderableWidget(Button.builder(CommonComponents.GUI_BACK, b -> onClose())
                .bounds(x, y + GAP * rows + 8, W, 20).build());
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
