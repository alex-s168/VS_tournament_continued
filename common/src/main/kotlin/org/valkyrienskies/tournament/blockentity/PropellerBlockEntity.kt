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

class PropellerBlockEntity(
    pos: BlockPos,
    state: BlockState,
    private val redstoneFun: (state: BlockState, level: Level, pos: BlockPos) -> Int = { _, level, bp ->
        println("[Tournament] PropellerBlockEntity.redstoneFun default constructor called! This should not happen!")
        level.getBestNeighborSignal(bp)
    }
): BlockEntity(TournamentBlockEntities.PROPELLER.get(), pos, state) {

    var signal: Int = -1
    var rotation: Float = 0.0f
    var speed: Float = 0.0f

    fun tick(level: Level) {
        if (signal == -1) {
            signal = redstoneFun(blockState, level, blockPos)
        }
        val targetSpeed = signal
        if (speed < targetSpeed) {
            speed += 0.1f
        } else if (speed > targetSpeed) {
            speed -= 0.2f
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