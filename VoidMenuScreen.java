package com.voidclient.screen;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.gui.screens.Screen;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

/** Main menu: click a module's name to toggle it, click ">" for that module's own settings GUI. */
public class VoidMenuScreen extends ModuleScreen {
    public VoidMenuScreen(Screen parent) {
        super("Void Client", parent);
    }

    @Override
    protected int addRows() {
        ClientConfig c = VoidClient.CONFIG;
        module(0, "FOV Changer", () -> c.fovEnabled, v -> c.fovEnabled = v, () -> new FovScreen(this));
        module(1, "Fullbright", () -> c.fullbrightEnabled, v -> c.fullbrightEnabled = v, () -> new FullbrightScreen(this));
        module(2, "Blur", () -> c.blurEnabled, v -> c.blurEnabled = v, () -> new BlurScreen(this));
        addBackRow(3, "Close");
        return 4;
    }

    private void module(int row, String name, BooleanSupplier on, Consumer<Boolean> setter, Supplier<Screen> settings) {
        int gear = ROW_H;
        addRenderableWidget(Widgets.toggleRow(rowX(), rowY(row), rowW() - gear - 2, ROW_H, name, on, setter));
        addRenderableWidget(Widgets.flat(rowX() + rowW() - gear, rowY(row), gear, ROW_H, ">",
                b -> this.minecraft.setScreen(settings.get())));
    }

    @Override
    protected String footer() {
        return "click > for settings";
    }
}
