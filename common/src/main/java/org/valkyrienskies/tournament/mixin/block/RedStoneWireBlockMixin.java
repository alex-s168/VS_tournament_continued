package org.valkyrienskies.tournament.mixin.block;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.tournament.TournamentBlocks;

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
        if (state.is(TournamentBlocks.SENSOR.get())) {
            cir.setReturnValue(direction == state.getValue(BlockStateProperties.FACING));
        }
    }
}

