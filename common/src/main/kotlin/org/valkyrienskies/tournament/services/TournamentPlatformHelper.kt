package org.valkyrienskies.tournament.services

import net.minecraft.client.resources.model.BakedModel
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import java.util.*
import java.util.function.Supplier

interface TournamentPlatformHelper {

    fun loadBakedModel(modelLocation: ResourceLocation): BakedModel?

    companion object {
        fun get() = ServiceLoader
            .load(TournamentPlatformHelper::class.java)
            .findFirst()
            .get()
    }

}