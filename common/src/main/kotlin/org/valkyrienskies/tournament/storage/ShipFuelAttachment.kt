package org.valkyrienskies.tournament.storage

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.common.util.concurrent.AtomicDouble
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
    isGetterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY,
    setterVisibility = JsonAutoDetect.Visibility.PUBLIC_ONLY
)
class ShipFuelAttachment(
    @JsonIgnore
    override var ship: ServerShip?
) : ServerShipUser {

    var fuelSources = ArrayList<AtomicDouble>()
    var fuel = 0.0

    fun getTotalFuelValue(): Double {
        return fuel + fuelSources.sumOf { it.get() }
    }

    companion object {
        fun getOrCreate(ship: ServerShip): ShipFuelAttachment {
            return ship.getAttachment<ShipFuelAttachment>()
                ?: ShipFuelAttachment(ship).also { ship.saveAttachment(it) }
        }
    }

}