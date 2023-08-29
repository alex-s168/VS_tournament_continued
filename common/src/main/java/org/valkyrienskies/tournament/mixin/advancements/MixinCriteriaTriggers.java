package org.valkyrienskies.tournament.mixin.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import java.util.Map;

@Mixin(CriteriaTriggers.class)
public interface MixinCriteriaTriggers {

    @Accessor("CRITERIA")
    public static Map<ResourceLocation, CriterionTrigger<?>> getCriteria() {
      throw new AssertionError();
    }

}
