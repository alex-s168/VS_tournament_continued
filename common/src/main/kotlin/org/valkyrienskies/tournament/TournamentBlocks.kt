package org.valkyrienskies.tournament

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.Explosion
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.FireBlock
import org.valkyrienskies.mod.common.hooks.VSGameEvents
import org.valkyrienskies.tournament.api.extension.explodeShip
import org.valkyrienskies.tournament.blocks.*
import org.valkyrienskies.tournament.blocks.explosive.AbstractExplosiveBlock
import org.valkyrienskies.tournament.blocks.explosive.SimpleExplosiveStagedBlock
import org.valkyrienskies.tournament.blocks.explosive.TestExplosiveBlock
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.RegistrySupplier

@Suppress("unused")
object TournamentBlocks {
    private val BLOCKS = DeferredRegister.create(TournamentMod.MOD_ID, Registry.BLOCK_REGISTRY)

    lateinit var SHIP_ASSEMBLER           : RegistrySupplier<ShipAssemblerBlock>
    lateinit var BALLAST                  : RegistrySupplier<BallastBlock>
    lateinit var POWERED_BALLOON          : RegistrySupplier<BalloonBlock>
    lateinit var BALLOON                  : RegistrySupplier<BalloonBlock>
    lateinit var THRUSTER                 : RegistrySupplier<ThrusterBlock>
    lateinit var THRUSTER_TINY            : RegistrySupplier<ThrusterBlock>
    lateinit var SPINNER                  : RegistrySupplier<SpinnerBlock>
    lateinit var SEAT                     : RegistrySupplier<SeatBlock>
    lateinit var ROPE_HOOK                : RegistrySupplier<RopeHookBlock>
    lateinit var SENSOR                   : RegistrySupplier<SensorBlock>

    lateinit var EXPLOSIVE_INSTANT_SMALL  : RegistrySupplier<AbstractExplosiveBlock>
    lateinit var EXPLOSIVE_INSTANT_MEDIUM : RegistrySupplier<AbstractExplosiveBlock>
    lateinit var EXPLOSIVE_INSTANT_LARGE  : RegistrySupplier<AbstractExplosiveBlock>

    lateinit var EXPLOSIVE_STAGED_SMALL   : RegistrySupplier<AbstractExplosiveBlock>

    lateinit var EXPLOSIVE_TEST           : RegistrySupplier<TestExplosiveBlock>


    fun register() {
        SHIP_ASSEMBLER           = BLOCKS.register("ship_assembler", ::ShipAssemblerBlock)
        BALLAST                  = BLOCKS.register("ballast", ::BallastBlock)
        POWERED_BALLOON          = BLOCKS.register("balloon", ::PoweredBalloonBlock)
        BALLOON                  = BLOCKS.register("balloon_unpowered", ::BalloonBlock)
        THRUSTER                 = BLOCKS.register("thruster") { ThrusterBlock(
            1.0,
            ParticleTypes.FIREWORK,
            5
        )}
        THRUSTER_TINY            = BLOCKS.register("tiny_thruster") { ThrusterBlock(
            0.2,
            ParticleTypes.ELECTRIC_SPARK,
            3
        ) }
        SPINNER                  = BLOCKS.register("spinner", ::SpinnerBlock)
        SEAT                     = BLOCKS.register("seat", ::SeatBlock)
        ROPE_HOOK                = BLOCKS.register("rope_hook", ::RopeHookBlock)
        SENSOR                   = BLOCKS.register("sensor", ::SensorBlock)

        EXPLOSIVE_INSTANT_SMALL  = BLOCKS.register("explosive_instant_small") { object : AbstractExplosiveBlock() {
            override fun explode(level: ServerLevel, pos: BlockPos) {
                level.explodeShip(level, pos.x+0.5, pos.y+0.5, pos.z+0.5, 3.0f, Explosion.BlockInteraction.BREAK)
            }}
        }
        EXPLOSIVE_INSTANT_MEDIUM = BLOCKS.register("explosive_instant_medium") { object : AbstractExplosiveBlock() {
            override fun explode(level: ServerLevel, pos: BlockPos) {
                level.explodeShip(level, pos.x+0.5, pos.y+0.5, pos.z+0.5, 6.0f, Explosion.BlockInteraction.BREAK)
            }}
        }
        EXPLOSIVE_INSTANT_LARGE  = BLOCKS.register("explosive_instant_large") { object : AbstractExplosiveBlock() {
            override fun explode(level: ServerLevel, pos: BlockPos) {
                level.explodeShip(level, pos.x+0.5, pos.y+0.5, pos.z+0.5, 12.0f, Explosion.BlockInteraction.BREAK)
            }}
        }
        EXPLOSIVE_STAGED_SMALL   = BLOCKS.register("explosive_staged_small") {
            object : SimpleExplosiveStagedBlock(
                (3..7),
                (4..7),
                (-10..10),
                (-2..2),
                (-10..10),
                Explosion.BlockInteraction.BREAK
            ) {}
        }
        EXPLOSIVE_TEST           = BLOCKS.register("explosive_test", ::TestExplosiveBlock)

        // old:
        BLOCKS.register("shipifier") { OldBlock(SHIP_ASSEMBLER.get()) }
        BLOCKS.register("instantexplosive") { OldBlock(EXPLOSIVE_INSTANT_MEDIUM.get()) }
        BLOCKS.register("instantexplosive_big") { OldBlock(EXPLOSIVE_INSTANT_LARGE.get()) }
        BLOCKS.register("stagedexplosive") { OldBlock(EXPLOSIVE_STAGED_SMALL.get()) }
        BLOCKS.register("stagedexplosive_big") { OldBlock(Blocks.AIR) }


        BLOCKS.applyAll()
        VSGameEvents.registriesCompleted.on { _, _ ->
            makeFlammables()
        }
    }

    fun flammableBlock(block: Block, flameOdds: Int, burnOdds: Int) {
        val fire = Blocks.FIRE as FireBlock
        fire.setFlammable(block, flameOdds, burnOdds)
    }

    fun makeFlammables() {
        flammableBlock(SEAT.get(), 15, 25)
        flammableBlock(POWERED_BALLOON.get(), 30, 60)
    }

    fun registerItems(items: DeferredRegister<Item>) {
        BLOCKS.forEach {
            items.register(it.name) { BlockItem(it.get(), Item.Properties().tab(TournamentItems.TAB)) }
        }
    }

}
