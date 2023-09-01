package org.valkyrienskies.tournament.util

import net.minecraft.world.item.ItemStack
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.TournamentTags

fun ItemStack.getThrusterFuelValue(): Int? {
    if (this.`is`(TournamentTags.THRUSTER_FUEL_POOR)) return TournamentConfig.SERVER.fuelValuePoor
    if (this.`is`(TournamentTags.THRUSTER_FUEL_GOOD)) return TournamentConfig.SERVER.fuelValueGood
    if (this.`is`(TournamentTags.THRUSTER_FUEL_RICH)) return TournamentConfig.SERVER.fuelValueRich

    return null
}