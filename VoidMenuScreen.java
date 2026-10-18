package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Main menu, two pages. Click a name to toggle the module, click ">" for its own settings (and key bind). */
public class VoidMenuScreen extends ModuleScreen {
    public VoidMenuScreen(Screen parent) {
        super("Void Client", parent);
        this.pageCount = 2;
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        if (page == 0) {
            module(0, "FOV Changer", () -> c.fovEnabled, v -> c.fovEnabled = v, () -> new FovScreen(this));
            module(1, "Fullbright", () -> c.fullbrightEnabled, v -> c.fullbrightEnabled = v, () -> new FullbrightScreen(this));
            module(2, "Blur", () -> c.blurEnabled, v -> c.blurEnabled = v, () -> new BlurScreen(this));
            module(3, "Crosshair", () -> c.crosshairEnabled, v -> c.crosshairEnabled = v, () -> new CrosshairScreen(this));
            module(4, "Time Changer", () -> c.timeEnabled, v -> c.timeEnabled = v, () -> new TimeScreen(this));
        } else {
            module(0, "Low Fire", () -> c.lowFireEnabled, v -> c.lowFireEnabled = v, () -> new LowFireScreen(this));
            module(1, "Low Particles", () -> c.lowParticlesEnabled, v -> c.lowParticlesEnabled = v, () -> new LowParticlesScreen(this));
            module(2, "No Fog", () -> c.noFogEnabled, v -> c.noFogEnabled = v, () -> new NoFogScreen(this));
            module(3, "Freecam", () -> c.freecamEnabled, v -> c.freecamEnabled = v, () -> new FreecamScreen(this));
            module(4, "Freelook", () -> c.freelookEnabled, v -> c.freelookEnabled = v, () -> new FreelookScreen(this));
        }
        addPageRow(5);
        addBackRow(6, "Close");
        return 7;
    }

    private void module(int row, String name, BooleanSupplier on, Consumer<Boolean> setter, Supplier<Screen> settings) {
        int gear = ROW_H;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(row), rowW() - gear - 2, ROW_H, name, on, setter));
        addRenderableWidget(Widgets.flat(rowX() + rowW() - gear, rowY(row), gear, ROW_H, ">",
                b -> this.minecraft.setScreen(settings.get())));
    }

    @Override
    protected String footer() {
        return "click > for settings and keys";
    }
}
