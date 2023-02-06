package org.valkyrienskies.Tournament

import org.valkyrienskies.core.impl.config.VSConfigClass


object TournamentMod {
    const val MOD_ID = "vs_tournament"

    @JvmStatic
    fun init() {
        TournamentBlocks.register()
        TournamentBlockEntities.register()
        TournamentItems.register()
        TournamentScreens.register()
        TournamentEntities.register()
        TournamentWeights.register()
        VSConfigClass.registerConfig("vs_tournament", TournamentConfig::class.java)
    }

    @JvmStatic
    fun initClient() {
        TournamentClientScreens.register()
    }
}
