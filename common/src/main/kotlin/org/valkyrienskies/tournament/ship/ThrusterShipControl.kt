package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import net.minecraft.core.BlockPos
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.util.extension.toDouble
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

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        Thrusters.forEach {
            thrusters.add(it)
        }
        Thrusters.clear()

        thrusters.forEach {
            val (pos, force, tier) = it

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

    fun addThruster(pos: BlockPos, tier: Double, force: Vector3d) {
        thrusters.add(Triple(pos.toJOML(), force, tier))
    }

    fun removeThruster(pos: BlockPos, tier: Double, force: Vector3d) {
        thrusters.remove(Triple(pos.toJOML(), force, tier))
    }

    fun forceStopThruster(pos: BlockPos) {
        thrusters.removeAll { it.first == pos.toJOML() }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): ThrusterShipControl {
            return ship.getAttachment<ThrusterShipControl>()
                ?: ThrusterShipControl().also { ship.saveAttachment(it) }
        }
    }

}