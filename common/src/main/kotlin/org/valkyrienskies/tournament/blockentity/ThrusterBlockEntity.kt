package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.ship.ThrusterShipControl
import org.valkyrienskies.tournament.storage.ShipFuelStorage

class ThrusterBlockEntity(
    pos: BlockPos,
    state: BlockState
): BlockEntity(
    TournamentBlockEntities.THRUSTER.get(),
    pos,
    state
) {

    var mult: Double = 0.0

    var tier: Int = -1
        set(a) {
            field = a
            level?.getChunk(worldPosition)?.isUnsaved = true
        }

    var redstone: Int = -1
        set(a) {
            field = a
            if (a == 0) disable() else enable()
            level?.getChunk(worldPosition)?.isUnsaved = true
        }

    var facing: Direction = Direction.NORTH
        set(a) {
            field = a
            level?.getChunk(worldPosition)?.isUnsaved = true
        }

    var running: Boolean = false
        set(a) {
            field = a
            level?.getChunk(worldPosition)?.isUnsaved = true
        }

    private var realRunning = false

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(): CompoundTag =
        CompoundTag().also { saveAdditional(it) }

    override fun saveAdditional(tag: CompoundTag) {
        tag.putInt("tier", tier)
        tag.putInt("redstone", redstone)
        tag.putInt("facing", facing.ordinal)
        tag.putBoolean("running", running)

        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        tier = tag.getInt("tier")
        redstone = tag.getInt("redstone")
        facing = Direction.values()[tag.getInt("facing")]
        running = tag.getBoolean("running")

        if (running && !realRunning) {
            enable()
        } else if (!running && realRunning) {
            disable()
        }

        super.load(tag)
    }

    fun enable() {
        if (level !is ServerLevel) return
        if (redstone == 0) return
        println("enabled: $redstone")

        running = true
        realRunning = true

        val ship = (level as ServerLevel).getShipObjectManagingPos(worldPosition)
            ?: (level as ServerLevel).getShipManagingPos(worldPosition)
            ?: return

        ThrusterShipControl.getOrCreate(ship).let {
            it.stopThruster(worldPosition)
            it.addThruster(
                worldPosition,
                tier * mult,
                facing
                    .normal
                    .toJOMLD()
                    .mul(redstone.toDouble())
            )
        }
    }

    fun disable() {
        if (level !is ServerLevel) return

        running = false
        realRunning = false

        ThrusterShipControl.getOrCreate(
        (level as ServerLevel).getShipObjectManagingPos(worldPosition)
            ?: (level as ServerLevel).getShipManagingPos(worldPosition)
            ?: return
        ).stopThruster(worldPosition)
    }

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, be: BlockEntity) {
            if (level !is ServerLevel) return
            be as ThrusterBlockEntity

            if (be.tier == -1 || be.redstone == -1) {
                println("Thruster BlockEntity not saved correctly!")
                return
            }

            val ship = level.getShipObjectManagingPos(pos)
                ?: (level).getShipManagingPos(pos)
                ?: return

            if (be.running && !ShipFuelStorage.get(ship).drainFuel(
                    level,
                    TournamentConfig.SERVER.thrusterFuelConsumtion * be.tier
            )) {
                be.disable()
                return
            }

            println(be.running)
        }
    }

}