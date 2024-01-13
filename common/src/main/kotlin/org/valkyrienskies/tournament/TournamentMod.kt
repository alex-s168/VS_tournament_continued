package org.valkyrienskies.tournament

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

                val balloonShipCtrl = ship.getAttachment<BalloonShipControl>()
                if (balloonShipCtrl != null) {
                    TournamentShips.getOrCreate(ship).addBalloons(balloonShipCtrl.balloons)
                    ship.saveAttachment<BalloonShipControl>(null)
                }

                val spinnerShipCtrl = ship.getAttachment<SpinnerShipControl>()
                if (spinnerShipCtrl != null) {
                    TournamentShips.getOrCreate(ship).addSpinners(spinnerShipCtrl.spinners.with(spinnerShipCtrl.Spinners))
                    ship.saveAttachment<SpinnerShipControl>(null)
                }

                val pulsesShipCtrl = ship.getAttachment<PulseShipControl>()
                if (pulsesShipCtrl != null) {
                    TournamentShips.getOrCreate(ship).addPulses(pulsesShipCtrl.pulses.with(pulsesShipCtrl.Pulses))
                    ship.saveAttachment<PulseShipControl>(null)
                }
            }
        }


    }

    @JvmStatic
    fun initClient() {

    }
}
