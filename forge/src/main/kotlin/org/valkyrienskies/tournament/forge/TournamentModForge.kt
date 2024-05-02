package org.valkyrienskies.tournament.forge

import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.client.event.ModelEvent
import net.minecraftforge.event.TickEvent.ServerTickEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.valkyrienskies.tournament.TickScheduler
import org.valkyrienskies.tournament.TournamentMod
import org.valkyrienskies.tournament.TournamentMod.init
import org.valkyrienskies.tournament.TournamentMod.initClient
import org.valkyrienskies.tournament.TournamentMod.initClientRenderers
import org.valkyrienskies.tournament.TournamentModels
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.MOD_BUS

@Mod(TournamentMod.MOD_ID)
class TournamentModForge {
    private var happendClientSetup = false

    init {
        FORGE_BUS.addListener { event: ServerTickEvent ->
            TickScheduler.tickServer(event.server)
        }

        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(
                event
            )
        }
        FORGE_BUS.addListener { event: ModelEvent? ->
            onModelRegistry(
                event
            )
        }
        MOD_BUS.addListener { event: RegisterRenderers ->
            entityRenderers(
                event
            )
        }
        init()
    }

    private fun clientSetup(event: FMLClientSetupEvent?) {
        if (happendClientSetup) {
            return
        }
        happendClientSetup = true
        initClient()
    }

    private fun entityRenderers(event: RegisterRenderers) {
        initClientRenderers(
            object : TournamentMod.ClientRenderers {
                override fun <T : BlockEntity> registerBlockEntityRenderer(
                    t: BlockEntityType<T>,
                    r: BlockEntityRendererProvider<T>
                ) = event.registerBlockEntityRenderer(t, r)
            }
        )
    }

    private fun onModelRegistry(event: ModelEvent?) {
        println("[Tournament] Registering models")
        TournamentModels.MODELS.forEach { rl ->
            println("[Tournament] Registering model $rl")
            // ForgeModelBakery.addSpecialModel(rl)
        }
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}