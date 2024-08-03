package org.valkyrienskies.tournament.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.core.util.datastructures.DenseBlockPosSet

object ShipAssembler {

    fun findStructure(level: ServerLevel, pos: BlockPos, blacklist: Set<String>) : DenseBlockPosSet {
        val set = level.blockGroup(pos, shouldCancel = { it > 2000 }) {
            !it.isAir && Registry.BLOCK.getKey(it.block).toString() !in blacklist
        }

        return set.toVsSlow()
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