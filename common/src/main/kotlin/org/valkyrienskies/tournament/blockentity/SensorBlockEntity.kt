package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ClipContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.HitResult
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.world.clipIncludeShips
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.util.helper.Helper3d
import kotlin.math.min
import kotlin.math.max

class SensorBlockEntity(pos: BlockPos, state: BlockState)
    : BlockEntity(TournamentBlockEntities.SENSOR.get(), pos, state)
{

    var lastVal = 0

    private fun basePoint(): Vector3d = blockPos.toJOMLD()
        .add(0.5, 0.5,0.5)
        .add(blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD().mul(0.5))

    fun getResult(level: ServerLevel): Int {
        val lookingTowards = blockState.getValue(BlockStateProperties.FACING).normal.toJOMLD()

        val ship = level.getShipObjectManagingPos(blockPos)

        val clipResult = level.clipIncludeShips(
            ClipContext(
                (Vector3d(basePoint()).let {
                    ship?.shipToWorld?.transformPosition(it) ?: it
                }).toMinecraft(),
                (blockPos.toJOMLD()
                    .add(0.5, 0.5,0.5)
                    .add(Vector3d(lookingTowards).mul(TournamentConfig.SERVER.sensorDistance))
                    .let {
                        ship?.shipToWorld?.transformPosition(it) ?: it
                    }).toMinecraft(),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null
            ),
            false
        )

        val value = max(1, min(15, (
                (1 - Helper3d.convertShipToWorldSpace(level, clipResult.location.toJOML())
                    .distance(Helper3d.convertShipToWorldSpace(level, blockPos))
                        / TournamentConfig.SERVER.sensorDistance)
                        * 16
                ).toInt()
        ))

        return if(clipResult.type == HitResult.Type.BLOCK) {
            value
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