package org.valkyrienskies.tournament

import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.tournament.ship.*

object TournamentMod {
    const val MOD_ID = "vs_tournament"

    @JvmStatic
    fun init() {
        VSConfigClass.registerConfig("vs_tournament", TournamentConfig::class.java)
        TournamentBlocks.register()
        TournamentBlockEntities.register()
        TournamentItems.register()
        // TournamentWeights.register()
        TournamentTriggers.init()
        TournamentOres.register()

        var removed = false
        if (TournamentConfig.SERVER.removeAllAttachments) {
            VSEvents.tickEndEvent.on { e ->
                if (removed) return@on
                e.world.allShips.forEach { ship ->
                    ship.saveAttachment<BalloonShipControl>(null)
                    ship.saveAttachment<PulseShipControl>(null)
                    ship.saveAttachment<SimpleShipControl>(null)
                    ship.saveAttachment<SpinnerShipControl>(null)
                    ship.saveAttachment<ThrusterShipControl>(null)
                    ship.saveAttachment<tournamentShipControl>(null)
                }
                println("Removed all attachments from all ships!")
                removed = true
            }
        }
    }

    @JvmStatic
    fun initClient() {

    }
}
