package org.valkyrienskies.tournament.forge.services;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.tournament.services.TournamentPlatformHelper;

public class TournamentPlatformHelperForge implements TournamentPlatformHelper {
    @Nullable
    @Override
    public BakedModel loadBakedModel(@NotNull ResourceLocation modelLocation) {
        ModelBakery fmb = new ModelBakery();
        if (fmb == null) {
            return null;
        }
        return fmb.getBakedTopLevelModels()
                .getOrDefault(
                    modelLocation,
                    null
                );
    }
}
