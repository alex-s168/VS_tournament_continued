package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import de.m_marvin.univec.impl.Vec3d
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.tournament.api.extension.conv
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class SimpleShipControl : ShipForcesInducer {

    private val Forces = CopyOnWriteArrayList<Vector3d>()

    override fun applyForces(physShip: PhysShip) {
        if (physShip == null) return
        physShip as PhysShipImpl

        Forces.forEach {
            val force = it

            println("force to apply: $force")

            physShip.applyInvariantForce(force)
        }
        Forces.clear()
    }

    fun addInvariantForce(force: Vec3d) {
        Forces.add(force.conv())
    }

    companion object {
        fun getOrCreate(ship: ServerShip): SimpleShipControl {
            return ship.getAttachment<SimpleShipControl>()
                ?: SimpleShipControl().also { ship.saveAttachment(it) }
        }
    }

}