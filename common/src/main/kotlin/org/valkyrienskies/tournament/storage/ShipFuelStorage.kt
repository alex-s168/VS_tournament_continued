package org.valkyrienskies.tournament.storage

import com.fasterxml.jackson.annotation.JsonAutoDetect
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import org.joml.Vector3i
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.core.api.ships.getAttachment
import org.valkyrienskies.core.api.ships.saveAttachment
import org.valkyrienskies.core.impl.api.ServerShipUser
import org.valkyrienskies.mod.common.util.toBlockPos
import org.valkyrienskies.mod.common.util.toJOML
import org.valkyrienskies.tournament.blockentity.FuelContainerBlockEntity
import java.util.concurrent.CopyOnWriteArrayList

@JsonAutoDetect(
    fieldVisibility = JsonAutoDetect.Visibility.ANY,
    getterVisibility = JsonAutoDetect.Visibility.NONE,
    isGetterVisibility = JsonAutoDetect.Visibility.NONE,
    setterVisibility = JsonAutoDetect.Visibility.NONE
)
@JsonIgnoreProperties(ignoreUnknown = true)
class ShipFuelStorage: ServerShipUser {
    @JsonIgnore
    override var ship: ServerShip? = null

    private val sources = CopyOnWriteArrayList<Vector3i>()

    @JsonIgnore
    private var cachedFuel: Double = 0.0
    @JsonIgnore
    private var lastLoadCacheTime = 0L

    @JsonIgnore
    private var cachedUsedFuel: Double = 0.0
    @JsonIgnore
    private var lastUsedCacheTime = 0L

    init {
        println(sources.size)
    }

    fun removeSource(source: BlockPos) {
        sources.remove(source.toJOML())
    }

    fun addSource(source: BlockPos) {
        sources.add(source.toJOML())
    }

    fun clearSources() {
        sources.clear()
    }

    fun drainFuel(level: Level, amount: Double): Boolean {
        if (sources.isEmpty()) return false

        val time = System.currentTimeMillis()
        if (time - lastUsedCacheTime > 1000) {
            for (block in sources) {
                if (cachedUsedFuel == 0.0) break

                val be = level.getBlockEntity(block.toBlockPos()) as FuelContainerBlockEntity
                if (be.amount < cachedUsedFuel) {
                    cachedUsedFuel -= be.amount
                    be.amount = 0.0
                    continue
                }

                be.amount -= cachedUsedFuel
                cachedUsedFuel = 0.0
            }

            if (cachedUsedFuel > 0.0) return false

            lastUsedCacheTime = time

            return true
        }

        cachedUsedFuel += amount

        return true
    }

    fun getFuel(level: Level): Double {
        val time = System.currentTimeMillis()
        if (time - lastLoadCacheTime > 1000) {
            cachedFuel = sources.sumOf { (level.getBlockEntity(it.toBlockPos()) as FuelContainerBlockEntity).amount }
            lastLoadCacheTime = time
        }
        return cachedFuel
    }

    companion object {
        fun get(ship: ServerShip): ShipFuelStorage =
            ship.getAttachment<ShipFuelStorage>()
                ?: ShipFuelStorage()
                    .also { ship.saveAttachment(it) }
    }

}