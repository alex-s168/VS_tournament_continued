package org.valkyrienskies.tournament

import net.minecraft.advancements.CriteriaTriggers
import org.valkyrienskies.tournament.mixin.advancements.MixinCriteriaTriggers
import org.valkyrienskies.tournament.trigger.ShipAssemblyTrigger
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.resources.ResourceLocation

object TournamentTriggers {

    private val all = ArrayList<CriterionTrigger<*>>()

    val SHIP_ASSEMBLY_TRIGGER = reg(ShipAssemblyTrigger())

    private fun <T: CriterionTrigger<*>> reg (trigger: T): T {
        all.add(trigger)
        return trigger
    }

    fun init() {
        println("pre:")
        CriteriaTriggers.all().forEach {
            println(it.id)
        }
        all.forEach {
            (MixinCriteriaTriggers.getCriteria() as HashMap<ResourceLocation, CriterionTrigger<*>>)[it.id] = it
        }
        println("post:")
        CriteriaTriggers.all().forEach {
            println(it.id)
        }
    }

}