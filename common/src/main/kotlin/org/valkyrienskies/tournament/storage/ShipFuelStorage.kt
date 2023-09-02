package org.valkyrienskies.tournament.storage

import com.google.common.util.concurrent.AtomicDouble
import org.valkyrienskies.core.api.ships.ServerShip
import java.util.concurrent.CopyOnWriteArrayList

object ShipFuelStorage {

    val ships = HashMap<ServerShip, CopyOnWriteArrayList<AtomicDouble>>()

    fun getFuel(ship: ServerShip): Double {
        return ships[ship]?.sumOf { it.get() } ?: 0.0
    }

}