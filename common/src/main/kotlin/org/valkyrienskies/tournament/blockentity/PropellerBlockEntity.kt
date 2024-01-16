package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.blocks.PropellerBlock

abstract class PropellerBlockEntity<T: BlockEntity>(
    be: BlockEntityType<T>,
    pos: BlockPos,
    state: BlockState,
    val maxSpeed: Float,
    val accel: Float
): BlockEntity(be, pos, state) {

    var signal: Int = -1
    var rotation: Double = 0.0
    var speed: Double = 0.0

    fun tick(level: Level) {
        if (signal == -1) {
            signal = PropellerBlock.getPropSignal(blockState, level, blockPos)
        }
        val targetSpeed = signal / 15.0f * maxSpeed
        if (speed < targetSpeed) {
            speed += accel
        } else if (speed > targetSpeed) {
            speed -= accel * 2
        }
        if (speed < 0.0f) {
            speed = 0.0
        }
        rotation -= speed
        rotation %= 360.0
    }

    companion object {
        val ticker = BlockEntityTicker<PropellerBlockEntity<*>> { level, _, _, be ->
            be.tick(level)
        }
    }

    override fun getUpdateTag(): CompoundTag =
        CompoundTag().also {
            saveAdditional(it)
        }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? =
        ClientboundBlockEntityDataPacket.create(this)

    override fun saveAdditional(tag: CompoundTag) {
        tag.putDouble("speed", speed)
        tag.putDouble("rotation", rotation)
        tag.putInt("signal", signal)

        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        speed = tag.getDouble("speed")
        rotation = tag.getDouble("rotation")
        signal = tag.getInt("signal")

        super.load(tag)
    }

    fun update() {
        level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL_IMMEDIATE)
    }

}

class BigPropellerBlockEntity(
    pos: BlockPos,
    state: BlockState,
): PropellerBlockEntity<BigPropellerBlockEntity>(
    TournamentBlockEntities.PROP_BIG.get(),
    pos,
    state,
    TournamentConfig.SERVER.propellerBigSpeed,
    TournamentConfig.SERVER.propellerBigAccel
)

class SmallPropellerBlockEntity(
    pos: BlockPos,
    state: BlockState,
): PropellerBlockEntity<SmallPropellerBlockEntity>(
    TournamentBlockEntities.PROP_SMALL.get(),
    pos,
    state,
    TournamentConfig.SERVER.propellerSmallSpeed,
    TournamentConfig.SERVER.propellerSmallAccel
)