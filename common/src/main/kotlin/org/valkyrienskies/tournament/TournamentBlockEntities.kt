package org.valkyrienskies.tournament

import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.util.datafix.fixes.References
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.blockentity.*
import org.valkyrienskies.tournament.blockentity.explosive.ExplosiveBlockEntity
import org.valkyrienskies.tournament.registry.DeferredRegister
import org.valkyrienskies.tournament.registry.RegistrySupplier

@Suppress("unused")
object TournamentBlockEntities {
    private val BLOCKENTITIES = DeferredRegister.create(TournamentMod.MOD_ID, Registry.BLOCK_ENTITY_TYPE_REGISTRY)

    val SENSOR              = TournamentBlocks.SENSOR   withBE ::SensorBlockEntity   byName "sensor"
    val ROPE_HOOK           = TournamentBlocks.ROPE_HOOK withBE ::RopeHookBlockEntity byName "rope_hook"

    // explosives:
    val EXPLOSIVE           = TournamentBlocks.EXPLOSIVE_INSTANT_SMALL withBE ::ExplosiveBlockEntity byName "explosive_instant_small"
    val EXPLOSIVE_M         = TournamentBlocks.EXPLOSIVE_INSTANT_MEDIUM withBE ::ExplosiveBlockEntity byName "explosive_instant_medium"
    val EXPLOSIVE_L         = TournamentBlocks.EXPLOSIVE_INSTANT_LARGE withBE ::ExplosiveBlockEntity byName "explosive_instant_large"
    val EXPLOSIVE_STAGED_S  = TournamentBlocks.EXPLOSIVE_STAGED_SMALL withBE ::ExplosiveBlockEntity byName "explosive_staged_small"

    val FUEL_CONTAINER_FULL = TournamentBlocks.FUEL_CONTAINER_FULL withBE { pos, state ->
        FuelContainerBlockEntity(pos, state, 1000)
    } byName "fuel_container_full"

    val THRUSTER            = TournamentBlocks.THRUSTER withBE ::ThrusterBlockEntity byName "thruster"

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
