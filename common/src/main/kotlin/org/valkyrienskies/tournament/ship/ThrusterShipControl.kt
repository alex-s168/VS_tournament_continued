package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TickScheduler
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.util.extension.toDouble
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class ThrusterShipControl : ShipForcesInducer {

    // for compat only!!
    private val Thrusters = mutableListOf<Triple<Vector3i, Vector3d, Double>>()

    private val thrusters = CopyOnWriteArrayList<Triple<Vector3i, Vector3d, Double>>()

    data class ThrusterData(
        val force: Vector3d,
        val mult: Double,
        var submerged: Boolean,
        var level: ResourceKey<Level>? = null
    )

    private val thrustersNew = ConcurrentHashMap<Vector3i, ThrusterData>()

    @JsonIgnore
    private var hasTicker = false

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        Thrusters.forEach {
            thrustersNew[it.first] = ThrusterData(it.second, it.third, false)
        }
        Thrusters.clear()

        thrusters.forEach {
            thrustersNew[it.first] = ThrusterData(it.second, it.third, false)
        }
        thrusters.clear()

        if (!hasTicker) {
            TickScheduler.serverTickPerm {
                thrustersNew.forEach { (pos, data) ->

                }
            }
            hasTicker = true
        }

        thrustersNew.forEach { (pos, data) ->
            val (force, tier, submerged) = data

            if (submerged) {
                return@forEach
            }

            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d())
            val tPos = pos.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

            if (force.isFinite && (
                    TournamentConfig.SERVER.thrusterShutoffSpeed == -1
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
        force: Vector3d,
        level: ResourceKey<Level>?
    ) {
        thrustersNew[pos.toJOML()] = ThrusterData(force, tier, false)
    }

    fun stopThruster(
        pos: BlockPos,
        level: ResourceKey<Level>?
    ) {
        thrustersNew.remove(pos.toJOML())
    }

    companion object {
        fun getOrCreate(ship: ServerShip): ThrusterShipControl {
            return ship.getAttachment<ThrusterShipControl>()
                ?: ThrusterShipControl().also { ship.saveAttachment(it) }
        }
    }

}