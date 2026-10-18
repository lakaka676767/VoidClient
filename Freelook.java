package com.voidclient;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

/**
 * Freelook: hold the key and the mouse turns the CAMERA only. Your character keeps facing (and
 * walking) the way it was facing when you pressed the key. Works in first person.
 *
 * Every frame the turn the game just applied to the player is moved onto the camera and the
 * player's own rotation is put straight back, so the player never actually turns.
 */
public final class Freelook {
    public static boolean active;
    public static float yaw, pitch;

    private static float heldYaw, heldPitch;

    private Freelook() {}

    /** Called every client tick: freelook is on while the key is held. */
    public static void tick(Minecraft mc) {
        boolean want = VoidClient.CONFIG.freelookEnabled
                && mc.player != null && mc.level != null && mc.screen == null
                && !Freecam.active
                && VoidClient.freelookKey != null && VoidClient.freelookKey.isDown();

        if (want && !active) {
            LocalPlayer p = mc.player;
            yaw = heldYaw = p.getYRot();
            pitch = heldPitch = p.getXRot();
            active = true;
        } else if (!want && active) {
            active = false;
        }
    }

    public static void stop() {
        active = false;
    }

    /** Called once per rendered frame, just before the camera is set up. */
    public static void frameUpdate(Minecraft mc) {
        LocalPlayer p = mc.player;
        if (p == null) {
            active = false;
            return;
        }
        float py = p.getYRot(), pp = p.getXRot();
        yaw += py - heldYaw;
        pitch = Math.max(-90.0F, Math.min(90.0F, pitch + (pp - heldPitch)));
        p.setYRot(heldYaw);
        p.setXRot(heldPitch);
    }
}
