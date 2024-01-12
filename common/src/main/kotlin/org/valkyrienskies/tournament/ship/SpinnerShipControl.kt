package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
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

    // for compat only!!
    private val Spinners = mutableListOf<Pair<Vector3i, Vector3d>>()

    private val spinners = CopyOnWriteArrayList<Pair<Vector3i, Vector3d>>()

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        Spinners.forEach {
            spinners.add(it)
        }
        spinners.clear()

        spinners.forEach { (pos, torque) ->
            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vector3d())
            physShip.applyInvariantTorque(torqueGlobal.mul(TournamentConfig.SERVER.spinnerSpeed))
        }
    }

    fun addSpinner(pos: Vector3i, torque: Vector3d) {
        spinners.add(pos to torque)
    }

    fun stopSpinner(pos: Vector3i) {
        spinners.removeIf { it.first == pos }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): SpinnerShipControl {
            return ship.getAttachment<SpinnerShipControl>()
                ?: SpinnerShipControl().also { ship.saveAttachment(it) }
        }
    }

}