package org.valkyrienskies.tournament

import com.github.imifou.jsonschema.module.addon.annotation.JsonSchema

object TournamentConfig {
    @JvmField
    val CLIENT = Client()

    @JvmField
    val SERVER = Server()

    class Client {

        @JsonSchema(description = "Use the particle rope renderer instead of the line rope renderer")
        var particleRopeRenderer = true

    }

    class Server {

        @JsonSchema(description = "Gravity gun enabled")
        var gravityGunEnabled = false

        @JsonSchema(description = "The maximum force a rope can handle before breaking")
        var ropeMaxForce = 1e10

        @JsonSchema(description = "The force a spinner applies to a ship")
        var spinnerSpeed = 4000.0

        @JsonSchema(description = "The force a balloon applies to a ship")
        var balloonPower = 30.0

        @JsonSchema(description = "How much stronger a balloon will get when powered (1.0 is 15x stronger at max power)")
        var balloonAnalogStrength = 1.0

        @JsonSchema(description = "The force multiplier of a balloon (not for \"powered balloon\")")
        var unpoweredBalloonMul = 5.0

        @JsonSchema(description = "Base height of a balloon")
        var balloonBaseHeight = 100.0

        @JsonSchema(description = "The force a thruster applies to a ship")
        var thrusterSpeed = 10000.0

        @JsonSchema(description = "The weight of a ballast when not redstone powered")
        var ballastWeight = 10000.0

        @JsonSchema(description = "The weight of a ballast when redstone powered")
        var ballastNoWeight = 10.0

        @JsonSchema(description = "The force the pulse gun applies to a ship")
        var pulseGunForce = 300.0

        @JsonSchema(description = "Maximum distance a sensor can detect a ship from")
        var sensorDistance = 5.0

        @JsonSchema(description = "The speed at which the thruster will stop applying force")
        var thrusterShutoffSpeed = 50

        @JsonSchema(description = "The list of blocks that don't get assembled by the ship assembler")
        var blockBlacklist = setOf(
            "minecraft:dirt",
            "minecraft:grass_block",
            "minecraft:grass_path",
            "minecraft:stone",
            "minecraft:bedrock",
            "minecraft:sand",
            "minecraft:gravel",
            "minecraft:water",
            "minecraft:flowing_water",
            "minecraft:lava",
            "minecraft:flowing_lava",
            "minecraft:lily_pad",
            "minecraft:coarse_dirt",
            "minecraft:podzol",
            "minecraft:granite",
            "minecraft:diorite",
            "minecraft:andesite",
            "minecraft:crimson_nylium",
            "minecraft:warped_nylium",
            "minecraft:red_sand",
            "minecraft:sandstone",
            "minecraft:end_stone",
            "minecraft:red_sandstone",
            "minecraft:blackstone",
            "minecraft:netherrack",
            "minecraft:soul_sand",
            "minecraft:soul_soil",
            "minecraft:grass",
            "minecraft:fern",
            "minecraft:dead_bush",
            "minecraft:seagrass",
            "minecraft:tall_seagrass",
            "minecraft:sea_pickle",
            "minecraft:kelp",
            "minecraft:bamboo",
            "minecraft:dandelion",
            "minecraft:poppy",
            "minecraft:blue_orchid",
            "minecraft:allium",
            "minecraft:azure_bluet",
            "minecraft:red_tulip",
            "minecraft:orange_tulip",
            "minecraft:white_tulip",
            "minecraft:pink_tulip",
            "minecraft:oxeye_daisy",
            "minecraft:cornflower",
            "minecraft:lily_of_the_valley",
            "minecraft:brown_mushroom",
            "minecraft:red_mushroom",
            "minecraft:crimson_fungus",
            "minecraft:warped_fungus",
            "minecraft:crimson_roots",
            "minecraft:warped_roots",
            "minecraft:nether_sprouts",
            "minecraft:weeping_vines",
            "minecraft:twisting_vines",
            "minecraft:chorus_plant",
            "minecraft:chorus_flower",
            "minecraft:snow",
            "minecraft:cactus",
            "minecraft:vine",
            "minecraft:sunflower",
            "minecraft:lilac",
            "minecraft:rose_bush",
            "minecraft:peony",
            "minecraft:tall_grass",
            "minecraft:large_fern",
            "minecraft:air",
            "minecraft:ice",
            "minecraft:packed_ice",
            "minecraft:blue_ice",
            "minecraft:portal",
            "minecraft:bedrock",
            "minecraft:end_portal_frame",
            "minecraft:end_portal",
            "minecraft:end_gateway",
            "minecraft:portal",
            "minecraft:oak_sapling",
            "minecraft:spruce_sapling",
            "minecraft:birch_sapling",
            "minecraft:jungle_sapling",
            "minecraft:acacia_sapling",
            "minecraft:dark_oak_sapling",
            "minecraft:oak_leaves",
            "minecraft:spruce_leaves",
            "minecraft:birch_leaves",
            "minecraft:jungle_leaves",
            "minecraft:acacia_leaves",
            "minecraft:dark_oak_leaves"
        )

    }
}
