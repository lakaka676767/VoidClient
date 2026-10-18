package com.voidclient.mixin;

import com.voidclient.Freecam;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Freecam: while it's on, the player's movement/attack/use keys read as "not pressed". */
@Mixin(KeyMapping.class)
public class KeyMappingMixin {
    @Inject(method = "isDown", at = @At("RETURN"), cancellable = true)
    private void voidclient$blockIsDown(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.shouldBlock((KeyMapping) (Object) this)) cir.setReturnValue(false);
    }

    @Inject(method = "consumeClick", at = @At("RETURN"), cancellable = true)
    private void voidclient$blockClick(CallbackInfoReturnable<Boolean> cir) {
        if (Freecam.shouldBlock((KeyMapping) (Object) this)) cir.setReturnValue(false);
    }
}
