package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.blocks.PropellerBlock

class PropellerBlockEntity(
    pos: BlockPos,
    state: BlockState,
    val maxSpeed: Float,
    val accel: Float
): BlockEntity(TournamentBlockEntities.PROPELLER.get(), pos, state) {

    var signal: Int = -1
    var rotation: Float = 0.0f
    var speed: Float = 0.0f

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
            speed = 0.0f
        }
        rotation += speed
        rotation %= 360.0f
    }

    companion object {
        val ticker = BlockEntityTicker<PropellerBlockEntity> { level, _, _, be ->
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
        tag.putFloat("speed", speed)
        tag.putFloat("rotation", rotation)
        tag.putInt("signal", signal)

        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        speed = tag.getFloat("speed")
        rotation = tag.getFloat("rotation")
        signal = tag.getInt("signal")

        super.load(tag)
    }

    fun update() {
        level?.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL_IMMEDIATE)
    }

}