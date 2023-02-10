package org.valkyrienskies.tournament.blockentity

import de.m_marvin.univec.impl.Vec3d
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.TournamentDebugHelper
import org.valkyrienskies.tournament.api.helper.Helper3d
import org.valkyrienskies.tournament.api.extension.fromPos
import org.valkyrienskies.tournament.ship.SimpleShipControl

class TargeterBlockEntity(pos: BlockPos, state: BlockState) : BlockEntity(TournamentBlockEntities.TARGETER.get(), pos, state) {

    val TargetVec = Vec3d(200.0, 200.0, 200.0)

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, be: BlockEntity) {
            if (state.getValue(BlockStateProperties.POWER) < 1) { return }

            be as TargeterBlockEntity

            val ship = level.getShipObjectManagingPos(pos) ?: return

            val pos = Vec3d().fromPos(pos)

            val worldPos = Helper3d.MaybeShipToWorldspace(level, pos)
            println("worldPos: $worldPos")

            val rotn = be.TargetVec.sub(worldPos).normalize()
            println("rotn: $rotn")

            if (!level.isClientSide) {
                ship as ServerShip

                val force = rotn.mul(1e4 * ship.inertiaData.mass)
                println("force: $force")

                TournamentDebugHelper.addTickDebugLine(worldPos, force.add(worldPos), 20)

                SimpleShipControl.getOrCreate(ship).addInvariantForce(force)
            }
        }
    }

}
