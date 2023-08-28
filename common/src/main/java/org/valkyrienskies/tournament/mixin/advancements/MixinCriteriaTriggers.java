package org.valkyrienskies.tournament.mixin.advancements;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(CriteriaTriggers.class)
public interface MixinCriteriaTriggers {

    @Invoker("register")
    public static <T extends CriterionTrigger<?>> T registerInvoker(T criterion) {
        throw new AssertionError();
    }

}
