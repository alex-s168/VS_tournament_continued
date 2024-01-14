package org.valkyrienskies.tournament.fabric.services;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.client.model.BakedModelManagerHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.tournament.services.TournamentPlatformHelper;

import java.util.function.Supplier;

public class TournamentPlatformHelperFabric implements TournamentPlatformHelper {

    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(@NotNull ResourceLocation id, @NotNull Supplier<ItemStack> stack) {
        return FabricItemGroupBuilder
                .create(id)
                .icon(stack)
                .build();
    }

    @Nullable
    @Override
    public BakedModel loadBakedModel(@NotNull ResourceLocation modelLocation) {
        return BakedModelManagerHelper.getModel(
                Minecraft.getInstance().getModelManager(),
                modelLocation
        );
    }
}
