package com.voidclient.mixin;

import com.voidclient.ClientConfig;
import com.voidclient.VoidClient;
import net.minecraft.world.level.Level;
import net.minecraft.world.timeline.Timeline;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

/**
 * Time Changer: on the CLIENT only, the 24000-tick day timeline reports the time you picked.
 * The server's real time is never touched. require = 0 means it fails quietly if the hook changes.
 */
@Mixin(Timeline.class)
public abstract class TimelineMixin {
    @Shadow
    public abstract Optional<Integer> periodTicks();

    @Inject(method = "getCurrentTicks", at = @At("RETURN"), cancellable = true, require = 0)
    private void voidclient$timeOfDay(Level level, CallbackInfoReturnable<Long> cir) {
        ClientConfig c = VoidClient.CONFIG;
        if (!c.timeEnabled || !level.isClientSide() || !isDayTimeline()) return;
        cir.setReturnValue((long) c.timeOfDay);
    }

    @Inject(method = "getTotalTicks", at = @At("RETURN"), cancellable = true, require = 0)
    private void voidclient$totalTime(Level level, CallbackInfoReturnable<Long> cir) {
        ClientConfig c = VoidClient.CONFIG;
        if (!c.timeEnabled || !level.isClientSide() || !isDayTimeline()) return;
        long original = cir.getReturnValue();
        // keep the real day number (moon phase etc.), replace only the time inside the day
        cir.setReturnValue(original - Math.floorMod(original, 24000L) + c.timeOfDay);
    }

    private boolean isDayTimeline() {
        Optional<Integer> period = periodTicks();
        return period.isPresent() && period.get() == 24000;
    }
}
