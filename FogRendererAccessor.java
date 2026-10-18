package com.voidclient.mixin;

import net.minecraft.client.renderer.fog.FogRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Lets Void Client flip the game's own global fog switch. */
@Mixin(FogRenderer.class)
public interface FogRendererAccessor {
    @Accessor("fogEnabled")
    static void voidclient$setFogEnabled(boolean enabled) {
        throw new AssertionError();
    }
}
