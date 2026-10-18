package com.voidclient;

import com.mojang.blaze3d.platform.InputConstants;
import com.voidclient.screen.VoidMenuScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class VoidClient implements ClientModInitializer {
    public static ClientConfig CONFIG = new ClientConfig();

    // keys with a default
    public static KeyMapping openMenuKey;
    public static KeyMapping zoomKey;
    public static KeyMapping freecamKey;
    public static KeyMapping freelookKey;

    // module on/off keys (unbound until you pick one in the module's settings screen)
    public static KeyMapping fovKey, fullbrightKey, blurKey, crosshairKey, timeKey, lowFireKey, particlesKey, noFogKey;

    private static KeyMapping register(String name, int defaultKey) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(
                name, InputConstants.Type.KEYSYM, defaultKey, KeyMapping.Category.MISC));
    }

    /** Small message above the hotbar. */
    public static void say(Minecraft mc, String text) {
        if (mc.player != null) mc.player.displayClientMessage(Component.literal(text), true);
    }

    private static void toggleOnKey(Minecraft mc, KeyMapping key, String name,
                                    BooleanSupplier get, Consumer<Boolean> set) {
        while (key.consumeClick()) {
            boolean now = !get.getAsBoolean();
            set.accept(now);
            CONFIG.save();
            say(mc, name + (now ? " ON" : " OFF"));
        }
    }

    @Override
    public void onInitializeClient() {
        CONFIG = ClientConfig.load();

        openMenuKey = register("key.voidclient.open_menu", GLFW.GLFW_KEY_F4);
        zoomKey = register("key.voidclient.zoom", GLFW.GLFW_KEY_C);
        freecamKey = register("key.voidclient.freecam", GLFW.GLFW_KEY_F6);
        freelookKey = register("key.voidclient.freelook", GLFW.GLFW_KEY_LEFT_ALT);

        int none = GLFW.GLFW_KEY_UNKNOWN;
        fovKey = register("key.voidclient.toggle_fov", none);
        fullbrightKey = register("key.voidclient.toggle_fullbright", none);
        blurKey = register("key.voidclient.toggle_blur", none);
        crosshairKey = register("key.voidclient.toggle_crosshair", none);
        timeKey = register("key.voidclient.toggle_time", none);
        lowFireKey = register("key.voidclient.toggle_lowfire", none);
        particlesKey = register("key.voidclient.toggle_particles", none);
        noFogKey = register("key.voidclient.toggle_nofog", none);

        // Custom crosshair: when it's on we draw ours and skip the vanilla one.
        HudElementRegistry.replaceElement(VanillaHudElements.CROSSHAIR, original -> (graphics, tickCounter) -> {
            if (CONFIG.crosshairEnabled) {
                CrosshairRenderer.render(graphics);
            } else {
                original.render(graphics, tickCounter);
            }
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openMenuKey.consumeClick()) {
                client.setScreen(new VoidMenuScreen(client.screen));
            }

            toggleOnKey(client, fovKey, "FOV Changer", () -> CONFIG.fovEnabled, v -> CONFIG.fovEnabled = v);
            toggleOnKey(client, fullbrightKey, "Fullbright", () -> CONFIG.fullbrightEnabled, v -> CONFIG.fullbrightEnabled = v);
            toggleOnKey(client, blurKey, "Blur", () -> CONFIG.blurEnabled, v -> CONFIG.blurEnabled = v);
            toggleOnKey(client, crosshairKey, "Crosshair", () -> CONFIG.crosshairEnabled, v -> CONFIG.crosshairEnabled = v);
            toggleOnKey(client, timeKey, "Time Changer", () -> CONFIG.timeEnabled, v -> CONFIG.timeEnabled = v);
            toggleOnKey(client, lowFireKey, "Low Fire", () -> CONFIG.lowFireEnabled, v -> CONFIG.lowFireEnabled = v);
            toggleOnKey(client, particlesKey, "Low Particles", () -> CONFIG.lowParticlesEnabled, v -> CONFIG.lowParticlesEnabled = v);
            toggleOnKey(client, noFogKey, "No Fog", () -> CONFIG.noFogEnabled, v -> CONFIG.noFogEnabled = v);

            while (freecamKey.consumeClick()) {
                if (CONFIG.freecamEnabled) {
                    Freecam.toggle(client);
                } else {
                    say(client, "Freecam is off - turn it on in the Void Client menu (page 2)");
                }
            }
            if (!CONFIG.freelookEnabled) {
                while (freelookKey.consumeClick()) {
                    say(client, "Freelook is off - turn it on in the Void Client menu (page 2)");
                }
            }

            if (client.player == null) {
                Freecam.stop();
                Freelook.stop();
            }
            Freelook.tick(client);
            Freecam.diagnose(client);
            Effects.tick(client);
        });

        // Put the player's own brightness/blur back before options.txt is written on exit.
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Effects.restore(client);
            CONFIG.save();
        });
    }
}
