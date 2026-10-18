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
import org.lwjgl.glfw.GLFW;

public class VoidClient implements ClientModInitializer {
    public static ClientConfig CONFIG = new ClientConfig();
    public static KeyMapping openMenuKey;
    public static KeyMapping zoomKey;

    @Override
    public void onInitializeClient() {
        CONFIG = ClientConfig.load();

        openMenuKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.voidclient.open_menu",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_F4,
                KeyMapping.Category.MISC));
        zoomKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.voidclient.zoom",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_C,
                KeyMapping.Category.MISC));

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
            Effects.tick(client);
        });

        // Put the player's own brightness/blur back before options.txt is written on exit.
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            Effects.restore(client);
            CONFIG.save();
        });
    }
}
