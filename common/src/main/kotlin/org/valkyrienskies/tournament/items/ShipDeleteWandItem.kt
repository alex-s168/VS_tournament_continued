package org.valkyrienskies.tournament.items

import de.m_marvin.industria.core.physics.PhysicUtility
import de.m_marvin.univec.impl.Vec3d
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.tournament.TournamentItems

class ShipDeleteWandItem : Item(
        Properties().stacksTo(1).tab(TournamentItems.TAB)
){

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        val blockPosition = context.clickedPos
        val blockLocation = Vec3d(context.clickLocation)

        if(context.level.isClientSide || player == null) {
            return InteractionResult.PASS
        }

        val level = context.level
        if(level !is ServerLevel){
            return InteractionResult.PASS
        }

        val ship = level.getShipObjectManagingPos(blockPosition) ?: return InteractionResult.PASS

        PhysicUtility.removeContraption(level, ship)

        return super.useOn(context)
    }
}