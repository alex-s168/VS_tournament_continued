package org.valkyrienskies.tournament

import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.apigame.world.chunks.BlockType
import org.valkyrienskies.mod.common.BlockStateInfoProvider
import org.valkyrienskies.physics_api.Lod1BlockStateId
import org.valkyrienskies.physics_api.Lod1LiquidBlockStateId
import org.valkyrienskies.physics_api.Lod1SolidBlockStateId
import org.valkyrienskies.physics_api.voxel.Lod1LiquidBlockState
import org.valkyrienskies.physics_api.voxel.Lod1SolidBlockState

// TODO: ballast weight
object TournamentWeights : BlockStateInfoProvider {

    fun register() {

    }

    override val blockStateData: List<Triple<Lod1SolidBlockStateId, Lod1LiquidBlockStateId, Lod1BlockStateId>>
        get() = TODO("Not yet implemented")
    override val liquidBlockStates: List<Lod1LiquidBlockState>
        get() = TODO("Not yet implemented")
    override val priority: Int
        get() = TODO("Not yet implemented")
    override val solidBlockStates: List<Lod1SolidBlockState>
        get() = TODO("Not yet implemented")

    override fun getBlockStateMass(blockState: BlockState): Double? {
        TODO("Not yet implemented")
    }

    override fun getBlockStateType(blockState: BlockState): BlockType? {
        TODO("Not yet implemented")
    }
}
