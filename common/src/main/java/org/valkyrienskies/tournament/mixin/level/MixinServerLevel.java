package org.valkyrienskies.tournament.mixin.level;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.valkyrienskies.tournament.chunk.ChunkLoaderManager;

@Mixin(ServerLevel.class)
public class MixinServerLevel {
    @Inject(
        method = "setChunkForced(IIZ)Z",
        at = @At("HEAD"),
        cancellable = true
    )
    private void vs_tournament$setChunkForced(int chunkX,
                                              int chunkZ,
                                              boolean add,
                                              CallbackInfoReturnable<Boolean> r) {
        if (add)
            return;

        ChunkLoaderManager clm =
                ChunkLoaderManager.Companion.getForOrNull((ServerLevel) (Object) this);

        if (clm == null)
            return;

        boolean cancel =
                clm.shouldCancelUnload(new ChunkPos(chunkX, chunkZ));

        if (cancel)
            r.setReturnValue(true /* loaded */);
    }
}
