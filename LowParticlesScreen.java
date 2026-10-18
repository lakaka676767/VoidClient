package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

public class LowParticlesScreen extends ModuleScreen {
    public LowParticlesScreen(Screen parent) {
        super("Low Particles", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(0), rowW(), ROW_H, "Enabled",
                () -> c.lowParticlesEnabled, v -> c.lowParticlesEnabled = v));
        // two-step slider: left = Decreased, right = Minimal (fewest particles)
        addRenderableWidget(Widgets.slider(rowX(), rowY(1), rowW(), ROW_H, "Level", 0, 1,
                v -> v == 0 ? "Decreased" : "Minimal", c.particleLevel, v -> c.particleLevel = v));
        addBackRow(2, "< Back");
        return 3;
    }

    @Override
    protected String footer() {
        return "uses the game's own particle setting";
    }
}
