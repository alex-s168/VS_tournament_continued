package org.valkyrienskies.tournament

import net.minecraft.core.BlockPos
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.material.MapColor
import org.valkyrienskies.mod.common.hooks.VSGameEvents
import org.valkyrienskies.tournament.blockentity.BigPropellerBlockEntity
import org.valkyrienskies.tournament.blockentity.SmallPropellerBlockEntity
import org.valkyrienskies.tournament.util.extension.explodeShip
import org.valkyrienskies.tournament.blocks.*
import org.valkyrienskies.tournament.blocks.explosive.AbstractExplosiveBlock
import org.valkyrienskies.tournament.blocks.explosive.SimpleExplosiveStagedBlock
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.RegistrySupplier

@Suppress("unused")
object TournamentBlocks {
    private val BLOCKS = DeferredRegister.create(TournamentMod.MOD_ID, Registries.BLOCK)
    private val ITEMS = ArrayList<Pair<String, ()->Item>>()


    lateinit var SHIP_ASSEMBLER           : RegistrySupplier<ShipAssemblerBlock>
    lateinit var BALLAST                  : RegistrySupplier<BallastBlock>
    lateinit var POWERED_BALLOON          : RegistrySupplier<BalloonBlock>
    lateinit var BALLOON                  : RegistrySupplier<BalloonBlock>
    lateinit var FLOATER                  : RegistrySupplier<Block>
    lateinit var THRUSTER                 : RegistrySupplier<ThrusterBlock>
    lateinit var THRUSTER_TINY            : RegistrySupplier<ThrusterBlock>
    lateinit var SPINNER                  : RegistrySupplier<SpinnerBlock>
    lateinit var SEAT                     : RegistrySupplier<SeatBlock>
    lateinit var ROPE_HOOK                : RegistrySupplier<RopeHookBlock>
    lateinit var SENSOR                   : RegistrySupplier<SensorBlock>
    lateinit var PROP_BIG                 : RegistrySupplier<PropellerBlock>
    lateinit var PROP_SMALL               : RegistrySupplier<PropellerBlock>
    lateinit var CHUNK_LOADER             : RegistrySupplier<ChunkLoaderBlock>
    lateinit var CONNECTOR                : RegistrySupplier<ConnectorBlock>

    lateinit var EXPLOSIVE_INSTANT_SMALL  : RegistrySupplier<AbstractExplosiveBlock>
    lateinit var EXPLOSIVE_INSTANT_MEDIUM : RegistrySupplier<AbstractExplosiveBlock>
    lateinit var EXPLOSIVE_INSTANT_LARGE  : RegistrySupplier<AbstractExplosiveBlock>

    lateinit var EXPLOSIVE_STAGED_SMALL   : RegistrySupplier<AbstractExplosiveBlock>

    fun register() {
        SHIP_ASSEMBLER           = register("ship_assembler", ::ShipAssemblerBlock)
        BALLAST                  = register("ballast", ::BallastBlock)
        POWERED_BALLOON          = register("balloon", ::PoweredBalloonBlock)
        BALLOON                  = register("balloon_unpowered", ::BalloonBlock)
        FLOATER                  = register("floater") { Block(
            BlockBehaviour.Properties.of().mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .strength(1.0f, 2.0f)
        )}
        THRUSTER                 = register("thruster") {
            ThrusterBlock(
                { 1.0 },
                ParticleTypes.CAMPFIRE_SIGNAL_SMOKE
            ) {
                val t = TournamentConfig.SERVER.thrusterTiersNormal
                if (t !in 1..5) {
                    throw IllegalStateException("Thruster tier must be in range 1..5")
                }
                t
            }
        }
        THRUSTER_TINY            = register("tiny_thruster") {
            ThrusterBlock(
                { TournamentConfig.SERVER.thrusterTinyForceMultiplier },
                ParticleTypes.CAMPFIRE_COSY_SMOKE
            ) {
                val t = TournamentConfig.SERVER.thrusterTiersTiny
                if (t !in 1..5) {
                    throw IllegalStateException("Thruster tier must be in range 1..5")
                }
                t
            }
        }
        SPINNER                  = register("spinner", ::SpinnerBlock)
        SEAT                     = register("seat", ::SeatBlock)
        SENSOR                   = register("sensor", ::SensorBlock)
        ROPE_HOOK                = register("rope_hook", ::RopeHookBlock)
        PROP_BIG                 = register("prop_big") {
            PropellerBlock(
                TournamentConfig.SERVER.propellerBigForce,
                ::BigPropellerBlockEntity
            )
        }
        PROP_SMALL               = register("prop_small") {
            PropellerBlock(
                TournamentConfig.SERVER.propellerSmallForce,
                ::SmallPropellerBlockEntity
            )
        }
        CHUNK_LOADER             = register("chunk_loader", ::ChunkLoaderBlock)
        CONNECTOR                = register("connector", ::ConnectorBlock)

        EXPLOSIVE_INSTANT_SMALL  = register("explosive_instant_small") { object : AbstractExplosiveBlock() {
            override fun explode(level: ServerLevel, pos: BlockPos) {
                level.explodeShip(pos.x+0.5, pos.y+0.5, pos.z+0.5, 3.0f, Level.ExplosionInteraction.TNT)
            }}
        }
        EXPLOSIVE_INSTANT_MEDIUM = register("explosive_instant_medium") { object : AbstractExplosiveBlock() {
            override fun explode(level: ServerLevel, pos: BlockPos) {
                level.explodeShip(pos.x+0.5, pos.y+0.5, pos.z+0.5, 6.0f, Level.ExplosionInteraction.TNT)
            }}
        }
        EXPLOSIVE_INSTANT_LARGE  = register("explosive_instant_large") { object : AbstractExplosiveBlock() {
            override fun explode(level: ServerLevel, pos: BlockPos) {
                level.explodeShip(pos.x+0.5, pos.y+0.5, pos.z+0.5, 12.0f, Level.ExplosionInteraction.TNT)
            }}
        }
        EXPLOSIVE_STAGED_SMALL   = register("explosive_staged_small") {
            object : SimpleExplosiveStagedBlock(
                (3..7),
                (4..7),
                (-10..10),
                (-2..2),
                (-10..10),
                Level.ExplosionInteraction.TNT
            ) {}
        }
        // EXPLOSIVE_TEST           = register("explosive_test", ::TestExplosiveBlock)

        /*
        register("ore_phynite") {
            OreBlock(BlockBehaviour.Properties.of(TournamentMaterials.PHYNITE)
                .strength(3.0f, 3.0f)
            )
        }
         */


        // old:
        register("shipifier") { OldBlock(SHIP_ASSEMBLER.get()) }
        register("instantexplosive") { OldBlock(EXPLOSIVE_INSTANT_MEDIUM.get()) }
        register("instantexplosive_big") { OldBlock(EXPLOSIVE_INSTANT_LARGE.get()) }
        register("stagedexplosive") { OldBlock(EXPLOSIVE_STAGED_SMALL.get()) }
        register("stagedexplosive_big") { OldBlock(Blocks.AIR) }

        BLOCKS.applyAll()
        VSGameEvents.registriesCompleted.on { _, _ ->
            makeFlammables()
        }
    }

    private fun <T: Block> register(name: String, block: () -> T): RegistrySupplier<T> {
        val supplier = BLOCKS.register(name, block)
        ITEMS.add(name to { BlockItem(supplier.get(), Item.Properties()) })
        return supplier
    }

    private fun <T: Block> register(name: String, block: () -> T, item: () -> Item): RegistrySupplier<T> {
        val supplier = BLOCKS.register(name, block)
        ITEMS.add(name to item)
        return supplier
    }

    fun flammableBlock(block: Block, flameOdds: Int, burnOdds: Int) {
        val fire = Blocks.FIRE as FireBlock
        fire.setFlammable(block, flameOdds, burnOdds)
    }

    fun makeFlammables() {
        flammableBlock(SEAT.get(), 15, 25)
        flammableBlock(POWERED_BALLOON.get(), 30, 60)
        flammableBlock(BALLOON.get(), 30, 60)
        flammableBlock(FLOATER.get(), 30, 60)
    }

    fun registerItems(items: DeferredRegister<Item>) {
        ITEMS.forEach { items.register(it.first, it.second) }
    }

}
