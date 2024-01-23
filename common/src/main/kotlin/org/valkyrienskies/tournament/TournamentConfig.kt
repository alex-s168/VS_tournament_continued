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
        @JsonSchema(description = "The maximum force a rope can handle before breaking")
        var ropeMaxForce = 1e10

        @JsonSchema(description = "The force a spinner applies to a ship")
        var spinnerSpeed = 5000.0

        @JsonSchema(description = "The force a balloon applies to a ship")
        var balloonPower = 30.0

        @JsonSchema(description = "How much stronger a balloon will get when powered (1.0 is 15x stronger at max power)")
        var balloonAnalogStrength = 1.0

        @JsonSchema(description = "The force multiplier of a balloon (not for \"powered balloon\")")
        var unpoweredBalloonMul = 5.0

        @JsonSchema(description = "Base height of a balloon")
        var balloonBaseHeight = 100.0

        @JsonSchema(description = "The force a thruster applies to a ship * tier")
        var thrusterSpeed = 10000.0

        @JsonSchema(description = "The maximum amount of tiers a normal thruster can have (1-5)")
        var thrusterTiersNormal = 4

        @JsonSchema(description = "The maximum amount of tiers a tiny thruster can have (1-5)")
        var thrusterTiersTiny = 2

        @JsonSchema(description = "The force multiplier of a tiny thruster")
        var thrusterTinyForceMultiplier = 0.2

        @JsonSchema(description = "The speed at which the thruster will stop applying force. (-1 means that it always applies force)")
        var thrusterShutoffSpeed = 80.0

        @JsonSchema(description = "The weight of a ballast when not redstone powered")
        var ballastWeight = 10000.0

        @JsonSchema(description = "The weight of a ballast when redstone powered")
        var ballastNoWeight = 800.0

        @JsonSchema(description = "The force the pulse gun applies to a ship")
        var pulseGunForce = 300.0

        @JsonSchema(description = "Maximum distance a sensor can detect a ship from")
        var sensorDistance = 10.0

        @JsonSchema(description = "The force of a big propeller at max speed")
        var propellerBigForce = 10000.0

        @JsonSchema(description = "The max speed of a big propeller at max redstone input")
        var propellerBigSpeed = 7.0f

        @JsonSchema(description = "The acceleration of a big propeller. (deaccel = accel * 2)")
        var propellerBigAccel = 0.1f

        @JsonSchema(description = "The force of a big propeller at max speed")
        var propellerSmallForce = 1000.0

        @JsonSchema(description = "The max speed of a big propeller at max redstone input")
        var propellerSmallSpeed = 50.0f

        @JsonSchema(description = "The acceleration of a big propeller. (deaccel = accel * 2)")
        var propellerSmallAccel = 1.0f

        @JsonSchema(description = "How many chunk tickets can be processed each level tick? (-1 means unlimited)")
        var chunkTicketsPerTick = -1

        @JsonSchema(description = "How many chunks can be loaded per chunk ticket?")
        var chunksPerTicket = 100

        @JsonSchema(description = "After how many ticks to error when loading chunk still not finished? (throws error when double this amount of ticks has passed)")
        var chunkLoadTimeout = 40

        // TODO: add stuff idk
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

        @JsonSchema(description = "DO NOT CHANGE THIS UNLESS YOU KNOW WHAT YOU ARE DOING!")
        var removeAllAttachments = false

    }
}
