package org.valkyrienskies.tournament.fabric.mixin.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.tournament.blocks.RedstoneConnectingBlock;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {

    @Inject(
            method = "shouldConnectTo(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/Direction;)Z",
            at = @At("HEAD"),
            cancellable = true
    )
    private static void tournament$shouldConnectTo(
            BlockState state,
            Direction direction,
            CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() instanceof RedstoneConnectingBlock cb) {
            cir.setReturnValue(cb.canConnectTo(state, direction));
        }
    }
}

