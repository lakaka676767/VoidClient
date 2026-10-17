package com.voidclient.screen;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

/** Small helpers so every module screen builds sliders and toggles the same way. */
public final class Widgets {
    private Widgets() {}

    public static Component onOff(boolean on) {
        return on ? Component.literal("ON").withStyle(ChatFormatting.GREEN)
                  : Component.literal("OFF").withStyle(ChatFormatting.RED);
    }

    public static Button toggle(int x, int y, int w, String name, boolean initial, Consumer<Boolean> setter) {
        boolean[] state = {initial};
        return Button.builder(label(name, initial), b -> {
            state[0] = !state[0];
            setter.accept(state[0]);
            b.setMessage(label(name, state[0]));
        }).bounds(x, y, w, 20).build();
    }

    private static Component label(String name, boolean on) {
        return Component.literal(name + ": ").append(onOff(on));
    }

    /** Integer slider from min to max; shows "label: value suffix". */
    public static AbstractSliderButton slider(int x, int y, int w, String label, int min, int max,
                                              String suffix, int value, IntConsumer onChange) {
        return new ValueSlider(x, y, w, 20, label, min, max, suffix, value, onChange);
    }

    private static class ValueSlider extends AbstractSliderButton {
        private final String label, suffix;
        private final int min, max;
        private final IntConsumer onChange;

        ValueSlider(int x, int y, int w, int h, String label, int min, int max, String suffix,
                    int value, IntConsumer onChange) {
            super(x, y, w, h, Component.empty(), (value - min) / (double) (max - min));
            this.label = label;
            this.suffix = suffix;
            this.min = min;
            this.max = max;
            this.onChange = onChange;
            updateMessage();
        }

        private int current() {
            return (int) Math.round(min + this.value * (max - min));
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal(label + ": " + current() + suffix));
        }

        @Override
        protected void applyValue() {
            onChange.accept(current());
        }
    }
}
