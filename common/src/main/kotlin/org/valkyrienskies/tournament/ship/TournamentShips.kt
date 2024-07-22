package org.valkyrienskies.tournament.ship

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.common.util.concurrent.AtomicDouble
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.*
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.world.properties.DimensionId
import org.valkyrienskies.core.impl.game.ships.PhysShipImpl
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.tournament.*
import org.valkyrienskies.tournament.blockentity.PropellerBlockEntity
import org.valkyrienskies.tournament.util.extension.toBlock
import org.valkyrienskies.tournament.util.extension.toDimensionKey
import org.valkyrienskies.tournament.util.extension.toDouble
import org.valkyrienskies.tournament.util.helper.Helper3d
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
class TournamentShips: ShipForcesInducer {

    var level: DimensionId = "minecraft:overworld"
        private set

    data class ThrusterData(
        val pos: Vector3i,
        val force: Vector3d,
        val mult: Double,
        var submerged: Boolean
    )

    data class ThrusterDataV2(
        val dir: Vector3d,
        // for normal thruster between 0 and 15 * max tier
        @Volatile
        var throttle: Float,
        @Volatile
        var submerged: Boolean,
        @Volatile
        var lastPower: Float,
    )

    val thrusters =
        CopyOnWriteArrayList<ThrusterData>()

    val thrustersV2 =
        ConcurrentHashMap<Vector3d, ThrusterDataV2>()

    private val balloons =
        CopyOnWriteArrayList<Pair<Vector3i, Double>>()

    private val spinners =
        CopyOnWriteArrayList<Pair<Vector3i, Vector3d>>()

    private val pulses =
        CopyOnWriteArrayList<Pair<Vector3d, Vector3d>>()

    @Volatile
    var fuelType: FuelType? =
        null

    @Volatile
    var fuelCount = 0.0f

    @Volatile
    var fuelCap = 0.0f

    fun useFuel(count: Float): FuelType? =
        if (fuelCount > 0) {
            fuelCount -= count
            fuelType
        } else {
            fuelType = null
            null
        }

    fun useFuelThrottle(throttle: Float, mult: Int = 1): Float =
        fuelType?.let {
            useFuel(it.calcBurnRate(throttle) * mult)
            it.calcPower(throttle)
        } ?: 0.0f

    data class PropellerData(
        val pos: Vector3i,
        val force: Vector3d,
        var speed: AtomicDouble,
        var touchingWater: Boolean
    )

    private val propellers =
        CopyOnWriteArrayList<PropellerData>()

    @JsonIgnore
    private var ticker: TickScheduler.Ticking? = null

    private var lastFuelType = fuelType
    override fun applyForces(physShip: PhysShip) {
        physShip as PhysShipImpl

        if (fuelType != lastFuelType) {
            TournamentNetworking.ShipFuelTypeChange(
                physShip.id,
                TournamentFuelManager.getKey(fuelType)
            ).send()
            lastFuelType = fuelType
        }

        if (ticker == null) {
            ticker = TickScheduler.serverTickPerm(::tickfn)
        }

        val vel = physShip.poseVel.vel

        val notShutOff = TournamentConfig.SERVER.thrusterShutoffSpeed == -1.0 ||
                         physShip.poseVel.vel.length() < TournamentConfig.SERVER.thrusterShutoffSpeed

        thrustersV2.forEach { (pos, t) ->
            if (t.submerged) {
                t.lastPower = 0.0f
                return@forEach
            }

            val force = useFuelThrottle(t.throttle)

            if (force == 0.0f || !force.isFinite() || !notShutOff) {
                t.lastPower = 0.0f
                return@forEach
            }

            t.lastPower = force

            val tForce = physShip.transform.shipToWorld.transformDirection(t.dir, Vector3d())
            tForce.mul(force.toDouble())
            val tPos = pos.add(0.5, 0.5, 0.5, Vector3d()).sub(physShip.transform.positionInShip)

            physShip.applyInvariantForceToPos(tForce, tPos)
        }

        thrusters.forEach { data ->
            val (pos, force, tier, submerged) = data

            if (submerged) {
                return@forEach
            }

            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d())
            val tPos = pos.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)

            if (force.isFinite && notShutOff) {
                physShip.applyInvariantForceToPos(tForce.mul(TournamentConfig.SERVER.thrusterSpeed * tier), tPos)
            }
        }

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
                    0.0
                ),
                tPos
            )
        }

        spinners.forEach {
            val (_, torque) = it    // TODO: WATF

            val torqueGlobal = physShip.transform.shipToWorldRotation.transform(torque, Vector3d())

            physShip.applyInvariantTorque(torqueGlobal.mul(TournamentConfig.SERVER.spinnerSpeed))
        }

        pulses.forEach {
            val (pos, force) = it
            val tPos = pos.add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            val tForce = physShip.transform.worldToShip.transformDirection(force)

            physShip.applyRotDependentForceToPos(tForce, tPos)
        }
        pulses.clear()

        propellers.forEach {
            val (pos, force, speed, touchingWater) = it

            if (!touchingWater) {
                return@forEach
            }

            val tPos = pos.toDouble().add(0.5, 0.5, 0.5).sub(physShip.transform.positionInShip)
            val tForce = physShip.transform.shipToWorld.transformDirection(force, Vector3d())

            physShip.applyInvariantForceToPos(tForce.mul(speed.get()), tPos)
        }
    }

    private fun tickfn(server: MinecraftServer) {
        val lvl = server.getLevel(level.toDimensionKey()) ?: return

        if (fuelCount > fuelCap)
            fuelCount = fuelCap

        if (fuelCount <= 0) {
            fuelCount = 0.0f
            fuelType = null
        }

        thrusters.forEach { t ->
            val water = lvl.isWaterAt(
                Helper3d
                    .convertShipToWorldSpace(lvl, t.pos.toDouble())
                    .toBlock()
            )
            t.submerged = water
        }

        thrustersV2.forEach { (pos, t) ->
            val water = lvl.isWaterAt(
                Helper3d
                    .convertShipToWorldSpace(lvl, Vector3d(pos))
                    .toBlock()
            )
            t.submerged = water
        }

        propellers.forEach { p ->
            // TODO: check if water is on the outside if big propeller
            val water = lvl.isWaterAt(
                Helper3d
                    .convertShipToWorldSpace(lvl, p.pos.toDouble())
                    .toBlock()
            )
            p.touchingWater = water

            val be = lvl.getBlockEntity(
                p.pos.toBlockPos()
            ) as PropellerBlockEntity<*>?

            if (be != null) {
                p.speed.set(be.speed)
            }
        }
    }

    fun addThrusterV2(
        pos: BlockPos,
        throttle: Float,
        dir: Vector3d
    ) {
        thrustersV2[pos.toJOMLD()] = ThrusterDataV2(dir, throttle, false, 0.0f)
    }

    fun thrusterLastPowerV2(pos: BlockPos) =
        thrustersV2[pos.toJOMLD()]!!.lastPower

    fun addThrustersV1(
        list: Iterable<Triple<Vector3i, Vector3d, Double>>
    ) {
        list.forEach { (pos, force, tier) ->
            thrusters += ThrusterData(pos, force, tier, false)
        }
    }

    fun stopThruster(
        pos: BlockPos
    ) {
        thrusters.removeIf { pos.toJOML() == it.pos }
        thrustersV2.remove(pos.toJOMLD())
    }

    fun addBalloon(pos: BlockPos, pow: Double) {
        balloons.add(pos.toJOML() to pow)
    }

    fun addBalloons(list: Iterable<Pair<Vector3i, Double>>) {
        balloons.addAll(list)
    }

    fun removeBalloon(pos: BlockPos) {
        balloons.removeAll { it.first == pos.toJOML() }
    }

    fun addSpinner(pos: Vector3i, torque: Vector3d) {
        spinners.add(pos to torque)
    }

    fun addSpinners(list: Iterable<Pair<Vector3i, Vector3d>>) {
        spinners.addAll(list)
    }

    fun removeSpinner(pos: Vector3i) {
        spinners.removeAll { it.first == pos }
    }

    fun addPulse(pos: Vector3d, force: Vector3d) {
        pulses.add(pos to force)
    }

    fun addPulses(list: Iterable<Pair<Vector3d, Vector3d>>) {
        pulses.addAll(list)
    }

    fun addPropeller(pos: Vector3i, force: Vector3d) {
        propellers += PropellerData(pos, force, AtomicDouble(), false)
    }

    fun removePropeller(pos: Vector3i) {
        propellers.removeIf { it.pos == pos }
    }

    companion object {
        fun getOrCreate(ship: ServerShip, level: DimensionId) =
            ship.getAttachment<TournamentShips>()
                ?: TournamentShips().also {
                    it.level = level
                    ship.saveAttachment(it)
                }

        fun getOrCreate(ship: ServerShip): TournamentShips =
            getOrCreate(ship, ship.chunkClaimDimension)

        fun get(level: Level, pos: BlockPos)  =
            ((level.getShipObjectManagingPos(pos)
                ?: level.getShipManagingPos(pos))
                    as? ServerShip)?.let { getOrCreate(it) }
    }

    @Environment(EnvType.CLIENT)
    object Client {
        private val ships = mutableMapOf<ShipId, Data>()

        operator fun get(ship: ShipId) =
            ships.computeIfAbsent(ship) {
                Data(null)
            }

        operator fun get(ship: Ship) =
            get(ship.id)

        data class Data(
            @Volatile
            var fuelType: FuelType?
        )
    }
}