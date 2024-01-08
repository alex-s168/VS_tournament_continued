package org.valkyrienskies.tournament.util.extension

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation

fun String.toResourceLocation() =
    ResourceLocation(this)

fun ResourceLocation.toDimensionKey() =
    ResourceKey.create(Registry.DIMENSION_REGISTRY, this)