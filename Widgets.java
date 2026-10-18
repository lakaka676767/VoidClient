package com.voidclient.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;

/** Flat, dark, purple-accent widgets used by every Void Client screen. */
public final class Widgets {
    private Widgets() {}

    private static void centeredText(GuiGraphics g, Component text, int x, int y, int w, int h, int color) {
        g.drawCenteredString(Minecraft.getInstance().font, text, x + w / 2, y + (h - 9) / 2 + 1, color);
    }

    // ------------------------------------------------------------------ toggle row

    /** A list row. Click toggles it; when ON it gets a purple bar and tinted background. */
    public static Button toggleRow(int x, int y, int w, int h, String name,
                                   BooleanSupplier on, Consumer<Boolean> setter) {
        return new ToggleRow(x, y, w, h, name, on, setter);
    }

    private static class ToggleRow extends Button {
        private final BooleanSupplier isOn;

        ToggleRow(int x, int y, int w, int h, String name, BooleanSupplier on, Consumer<Boolean> setter) {
            super(x, y, w, h, Component.literal(name), b -> setter.accept(!on.getAsBoolean()), DEFAULT_NARRATION);
            this.isOn = on;
        }

        @Override
        public void renderContents(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            boolean enabled = isOn.getAsBoolean();
            if (enabled) g.fill(x, y, x + w, y + h, Theme.ROW_ON);
            if (isHoveredOrFocused()) g.fill(x, y, x + w, y + h, Theme.ROW_HOVER);
            if (enabled) g.fill(x, y, x + 2, y + h, Theme.ACCENT);
            centeredText(g, getMessage(), x, y, w, h, enabled ? Theme.TEXT : Theme.TEXT_OFF);
        }
    }

    // ------------------------------------------------------------------ flat button

    public static Button flat(int x, int y, int w, int h, String label, Button.OnPress onPress) {
        return new FlatButton(x, y, w, h, label, onPress);
    }

    private static class FlatButton extends Button {
        FlatButton(int x, int y, int w, int h, String label, Button.OnPress onPress) {
            super(x, y, w, h, Component.literal(label), onPress, DEFAULT_NARRATION);
        }

        @Override
        public void renderContents(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            boolean hot = isHoveredOrFocused();
            if (hot) g.fill(x, y, x + w, y + h, Theme.ROW_HOVER);
            centeredText(g, getMessage(), x, y, w, h, hot ? Theme.TEXT : Theme.TEXT_OFF);
        }
    }

    // ------------------------------------------------------------------ sliders

    /** Integer slider from min to max; shows "label: value suffix" over a purple fill. */
    public static AbstractSliderButton slider(int x, int y, int w, int h, String label, int min, int max,
                                              String suffix, int value, IntConsumer onChange) {
        return new FlatSlider(x, y, w, h, label, min, max, v -> v + suffix, Theme.SLIDER_FILL, value, onChange);
    }

    /** Same, but with your own text for the value (for example a clock time) . */
    public static AbstractSliderButton slider(int x, int y, int w, int h, String label, int min, int max,
                                              IntFunction<String> format, int value, IntConsumer onChange) {
        return new FlatSlider(x, y, w, h, label, min, max, format, Theme.SLIDER_FILL, value, onChange);
    }

    /** Slider with its own fill colour, handy for red/green/blue. */
    public static AbstractSliderButton colorSlider(int x, int y, int w, int h, String label, int min, int max,
                                                   int fillColor, int value, IntConsumer onChange) {
        return new FlatSlider(x, y, w, h, label, min, max, v -> Integer.toString(v), fillColor, value, onChange);
    }

    private static class FlatSlider extends AbstractSliderButton {
        private final String label;
        private final IntFunction<String> format;
        private final int min, max, fill;
        private final IntConsumer onChange;

        FlatSlider(int x, int y, int w, int h, String label, int min, int max, IntFunction<String> format,
                   int fill, int value, IntConsumer onChange) {
            super(x, y, w, h, Component.empty(), (value - min) / (double) (max - min));
            this.label = label;
            this.format = format;
            this.min = min;
            this.max = max;
            this.fill = fill;
            this.onChange = onChange;
            updateMessage();
        }

        private int current() {
            return (int) Math.round(min + this.value * (max - min));
        }

        @Override
        protected void updateMessage() {
            if (format == null) return;
            setMessage(Component.literal(label + ": " + format.apply(current())));
        }

        @Override
        protected void applyValue() {
            onChange.accept(current());
        }

        @Override
        public void renderWidget(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
            int x = getX(), y = getY(), w = getWidth(), h = getHeight();
            g.fill(x, y, x + w, y + h, Theme.SLIDER_TRACK);
            int filled = (int) Math.round(w * this.value);
            g.fill(x, y, x + filled, y + h, fill);
            int knob = Math.max(x, Math.min(x + w - 2, x + filled - 1));
            g.fill(knob, y, knob + 2, y + h, Theme.ACCENT);
            if (isHoveredOrFocused()) g.fill(x, y, x + w, y + h, Theme.ROW_HOVER);
            centeredText(g, getMessage(), x, y, w, h, Theme.TEXT);
        }
    }
}
