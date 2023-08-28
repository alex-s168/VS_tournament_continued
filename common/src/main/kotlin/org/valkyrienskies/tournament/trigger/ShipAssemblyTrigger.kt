package org.valkyrienskies.tournament.trigger

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
        entityPredicate: EntityPredicate.Composite,
        conditionsParser: DeserializationContext
    ): TriggerInstance
    {
        val ints = Ints.fromJson(json["ship_size"])
        return TriggerInstance(entityPredicate, ints)
    }

    fun trigger(player: ServerPlayer, shipSize: Int) {
        this.trigger(player) { triggerInstance: TriggerInstance ->
            triggerInstance.matches(shipSize)
        }
    }


    class TriggerInstance(
        composite: EntityPredicate.Composite,
        private val shipSize: Ints
    ): AbstractCriterionTriggerInstance(ID, composite)
    {
        override fun serializeToJson(context: SerializationContext): JsonObject {
            val jsonObject = super.serializeToJson(context)
            jsonObject.add("ship_size", shipSize.serializeToJson())
            return jsonObject
        }

        fun matches(shipSize: Int): Boolean = this.shipSize.matches(shipSize)

        companion object {
            fun shipAssembled(shipSize: Ints): TriggerInstance {
                return TriggerInstance(EntityPredicate.Composite.ANY, shipSize)
            }
        }
    }

}
