package org.valkyrienskies.Tournament

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.Tournament.blockentity.EngineBlockEntity
import org.valkyrienskies.Tournament.blockentity.ShipHelmBlockEntity
import org.valkyrienskies.Tournament.registry.DeferredRegister
import org.valkyrienskies.Tournament.registry.RegistrySupplier

@Suppress("unused")
object TournamentBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(TournamentMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    val SHIP_HELM = setOf(
        TournamentBlocks.OAK_SHIP_HELM,
        TournamentBlocks.SPRUCE_SHIP_HELM,
        TournamentBlocks.BIRCH_SHIP_HELM,
        TournamentBlocks.JUNGLE_SHIP_HELM,
        TournamentBlocks.ACACIA_SHIP_HELM,
        TournamentBlocks.DARK_OAK_SHIP_HELM,
        TournamentBlocks.CRIMSON_SHIP_HELM,
        TournamentBlocks.WARPED_SHIP_HELM
    ) withBE ::ShipHelmBlockEntity byName "ship_helm"

    val ENGINE = TournamentBlocks.ENGINE withBE ::EngineBlockEntity byName "engine"

    fun register() {
        BLOCKENTITIES.applyAll()
    }

    private infix fun <T : BlockEntity> Set<RegistrySupplier<out Block>>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(this, blockEntity)

    private infix fun <T : BlockEntity> RegistrySupplier<out Block>.withBE(blockEntity: (BlockPos, BlockState) -> T) =
        Pair(setOf(this), blockEntity)

    private infix fun <T : BlockEntity> Block.withBE(blockEntity: (BlockPos, BlockState) -> T) = Pair(this, blockEntity)
    private infix fun <T : BlockEntity> Pair<Set<RegistrySupplier<out Block>>, (BlockPos, BlockState) -> T>.byName(name: String): RegistrySupplier<BlockEntityType<T>> =
        BLOCKENTITIES.register(name) {
            val type = Util.fetchChoiceType(References.BLOCK_ENTITY, name)

            BlockEntityType.Builder.of(
                this.second,
                *this.first.map { it.get() }.toTypedArray()
            ).build(type)
        }
}
