package org.valkyrienskies.tournament.mixin.level;

import net.minecraft.world.level.biome.BiomeGenerationSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.tournament.TournamentEvents;
import org.valkyrienskies.tournament.TournamentWorldGen;

@Mixin(BiomeGenerationSettings.Builder.class)
public class MixinBiomeGenerationSettingsBuilder {
    @Inject(at = @At("HEAD"), method = "build")
    private void preBuild(CallbackInfoReturnable<BiomeGenerationSettings> cir) {
        var self = (BiomeGenerationSettings.Builder) (Object) this;
        TournamentWorldGen.INSTANCE.register(); // make sure event listener registed
        TournamentEvents.INSTANCE.getWorldGenFeatures()
                .emit(self);
    }
}
