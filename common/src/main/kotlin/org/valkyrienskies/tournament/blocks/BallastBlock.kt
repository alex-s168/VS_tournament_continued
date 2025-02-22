package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties.POWER
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.doc.Doc
import org.valkyrienskies.tournament.doc.Documented
import org.valkyrienskies.tournament.doc.documentation

class BallastBlock : Block(
    Properties.of()
        .mapColor(MapColor.STONE)
        .sound(SoundType.STONE)
        .strength(1.0f, 2.0f)
) {

    init {
        registerDefaultState(defaultBlockState().setValue(POWER, 0))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
        super.createBlockStateDefinition(builder)
        builder.add(POWER)
    }

    override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, isMoving: Boolean) {
        super.onPlace(state, level, pos, oldState, isMoving)

        if (level as? ServerLevel == null) return

        val signal = level.getBestNeighborSignal(pos)

        if (signal != state.getValue(POWER))
            level.setBlock(pos, state.setValue(POWER, signal), 2)
    }

    override fun neighborChanged(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        block: Block,
        fromPos: BlockPos,
        isMoving: Boolean
    ) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving)

        if (level as? ServerLevel == null) return

        val signal = level.getBestNeighborSignal(pos)
        level.setBlock(pos, state.setValue(POWER, signal), 2)
    }

    class DocImpl: Documented {
        override fun getDoc() = documentation {
            page("Ballast")
                .kind(Doc.Kind.BLOCK)
                .summary("A block that has a redstone adjustable weight")
                .section("Usage") {
                    content("Power with redstone to increase weight")
                }
                .section("Config") {
                    content("The on and off weight of the ballast is configurable. " +
                            "The default off weight is ${TournamentConfig.SERVER.ballastNoWeight}, " +
                            "and the default on weight is ${TournamentConfig.SERVER.ballastWeight}.")
                }
        }
    }
}
