package org.valkyrienskies.tournament

import net.minecraft.Util
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.blockentity.*
import org.valkyrienskies.tournament.blockentity.explosive.ExplosiveBlockEntity
import org.valkyrienskies.tournament.blockentity.render.PropellerBlockEntityRender
import org.valkyrienskies.tournament.blockentity.render.SensorBlockEntityRender
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.RegistrySupplier

@Suppress("unused")
object TournamentBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(TournamentMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    private val renderers = mutableListOf<RendererEntry<*>>()

    /* ================================================================== */
    val CONNECTOR           = TournamentBlocks.CONNECTOR
        .withBE(::ConnectorBlockEntity)
        .byName ("connector")
    /* ================================================================== */
    val SENSOR              = TournamentBlocks.SENSOR
        .withBE(::SensorBlockEntity)
        .byName ("sensor")
        .withRenderer {
            SensorBlockEntityRender()
        }
    /* ================================================================== */
    val ROPE_HOOK           = TournamentBlocks.ROPE_HOOK
        .withBE(::RopeHookBlockEntity)
        .byName ("rope_hook")
    /* ================================================================== */
    val PROP_BIG            = TournamentBlocks.PROP_BIG
        .withBE(::BigPropellerBlockEntity)
        .byName ("prop_big")
        .withRenderer {
            PropellerBlockEntityRender<BigPropellerBlockEntity>(
                TournamentModels.PROP_BIG
            )
        }
    /* ================================================================== */
    val PROP_SMALL          = TournamentBlocks.PROP_SMALL
        .withBE(::SmallPropellerBlockEntity)
        .byName ("prop_small")
        .withRenderer {
            PropellerBlockEntityRender<SmallPropellerBlockEntity>(
                TournamentModels.PROP_SMALL
            )
        }
    /* ================================================================== */
    val CHUNK_LOADER        = TournamentBlocks.CHUNK_LOADER
        .withBE(::ChunkLoaderBlockEntity)
        .byName ("chunk_loader")
    /* ================================================================== */
    val EXPLOSIVE           = TournamentBlocks.EXPLOSIVE_INSTANT_SMALL
        .withBE(::ExplosiveBlockEntity)
        .byName("explosive_instant_small")
    /* ================================================================== */
    val EXPLOSIVE_M         = TournamentBlocks.EXPLOSIVE_INSTANT_MEDIUM
        .withBE(::ExplosiveBlockEntity)
        .byName("explosive_instant_medium")
    /* ================================================================== */
    val EXPLOSIVE_L         = TournamentBlocks.EXPLOSIVE_INSTANT_LARGE
        .withBE(::ExplosiveBlockEntity)
        .byName("explosive_instant_large")
    /* ================================================================== */
    val EXPLOSIVE_STAGED_S  = TournamentBlocks.EXPLOSIVE_STAGED_SMALL
        .withBE(::ExplosiveBlockEntity)
        .byName("explosive_staged_small")
    /* ================================================================== */

    fun register() {
        BLOCKENTITIES.applyAll()
    }

    private infix fun <T : BlockEntity> Set<RegistrySupplier<out Block>>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(this, blockEntity)

    private infix fun <T : BlockEntity> RegistrySupplier<out Block>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(setOf(this), blockEntity)

    private infix fun <T : BlockEntity> Block.withBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(this, blockEntity)

    private data class RendererEntry<T: BlockEntity>(
        val type: RegistrySupplier<BlockEntityType<T>>,
        val renderer: () -> Any
    )

    @Suppress("UNCHECKED_CAST")
    fun initClientRenderers(clientRenderers: TournamentMod.ClientRenderers) {
        renderers.forEach { x ->
            val rp = BlockEntityRendererProvider {
                x.renderer() as BlockEntityRenderer<BlockEntity>
            }
            clientRenderers.registerBlockEntityRenderer(
                x.type.get() as BlockEntityType<BlockEntity>,
                rp
            )
        }
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    private infix fun <T : BlockEntity> Pair<Set<RegistrySupplier<out Block>>, (BlockPos, BlockState) -> T>.byName(name: String): RegistrySupplier<BlockEntityType<T>> =
        BLOCKENTITIES.register(name) {
            val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)

            BlockEntityType.Builder.of(
                this.second,
                *this.first.map { it.get() }.toTypedArray()
            ).build(type)
        }

    private infix fun <T : BlockEntity> RegistrySupplier<BlockEntityType<T>>.withRenderer(renderer: () -> Any) =
        this.also {
            renderers += RendererEntry(it, renderer)
        }
}
