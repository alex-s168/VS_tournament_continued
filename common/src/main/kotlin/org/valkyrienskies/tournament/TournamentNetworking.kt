package org.valkyrienskies.tournament

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
        val fuel: String?
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

    fun register() {
        // TODO after vs update
        // with(vsCore.simplePacketNetworking) {
        ShipFuelTypeChange::class.register()
        // }

        // TODO after vs update
        // with(vsCore.simplePacketNetworking) {
        ShipFuelTypeChange::class.registerClientHandler {
            TournamentShips.Client[it.ship].fuelType = it.fuelFuel()
        }
        // }
    }
}