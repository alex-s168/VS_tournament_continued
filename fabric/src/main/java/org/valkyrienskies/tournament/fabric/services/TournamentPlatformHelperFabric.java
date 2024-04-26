package org.valkyrienskies.tournament.fabric.services;

import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.tournament.services.TournamentPlatformHelper;

public class TournamentPlatformHelperFabric implements TournamentPlatformHelper {

    @Nullable
    @Override
    public BakedModel loadBakedModel(@NotNull ResourceLocation modelLocation) {
        return BakedModelManagerHelper.getModel(
                Minecraft.getInstance().getModelManager(),
                modelLocation
        );
    }
}
