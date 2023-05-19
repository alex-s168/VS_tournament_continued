package org.valkyrienskies.tournament.blockentity

import de.m_marvin.univec.impl.Vec3d
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.physics_api.ConstraintId
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.TournamentDebugHelper
import org.valkyrienskies.tournament.api.debug.DebugLine
import org.valkyrienskies.tournament.api.debug.DebugObjectID
import java.awt.Color

class RopeHookBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(TournamentBlockEntities.ROPE_HOOK.get(), pos, state) {

    var ropeId: ConstraintId? = null
    var mainPos: Vec3d? = null
    var otherPos: Vec3d? = null

    var maxLen: Double = 0.0

    var debugID : Long = -1

    var isSecondary = false
    var conPos: BlockPos? = null

    fun setRopeID(rope: ConstraintId, main:Vec3d?, other:Vec3d?, level:Level) {
        println("Block>> " + rope)

        ropeId = rope
        otherPos = other
        mainPos = main
        maxLen = 0.0

        debugID = TournamentDebugHelper.addObject(DebugLine(main!!, other!!, Color.RED, !TournamentConfig.CLIENT.particleRopeRenderer))

        level.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL_IMMEDIATE)
    }

    fun setSecondary(main: BlockPos) {
        println("set secondary $main")

        isSecondary = true
        conPos = main

        level!!.sendBlockUpdated(blockPos, blockState, blockState, Block.UPDATE_ALL_IMMEDIATE)
    }

    override fun getUpdateTag(): CompoundTag {
        val tag = CompoundTag()
        saveAdditional(tag)
        return tag
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun saveAdditional(tag: CompoundTag) {
        if(mainPos == null || otherPos == null)
            return

        tag.putDouble("mainX", mainPos!!.x)
        tag.putDouble("mainY", mainPos!!.y)
        tag.putDouble("mainZ", mainPos!!.z)

        tag.putDouble("otherX", otherPos!!.x)
        tag.putDouble("otherY", otherPos!!.y)
        tag.putDouble("otherZ", otherPos!!.z)

        tag.putDouble("maxLen", maxLen)
        tag.putLong("debugID", debugID)
        println("saved debugID: $debugID")
        tag.putInt("ropeId", ropeId!!.toInt())

        tag.putBoolean("secondary", isSecondary)

        if(isSecondary) {
            println("saved: secondary")

            tag.putInt("conX", conPos!!.x)
            tag.putInt("conY", conPos!!.y)
            tag.putInt("conZ", conPos!!.z)
        }
    }

    override fun load(tag: CompoundTag) {
        mainPos = Vec3d(
            tag.getDouble("mainX"),
            tag.getDouble("mainY"),
            tag.getDouble("mainZ"),
        )

        otherPos = Vec3d(
            tag.getDouble("otherX"),
            tag.getDouble("otherY"),
            tag.getDouble("otherZ"),
        )

        maxLen = tag.getDouble("maxLen")
        debugID = tag.getLong("debugID")
        ropeId = tag.getInt("ropeId")

        isSecondary = tag.getBoolean("secondary")
        if(isSecondary) {
            println("loaded secondary")
            conPos = BlockPos(
                tag.getInt("conX"),
                tag.getInt("conY"),
                tag.getInt("conZ"),
            )
        }
    }

}
