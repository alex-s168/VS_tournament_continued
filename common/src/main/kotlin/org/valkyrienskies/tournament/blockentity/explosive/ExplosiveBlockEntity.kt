package org.valkyrienskies.tournament.blockentity.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.blocks.explosive.AbstractExplosiveBlock

class ExplosiveBlockEntity(pos: BlockPos, state: BlockState)
    : BlockEntity(TournamentBlockEntities.EXPLOSIVE.get(), pos, state)
{

    var explosionTicks = 0;

    companion object {
        final fun tick(level: Level, pos: BlockPos, state: BlockState, be: BlockEntity) {
            be as ExplosiveBlockEntity
            if(level.isClientSide)
                return

            if(be.explosionTicks > 1) {
                (state.block as AbstractExplosiveBlock).explodeTick(level as ServerLevel, pos)
                be.explosionTicks--
            }
            if(be.explosionTicks == 1) {
                level.removeBlock(pos, false)
                be.explosionTicks--
            }
        }
    }
}