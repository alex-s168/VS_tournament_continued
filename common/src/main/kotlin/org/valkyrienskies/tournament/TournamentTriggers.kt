package org.valkyrienskies.tournament

import org.valkyrienskies.tournament.mixin.advancements.MixinCriteriaTriggers
import org.valkyrienskies.tournament.trigger.ShipAssemblyTrigger

object TournamentTriggers {

    val SHIP_ASSEMBLY_TRIGGER = MixinCriteriaTriggers.registerInvoker(ShipAssemblyTrigger()) as ShipAssemblyTrigger

}