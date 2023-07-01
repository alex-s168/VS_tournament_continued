package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import org.valkyrienskies.tournament.blocks.*
import org.valkyrienskies.tournament.blocks.explosive.*
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object TournamentBlocks {
    private val BLOCKS = DeferredRegister.create(TournamentMod.MOD_ID, Registry.BLOCK_REGISTRY)

    val SHIP_ASSEMBLER           = BLOCKS.register("ship_assembler", ::ShipAssemblerBlock)
    val BALLAST                  = BLOCKS.register("ballast", ::BallastBlock)
    val BALLOON                  = BLOCKS.register("balloon", ::BalloonBlock)
    val THRUSTER                 = BLOCKS.register("thruster", ::ThrusterBlock)
    val THRUSTER_TINY            = BLOCKS.register("tiny_thruster", ::TinyThrusterBlock)
    val SPINNER                  = BLOCKS.register("spinner", ::SpinnerBlock)
    val SEAT                     = BLOCKS.register("seat", ::SeatBlock)
    val ROPE_HOOK                = BLOCKS.register("rope_hook", ::RopeHookBlock)
    val SENSOR                   = BLOCKS.register("sensor", ::SensorBlock)

    // EXPLOSIVES:
    val EXPLOSIVE_INSTANT_SMALL  = BLOCKS.register("explosive_instant_small", ::ExplosiveInstantBlockSmall)
    val EXPLOSIVE_INSTANT_MEDIUM = BLOCKS.register("explosive_instant_medium", ::ExplosiveInstantBlockMedium)
    val EXPLOSIVE_INSTANT_LARGE  = BLOCKS.register("explosive_instant_large", ::ExplosiveInstantBlockLarge)

    val EXPLOSIVE_STAGED_SMALL   = BLOCKS.register("explosive_staged_small", ::ExplosiveStagedBlockSmall)

    fun register() {
        BLOCKS.applyAll()
    }

    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.forEach {
            items.register(it.name) { BlockItem(it.get(), Item.Properties().tab(TournamentItems.TAB)) }
        }
    }

}
