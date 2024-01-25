package org.valkyrienskies.tournament.forge.mixin.cc;

import dan200.computercraft.shared.computer.blocks.TileComputer;
import dan200.computercraft.shared.computer.core.ServerComputer;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.tournament.cc.TournamentCC;

@Mixin(TileComputer.class)
public class TileComputerMixin {
    @Inject(
            method = "createComputer",
            at = @At("RETURN"),
            cancellable = true,
            remap = false
    )
    private void tournament$addShipAPI(int id, CallbackInfoReturnable<ServerComputer> cir) {
        ServerComputer computer = cir.getReturnValue();
        ServerLevel level = computer.getLevel();

        TournamentCC.applyCCAPIs(computer, level);
    }
}
