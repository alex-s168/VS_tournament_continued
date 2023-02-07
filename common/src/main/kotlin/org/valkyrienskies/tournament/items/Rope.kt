// aded back from 15a9250 for test stuff

package org.valkyrienskies.tournament.item

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.block.RopeHookBlock
import org.valkyrienskies.tournament.tournamentBlocks
import org.valkyrienskies.tournament.tournamentItems


class Rope : Item(
        Properties().stacksTo(1).tab(tournamentItems.TAB)
) {

    private var clickedPosition: BlockPos? = null
    private var clickedShipId: ShipId? = null
    private var ropeConstraintId: ConstraintId? = null


    override fun useOn(context: UseOnContext): InteractionResult {

        val level = context.level
        val blockPos = context.clickedPos.immutable()
        val ship = context.level.getShipObjectManagingPos(blockPos)
        val player: Player? = context.player
        var shipID: ShipId?

        if(ship != null) {
            shipID = ship.id
        } else {
            shipID = null
        }

        if (level is ServerLevel) {

            // if its a hook block
            if (level.getBlockState(blockPos).block == tournamentBlocks.ROPEHOOK.get()) {

                //hook it up
                ConnectRope(level.getBlockState(blockPos).block as RopeHookBlock, blockPos, shipID, level)


                println("  ROPE --> " + tournamentBlocks.ROPEHOOK.get() + " < == > " + level.getBlockState(blockPos).block)
            } else {
                println(" !ROPE --> " + tournamentBlocks.ROPEHOOK.get() + " < != > " + level.getBlockState(blockPos).block)
            }
        }
        return super.useOn(context)
    }

    fun ConnectRope(hookBlock: RopeHookBlock, blockPos:BlockPos, shipId: ShipId?, level: ServerLevel)   {
        if(clickedPosition != null) {

            // CONNECT FULL ROPE
            var otherShipId = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!
            var thisShipId = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!

            if (shipId != null){ otherShipId = shipId }
            if (clickedShipId != null){ thisShipId = clickedShipId as ShipId }

            println("other " + otherShipId)
            println("this " + thisShipId)

            if(clickedPosition == null){clickedPosition = blockPos}

            var PosA = clickedPosition!!.toJOMLD().add(0.5, 0.5, 0.5)
            var PosB = blockPos.toJOMLD().add(0.5, 0.5, 0.5)

            var PosC = clickedPosition!!.toJOMLD().add(0.5, 0.5, 0.5)
            var PosD = blockPos.toJOMLD().add(0.5, 0.5, 0.5)

            if(level.getShipObjectManagingPos(clickedPosition!!) != null){
                PosC = level.getShipObjectManagingPos(clickedPosition!!)!!.transform.shipToWorld.transformPosition(clickedPosition!!.toJOMLD()) }

            if(level.getShipObjectManagingPos(blockPos) != null){
                PosD = level.getShipObjectManagingPos(blockPos)!!.transform.shipToWorld.transformPosition(blockPos.toJOMLD()) }

            println("A1" + PosA)
            println("B1" + PosB)
            println("C1" + PosC)
            println("D1" + PosD)

            val RopeCompliance = 1e-5 / (level.getShipObjectManagingPos(blockPos)?.inertiaData?.mass ?: 1).toDouble()
            val RopeMaxForce = 1e10
            val RopeConstraint = VSRopeConstraint(
                thisShipId, otherShipId, RopeCompliance, PosA, PosB,
                RopeMaxForce, PosC.sub(PosD, Vector3d()).length() + 1.0
            )

            println("Legnth: "+ PosC.sub(PosD, Vector3d()).length())
            println(RopeConstraint)

            val RopeConstraintId = level.shipObjectWorld.createNewConstraint(RopeConstraint)
            ropeConstraintId = RopeConstraintId
            RopeConstraintId?.let { hookBlock.SetRopeId(it, PosA, PosB) }


            clickedPosition = null
            clickedShipId = null
            ropeConstraintId = null

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