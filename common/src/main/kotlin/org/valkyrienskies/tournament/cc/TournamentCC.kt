package org.valkyrienskies.tournament.cc

import dan200.computercraft.shared.computer.core.ServerComputer
import net.minecraft.server.level.ServerLevel

/*
This works because the JVM only loads the class when it is first used, and it will only be used by the optional CC mixins.
 */
object TournamentCC {

    @JvmStatic
    fun applyCCAPIs(computer: ServerComputer, level: ServerLevel) {
        computer.addAPI(CCChunkLoadingAPI(computer, level))
    }
}