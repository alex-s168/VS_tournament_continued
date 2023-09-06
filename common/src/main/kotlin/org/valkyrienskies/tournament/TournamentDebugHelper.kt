package org.valkyrienskies.tournament

import org.valkyrienskies.tournament.util.debug.DebugObject
import org.valkyrienskies.tournament.util.debug.DebugObjectID

class TournamentDebugHelper {
    companion object {

        private var objects = HashMap<Long, DebugObject?>()

        fun addObject(obj : DebugObject) : Long {
            val id = objects.size
            objects[id.toLong()] = obj
            return (id).toLong()
        }

        fun updateObject(id : DebugObjectID, obj : DebugObject) : DebugObjectID {
            if (id.toInt() == -1) {
                return addObject(obj)
            } else {
                objects[id.toInt().toLong()] = obj
            }
            return id
        }

        fun removeObject(id : DebugObjectID) {
            objects.remove(id)
        }

        fun list() : HashMap<Long, DebugObject?> {
            return objects
        }

        fun exists(id : Long) : Boolean {
            return objects.containsKey(id)
        }

    }

}