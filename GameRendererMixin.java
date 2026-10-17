package com.voidclient.mixin;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** FOV changer: custom FOV (30-170), optional "ignore sprint/speed FOV", and hold-to-zoom. */
@Mixin(GameRenderer.class)
public class GameRendererMixin {
    @Inject(method = "getFov", at = @At("RETURN"), cancellable = true)
    private void voidclient$modifyFov(Camera camera, float partialTick, boolean useFovSetting,
                                      CallbackInfoReturnable<Float> cir) {
        ClientConfig cfg = VoidClient.CONFIG;
        if (!useFovSetting || !cfg.fovEnabled) return; // hand FOV passes false; leave it alone

        float vanillaBase = Minecraft.getInstance().options.fov().get();
        float original = cir.getReturnValue();
        // ratio of vanilla's final FOV to the slider value = sprint/speed/underwater effects
        float effects = vanillaBase > 0 ? original / vanillaBase : 1.0f;

        float result = cfg.ignoreFovEffects ? cfg.fov : cfg.fov * effects;
        if (VoidClient.zoomKey != null && VoidClient.zoomKey.isDown()) {
            result /= cfg.zoomFactor;
        }
        cir.setReturnValue(Math.max(1.0f, result));
    }
}
