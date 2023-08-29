package org.valkyrienskies.tournament

import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.tournament.TournamentBlocks.SHIP_ASSEMBLER


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
    }

    @JvmStatic
    fun initClient() {

    }
}
