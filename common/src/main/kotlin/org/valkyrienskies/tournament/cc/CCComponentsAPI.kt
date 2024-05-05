package org.valkyrienskies.tournament.cc

import dan200.computercraft.api.lua.ILuaAPI
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.shared.computer.core.ServerComputer
import net.minecraft.server.level.ServerLevel
import org.joml.Vector3d
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.tournament.TournamentConfig
import org.valkyrienskies.tournament.doc.Doc
import org.valkyrienskies.tournament.doc.Documented
import org.valkyrienskies.tournament.doc.documentation
import org.valkyrienskies.tournament.ship.TournamentShips
import org.valkyrienskies.tournament.util.extension.toDouble

class CCComponentsAPI(
    private val computer: ServerComputer,
    private val level: ServerLevel,
    private val ship: ServerShip?
): ILuaAPI {
    private fun requireShip() {
        if (ship == null)
            throw LuaException("This computer is not on a ship!")
    }

    override fun getNames() =
        arrayOf("vst_components")

    private fun Vector3d.toMap(): Map<String, Double> =
        mapOf(
            "x" to x,
            "y" to y,
            "z" to z
        )

    @LuaFunction
    fun get_thrusters(): Array<Map<*, *>> {
        requireShip()
        val ctrl = TournamentShips.getOrCreate(ship!!)
        return Array(ctrl.thrusters.size) {
            val t = ctrl.thrusters[it]
            val force = t.force.mul(t.mult * TournamentConfig.SERVER.thrusterSpeed * if (t.submerged) 0 else 1, Vector3d())
            val pos = t.pos.toDouble().add(0.5, 0.5, 0.5).sub(ship.inertiaData.centerOfMassInShip)
            mapOf(
                "pos" to pos.toMap(),
                "force" to force.toMap(),
            )
        }
    }

    class DocImpl: Documented {
        override fun getDoc() = documentation {
            page("Components API")
                .kind(Doc.Kind.CC_API)
                .summary("A simple API for getting a list of all the thrusters on the current ship")
                .summary("It is recommended to use CC: VS with this API")
                .section("vst_components.get_thrusters") {
                    content("`get_thrusters()`")
                    content("Returns a list of all the thrusters on the current ship")
                    content("Every element in that list looks similar to this: {pos:{x,y,z}, force:{x,y,z}}")
                }
        }
    }
}