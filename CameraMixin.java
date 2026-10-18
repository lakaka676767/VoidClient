package com.voidclient.mixin;

import com.voidclient.Freecam;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Freecam: after the game has set the camera up each frame, move it to the freecam spot. */
@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    protected abstract void setPosition(double x, double y, double z);

    @Shadow
    protected abstract void setRotation(float yRot, float xRot);

    @Shadow
    private boolean detached;

    @Inject(method = "setup", at = @At("HEAD"))
    private void voidclient$freecamHead(Level level, Entity entity, boolean isDetached, boolean reversed,
                                        float partialTick, CallbackInfo ci) {
        if (Freecam.active) Freecam.frameUpdate(Minecraft.getInstance());
    }

    @Inject(method = "setup", at = @At("RETURN"))
    private void voidclient$freecamTail(Level level, Entity entity, boolean isDetached, boolean reversed,
                                        float partialTick, CallbackInfo ci) {
        if (!Freecam.active) return;
        this.setPosition(Freecam.x, Freecam.y, Freecam.z);
        this.setRotation(Freecam.yaw, Freecam.pitch);
        this.detached = true; // draws your character's body, hides the first-person hand
    }
}
