package com.voidclient;

import com.voidclient.mixin.FogRendererAccessor;
import com.voidclient.mixin.OptionInstanceAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.server.level.ParticleStatus;

/**
 * Applies fullbright and blur by writing straight into the game's own options,
 * and puts the player's original values back when a module is switched off.
 */
public final class Effects {
    private static final double MAX_GAMMA = 15.0;

    private static Double savedGamma;
    private static Integer savedBlur;
    private static ParticleStatus savedParticles;
    private static boolean fogTouched;

    private Effects() {}

    public static void tick(Minecraft mc) {
        if (mc.options == null) return;
        ClientConfig c = VoidClient.CONFIG;

        // ---- Fullbright: 1% = your normal brightness, 100% = fully lit ----
        if (c.fullbrightEnabled) {
            if (savedGamma == null) savedGamma = mc.options.gamma().get();
            double g = savedGamma + (MAX_GAMMA - savedGamma) * (c.fullbrightPercent / 100.0);
            setRaw(mc.options.gamma(), g);
        } else if (savedGamma != null) {
            setRaw(mc.options.gamma(), savedGamma);
            savedGamma = null;
        }

        // ---- Blur behind menus: 1-100% mapped onto vanilla's 1-10 blur strength ----
        if (c.blurEnabled) {
            if (savedBlur == null) savedBlur = mc.options.menuBackgroundBlurriness().get();
            int strength = Math.max(1, Math.min(10, Math.round(c.blurPercent / 10f)));
            setRaw(mc.options.menuBackgroundBlurriness(), strength);
        } else if (savedBlur != null) {
            setRaw(mc.options.menuBackgroundBlurriness(), savedBlur);
            savedBlur = null;
        }

        // ---- Low particles: switches the game's own particle setting, puts yours back when off ----
        if (c.lowParticlesEnabled) {
            if (savedParticles == null) savedParticles = mc.options.particles().get();
            ParticleStatus target = c.particleLevel == 0 ? ParticleStatus.DECREASED : ParticleStatus.MINIMAL;
            if (mc.options.particles().get() != target) mc.options.particles().set(target);
        } else if (savedParticles != null) {
            mc.options.particles().set(savedParticles);
            savedParticles = null;
        }

        // ---- No fog: turns the game's global fog switch off while the module is on ----
        if (c.noFogEnabled) {
            FogRendererAccessor.voidclient$setFogEnabled(false);
            fogTouched = true;
        } else if (fogTouched) {
            FogRendererAccessor.voidclient$setFogEnabled(true);
            fogTouched = false;
        }
    }

    public static void restore(Minecraft mc) {
        if (mc.options == null) return;
        if (savedGamma != null) {
            setRaw(mc.options.gamma(), savedGamma);
            savedGamma = null;
        }
        if (savedBlur != null) {
            setRaw(mc.options.menuBackgroundBlurriness(), savedBlur);
            savedBlur = null;
        }
        if (savedParticles != null) {
            mc.options.particles().set(savedParticles);
            savedParticles = null;
        }
        if (fogTouched) {
            FogRendererAccessor.voidclient$setFogEnabled(true);
            fogTouched = false;
        }
    }

    /** Sets an option without vanilla's range check (fullbright needs gamma above 1.0). */
    @SuppressWarnings("unchecked")
    private static <T> void setRaw(OptionInstance<T> option, T value) {
        ((OptionInstanceAccessor<T>) (Object) option).voidclient$setValue(value);
    }
}
