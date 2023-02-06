package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import de.m_marvin.univec.impl.*
import net.minecraft.core.BlockPos
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.core.impl.api.ShipForcesInducer
import org.valkyrienskies.core.impl.api.Ticked
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.TournamentConfig
import java.util.concurrent.CopyOnWriteArrayList

//TODO: cleaner

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
    private val Spinners = mutableListOf<Pair<Vec3i, Vec3d>>()
    private val Thrusters = mutableListOf<Triple<Vec3i, Vec3d, Double>>()
    private val Pulses = CopyOnWriteArrayList<Pair<Vec3d, Vec3d>>()

    var consumed = 0f
        private set

    override fun applyForces(physShip: PhysShip) {
        if (ship == null) return
        physShip as PhysShipImpl

        // if moving to fast or is to high dont apply a force
        if (physShip.poseVel.vel.y() < 2 || physShip.transform.positionInWorld.y() > TournamentConfig.SERVER.BaseHeight)    {
            BalloonsPower = 0.0
        }

        println(physShip.transform.positionInWorld.y())

        if(BalloonsPower != 0.0) {

            var centerOfLift = weightedCenterOfLift.div(BalloonsPower)
            physShip.applyInvariantForceToPos(
                Vec3d(
                    0.0,
                    (BalloonsPower),
                    0.0
                ).writeTo(Vector3d()), centerOfLift.writeTo(Vector3d())
            )
        }

        Spinners.forEach {
            val (pos, torque) = it

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque.writeTo(Vector3d()), Vector3d())

            physShip.applyInvariantTorque(torqueGlobal.mul(TournamentConfig.SERVER.SpinnerSpeed ))

        }

        Thrusters.forEach {
            val (pos, force, tier) = it

            val tForce = physShip.transform.shipToWorld.transformDirection(force.writeTo(Vector3d()), Vector3d()) //.shipToWorld.transformDirection(force, Vector3d())
            val tPos = Vec3d(pos).add(0.5, 0.5, 0.5).sub(Vec3d().readFrom(physShip.transform.positionInShip))

            if (force.isFinite && physShip.poseVel.vel.length() < 50) {
                physShip.applyInvariantForceToPos(tForce.mul(TournamentConfig.SERVER.ThrusterSpeed * tier, Vector3d()), tPos)
            }
        }

        //Pulse Gun
        Pulses.forEach {
            val (pos, force) = it
            val tPos = Vector3d(pos).add( 0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            val tForce = physShip.transform.worldToShip.transformDirection(force, Vector3d())

            physShip.applyRotDependentForceToPos(tForce, tPos)
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
//        balloonTick()
    }

    private fun deleteIfEmpty() {
        if (balloons == 0) {
            ship?.saveAttachment<TournamentShipControl>(null)
        }
    }


    fun addBalloon(pos: BlockPos, pow: Double) {
        weightedCenterOfLift = weightedCenterOfLift.add(pos.toJOMLD().mul(pow))
        BalloonsPower += pow
    }

    fun removeBalloon(pos: BlockPos, pow: Double) {
        weightedCenterOfLift = weightedCenterOfLift.sub(pos.toJOMLD().mul(pow))
        BalloonsPower -= pow
    }

    fun addThruster(pos: BlockPos, tier: Double, force: Vector3dc) {
        Thrusters.add(Triple(pos.toJOML(), force, tier))
    }
    fun removeThruster(pos: BlockPos, tier: Double, force: Vector3dc) {
        Thrusters.remove(Triple(pos.toJOML(), force, tier))
    }

    fun addSpinner(pos: Vector3ic, torque: Vector3dc) {
        Spinners.add(pos to torque)
    }
    fun removeSpinner(pos: Vector3ic, torque: Vector3dc) {
        Spinners.remove(pos to torque)
    }

    fun addPulse(pos: Vector3d, force: Vector3d) {
        Pulses.add(pos to force)
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
