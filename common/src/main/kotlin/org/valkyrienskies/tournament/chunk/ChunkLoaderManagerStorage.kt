package org.valkyrienskies.tournament.chunk

import net.minecraft.resources.ResourceLocation
import org.valkyrienskies.tournament.TournamentMod
import org.valkyrienskies.tournament.storage.PersistentLevelStorage

class ChunkLoaderManagerStorage: PersistentLevelStorage<ChunkLoaderManagerStorage>(
    ResourceLocation(TournamentMod.MOD_ID, "chunk_loader_manager")
) {
    var toLoad by nbtList(nbtChunkPos)
}