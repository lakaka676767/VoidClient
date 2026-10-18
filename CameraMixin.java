package com.voidclient.mixin;

import com.voidclient.Freecam;
import com.voidclient.Freelook;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Freecam and Freelook: after the game sets the camera up each frame, adjust it. */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Shadow
    private boolean detached;

    @Inject(method = "setup", at = @At("HEAD"))
    private void voidclient$cameraHead(Level level, Entity entity, boolean isDetached, boolean reversed,
                                       float partialTick, CallbackInfo ci) {
        if (Freecam.active) {
            Freecam.frameUpdate(Minecraft.getInstance());
        } else if (Freelook.active) {
            Freelook.frameUpdate(Minecraft.getInstance());
        }
    }

    @Inject(method = "setup", at = @At("RETURN"))
    private void voidclient$cameraTail(Level level, Entity entity, boolean isDetached, boolean reversed,
                                       float partialTick, CallbackInfo ci) {
        if (Freecam.active) {
            this.setPosition(Freecam.x, Freecam.y, Freecam.z);
            this.setRotation(Freecam.yaw, Freecam.pitch);
            this.detached = true; // draws your character's body, hides the first-person hand
        } else if (Freelook.active && !this.detached) {
            this.setRotation(Freelook.yaw, Freelook.pitch); // first person only
        }
    }
}
