package org.valkyrienskies.tournament.blocks

import net.minecraft.core.BlockPos
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.SoundType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.Material
import net.minecraft.world.phys.BlockHitResult
import org.valkyrienskies.tournament.TournamentItems
import org.valkyrienskies.tournament.TournamentTags

class MechanicalPressBlock: Block(
    Properties.of(Material.METAL)
    .sound(SoundType.STONE).strength(5.0f, 5.0f)
) {

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (hand == InteractionHand.OFF_HAND) {
            val item = player.inventory.items
                .firstOrNull { it.`is`(TournamentTags.MC_WOOL) }
                ?: return InteractionResult.PASS

            item.count --
            popResource(level, pos,
                ItemStack(TournamentItems.WOOL_SHEET.get(), 4))
            return InteractionResult.CONSUME_PARTIAL
        }

        return InteractionResult.PASS
    }

}