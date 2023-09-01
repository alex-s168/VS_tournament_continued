package org.valkyrienskies.tournament

import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.features.OreFeatures
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration

object TournamentOres {

    val ORE_PHYSICS =
        FeatureUtils.register(
            "vs_tournament:ore_shitonium",
            Feature.ORE,
            OreConfiguration(OreFeatures.ORE_IRON_TARGET_LIST, 9)
        )

}