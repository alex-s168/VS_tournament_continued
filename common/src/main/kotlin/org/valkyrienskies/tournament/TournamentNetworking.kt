package org.valkyrienskies.tournament

import blitz.collections.remove
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.impl.networking.simple.SimplePacket
import org.valkyrienskies.core.impl.networking.simple.register
import org.valkyrienskies.core.impl.networking.simple.registerClientHandler
import org.valkyrienskies.core.impl.networking.simple.sendToAllClients
import org.valkyrienskies.tournament.ship.TournamentShips

object TournamentNetworking {
    data class ShipFuelTypeChange(
        val ship: ShipId,
        val fuel: String?,
    ) : SimplePacket {
        constructor(
            ship: ShipId,
            fuel: ResourceLocation?
        ): this(ship, fuel?.toString())

        fun fuelKey() =
            fuel?.let(::ResourceLocation)

        fun fuelFuel() =
            fuelKey()?.let(TournamentFuelManager.fuels::get)

        fun send() {
            // TODO after vs update
            // with(vsCore.simplePacketNetworking) {
            this.sendToAllClients()
            // }
        }
    }

    data class ShipThrusterChange(
        val ship: ShipId,
        val posX: Int,
        val posY: Int,
        val posZ: Int,
        val throttle: Float,
    ): SimplePacket {
        fun removed() =
            throttle < 0.0f

        fun send() {
            // TODO after vs update
            // with(vsCore.simplePacketNetworking) {
            this.sendToAllClients()
            // }
        }
    }

    fun register() {
        // TODO after vs update
        // with(vsCore.simplePacketNetworking) {
        ShipFuelTypeChange::class.register()
        ShipThrusterChange::class.register()
        // }

        // TODO after vs update
        // with(vsCore.simplePacketNetworking) {
        ShipFuelTypeChange::class.registerClientHandler {
            val client = TournamentShips.Client[it.ship]
            client.fuelType.set(it.fuelFuel())
        }
        ShipThrusterChange::class.registerClientHandler {
            val client = TournamentShips.Client[it.ship]
            val idx = client.thrusters.index(BlockPos(it.posX, it.posY, it.posZ))
            if (it.removed()) {
                client.thrusters.remove(idx)
            } else {
                client.thrusters[idx] = TournamentShips.Client.Data.Thruster(it.throttle)
            }
        }
        // }
    }
}