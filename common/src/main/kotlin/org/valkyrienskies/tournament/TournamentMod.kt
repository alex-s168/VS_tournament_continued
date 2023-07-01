package org.valkyrienskies.tournament

import org.valkyrienskies.core.impl.config.VSConfigClass


object TournamentMod {
    const val MOD_ID = "vs_tournament"

    @JvmStatic
    fun init() {
        VSConfigClass.registerConfig("vs_tournament", TournamentConfig::class.java)
        TournamentBlocks.register()
        TournamentBlockEntities.register()
        TournamentItems.register()
        TournamentWeights.register()
    }

    @JvmStatic
    fun initClient() {

    }
}
