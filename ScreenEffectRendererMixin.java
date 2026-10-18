package com.voidclient.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Low Fire: pushes the first-person fire overlay down the screen.
 * require = 0 means that if this hook ever stops matching, the feature quietly does nothing.
 */
@Mixin(ScreenEffectRenderer.class)
public class ScreenEffectRendererMixin {
    @Inject(method = "renderFire", at = @At("HEAD"), require = 0)
    private static void voidclient$fireDown(PoseStack poseStack, MultiBufferSource buffer,
                                            TextureAtlasSprite sprite, CallbackInfo ci) {
        ClientConfig c = VoidClient.CONFIG;
        poseStack.pushPose();
        if (c.lowFireEnabled) {
            poseStack.translate(0.0F, -(c.fireLowering / 100.0F) * 0.6F, 0.0F);
        }
    }

    @Inject(method = "renderFire", at = @At("RETURN"), require = 0)
    private static void voidclient$fireDownEnd(PoseStack poseStack, MultiBufferSource buffer,
                                               TextureAtlasSprite sprite, CallbackInfo ci) {
        poseStack.popPose();
    }
}
