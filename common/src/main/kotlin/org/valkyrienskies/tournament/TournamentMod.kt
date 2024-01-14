package org.valkyrienskies.tournament

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.config.VSConfigClass
import org.valkyrienskies.core.impl.hooks.VSEvents
import org.valkyrienskies.tournament.blockentity.render.PropellerBlockEntityRender
import org.valkyrienskies.tournament.ship.*
import org.valkyrienskies.tournament.util.extension.with

object TournamentMod {
    const val MOD_ID = "vs_tournament"

    @JvmStatic
    fun init() {
        VSConfigClass.registerConfig("vs_tournament", TournamentConfig::class.java)
        TournamentBlocks.register()
        TournamentBlockEntities.register()
        TournamentItems.register()
        TournamentWeights.register()
        TournamentTriggers.init()
        TournamentOres.register()

        VSEvents.shipLoadEvent.on { e ->
            val ship = e.ship

            if (TournamentConfig.SERVER.removeAllAttachments) {
                ship.saveAttachment<BalloonShipControl>(null)
                ship.saveAttachment<PulseShipControl>(null)
                ship.saveAttachment<SpinnerShipControl>(null)
                ship.saveAttachment<ThrusterShipControl>(null)
                ship.saveAttachment<tournamentShipControl>(null)
                ship.saveAttachment<TournamentShips>(null)
            }
            else {
                val thrusterShipCtrl = ship.getAttachment<ThrusterShipControl>()
                if (thrusterShipCtrl != null) {
                    TournamentShips.getOrCreate(ship).addThrusters(thrusterShipCtrl.Thrusters.with(thrusterShipCtrl.thrusters))
                    ship.saveAttachment<ThrusterShipControl>(null)
                }

                val balloonShipCtrl = ship.getAttachment<BalloonShipControl>()
                if (balloonShipCtrl != null) {
                    TournamentShips.getOrCreate(ship).addBalloons(balloonShipCtrl.balloons)
                    ship.saveAttachment<BalloonShipControl>(null)
                }

                val spinnerShipCtrl = ship.getAttachment<SpinnerShipControl>()
                if (spinnerShipCtrl != null) {
                    TournamentShips.getOrCreate(ship).addSpinners(spinnerShipCtrl.spinners.with(spinnerShipCtrl.Spinners))
                    ship.saveAttachment<SpinnerShipControl>(null)
                }

                val pulsesShipCtrl = ship.getAttachment<PulseShipControl>()
                if (pulsesShipCtrl != null) {
                    pulsesShipCtrl.addToNew(TournamentShips.getOrCreate(ship))
                    ship.saveAttachment<PulseShipControl>(null)
                }
            }
        }
    }

    @JvmStatic
    fun initClient() {

    }

    interface ClientRenderers {
        fun <T: BlockEntity> registerBlockEntityRenderer(t: BlockEntityType<T>, r: BlockEntityRendererProvider<T>)
    }

    @JvmStatic
    fun initClientRenderers(clientRenderers: ClientRenderers) {
        fun <T: BlockEntity> renderer(be: BlockEntityType<T>, renderer: BlockEntityRendererProvider<T>) {
            clientRenderers.registerBlockEntityRenderer(be, renderer)
        }

        renderer(TournamentBlockEntities.PROPELLER.get()) { PropellerBlockEntityRender() }
    }
}
