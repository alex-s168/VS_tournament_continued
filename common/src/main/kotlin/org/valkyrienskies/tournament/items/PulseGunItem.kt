package org.valkyrienskies.tournament.items

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.joml.Vector3d
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.TournamentItems
import org.valkyrienskies.tournament.ship.PulseShipControl

class PulseGunItem : Item(
    Properties().stacksTo(1).tab(TournamentItems.TAB)
){

    private var pulseForce : Vector3d? = null

    override fun useOn(context: UseOnContext): InteractionResult {
        val force = TournamentConfig.SERVER.pulseGunForce

        val player = context.player
        val blockPosition = context.clickedPos
        val blockLocation = context.clickLocation

        if(context.level.isClientSide || player == null) {
            return InteractionResult.PASS
        }

        val level = context.level
        if(level !is ServerLevel){
            return InteractionResult.PASS
        }

        val ship = level.getShipObjectManagingPos(blockPosition) ?: return InteractionResult.PASS

        pulseForce = player.lookAngle.normalize().scale(force * ship.inertiaData.mass).toJOML()

        PulseShipControl.getOrCreate(ship).addPulse(blockLocation.toJOML(), pulseForce!!)

        return super.useOn(context)
    }
}