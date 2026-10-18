package com.voidclient;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

/**
 * Freecam: the CAMERA flies around while your character stays exactly where it is.
 *
 * Nothing extra is sent to the server. While freecam is on, the movement/attack/use keys are
 * blocked for the player (so it just stands there like an idle player), and the mouse turns the
 * camera instead of the player (the player's own rotation is put straight back every frame).
 */
public final class Freecam {
    public static boolean active;
    /** Set briefly by our own code so it can read the real key state. */
    public static boolean bypass;

    public static double x, y, z;
    public static float yaw, pitch;

    /** Set by the camera hook every frame. If it stays false, the camera hook isn't loaded. */
    public static boolean hookSeen;

    private static float heldYaw, heldPitch;
    private static long lastNanos;
    private static int waitTicks;

    private Freecam() {}

    public static void toggle(Minecraft mc) {
        if (active) {
            active = false;
            VoidClient.say(mc, "Freecam OFF");
            return;
        }
        LocalPlayer p = mc.player;
        if (p == null || mc.level == null) return;

        Vec3 eye = p.getEyePosition();
        x = eye.x;
        y = eye.y;
        z = eye.z;
        yaw = heldYaw = p.getYRot();
        pitch = heldPitch = p.getXRot();
        lastNanos = System.nanoTime();
        active = true;
        VoidClient.say(mc, "Freecam ON");
    }

    public static void stop() {
        active = false;
    }

    /** If freecam/freelook is on but the camera hook never ran, tell the player what to fix. */
    public static void diagnose(Minecraft mc) {
        if ((active || Freelook.active) && !hookSeen) {
            if (++waitTicks == 20) {
                VoidClient.say(mc, "Camera hook not loaded - upload ALL files from the zip, then rebuild");
            }
        } else {
            waitTicks = 0;
        }
    }

    /** True for the keys that should do nothing for the player while freecam is on. */
    public static boolean shouldBlock(KeyMapping key) {
        if (!active || bypass) return false;
        Options o = Minecraft.getInstance().options;
        if (o == null) return false;
        return key == o.keyUp || key == o.keyDown || key == o.keyLeft || key == o.keyRight
                || key == o.keyJump || key == o.keyShift || key == o.keySprint
                || key == o.keyAttack || key == o.keyUse || key == o.keyPickItem
                || key == o.keyDrop || key == o.keySwapOffhand;
    }

    /** Called once per rendered frame (from the camera hook) while freecam is on. */
    public static void frameUpdate(Minecraft mc) {
        LocalPlayer p = mc.player;
        // leave freecam if we leave the world, take damage, or the module is switched off
        if (p == null || mc.level == null || p.hurtTime > 0 || !VoidClient.CONFIG.freecamEnabled) {
            active = false;
            return;
        }

        long now = System.nanoTime();
        double dt = Math.min(0.1, (now - lastNanos) / 1.0E9);
        lastNanos = now;

        // Mouse: take the turn the game just applied to the player, give it to the camera,
        // and put the player's rotation back so the player never actually turns.
        float py = p.getYRot(), pp = p.getXRot();
        yaw += py - heldYaw;
        pitch = Math.max(-90.0F, Math.min(90.0F, pitch + (pp - heldPitch)));
        p.setYRot(heldYaw);
        p.setXRot(heldPitch);

        if (mc.screen != null) return;

        bypass = true;
        Options o = mc.options;
        double forward = (o.keyUp.isDown() ? 1 : 0) - (o.keyDown.isDown() ? 1 : 0);
        double strafe = (o.keyRight.isDown() ? 1 : 0) - (o.keyLeft.isDown() ? 1 : 0);
        double vertical = (o.keyJump.isDown() ? 1 : 0) - (o.keyShift.isDown() ? 1 : 0);
        boolean fast = o.keySprint.isDown();
        bypass = false;

        double yr = Math.toRadians(yaw), pr = Math.toRadians(pitch);
        double lookX = -Math.sin(yr) * Math.cos(pr);
        double lookY = -Math.sin(pr);
        double lookZ = Math.cos(yr) * Math.cos(pr);
        double rightX = -Math.cos(yr);
        double rightZ = -Math.sin(yr);

        double mx = lookX * forward + rightX * strafe;
        double my = lookY * forward + vertical;
        double mz = lookZ * forward + rightZ * strafe;
        double len = Math.sqrt(mx * mx + my * my + mz * mz);
        if (len < 1.0E-6) return;
        if (len > 1.0) {
            mx /= len;
            my /= len;
            mz /= len;
        }

        double speed = VoidClient.CONFIG.freecamSpeed * (fast ? 2.5 : 1.0) * dt;
        x += mx * speed;
        y += my * speed;
        z += mz * speed;
    }
}
