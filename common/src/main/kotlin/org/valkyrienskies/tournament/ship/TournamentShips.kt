package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.apigame.world.properties.DimensionId
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TickScheduler
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.util.extension.toBlock
import org.valkyrienskies.tournament.util.extension.toDimensionKey
import org.valkyrienskies.tournament.util.extension.toDouble
import org.valkyrienskies.tournament.util.helper.Helper3d
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
// TODO: port balloon, pulse and spinner control to this
class TournamentShips: ShipForcesInducer {

    var level: DimensionId = "minecraft:overworld"

    data class ThrusterData(
        val pos: Vector3i,
        val force: Vector3d,
        val mult: Double,
        var submerged: Boolean,
        var level: ResourceKey<Level>? = null
    )

    private val thrusters =
        CopyOnWriteArrayList<ThrusterData>()

    @JsonIgnore
    private var hasTicker = false

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        if (!hasTicker) {
            TickScheduler.serverTickPerm { server ->
                val lvl = server.getLevel(level.toDimensionKey())
                    ?: return@serverTickPerm
                thrusters.forEach { t ->
                    val water = lvl.isWaterAt(
                        Helper3d
                            .convertShipToWorldSpace(lvl, t.pos.toDouble())
                            .toBlock()
                    )
                    t.submerged = water
                }
            }
            hasTicker = true
        }

        thrusters.forEach { data ->
            val (pos, force, tier, submerged) = data

            if (submerged) {
                return@forEach
            }

            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d())
            val tPos = pos.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

            if (force.isFinite && (
                        TournamentConfig.SERVER.thrusterShutoffSpeed == -1.0
                                || physShip.poseVel.vel.length() < TournamentConfig.SERVER.thrusterShutoffSpeed
                        )
            ) {
                physShip.applyInvariantForceToPos(tForce.mul(TournamentConfig.SERVER.thrusterSpeed * tier), tPos)
            }
        }
    }

    fun addThruster(
        pos: BlockPos,
        tier: Double,
        force: Vector3d
    ) {
        thrusters += ThrusterData(pos.toJOML(), force, tier, false)
    }

    fun addThrusters(
        list: Iterable<Triple<Vector3i, Vector3d, Double>>
    ) {
        list.forEach { (pos, force, tier) ->
            thrusters += ThrusterData(pos, force, tier, false)
        }
    }

    fun stopThruster(
        pos: BlockPos
    ) {
        thrusters.removeIf { pos.toJOML() == it.pos }
    }

    companion object {
        fun getOrCreate(ship: ServerShip, level: DimensionId) =
            ship.getAttachment<TournamentShips>()
                ?: TournamentShips().also {
                    it.level = level
                    ship.saveAttachment(it)
                }

        fun getOrCreate(ship: ServerShip): TournamentShips =
            getOrCreate(ship, ship.chunkClaimDimension)
    }
}