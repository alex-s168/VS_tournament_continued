package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import de.m_marvin.univec.impl.Vec3d
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.api.Ticked
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.pipelines.SegmentUtils
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class PulseShipControl : ShipForcesInducer {

    private val Pulses = CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()

    override fun applyForces(physShip: PhysShip) {
        if (physShip == null) return
        physShip as PhysShipImpl

        val mass = physShip.inertia.shipMass
        val segment = physShip.segments.segments[0]?.segmentDisplacement!!
        val vel = SegmentUtils.getVelocity(physShip.poseVel, segment, Vector3d())

        Pulses.forEach {
            val (pos, force) = it
            val tPos = Vec3d(pos).add( 0.5, 0.5, 0.5).sub(Vec3d(physShip.transform.positionInShip))
            val tForce = Vec3d(physShip.transform.worldToShip.transformDirection(force))

            physShip.applyRotDependentForceToPos(tForce.conv(), tPos.conv())
        }
        Pulses.clear()
    }

    fun addPulse(pos: Vec3d, force: Vec3d) {
        Pulses.add(pos.conv() to force.conv())
    }

    companion object {
        fun getOrCreate(ship: ServerShip): PulseShipControl {
            return ship.getAttachment<PulseShipControl>()
                ?: PulseShipControl().also { ship.saveAttachment(it) }
        }
    }

}