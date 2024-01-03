package org.valkyrienskies.tournament.forge.mixin.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.RedStoneWireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RedstoneSide;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.tournament.blocks.RedstoneConnectingBlock;

@Mixin(RedStoneWireBlock.class)
public class RedStoneWireBlockMixin {

    @Inject(
            method = "getConnectingSide(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/core/Direction;Z)Lnet/minecraft/world/level/block/state/properties/RedstoneSide;",
            at = @At("HEAD"),
            cancellable = true
    )
    private void tournament$getConnectingSide(
            BlockGetter level,
            BlockPos pos,
            Direction direction,
            boolean nonNormalCubeAbove,
            CallbackInfoReturnable<RedstoneSide> cir) {
        BlockPos blockpos = pos.relative(direction);
        BlockState blockstate = level.getBlockState(blockpos);

        if (blockstate.getBlock() instanceof RedstoneConnectingBlock cb) {
            boolean c = cb.canConnectTo(blockstate, direction);
            cir.setReturnValue(c ? RedstoneSide.SIDE : RedstoneSide.NONE);
        }
    }
}

