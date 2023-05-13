package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JacksonAnnotation
import com.fasterxml.jackson.annotation.JsonAutoDetect
import de.m_marvin.univec.impl.Vec3d
import net.minecraft.core.BlockPos
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.core.impl.pipelines.SegmentUtils
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TournamentConfig
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class ThrusterShipControl : ShipForcesInducer {

    private val thrusters = CopyOnWriteArrayList<Triple<Vector3i, Vector3d, Double>>()

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        val mass = physShip.inertia.shipMass
        val segment = physShip.segments.segments[0]?.segmentDisplacement!!
        val vel = SegmentUtils.getVelocity(physShip.poseVel, segment, Vector3d())

        thrusters.forEach {
            val (pos, force, tier) = it

            val tForce = Vec3d(physShip.transform.shipToWorld.transformDirection(force, Vec3d().conv()))
            val tPos = Vec3d(pos).add(0.5, 0.5, 0.5).sub(Vec3d().readFrom(physShip.transform.positionInShip))

            if (force.isFinite && physShip.poseVel.vel.length() < TournamentConfig.SERVER.thrusterShutoffSpeed) {
                physShip.applyInvariantForceToPos(tForce.mul(TournamentConfig.SERVER.thrusterSpeed * tier).conv(), tPos.conv())
            }
        }
    }

    fun addThruster(pos: BlockPos, tier: Double, force: Vec3d) {
        thrusters.add(Triple(pos.toJOML(), force.conv(), tier))
    }

    fun removeThruster(pos: BlockPos, tier: Double, force: Vec3d) {
        thrusters.remove(Triple(pos.toJOML(), force.conv(), tier))
    }

    fun forceStopThruster(pos: BlockPos) {
        thrusters.removeAll { it.first == pos }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): ThrusterShipControl {
            return ship.getAttachment<ThrusterShipControl>()
                ?: ThrusterShipControl().also { ship.saveAttachment(it) }
        }
    }

}