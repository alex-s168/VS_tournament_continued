package org.valkyrienskies.tournament.items

import net.minecraft.core.BlockPos
import net.minecraft.network.chat.TextComponent
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.blocks.RopeHookBlock
import org.valkyrienskies.tournament.TournamentBlocks
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.TournamentItems
import org.valkyrienskies.tournament.api.annotation.RemoveDebug
import org.valkyrienskies.tournament.blockentity.RopeHookBlockEntity


class RopeItem : Item(
        Properties().stacksTo(1).tab(TournamentItems.TAB)
) {

    private var clickedPosition: BlockPos? = null
    private var clickedShipId: ShipId? = null
    private var ropeConstraintId: ConstraintId? = null


    override fun useOn(context: UseOnContext): InteractionResult {

        val level = context.level
        val blockPos = context.clickedPos.immutable()
        val ship = context.level.getShipObjectManagingPos(blockPos)
        val shipID: ShipId?

        if(ship != null) {
            shipID = ship.id
        } else {
            shipID = null
        }

        if (level is ServerLevel) {

            // if its a hook block
            if (level.getBlockState(blockPos).block == TournamentBlocks.ROPE_HOOK.get()) {

                //hook it up
                connectRope(level.getBlockState(blockPos).block as RopeHookBlock, blockPos, shipID, level)
                if(clickedPosition == null)
                    context.player!!.sendMessage(TextComponent("Rope connected!"), context.player!!.uuid)
                else
                    context.player!!.sendMessage(TextComponent("First position set!"), context.player!!.uuid)

                println("  ROPE --> " + TournamentBlocks.ROPE_HOOK.get() + " < == > " + level.getBlockState(blockPos).block)

                return InteractionResult.SUCCESS
            } else {
                println(" !ROPE --> " + TournamentBlocks.ROPE_HOOK.get() + " < != > " + level.getBlockState(blockPos).block)
            }
        }
        return super.useOn(context)
    }

    @RemoveDebug
    fun connectRope(hookBlock: RopeHookBlock, blockPos:BlockPos, shipId: ShipId?, level: ServerLevel)   {
        if(clickedPosition != null) {

            // CONNECT FULL ROPE
            var otherShipId = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!
            var thisShipId = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!

            if (shipId != null){ otherShipId = shipId }
            if (clickedShipId != null){ thisShipId = clickedShipId as ShipId }

            println("other $otherShipId")
            println("this $thisShipId")

            if(clickedPosition == null){clickedPosition = blockPos}

            val posA = clickedPosition!!.toJOMLD().add(0.5, 0.5, 0.5)
            val posB = blockPos.toJOMLD().add(0.5, 0.5, 0.5)

            var posC = clickedPosition!!.toJOMLD().add(0.5, 0.5, 0.5)
            var posD = blockPos.toJOMLD().add(0.5, 0.5, 0.5)

            if(level.getShipObjectManagingPos(clickedPosition!!) != null)
                posC = level.getShipObjectManagingPos(clickedPosition!!)!!.transform.shipToWorld.transformPosition(clickedPosition!!.toJOMLD())

            if(level.getShipObjectManagingPos(blockPos) != null)
                posD = level.getShipObjectManagingPos(blockPos)!!.transform.shipToWorld.transformPosition(blockPos.toJOMLD())

            println("A1 $posA")
            println("B1 $posB")
            println("C1 $posC")
            println("D1 $posD")

            val ropeCompliance = 1e-5 / (level.getShipObjectManagingPos(blockPos)?.inertiaData?.mass ?: 1).toDouble()
            val ropeMaxForce = TournamentConfig.SERVER.ropeMaxForce
            val ropeConstraint = VSRopeConstraint(
                thisShipId, otherShipId, ropeCompliance, posA, posB,
                ropeMaxForce, posC.sub(posD).length() + 1.0
            )

            println("Legnth: "+ posC.sub(posD).length())
            println(ropeConstraint)

            val ropeConstraintId = level.shipObjectWorld.createNewConstraint(ropeConstraint)
            this.ropeConstraintId = ropeConstraintId
            ropeConstraintId?.let {
                (level.getBlockEntity(blockPos) as RopeHookBlockEntity).setRopeID(it, posA, posB, level)
                (level.getBlockEntity(clickedPosition!!) as RopeHookBlockEntity).setSecondary(blockPos)
            }

            clickedPosition = null
            clickedShipId = null
            this.ropeConstraintId = null

            println("Done")
            println()

        } else {

            // CONNECT FIRST POINT
            clickedShipId = shipId
            clickedPosition = blockPos
            ropeConstraintId = null

        }
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }
}