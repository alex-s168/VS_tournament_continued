package org.valkyrienskies.tournament.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.tournament.*;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class TournamentModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before Tournament
        new ValkyrienSkiesModFabric().onInitialize();

        TournamentItems.TAB = FabricItemGroupBuilder
                .create(new ResourceLocation(TournamentMod.MOD_ID, "main_tab"))
                .icon(() -> new ItemStack(TournamentBlocks.INSTANCE.getSHIP_ASSEMBLER().get()))
                .build();

        ServerTickEvents.END_SERVER_TICK.register(TickScheduler.INSTANCE::tickServer);

        TournamentMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            TournamentMod.initClient();
            TournamentMod.initClientRenderers(new ClientRenderersFabric());

            ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) ->
                    TournamentModels.INSTANCE.getMODELS().forEach(out));
        }

        private static class ClientRenderersFabric implements TournamentMod.ClientRenderers {
            @Override
            public <T extends BlockEntity> void registerBlockEntityRenderer(
                    @NotNull BlockEntityType<T> t,
                    @NotNull BlockEntityRendererProvider<T> r) {
                BlockEntityRendererRegistry.register(t, r);
            }
        }
    }

    public static class ModMenu implements ModMenuApi {
        @Override
        public ConfigScreenFactory<?> getModConfigScreenFactory() {
            return (parent) -> VSClothConfig.createConfigScreenFor(
                    parent,
                    VSConfigClass.Companion.getRegisteredConfig(TournamentConfig.class)
            );
        }
    }
}
