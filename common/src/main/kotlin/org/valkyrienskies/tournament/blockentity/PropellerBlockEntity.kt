package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.TournamentBlockEntities

class PropellerBlockEntity(pos: BlockPos, state: BlockState):
    BlockEntity(TournamentBlockEntities.PROPELLER.get(), pos, state)
{

    var signal: Int = 0

    fun tick(level: Level) {

    }

    companion object {
        val ticker = BlockEntityTicker<PropellerBlockEntity> { level, _, _, be ->
            be.tick(level)
        }
    }

}