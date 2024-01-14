package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.util.helper.Helper3d
import org.valkyrienskies.tournament.util.math.lerp

class SensorBlockEntity(pos: BlockPos, state: BlockState):
    BlockEntity(TournamentBlockEntities.SENSOR.get(), pos, state)
{

    var lastVal = 0

    fun getResult(level: ServerLevel): Int {
        val lookingTowards = blockState
            .getValue(BlockStateProperties.FACING)
            .normal
            .toJOMLD()

        val start = Helper3d.convertShipToWorldSpace(level, blockPos)
            .add(0.5, 0.5,0.5)
            .add(lookingTowards.mul(0.5))

        val end = Helper3d.convertShipToWorldSpace(level, blockPos)
            .add(0.5, 0.5,0.5)
            .add(lookingTowards.mul(0.5 + TournamentConfig.SERVER.sensorDistance))

        val clipResult = level.clipIncludeShips(
            ClipContext(
                start.toMinecraft(),
                end.toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
            ),
            true
        )

        val hit = clipResult
            .location
            .toJOML()

        val value = lerp(1.0, 15.0, 1 - (start.distance(hit) / TournamentConfig.SERVER.sensorDistance))

        return if (clipResult.type != HitResult.Type.MISS) {
            value.toInt()
        } else {
            0
        }
    }

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, be: BlockEntity) {
            be as SensorBlockEntity
            if(level.isClientSide)
                return

            be.lastVal = be.getResult(level as ServerLevel)
            if (be.lastVal == state.getValue(BlockStateProperties.POWER))
                return

            level.setBlock(pos, state.setValue(BlockStateProperties.POWER, be.lastVal), 2)
        }
    }
}