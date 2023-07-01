package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class PulseShipControl : ShipForcesInducer {

    private val pulses = CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        pulses.forEach {
            val (pos, force) = it
            val tPos = pos.add( 0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            val tForce = physShip.transform.worldToShip.transformDirection(force)

            physShip.applyRotDependentForceToPos(tForce, tPos)
        }
        pulses.clear()
    }

    fun addPulse(pos: Vector3d, force: Vector3d) {
        pulses.add(pos to force)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): PulseShipControl {
            return ship.getAttachment<PulseShipControl>()
                ?: PulseShipControl().also { ship.saveAttachment(it) }
        }
    }

}