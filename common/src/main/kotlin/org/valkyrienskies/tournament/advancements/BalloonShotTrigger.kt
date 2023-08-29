package org.valkyrienskies.tournament.advancements

import com.google.gson.JsonObject
import net.minecraft.advancements.critereon.*
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

class BalloonShotTrigger
    : SimpleCriterionTrigger<BalloonShotTrigger.TriggerInstance>()
{
    companion object {
        val ID = ResourceLocation("vs_tournament", "balloon_shot")
    }

    override fun getId(): ResourceLocation = ID

    override fun createInstance(
        json: JsonObject,
        entityPredicate: EntityPredicate.Composite,
        conditionsParser: DeserializationContext
    ): TriggerInstance
    {
        return TriggerInstance(entityPredicate)
    }

    fun trigger(player: ServerPlayer) {
        this.trigger(player) { true }
    }

    class TriggerInstance(
        composite: EntityPredicate.Composite
    ): AbstractCriterionTriggerInstance(ID, composite)

}