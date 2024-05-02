package org.valkyrienskies.tournament.advancements

import com.google.gson.JsonObject
import net.minecraft.advancements.critereon.*
import net.minecraft.advancements.critereon.MinMaxBounds.Ints
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer

class ShipAssemblyTrigger
    : SimpleCriterionTrigger<ShipAssemblyTrigger.TriggerInstance>()
{
    companion object {
        val ID = ResourceLocation("vs_tournament", "ship_assembled")
    }

    override fun getId(): ResourceLocation = ID

    override fun createInstance(
        json: JsonObject,
        predicate: ContextAwarePredicate,
        conditionsParser: DeserializationContext
    ): TriggerInstance
    {
        val ints = Ints.fromJson(json["ship_size"])
        return TriggerInstance(predicate, ints)
    }

    fun trigger(player: ServerPlayer, shipSize: Int) =
        trigger(player) { triggerInstance: TriggerInstance ->
            triggerInstance.matches(shipSize)
        }

    class TriggerInstance(
        predicate: ContextAwarePredicate,
        private val shipSize: Ints
    ): AbstractCriterionTriggerInstance(ID, predicate)
    {
        override fun serializeToJson(context: SerializationContext): JsonObject {
            val jsonObject = super.serializeToJson(context)
            jsonObject.add("ship_size", shipSize.serializeToJson())
            return jsonObject
        }

        fun matches(shipSize: Int): Boolean = this.shipSize.matches(shipSize)
    }

}
