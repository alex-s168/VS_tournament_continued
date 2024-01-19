package org.valkyrienskies.tournament.chunk

data class ChunkLoadingTicket internal constructor(
    val manager: ChunkLoaderManager,
    val loader: ChunkLoader,
    val priority: Int
) {
    var active: Boolean = true
        private set

    fun dispose() {
        active = false
        manager.tickets -= this
    }
}