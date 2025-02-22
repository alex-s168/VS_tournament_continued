package org.valkyrienskies.tournament.util.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface WithExRenderInfo {
    FaceRenderType getFaceRenderType(BlockState state, BlockGetter level, BlockPos pos, Direction face);

    enum FaceRenderType {
        NORMAL,
        FORCE_RENDER,
        FORCE_NOT_RENDER,
    }
}
