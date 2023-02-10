package org.valkyrienskies.tournament

import de.m_marvin.univec.impl.Vec3d
import net.minecraft.client.Minecraft
import net.minecraft.util.Tuple
import org.valkyrienskies.tournament.api.utilities.Quadruple

class TournamentDebugHelper {

    companion object {

        private var debugLines = ArrayList<Tuple<Vec3d, Vec3d>>()
        private var tickDebugLines = ArrayList<Triple<Vec3d, Vec3d, Int>>()
        private var tickIDDebugLines = ArrayList<Quadruple<Vec3d, Vec3d, Int, Int>>()

        fun addConstantDebugLine( p1 : Vec3d, p2 : Vec3d ) {
            debugLines.add(Tuple(p1, p2))
            println("Added constant debug line from: $p1 to: $p2!")
        }

        fun addTickDebugLine( p1 : Vec3d, p2 : Vec3d, ticks : Int) {
            tickDebugLines.add(Triple(p1, p2, ticks))
            println("Added tick debug line from: $p1 to: $p2 for $ticks ticks!")
        }

        fun addTickedIDDebugLine( p1 : Vec3d, p2 : Vec3d, ticks : Int, id : Int) {
            var cont = false
            tickIDDebugLines.forEach {
                if(it.fourth == id) {cont = true}
            }
            if (!cont) {
                tickIDDebugLines.add(Quadruple(p1, p2, ticks, id))
                println("Added tick debug line from: $p1 to: $p2 for $ticks ticks with the ID $id!")
            } else {
                println("Already added tick debug line with id $id")
            }
        }

        fun updateIDDebugLine(idIn : Int, p1In : Vec3d, p2In : Vec3d, ticks : Int) {
            if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
                try {
                    tickIDDebugLines.forEach {
                        try {
                            val (p1, p2, tick, id) = it
                            if (id == idIn) {
                                tickIDDebugLines.remove(it)
                            }
                        } catch (e: Exception) {
                        }
                    }
                } catch (e: Exception) {
                }
                tickIDDebugLines.add(Quadruple(p1In, p2In, ticks, idIn))
                println("Updated debug line with id: $idIn")
            }
        }

        fun removeIDDebugLine(idIn : Int) {
            try {
                tickIDDebugLines.forEach {
                    try {
                        val id = it.fourth
                        if (id == idIn) {
                            tickIDDebugLines.remove(it)
                        }
                    } catch (e : Exception) {}
                }
            } catch (e : Exception) {}
            println("Removed debug line with id: $idIn")
        }

        fun queryLines() : List<Tuple<Vec3d, Vec3d>> {
            val lines = ArrayList<Tuple<Vec3d, Vec3d>>()
            lines.addAll(debugLines)

            try {
                tickDebugLines.forEach {
                    try {
                        val (p1, p2, tick) = it
                        tickDebugLines.remove(it)
                        if (tick > 0) {
                            lines.add(Tuple(p1, p2))
                            tickDebugLines.add(Triple(p1, p2, tick))
                        }
                    } catch (e : Exception) {}
                }
            } catch (e : Exception) {}
            try {
                tickIDDebugLines.forEach {
                    try {
                        val (p1, p2, tick, id) = it
                        tickIDDebugLines.remove(it)
                        if (tick > 0) {
                            lines.add(Tuple(p1, p2))
                            tickIDDebugLines.add(Quadruple(p1, p2, tick, id))
                        }
                    } catch (e : Exception) {}
                }
            } catch (e : Exception) {}

            println("query ${lines.size} total lines")

            return lines
        }

    }

}