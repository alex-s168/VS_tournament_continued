package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import net.minecraft.core.BlockPos
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.PhysShip
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ShipForcesInducer
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
class BalloonShipControl : ShipForcesInducer {


    private val balloons = CopyOnWriteArrayList<Pair<Vector3i, Double>>()

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        val vel = physShip.poseVel.vel

        balloons.forEach {
            val (pos, pow) = it

            val tPos = Vector3d(pos).add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            val tHeight = physShip.transform.positionInWorld.y()
            var tPValue = TournamentConfig.SERVER.balloonBaseHeight - ((tHeight * tHeight) / 1000.0)

            if (vel.y() > 10.0)    {
                tPValue = (-vel.y() * 0.25)
                tPValue -= (vel.y() * 0.25)
            }
            if(tPValue <= 0){
                tPValue = 0.0
            }
            physShip.applyInvariantForceToPos(
                    Vector3d(
                            0.0,
                            (pow + 1.0) * TournamentConfig.SERVER.balloonPower * tPValue,
                            0.0),
                    tPos
            )
        }
    }

    fun addBalloon(pos: BlockPos, pow: Double) {
        balloons.add(pos.toJOML() to pow)
    }

    fun removeBalloon(pos: BlockPos, pow: Double) {
        balloons.remove(pos.toJOML() to pow)
    }

    companion object {
        fun getOrCreate(ship: ServerShip): BalloonShipControl {
            return ship.getAttachment<BalloonShipControl>()
                ?: BalloonShipControl().also { ship.saveAttachment(it) }
        }
    }

}