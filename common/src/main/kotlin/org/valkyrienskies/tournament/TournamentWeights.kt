package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.core.apigame.world.chunks.BlockType
import org.valkyrienskies.mod.common.BlockStateInfo
import org.valkyrienskies.mod.common.BlockStateInfoProvider

object TournamentWeights : BlockStateInfoProvider {
    override val priority: Int
        get() = 200

    override fun getBlockStateMass(blockState: BlockState): Double? {
        if (blockState.block == TournamentBlocks.FLOATER.get()) {
            return TournamentConfig.SERVER.ballastWeight + (TournamentConfig.SERVER.ballastNoWeight - TournamentConfig.SERVER.ballastWeight) * (
                    (
                            blockState.getValue(
                                BlockStateProperties.POWER
                            ) + 1
                            ) / 16.0
                    )
        }

        return null
    }

    override fun getBlockStateType(blockState: BlockState): BlockType? {
        return null
    }

    fun register() {
        Registry.register(BlockStateInfo.REGISTRY, ResourceLocation(TournamentMod.MOD_ID, "ballast"), this)
    }
}
