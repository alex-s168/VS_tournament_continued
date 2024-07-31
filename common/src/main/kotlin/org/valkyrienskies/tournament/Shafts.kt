package org.valkyrienskies.tournament

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.max
import kotlin.math.min

data class ShaftStats(
    var force: Float = 0.0f,
    var speed: Float = 0.0f, // 0 to 1
) {
    fun reset() {
        force = 0.0f
        speed = 0.0f
    }
}

interface ShaftConsumer {
    fun wantSpeeds(level: ServerLevel, pos: BlockPos, state: BlockState): ClosedFloatingPointRange<Float>
    fun consume(level: ServerLevel, pos: BlockPos, state: BlockState, stats: ShaftStats)
    fun dobreak(level: ServerLevel, pos: BlockPos, state: BlockState)
}

class Shaft(
    val level: ServerLevel,
    val dir: Direction.Axis
) {
    // should only be 2
    val consumer = mutableListOf<Pair<BlockPos, ShaftConsumer>>()
    val statsThisTick = ShaftStats().also { it.reset() }
    val shaftChunks = mutableSetOf<ChunkPos>()
    val shaftBlocks = mutableSetOf<Pair<ChunkPos,BlockPos>>()

    fun wantSpeeds(): ClosedFloatingPointRange<Float>? {
        val all = consumer.map { it.second.wantSpeeds(level, it.first, level.getBlockState(it.first)) }
        val rang = all.reduceOrNull { acc, x ->
            val start = max(acc.start, x.start)
            val end = min(acc.endInclusive, x.endInclusive)
            object : ClosedFloatingPointRange<Float> {
                override fun lessThanOrEquals(a: Float, b: Float): Boolean = error("")
                override val endInclusive: Float = end
                override val start: Float = start
            }
        }
        return rang?.let {
            if (it.start > it.endInclusive) null
            else it.start..it.endInclusive
        }
    }

    fun addShaftBlock(pos: BlockPos) {
        val cp = ChunkPos(pos)
        shaftBlocks += cp to pos
        shaftChunks += cp
    }

    fun removeShaftBlock(pos: BlockPos) {
        val cp = ChunkPos(pos)
        shaftBlocks -= cp to pos
        val remaining = shaftBlocks.count { it.first == cp }
        if (remaining == 0) {
            shaftChunks -= cp
        }
    }

    fun mergeFrom(other: Shaft) {
        consumer += other.consumer
        shaftChunks += other.shaftChunks
        shaftBlocks += other.shaftBlocks
    }

    fun apply(force: Float, speed: Float) {
        val effect = statsThisTick.force / force
        statsThisTick.force += force
        statsThisTick.speed *= speed * effect
    }

    internal fun tick() {
        consumer.forEach { (pos, cons) ->
            val state = level.getBlockState(pos)
            if (statsThisTick.speed !in cons.wantSpeeds(level, pos, state))
                cons.dobreak(level, pos, state)
            else cons.consume(level, pos, state, statsThisTick)
        }
        statsThisTick.reset()
    }
}

fun Direction.Axis.directions() =
    when (this) {
        Direction.Axis.X -> listOf(Direction.EAST, Direction.WEST)
        Direction.Axis.Y -> listOf(Direction.UP, Direction.DOWN)
        Direction.Axis.Z -> listOf(Direction.SOUTH, Direction.NORTH)
    }

fun BlockPos.posOnAxis(axis: Direction.Axis) =
    axis.choose(x, y, z)

fun BlockPos.neighborBlocks() =
    listOf(this.above(), this.below(), this.north(), this.south(), this.east(), this.west())

class ShaftMan(val level: ServerLevel) {
    val shafts = mutableListOf<Shaft>()

    init {
        TickScheduler.serverTickPerm {
            shafts.forEach(Shaft::tick)
        }
    }

    fun shaftsInChunk(chunk: ChunkPos) =
        shafts.filter { chunk in it.shaftChunks }

    fun shaftsAdjacentTo(pos: BlockPos): List<Shaft> {
        val toSearch = pos.neighborBlocks() + pos
        val interestingChunks = toSearch.map(::ChunkPos).toSet()
        val a = interestingChunks
            .flatMap(::shaftsInChunk)
            .filter { s -> toSearch.any { pos -> s.shaftBlocks.any { it.first == pos } } }
        return a
    }

    fun shaftAdjacentTo(pos: BlockPos, axis: Direction.Axis): Shaft? =
        shaftsAdjacentTo(pos).find { it.dir == axis }

    fun addConsumer(pos: BlockPos, block: ShaftConsumer, axis: Direction.Axis) {
        shaftAdjacentTo(pos, axis)?.let { it.consumer += pos to block }
    }

    fun removeConsumer(pos: BlockPos, axis: Direction.Axis) {
        shaftAdjacentTo(pos, axis)?.let {
            it.consumer.removeIf { it.first == pos }
        }
    }

    @Deprecated(message = "Should not be used")
    fun shaftAt(pos: BlockPos): Shaft? {
        val cp = ChunkPos(pos)
        return shafts.find {
            cp in it.shaftChunks &&
                    it.shaftBlocks.any { it.first == pos }
        }
    }

    // remove / split
    fun removeShaft(pos: BlockPos) {
        val shaft = shaftAt(pos) ?: return
        shaft.removeShaftBlock(pos)
        val groups = shaft.shaftBlocks.groupBy { (it.second.posOnAxis(shaft.dir) - pos.posOnAxis(shaft.dir)) > 0 }.values
        val consumers = shaft.consumer.groupBy { (it.first.posOnAxis(shaft.dir) - pos.posOnAxis(shaft.dir)) > 0 }.values
        shafts -= shaft
        shafts += groups.zip(consumers).mapNotNull { (it, consumer) ->
            if (it.isNotEmpty()) {
                val res = Shaft(level, shaft.dir)
                res.consumer += consumer

                it.forEach { (cp, bp) ->
                    res.shaftChunks += cp
                    res.shaftBlocks += cp to bp
                }

                res
            } else null
        }
    }

    // extend neighbor shaft or create new shaft
    fun placeOrGetShaft(pos: BlockPos, dir: Direction.Axis): Shaft {
        val a = shaftsAdjacentTo(pos)
            .filter { it.dir == dir }
        a.firstOrNull { it.shaftBlocks.any { it.first == pos } }?.let { return it }
        val merge = a + Shaft(level, dir).also { it.addShaftBlock(pos) }
        val acc = merge.first()
        merge.forEach(shafts::remove)
        merge.drop(1).forEach {
            acc.mergeFrom(it)
        }
        shafts += acc
        return acc
    }

    companion object {
        private val all = mutableMapOf<ServerLevel, ShaftMan>()

        fun get(level: ServerLevel) =
            all.computeIfAbsent(level) { ShaftMan(it) }
    }
}
