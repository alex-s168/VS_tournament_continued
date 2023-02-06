package org.valkyrienskies.tournament

import net.minecraft.core.Registry
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import org.valkyrienskies.tournament.block.*
import org.valkyrienskies.tournament.registry.DeferredRegister

@Suppress("unused")
object TournamentBlocks {
    private val BLOCKS = DeferredRegister.create(TournamentMod.MOD_ID, Registry.BLOCK_REGISTRY)

    //todo: floater texture
    val FLOATER             = BLOCKS.register("floater", ::FloaterBlock)

    fun register() {
        BLOCKS.applyAll()
    }

    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.forEach {
            items.register(it.name) { BlockItem(it.get(), Item.Properties().tab(TournamentItems.TAB)) }
        }
    }

}
