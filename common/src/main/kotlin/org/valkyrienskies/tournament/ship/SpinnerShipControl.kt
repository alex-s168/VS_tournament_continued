package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.tournament.TournamentConfig
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class SpinnerShipControl : ShipForcesInducer {

    private val spinners = CopyOnWriteArrayList<Pair<Vector3i, Vector3d>>()

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        spinners.forEach {
            val (_, torque) = it

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vector3d())

            physShip.applyInvariantTorque(torqueGlobal.mul(TournamentConfig.SERVER.spinnerSpeed))

        }
    }

    fun addSpinner(pos: Vector3i, torque: Vector3d) {
        spinners.add(pos to torque)
    }
    fun removeSpinner(pos: Vector3i, torque: Vector3d) {
        spinners.remove(pos to torque)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): SpinnerShipControl {
            return ship.getAttachment<SpinnerShipControl>()
                ?: SpinnerShipControl().also { ship.saveAttachment(it) }
        }
    }

}