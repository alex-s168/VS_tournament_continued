package org.valkyrienskies.tournament.forge;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ConfigGuiHandler;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.tournament.TournamentBlocks;
import org.valkyrienskies.tournament.TournamentConfig;
import org.valkyrienskies.tournament.TournamentItems;
import org.valkyrienskies.tournament.TournamentMod;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;

@Mod(TournamentMod.MOD_ID)
public class
TournamentModForge {
    boolean happendClientSetup = false;
    static IEventBus MOD_BUS;

    public TournamentModForge() {
        // Submit our event bus to let architectury register our content on the right time
        MOD_BUS = FMLJavaModLoadingContext.get().getModEventBus();
        MOD_BUS.addListener(this::clientSetup);

        ModLoadingContext.get().registerExtensionPoint(ConfigGuiHandler.ConfigGuiFactory.class,
                () -> new ConfigGuiHandler.ConfigGuiFactory((Minecraft client, Screen parent) ->
                        VSClothConfig.createConfigScreenFor(parent,
                                VSConfigClass.Companion.getRegisteredConfig(TournamentConfig.class)))
        );

        MOD_BUS.addListener(this::onModelRegistry);
        MOD_BUS.addListener(this::clientSetup);
        MOD_BUS.addListener(this::entityRenderers);

        TournamentItems.TAB = new CreativeModeTab("vs_tournament.main_tab") {
            @Override
            public @NotNull ItemStack makeIcon() {
                return new ItemStack(TournamentBlocks.INSTANCE.getSHIP_ASSEMBLER().get());
            }
        };

        TournamentMod.init();
    }

    void clientSetup(final FMLClientSetupEvent event) {
        if (happendClientSetup) return;
        happendClientSetup = true;

        TournamentMod.initClient();
    }

    void entityRenderers(final EntityRenderersEvent.RegisterRenderers event) {
    }

    void onModelRegistry(final ModelRegistryEvent event) {
    }
}
