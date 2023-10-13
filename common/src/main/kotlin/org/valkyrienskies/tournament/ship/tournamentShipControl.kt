package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.ShipForcesInducer
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toBlockPos
import java.util.concurrent.CopyOnWriteArrayList

// THIS CLASS IS JUST FOR COMPAT WITH OLD TOURNAMENT VERSIONS!

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
// for compat only!!
class tournamentShipControl : ShipForcesInducer {

    @JsonIgnore
    var ship: ServerShip? = null

    private var extraForce = 0.0
    private var physConsumption = 0f
    var power = 0.0
    var consumed = 0f
        private set

    private val Balloons = mutableListOf<Pair<Vector3i, Double>>()
    private val Spinners = mutableListOf<Pair<Vector3i, Vector3d>>()
    private val Thrusters = mutableListOf<Triple<Vector3i, Vector3d, Double>>()
    private val Pulses = CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()

    override fun applyForces(physShip: PhysShip) {
        if (ship == null) return
        physShip as PhysShipImpl

        println("Converting old ship controller from ship ${ship!!.id} to new one")

        Balloons.forEach { (pos, force) ->
            BalloonShipControl.getOrCreate(ship!!).addBalloon(pos.toBlockPos(), force)
        }

        Thrusters.forEach { (pos, dir, strength) ->
            ThrusterShipControl.getOrCreate(ship!!).addThruster(pos.toBlockPos(), strength, dir)
        }

        Spinners.forEach { (pos, dir) ->
            SpinnerShipControl.getOrCreate(ship!!).addSpinner(pos, dir)
        }

        Pulses.forEach {(pos, force) ->
            PulseShipControl.getOrCreate(ship!!).addPulse(pos, force)
        }

        ship!!.saveAttachment(tournamentShipControl::class.java, null)
    }

}