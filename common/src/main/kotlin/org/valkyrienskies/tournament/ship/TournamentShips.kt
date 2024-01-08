package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TickScheduler
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.util.extension.toDimensionKey
import org.valkyrienskies.tournament.util.extension.toDouble
import org.valkyrienskies.tournament.util.extension.toResourceLocation
import java.util.concurrent.ConcurrentHashMap

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class TournamentShips(
    val level: ResourceKey<Level>
): ShipForcesInducer {

    data class ThrusterData(
        val force: Vector3d,
        val mult: Double,
        var submerged: Boolean,
        var level: ResourceKey<Level>? = null
    )

    private val thrusters =
        ConcurrentHashMap<Vector3i, ThrusterData>()

    @JsonIgnore
    private var hasTicker = false

    @JsonIgnore
    var overworld: ResourceKey<Level>? =
        null

    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        if (!hasTicker) {
            TickScheduler.serverTickPerm { server ->
                overworld = server.overworld().dimension()
                val lvl = server.getLevel(level)
                if (lvl == null) {
                    print("Level $level is null!")
                    return@serverTickPerm
                }
                thrusters.forEach { (pos, d) ->
                    val water = lvl.isWaterAt(pos.toBlockPos())
                    d.submerged = water
                }
            }
            hasTicker = true
        }

        thrusters.forEach { (pos, data) ->
            val (force, tier, submerged) = data

            if (submerged) {
                return@forEach
            }

            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d())
            val tPos = pos.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

            if (force.isFinite && (
                        TournamentConfig.SERVER.thrusterShutoffSpeed == -1
                                || physShip.poseVel.vel.length() < TournamentConfig.SERVER.thrusterShutoffSpeed
                        )
            ) {
                physShip.applyInvariantForceToPos(tForce.mul(TournamentConfig.SERVER.thrusterSpeed * tier), tPos)
            }
        }
    }

    fun addThruster(
        pos: BlockPos,
        tier: Double,
        force: Vector3d
    ) {
        thrusters[pos.toJOML()] = ThrusterData(force, tier, false)
    }

    fun addThrusters(
        list: Iterable<Triple<Vector3i, Vector3d, Double>>
    ) {
        list.forEach { (pos, force, tier) ->
            thrusters[pos] = ThrusterData(force, tier, false)
        }
    }

    fun stopThruster(
        pos: BlockPos
    ) {
        thrusters.remove(pos.toJOML())
    }

    companion object {
        fun getOrCreate(ship: ServerShip, level: ResourceKey<Level>) =
            ship.getAttachment<TournamentShips>()
                ?: TournamentShips(level).also { ship.saveAttachment(it) }

        fun getOrCreate(ship: ServerShip): TournamentShips {
            val key = ship.chunkClaimDimension.toResourceLocation().toDimensionKey()
            return getOrCreate(ship, key)
        }
    }
}