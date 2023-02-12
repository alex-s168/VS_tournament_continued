package org.valkyrienskies.tournament

import org.valkyrienskies.tournament.api.debug.DebugObject
import org.valkyrienskies.tournament.api.debug.DebugObjectID

class TournamentDebugHelper {
    companion object {

        private var objects = ArrayList<DebugObject?>()

        fun addObject(obj : DebugObject) : DebugObjectID {
            val id = objects.size
            objects.add(obj)
            return (id).toLong()
        }

        fun updateObject(id : DebugObjectID, obj : DebugObject) : DebugObjectID {
            if (id.toInt() == -1) {
                return addObject(obj)
            } else {
                objects[id.toInt()] = obj
            }
            return id
        }

        fun removeObject(id : DebugObjectID) {
            objects[id.toInt()] = null
        }

        fun query() : List<DebugObject?> {
            return objects.toList()
        }

    }

}