package org.valkyrienskies.tournament.items

import de.m_marvin.unimat.impl.Quaternion
import de.m_marvin.univec.impl.Vec3d
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
import org.valkyrienskies.core.api.ships.properties.ShipId
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.TournamentItems


class GrabGun : Item(
        Properties().stacksTo(1).tab(TournamentItems.getTab())
) {

    var CurrentPlayer : Player? = null
    var thisShipID : ShipId? = null
    var grabbing : Boolean = false

    var SettingRot : Quaternion? = null
    var CurrentPlayerPitch : Double = 0.0
    var CurrentPlayerYaw : Double = 0.0

    var SettingPos : Vec3d? = null
    var thisAttachPoint : Vec3d? = null

    var thisAttachConstraintID : ConstraintId? = null
    var thisRotationConstraintID : ConstraintId? = null
    var thisPosDampingConstraintID : ConstraintId? = null
    var thisRotDampingConstraintID : ConstraintId? = null

    override fun canAttackBlock(state: BlockState, level: Level, pos: BlockPos, player: Player): Boolean {
        return false
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        if(grabbing) {
            grabbing = false
            OnDropConstraints(context.level)
        } else {
            CurrentPlayer = context.player
            val level = context.level
            val hitLoc = context.clickLocation
            val blockPos = context.clickedPos
            val ship = context.level.getShipObjectManagingPos(blockPos)

            OnDropConstraints(level)

            if (level !is ServerLevel || ship == null) {
                return InteractionResult.PASS
            }

            val shipId = ship.id
            thisShipID = shipId

            SettingPos = Vec3d(ship.transform.shipToWorld.transformPosition(hitLoc.toJOML()))
            thisAttachPoint = Vec3d(hitLoc)

            SettingRot = Quaternion(ship.transform.shipToWorldRotation)
            CurrentPlayerPitch = CurrentPlayer!!.xRot.toDouble()
            CurrentPlayerYaw = CurrentPlayer!!.yRot.toDouble()

            grabbing = true

        }
        return super.useOn(context)
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (isSelected && thisShipID != null){
            val tempShip = level.shipObjectWorld.loadedShips.getById(thisShipID!!)

            if(tempShip != null && grabbing) {
                val MinVec = Vec3d(
                        tempShip!!.shipAABB!!.minX().toDouble(),
                        tempShip!!.shipAABB!!.minY().toDouble(),
                        tempShip!!.shipAABB!!.minZ().toDouble()
                )
                val MaxVec = Vec3d(
                        tempShip!!.shipAABB!!.maxX().toDouble(),
                        tempShip!!.shipAABB!!.maxY().toDouble(),
                        tempShip!!.shipAABB!!.maxZ().toDouble()
                )
                val totalScale = MinVec.sub(MaxVec).length() + 0.75

                println(totalScale)
                OnTickConstraints(totalScale, level)
            }
        } else {
            OnDropConstraints(level)
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }

    fun OnDropConstraints(level: Level) {
        grabbing = false
        if (level is ServerLevel && thisShipID != null && thisRotationConstraintID != null && thisAttachConstraintID != null) {
            level.shipObjectWorld.removeConstraint(thisRotationConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisAttachConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisPosDampingConstraintID!!)
            level.shipObjectWorld.removeConstraint(thisRotDampingConstraintID!!)
        }
    }

    fun OnTickConstraints(Distance: Double, level: Level) {
        if(grabbing) {
            if (level is ServerLevel && CurrentPlayer != null && thisShipID != null) {

                val tempShip = level.shipObjectWorld.loadedShips.getById(thisShipID!!)
                val otherShipId = level.shipObjectWorld.dimensionToGroundBodyIdImmutable[level.dimensionId]!!

                // Update Rot Values
                val newCurrentPlayerPitch = CurrentPlayer!!.xRot.toDouble()
                val newCurrentPlayerYaw = CurrentPlayer!!.yRot.toDouble()

                //todo: univec quaternion and matrix default constructors
                val ogPlayerRot = Quaternion(playerRotToQuaternion(CurrentPlayerPitch,CurrentPlayerYaw).normalize())
                val newPlayerRot = Quaternion(playerRotToQuaternion(newCurrentPlayerPitch, newCurrentPlayerYaw).normalize())
                val deltaPlayerRot = newPlayerRot.mul(ogPlayerRot.conjugate()).normalize()
                val newRot = deltaPlayerRot.mul(SettingRot).normalize()

                // Update Pos Values
                SettingPos = Vec3d(CurrentPlayer!!.position()).add(0.0, CurrentPlayer!!.eyeHeight.toDouble(), 0.0) .add(CurrentPlayer!!.lookAngle.toJOML().normalize().mul(Distance))
                val posOffset = thisAttachPoint!!.sub(tempShip!!.transform.positionInShip)
                val posGlobalOffset = tempShip.transform.shipToWorld.transformDirection(posOffset.conv())


                val Mass = tempShip.inertiaData.mass

                val AttachmentCompliance = 1e-7 / Mass
                val AttachmentMaxForce = 1e10 * Mass
                val AttachmentFixedDistance = 0.0
                val AttachmentConstraint = VSAttachmentConstraint(
                        thisShipID!!, otherShipId, AttachmentCompliance, thisAttachPoint!!.sub(posOffset).conv(), SettingPos!!.sub(posGlobalOffset).conv(),
                        AttachmentMaxForce, AttachmentFixedDistance
                )

                val RotationCompliance = 1e-6 / Mass
                val RotationMaxForce = 1e10 * Mass
                val RotationConstraint = VSFixedOrientationConstraint(
                        thisShipID!!, otherShipId, RotationCompliance, Quaternion(0f,0f,0f,0f).convD(), newRot!!,
                        RotationMaxForce
                )

                val PosDampingCompliance = 0.0
                val PosDampingMaxForce = 0.0
                val PosDampingEff = 0.0
                val PosDampingConstraint = VSPosDampingConstraint(
                        thisShipID!!, otherShipId, PosDampingCompliance, thisAttachPoint!!.sub(posOffset).conv(), SettingPos!!.sub(posGlobalOffset).conv(),
                        PosDampingMaxForce, PosDampingEff
                )

                val RotDampingCompliance = 0.0
                val RotDampingMaxForce = 0.0
                val RotDampingEff = 0.0
                val RotDampingConstraint = VSRotDampingConstraint(
                        thisShipID!!, otherShipId, RotDampingCompliance, Quaternion(0f,0f,0f,0f).convD(), newRot!!,
                        RotDampingMaxForce, RotDampingEff, VSRotDampingAxes.ALL_AXES
                )

                //Drop and re grab the Constraints
                OnDropConstraints(level)
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
            OnDropConstraints(level)
        }
    }

    fun playerRotToQuaternion(pitch:Double, yaw:Double) : Quaternion {
        return Quaternion(0f,0f,0f,0f).rotateY(Math.toRadians(-yaw))//.rotateX(Math.toRadians(pitch))
    }
}