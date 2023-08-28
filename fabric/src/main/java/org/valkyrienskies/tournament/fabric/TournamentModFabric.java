package org.valkyrienskies.tournament.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.tournament.TournamentBlocks;
import org.valkyrienskies.tournament.TournamentConfig;
import org.valkyrienskies.tournament.TournamentItems;
import org.valkyrienskies.tournament.TournamentMod;
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

        TournamentMod.init();
    }

    @Environment(EnvType.CLIENT)
    public static class Client implements ClientModInitializer {

        @Override
        public void onInitializeClient() {
            TournamentMod.initClient();
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
