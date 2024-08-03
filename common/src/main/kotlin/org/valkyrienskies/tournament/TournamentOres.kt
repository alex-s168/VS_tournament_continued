package org.valkyrienskies.tournament

import net.minecraft.core.Holder
import net.minecraft.data.worldgen.features.FeatureUtils
import net.minecraft.data.worldgen.features.OreFeatures
import net.minecraft.data.worldgen.placement.PlacementUtils
import net.minecraft.world.level.levelgen.GenerationStep.Decoration
import net.minecraft.world.level.levelgen.VerticalAnchor
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature
import net.minecraft.world.level.levelgen.feature.Feature
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration
import net.minecraft.world.level.levelgen.placement.*

object TournamentOres {

    lateinit var ORE_PHYNITE: Holder<ConfiguredFeature<OreConfiguration, *>>

    fun register() {
        ORE_PHYNITE = FeatureUtils.register(
            "vs_tournament:ore_phynite",
            Feature.ORE,
            OreConfiguration(OreFeatures.ORE_IRON_TARGET_LIST, 9)
        )

        val ores = mutableListOf<Holder<PlacedFeature>>()

        ores += PlacementUtils.register(
            "vs_tournament:ore_phynite_upper", ORE_PHYNITE, commonOrePlacement(
                90, HeightRangePlacement.triangle(
                    VerticalAnchor.absolute(80), VerticalAnchor.absolute(384)
                )
            )
        )
        ores += PlacementUtils.register(
            "vs_tournament:ore_phynite_middle", ORE_PHYNITE, commonOrePlacement(
                10, HeightRangePlacement.triangle(
                    VerticalAnchor.absolute(-24), VerticalAnchor.absolute(56)
                )
            )
        )
        ores += PlacementUtils.register(
            "vs_tournament:ore_phynite_small", ORE_PHYNITE, commonOrePlacement(
                10, HeightRangePlacement.uniform(
                    VerticalAnchor.bottom(), VerticalAnchor.absolute(72)
                )
            )
        )

        TournamentEvents.WorldGenFeatures.defaultOres.on { builder ->
            println("aaaaaa")
            ores.forEach {
                builder.addFeature(Decoration.UNDERGROUND_ORES, it)
            }
        }
    }

    private fun orePlacement(
        placementModifier: PlacementModifier,
        placementModifier2: PlacementModifier
    ): List<PlacementModifier> {
        return listOf(placementModifier, InSquarePlacement.spread(), placementModifier2, BiomeFilter.biome())
    }

    private fun commonOrePlacement(count: Int, heightRange: PlacementModifier): List<PlacementModifier> {
        return orePlacement(CountPlacement.of(count), heightRange)
    }

    private fun rareOrePlacement(chance: Int, heightRange: PlacementModifier): List<PlacementModifier> {
        return orePlacement(RarityFilter.onAverageOnceEvery(chance), heightRange)
    }

}