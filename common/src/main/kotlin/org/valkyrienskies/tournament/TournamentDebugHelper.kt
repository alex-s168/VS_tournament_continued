package org.valkyrienskies.tournament

import de.m_marvin.univec.impl.Vec3d
import net.minecraft.util.Tuple

class TournamentDebugHelper {

    companion object {

        private var debugLines = ArrayList<Tuple<Vec3d, Vec3d>>()

        fun renderDebugLine( p1 : Vec3d, p2 : Vec3d ) {
            debugLines.add(Tuple(p1, p2))
        }

        fun queryLines() : List<Tuple<Vec3d, Vec3d>> {
            val lines = debugLines
            debugLines.clear()
            return lines
        }

    }

}