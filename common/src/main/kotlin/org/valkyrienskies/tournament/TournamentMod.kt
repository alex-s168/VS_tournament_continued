package org.valkyrienskies.tournament

import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.tournament.ship.*
import org.valkyrienskies.tournament.util.extension.with

object TournamentMod {
    const val MOD_ID = "vs_tournament"

    @JvmStatic
    fun init() {
        VSConfigClass.registerConfig("vs_tournament", TournamentConfig::class.java)
        TournamentBlocks.register()
        TournamentBlockEntities.register()
        TournamentItems.register()
        TournamentWeights.register()
        TournamentTriggers.init()
        TournamentOres.register()

        VSEvents.shipLoadEvent.on { e ->
            val ship = e.ship

            if (TournamentConfig.SERVER.removeAllAttachments) {
                ship.saveAttachment<BalloonShipControl>(null)
                ship.saveAttachment<PulseShipControl>(null)
                ship.saveAttachment<SimpleShipControl>(null)
                ship.saveAttachment<SpinnerShipControl>(null)
                ship.saveAttachment<ThrusterShipControl>(null)
                ship.saveAttachment<tournamentShipControl>(null)
                ship.saveAttachment<TournamentShips>(null)
            }
            else {
                val thrusterShipCtrl = ship.getAttachment<ThrusterShipControl>()
                if (thrusterShipCtrl != null) {
                    TournamentShips.getOrCreate(ship).addThrusters(thrusterShipCtrl.Thrusters.with(thrusterShipCtrl.thrusters))
                    ship.saveAttachment<ThrusterShipControl>(null)
                }
            }
        }


    }

    @JvmStatic
    fun initClient() {

    }
}
