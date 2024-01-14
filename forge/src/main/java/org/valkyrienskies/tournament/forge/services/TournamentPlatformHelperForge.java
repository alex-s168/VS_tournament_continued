package org.valkyrienskies.tournament.forge.services;

import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.model.ForgeModelBakery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.valkyrienskies.tournament.services.TournamentPlatformHelper;
import java.util.function.Supplier;

public class TournamentPlatformHelperForge implements TournamentPlatformHelper {
    @NotNull
    @Override
    public CreativeModeTab createCreativeTab(@NotNull ResourceLocation id, @NotNull Supplier<ItemStack> stack) {
        return new CreativeModeTab(id.toString().replace(":", ".")) {
            @Override
            public ItemStack makeIcon() {
                return stack.get();
            }

            @Override
            public Component getDisplayName() {
                return new TranslatableComponent("itemGroup." + String.format("%s.%s", id.getNamespace(), id.getPath()));
            }
        };
    }

    @Nullable
    @Override
    public BakedModel loadBakedModel(@NotNull ResourceLocation modelLocation) {
        ForgeModelBakery fmb = ForgeModelBakery.instance();
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
