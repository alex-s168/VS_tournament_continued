package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import de.m_marvin.univec.impl.Vec3d
import de.m_marvin.univec.impl.Vec3i
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.pipelines.SegmentUtils
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

        val mass = physShip.inertia.shipMass
        val segment = physShip.segments.segments[0]?.segmentDisplacement!!
        val vel = SegmentUtils.getVelocity(physShip.poseVel, segment, Vector3d())

        spinners.forEach {
            val (pos, torque) = it

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vec3d().conv())

            physShip.applyInvariantTorque(torqueGlobal.mul(TournamentConfig.SERVER.spinnerSpeed ))

        }
    }

    fun addSpinner(pos: Vec3i, torque: Vec3d) {
        spinners.add(pos.conv() to torque.conv())
    }
    fun removeSpinner(pos: Vec3i, torque: Vec3d) {
        spinners.remove(pos.conv() to torque.conv())
    }

    companion object {
        fun getOrCreate(ship: ServerShip): SpinnerShipControl {
            return ship.getAttachment<SpinnerShipControl>()
                ?: SpinnerShipControl().also { ship.saveAttachment(it) }
        }
    }

}