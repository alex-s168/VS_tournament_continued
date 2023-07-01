package org.valkyrienskies.tournament.items

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.joml.Quaterniond
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.TournamentItems


class GrabGunItem : Item(
        Properties().stacksTo(1).tab(TournamentItems.TAB)
) {

    private var currentPlayer : Player? = null
    private var thisShipID : ShipId? = null
    private var grabbing : Boolean = false

    private var settingRot : Quaterniond? = null
    private var currentPlayerPitch : Double = 0.0
    private var currentPlayerYaw : Double = 0.0

    private var settingPos : Vector3d? = null
    private var thisAttachPoint : Vector3d? = null

    private var thisAttachConstraintID : ConstraintId? = null
    private var thisRotationConstraintID : ConstraintId? = null
    private var thisPosDampingConstraintID : ConstraintId? = null
    private var thisRotDampingConstraintID : ConstraintId? = null

    override fun canAttackBlock(state: BlockState, level: Level, pos: BlockPos, player: Player): Boolean {
        return false
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        if(grabbing) {
            grabbing = false
            onDropConstraints(context.level)
        } else {
            currentPlayer = context.player
            val level = context.level
            val hitLoc = context.clickLocation
            val blockPos = context.clickedPos
            val ship = context.level.getShipObjectManagingPos(blockPos)

            onDropConstraints(level)

            if (level !is ServerLevel || ship == null) {
                return InteractionResult.PASS
            }

            val shipId = ship.id
            thisShipID = shipId

            settingPos = ship.transform.shipToWorld.transformPosition(hitLoc.toJOML())
            thisAttachPoint = hitLoc.toJOML()

            settingRot = Quaterniond(ship.transform.shipToWorldRotation)
            currentPlayerPitch = currentPlayer!!.xRot.toDouble()
            currentPlayerYaw = currentPlayer!!.yRot.toDouble()

            grabbing = true

        }
        return super.useOn(context)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (isSelected && thisShipID != null){
            val tempShip = level.shipObjectWorld.loadedShips.getById(thisShipID!!)

            if(tempShip != null && grabbing) {
                val minVec = Vector3d(
                        tempShip.shipAABB!!.minX().toDouble(),
                        tempShip.shipAABB!!.minY().toDouble(),
                        tempShip.shipAABB!!.minZ().toDouble()
                )
                val maxVec = Vector3d(
                        tempShip.shipAABB!!.maxX().toDouble(),
                        tempShip.shipAABB!!.maxY().toDouble(),
                        tempShip.shipAABB!!.maxZ().toDouble()
                )
                val totalScale = minVec.sub(maxVec).length() + 0.75

                println(totalScale)
                onTickConstraints(totalScale, level)
            }
        } else {
            onDropConstraints(level)
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }

    fun onDropConstraints(level: Level) {
        grabbing = false
        if (level is ServerLevel && thisShipID != null && thisRotationConstraintID != null && thisAttachConstraintID != null) {
            level.shipObjectWorld.removeConstraint(thisRotationConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisAttachConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisPosDampingConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisRotDampingConstraintID!!)
        }
    }

    private fun onTickConstraints(Distance: Double, level: Level) {
        if(grabbing) {
            if (level is ServerLevel && currentPlayer != null && thisShipID != null) {

                val tempShip = level.shipObjectWorld.loadedShips.getById(thisShipID!!)
                val otherShipId = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!

                // Update Rot Values
                val newCurrentPlayerPitch = currentPlayer!!.xRot.toDouble()
                val newCurrentPlayerYaw = currentPlayer!!.yRot.toDouble()

                val ogPlayerRot = playerRotToQuaternion(currentPlayerPitch,currentPlayerYaw).normalize()
                val newPlayerRot = playerRotToQuaternion(newCurrentPlayerPitch, newCurrentPlayerYaw).normalize()
                val deltaPlayerRot = newPlayerRot.mul(ogPlayerRot.conjugate()).normalize()
                val newRot = deltaPlayerRot.mul(settingRot).normalize()

                // Update Pos Values
                settingPos = currentPlayer!!.position().toJOML().add(0.0, currentPlayer!!.eyeHeight.toDouble(), 0.0) .add(currentPlayer!!.lookAngle.toJOML().normalize().mul(Distance))
                val posOffset = thisAttachPoint!!.sub(tempShip!!.transform.positionInShip)
                val posGlobalOffset = tempShip.transform.shipToWorld.transformDirection(posOffset)


                val mass = tempShip.inertiaData.mass

                val AttachmentCompliance = 1e-7 / mass
                val AttachmentMaxForce = 1e10 * mass
                val AttachmentFixedDistance = 0.0
                val AttachmentConstraint = VSAttachmentConstraint(
                        thisShipID!!, otherShipId, AttachmentCompliance, thisAttachPoint!!.sub(posOffset), settingPos!!.sub(posGlobalOffset),
                        AttachmentMaxForce, AttachmentFixedDistance
                )

                val RotationCompliance = 1e-6 / mass
                val RotationMaxForce = 1e10 * mass
                val RotationConstraint = VSFixedOrientationConstraint(
                        thisShipID!!, otherShipId, RotationCompliance, Quaterniond(), newRot,
                        RotationMaxForce
                )

                val PosDampingCompliance = 0.0
                val PosDampingMaxForce = 0.0
                val PosDampingEff = 0.0
                val PosDampingConstraint = VSPosDampingConstraint(
                        thisShipID!!, otherShipId, PosDampingCompliance, thisAttachPoint!!.sub(posOffset), settingPos!!.sub(posGlobalOffset),
                        PosDampingMaxForce, PosDampingEff
                )

                val RotDampingCompliance = 0.0
                val RotDampingMaxForce = 0.0
                val RotDampingEff = 0.0
                val RotDampingConstraint = VSRotDampingConstraint(
                        thisShipID!!, otherShipId, RotDampingCompliance, Quaterniond(), newRot,
                        RotDampingMaxForce, RotDampingEff, VSRotDampingAxes.ALL_AXES
                )

                //Drop and re grab the Constraints
                onDropConstraints(level)
                grabbing = true

                val RotationConstraintId = level.shipObjectWorld.createNewConstraint(RotationConstraint)
                val AttachConstraintId = level.shipObjectWorld.createNewConstraint(AttachmentConstraint)
                val PosDampingConstraintId = level.shipObjectWorld.createNewConstraint(PosDampingConstraint)
                val RotDampingConstraintId = level.shipObjectWorld.createNewConstraint(RotDampingConstraint)
                thisAttachConstraintID = RotationConstraintId
                thisRotationConstraintID = AttachConstraintId
                thisPosDampingConstraintID = PosDampingConstraintId
                thisRotDampingConstraintID = RotDampingConstraintId

            }
        }else if (level is ServerLevel && thisShipID != null) {
            onDropConstraints(level)
        }
    }

    private fun playerRotToQuaternion(pitch:Double, yaw:Double) : Quaterniond {
        return Quaterniond().rotateY(Math.toRadians(-yaw))
    }
}