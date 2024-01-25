package org.valkyrienskies.tournament.fabric.mixin.cc;

import dan200.computercraft.shared.computer.core.ServerComputer;
import dan200.computercraft.shared.turtle.blocks.TileTurtle;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.tournament.cc.TournamentCC;

@Mixin(TileTurtle.class)
public class TileTurtleMixin {
    @Inject(
            method = "createComputer",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void cc_vs$addShipAPI(int instanceID, int id, CallbackInfoReturnable<ServerComputer> cir) {
        ServerComputer computer = cir.getReturnValue();
        Level level = computer.getLevel();

        TournamentCC.applyCCAPIs(computer, (ServerLevel) level);
    }
}