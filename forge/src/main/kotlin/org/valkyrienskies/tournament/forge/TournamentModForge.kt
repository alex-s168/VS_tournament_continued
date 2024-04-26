package org.valkyrienskies.tournament.forge

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.client.event.EntityRenderersEvent.RegisterRenderers
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
import org.valkyrienskies.core.impl.config.VSConfigClass.Companion.getRegisteredConfig
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig.createConfigScreenFor
import org.valkyrienskies.tournament.TournamentBlocks.SHIP_ASSEMBLER
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.TournamentItems
import org.valkyrienskies.tournament.TournamentMod
import org.valkyrienskies.tournament.TournamentMod.init
import org.valkyrienskies.tournament.TournamentMod.initClient
import org.valkyrienskies.tournament.TournamentMod.initClientRenderers
import org.valkyrienskies.tournament.TournamentModels
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.runForDist

@Mod(TournamentMod.MOD_ID)
class TournamentModForge {
    private var happendClientSetup = false

    init {
        // ServerTickEvents.END_SERVER_TICK.register(TickScheduler.INSTANCE::tickServer);



        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS.addListener { event: FMLClientSetupEvent? ->
            clientSetup(
                event
            )
        }
        MOD_BUS.addListener { event: ModelRegistryEvent? ->
            onModelRegistry(
                event
            )
        }
        MOD_BUS.addListener { event: RegisterRenderers ->
            entityRenderers(
                event
            )
        }
        LOADING_CONTEXT.registerExtensionPoint(
            ConfigGuiFactory::class.java
        ) {
            ConfigGuiFactory { _: Minecraft?, parent: Screen? ->
                createConfigScreenFor(
                    parent!!,
                    getRegisteredConfig(TournamentConfig::class.java)
                )
            }
        }
        TournamentItems.TAB = object : CreativeModeTab("vs_tournament.main_tab") {
            override fun makeIcon(): ItemStack {
                return ItemStack(SHIP_ASSEMBLER.get())
            }
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

    private fun onModelRegistry(event: ModelRegistryEvent?) {
        println("[Tournament] Registering models")
        TournamentModels.MODELS.forEach { rl ->
            println("[Tournament] Registering model $rl")
            ForgeModelBakery.addSpecialModel(rl)
        }
    }

    companion object {
        fun getModBus(): IEventBus = MOD_BUS
    }
}