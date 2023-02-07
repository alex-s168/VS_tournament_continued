package org.valkyrienskies.tournament.item

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionResult
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.context.UseOnContext
import org.joml.Vector3d
import org.valkyrienskies.core.apigame.constraints.*
import org.valkyrienskies.mod.common.getShipObjectManagingPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.ship.tournamentShipControl
import org.valkyrienskies.tournament.tournamentConfig
import org.valkyrienskies.tournament.tournamentItems

class PulseGun : Item(
    Properties().stacksTo(1).tab(tournamentItems.getTab())
){

    private var pulseForce : Vector3d? = null

    override fun useOn(context: UseOnContext): InteractionResult {
        val force = tournamentConfig.SERVER.pulseGunForce

        val player = context.player
        val blockPosition = context.clickedPos
        val blockLocation = context.clickLocation.toJOML()

        if(blockPosition == null || context.level.isClientSide || player == null) {
            return InteractionResult.PASS
        }

        val level = context.level
        if(level !is ServerLevel){
            return InteractionResult.PASS
        }

        val ship = level.getShipObjectManagingPos(blockPosition)
        if(ship == null) {
            return InteractionResult.PASS
        }

        pulseForce = player.lookAngle.toJOML().normalize().mul(force * ship.inertiaData.mass)

        tournamentShipControl.getOrCreate(ship).addPulse(blockLocation, pulseForce!!)

        return super.useOn(context)
    }
}