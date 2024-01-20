package org.valkyrienskies.tournament.chunk

import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import org.valkyrienskies.tournament.TournamentMod
import org.valkyrienskies.tournament.storage.PersistentLevelStorage
import org.valkyrienskies.tournament.storage.readStorage

class ChunkLoaderManagerStorage: PersistentLevelStorage<ChunkLoaderManagerStorage>(
    ResourceLocation(TournamentMod.MOD_ID, "chunk_loader_manager")
) {
    var toLoad by nbtList(nbtChunkPos)

    companion object {
        fun get(level: ServerLevel) =
            level.readStorage(ChunkLoaderManagerStorage())
    }
}