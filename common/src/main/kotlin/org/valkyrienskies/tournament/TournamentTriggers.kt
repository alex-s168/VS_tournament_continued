package org.valkyrienskies.tournament

import org.valkyrienskies.tournament.mixin.advancements.MixinCriteriaTriggers
import org.valkyrienskies.tournament.trigger.ShipAssemblyTrigger
import net.minecraft.advancements.CriterionTrigger;

object TournamentTriggers {

    val SHIP_ASSEMBLY_TRIGGER = reg(ShipAssemblyTrigger())

    private fun <T: CriterionTrigger<?>> reg (trigger: T): T {
        (MixinCriteriaTriggers.getCriteria() as HashMap)[trigger.getId()] = trigger
        return trigger
    }

}