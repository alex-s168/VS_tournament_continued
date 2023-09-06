package org.valkyrienskies.tournament.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.block.Rotation
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.impl.datastructures.DenseBlockPosSet
import org.valkyrienskies.core.impl.game.ships.ShipDataCommon
import org.valkyrienskies.core.impl.game.ships.ShipTransformImpl
import org.valkyrienskies.mod.common.dimensionId
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.mod.common.shipObjectWorld
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.mod.common.util.toJOMLD
import org.valkyrienskies.mod.common.util.toMinecraft
import org.valkyrienskies.mod.common.yRange
import org.valkyrienskies.mod.util.relocateBlock
import org.valkyrienskies.mod.util.updateBlock
import org.valkyrienskies.tournament.util.extension.toBlock
import org.valkyrienskies.tournament.util.extension.toDouble
import java.util.concurrent.atomic.AtomicInteger

object ShipAssembler {

    private fun findStructureLoop(level: ServerLevel, pos: BlockPos, blacklist: Set<String>, set : DenseBlockPosSet, checked : MutableSet<BlockPos>, amount : AtomicInteger) {
        if(amount.get() > 2000)
            return

        if(checked.contains(pos))
            return

        checked.add(pos)

        if(blacklist.contains(Registry.BLOCK.getKey(level.getBlockState(pos).block).toString()))
            return

        if(level.getBlockState(pos).isAir)
            return

        set.add(pos.toJOML())

        amount.getAndIncrement()

        findStructureLoop(level, BlockPos(pos.x+1, pos.y, pos.z), blacklist, set, checked, amount)
        findStructureLoop(level, BlockPos(pos.x, pos.y+1, pos.z), blacklist, set, checked, amount)
        findStructureLoop(level, BlockPos(pos.x, pos.y, pos.z+1), blacklist, set, checked, amount)

        findStructureLoop(level, BlockPos(pos.x-1, pos.y, pos.z), blacklist, set, checked, amount)
        findStructureLoop(level, BlockPos(pos.x, pos.y-1, pos.z), blacklist, set, checked, amount)
        findStructureLoop(level, BlockPos(pos.x, pos.y, pos.z-1), blacklist, set, checked, amount)
    }

    fun findStructure(level : ServerLevel, pos : BlockPos, blacklist : Set<String>) : DenseBlockPosSet {
        val set = DenseBlockPosSet()

        findStructureLoop(level, pos, blacklist, set, mutableSetOf(), AtomicInteger(0))

        return set
    }

    /*
    fun assemble(level: ServerLevel, structure : Set<BlockPos>) : ServerShip {
        val parentShip = level.getShipManagingPos(structure.first())

        val orig = structure.first().toJOML()
        val scale = 1.0
        val minScaling = .25

        // Make a ship
        val dimensionId = level.dimensionId
        val serverShip = level.shipObjectWorld.createNewShipAtBlock(orig, false, scale, dimensionId)

        val centerPos = serverShip.chunkClaim.getCenterBlockCoordinates(level.yRange).toBlockPos()

        // Move the blocks from the world to a ship
        structure.forEach {
            val relToOrig = it.toJOML().sub(orig)
            level.relocateBlock(it, centerPos.toJOML().add(relToOrig).toBlockPos(), false, serverShip, Rotation.NONE)
        }

        if (parentShip != null) {
            // Compute the ship transform
            val newShipPosInWorld =
                parentShip.shipToWorld.transformPosition(orig.toDouble().add(0.5, 0.5, 0.5))
            val newShipPosInShipyard = orig.toDouble().add(0.5, 0.5, 0.5)
            val newShipRotation = parentShip.transform.shipToWorldRotation
            var newShipScaling = parentShip.transform.shipToWorldScaling.mul(scale, Vector3d())
            if (newShipScaling.x() < scale) {
                // Do not allow scaling to go below minScaling
                newShipScaling = Vector3d(minScaling, minScaling, minScaling)
            }
            val shipTransform =
                ShipTransformImpl(newShipPosInWorld, newShipPosInShipyard, newShipRotation, newShipScaling)
            (serverShip as ShipDataCommon).transform = shipTransform
        }

        structure.forEach {
            val relToOrig = it.toJOML().sub(orig)
            updateBlock(level, it, centerPos.toJOML().add(relToOrig).toBlockPos(), level.getBlockState(it))
        }

        return serverShip
    }
*/
}