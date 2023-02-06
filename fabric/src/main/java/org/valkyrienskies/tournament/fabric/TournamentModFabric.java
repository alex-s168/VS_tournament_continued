package org.valkyrienskies.tournament.fabric;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import org.valkyrienskies.core.impl.config.VSConfigClass;
import org.valkyrienskies.tournament.TournamentConfig;
import org.valkyrienskies.tournament.TournamentMod;
import org.valkyrienskies.mod.compat.clothconfig.VSClothConfig;
import org.valkyrienskies.mod.fabric.common.ValkyrienSkiesModFabric;

public class TournamentModFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // force VS2 to load before Tournament
        new ValkyrienSkiesModFabric().onInitialize();

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
