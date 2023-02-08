package org.valkyrienskies.tournament.items

import net.minecraft.ResourceLocationException
import net.minecraft.Util
import net.minecraft.core.BlockPos
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.templatesystem.BlockRotProcessor
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate
import org.valkyrienskies.tournament.TournamentItems
import java.util.*

class ShipSpawner : Item(
    Properties().stacksTo(1).tab(TournamentItems.TAB)
) {

    private val integrity = 0.5f
    private lateinit var structure : ResourceLocation

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if(player.isCrouching) {
            if (level.isClientSide) {
                return InteractionResultHolder.pass(player.getItemInHand(usedHand))
            }
            if (level !is ServerLevel) {
                return InteractionResultHolder.pass(player.getItemInHand(usedHand))
            }

            //todo: open gui

        }

        if (this::structure.isInitialized) {
            if (level.isClientSide || player == null) {
                return InteractionResultHolder.pass(player.getItemInHand(usedHand))
            }

            if (level !is ServerLevel) {
                return InteractionResultHolder.pass(player.getItemInHand(usedHand))
            }

            val pos = player.blockPosition()

            return try {
                loadStructure(
                    level,
                    pos,
                    level.structureManager.get(this.structure).get()
                )
                InteractionResultHolder.pass(player.getItemInHand(usedHand))
            } catch (var6: ResourceLocationException) {
                println("couldn't find structure: $structure")
                InteractionResultHolder.fail(player.getItemInHand(usedHand))
            }
        }
        println("structure not set!")
        return InteractionResultHolder.fail(player.getItemInHand(usedHand))
    }

    fun loadStructure(level: ServerLevel, pos: BlockPos, structureTemplate: StructureTemplate): Boolean {
        val structurePlaceSettings = StructurePlaceSettings()
        if (this.integrity < 1.0f) {
            structurePlaceSettings.clearProcessors()
                .addProcessor(BlockRotProcessor(Mth.clamp(this.integrity, 0.0f, 1.0f))).setRandom(
                    this.createRandom(level.seed)
                )
        }
        structureTemplate.placeInWorld(
            level,
            pos,
            pos,
            structurePlaceSettings,
            this.createRandom(level.seed),
            2
        )
        return true
    }

    private fun createRandom(seed: Long): Random? {
        return if (seed == 0L) Random(Util.getMillis()) else Random(seed)
    }

}