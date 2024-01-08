package org.valkyrienskies.tournament.mixin.server;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.tournament.TickScheduler;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Inject(
        method = "tickChildren(Ljava/util/function/BooleanSupplier;)V",
        at = @At(value = "TAIL")
    )
    public void tickChildren(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        TickScheduler.INSTANCE.tickServer((MinecraftServer) (Object) this);
    }

}
