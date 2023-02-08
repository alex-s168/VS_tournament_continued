package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import de.m_marvin.univec.impl.Vec3d
import de.m_marvin.univec.impl.Vec3i
import net.minecraft.core.BlockPos
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
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TournamentConfig
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class TournamentShipControl : ShipForcesInducer, ServerShipUser, Ticked {

    @JsonIgnore
    override var ship: ServerShip? = null

    private var extraForce = 0.0
    private var physConsumption = 0f

    private var weightedCenterOfLift: Vec3d = Vec3d()
    private var BalloonsPower = 0.0

    @JsonIgnore
    private val Spinners = mutableListOf<Pair<Vector3i, Vector3d>>()
    @JsonIgnore
    private val Thrusters = mutableListOf<Triple<Vector3i, Vector3d, Double>>()
    @JsonIgnore
    private val Pulses = CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()

    var consumed = 0f
        private set

    override fun applyForces(physShip: PhysShip) {
        if (ship == null) return
        physShip as PhysShipImpl

        // if moving to fast or is to high dont apply a force
        if (physShip.poseVel.vel.y() < 2 || physShip.transform.positionInWorld.y() > TournamentConfig.SERVER.BaseHeight)    {
            BalloonsPower = 0.0
        }

        if(BalloonsPower != 0.0) {

            var centerOfLift = weightedCenterOfLift.div(BalloonsPower)
            physShip.applyInvariantForceToPos(
                Vec3d(
                    0.0,
                    (BalloonsPower),
                    0.0
                ).conv(), centerOfLift.conv()
            )
        }

        Spinners.forEach {
            val (pos, torque) = it

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vec3d().conv())

            physShip.applyInvariantTorque(torqueGlobal.mul(TournamentConfig.SERVER.SpinnerSpeed ))

        }

        Thrusters.forEach {
            val (pos, force, tier) = it

            val tForce = Vec3d(physShip.transform.shipToWorld.transformDirection(force, Vec3d().conv()))
            val tPos = Vec3d(pos).add(0.5, 0.5, 0.5).sub(Vec3d().readFrom(physShip.transform.positionInShip))

            if (force.isFinite && physShip.poseVel.vel.length() < 50) {
                physShip.applyInvariantForceToPos(tForce.mul(TournamentConfig.SERVER.ThrusterSpeed * tier).conv(), tPos.conv())
            }
        }

        //Pulse Gun
        Pulses.forEach {
            val (pos, force) = it
            val tPos = Vec3d(pos).add( 0.5, 0.5, 0.5).sub(Vec3d(physShip.transform.positionInShip))
            val tForce = Vec3d(physShip.transform.worldToShip.transformDirection(force))

            physShip.applyRotDependentForceToPos(tForce.conv(), tPos.conv())
        }

        Pulses.clear()
    }
    var power = 0.0

    var balloons = 0 // Amount of balloons
        set(v) {
            field = v; deleteIfEmpty()
        }

    override fun tick() {
        extraForce = power
        power = 0.0
        consumed = physConsumption *0.1f
        physConsumption = 0.0f
    }

    private fun deleteIfEmpty() {
        if (balloons == 0) {
            ship?.saveAttachment<TournamentShipControl>(null)
        }
    }


    fun addBalloon(pos: BlockPos, pow: Double) {
        weightedCenterOfLift = weightedCenterOfLift.add(Vec3d(pos).mul(pow))
        BalloonsPower += pow
    }

    fun removeBalloon(pos: BlockPos, pow: Double) {
        weightedCenterOfLift = weightedCenterOfLift.sub(Vec3d(pos).mul(pow))
        BalloonsPower -= pow
    }

    fun addThruster(pos: BlockPos, tier: Double, force: Vec3d) {
        Thrusters.add(Triple(pos.toJOML(), force.conv(), tier))
    }
    fun removeThruster(pos: BlockPos, tier: Double, force: Vec3d) {
        Thrusters.remove(Triple(pos.toJOML(), force.conv(), tier))
    }

    fun addSpinner(pos: Vec3i, torque: Vec3d) {
        Spinners.add(pos.conv() to torque.conv())
    }
    fun removeSpinner(pos: Vec3i, torque: Vec3d) {
        Spinners.remove(pos.conv() to torque.conv())
    }

    fun addPulse(pos: Vec3d, force: Vec3d) {
        Pulses.add(pos.conv() to force.conv())
    }

    fun forceStopThruster(pos: BlockPos) {
        Thrusters.removeAll { it.first == pos }
    }



    companion object {
        fun getOrCreate(ship: ServerShip): TournamentShipControl {
            return ship.getAttachment<TournamentShipControl>()
                ?: TournamentShipControl().also { ship.saveAttachment(it) }
        }

        private val forcePerBalloon get() = TournamentConfig.SERVER.BalloonPower * -GRAVITY

        private const val GRAVITY = -10.0
    }
}
