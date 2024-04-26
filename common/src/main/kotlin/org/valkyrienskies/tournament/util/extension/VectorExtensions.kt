package org.valkyrienskies.tournament.util.extension

import net.minecraft.core.BlockPos
import net.minecraft.world.level.ChunkPos
import org.joml.Vector2d
import org.joml.Vector2i
import org.joml.Vector3d
import org.joml.Vector3i

fun Vector3i.toDouble(): Vector3d =
    Vector3d(x.toDouble(), y.toDouble(), z.toDouble())

fun Vector3d.to2d() : Vector2d =
    Vector2d(x, z)

fun Vector2d.to3d(): Vector3d =
    Vector3d(x, 0.0, y)

fun Vector3d.toBlock(): BlockPos =
    BlockPos(x.toInt(), y.toInt(), z.toInt())

fun BlockPos.toChunkPos(): ChunkPos =
    ChunkPos(x shr 4, z shr 4)

fun Vector3d.toChunkPos(): ChunkPos =
    ChunkPos(x.toInt() shr 4, z.toInt() shr 4)

fun BlockPos.toChunkPos(ofX: Int, ofZ: Int): ChunkPos =
    ChunkPos((x shr 4) + ofX, (z shr 4) + ofZ)

fun BlockPos.toChunkPos(of: Vector2i): ChunkPos =
    ChunkPos((x shr 4) + of.x, (z shr 4) + of.y)

fun Vector3d.toChunkPos(ofX: Int, ofZ: Int): ChunkPos =
    ChunkPos((x.toInt() shr 4) + ofX, (z.toInt() shr 4) + ofZ)

fun Vector3d.toChunkPos(of: Vector2i): ChunkPos =
    ChunkPos((x.toInt() shr 4) + of.x, (z.toInt() shr 4) + of.y)


fun Vector2d.asChunkPos(): ChunkPos =
    ChunkPos(x.toInt(), y.toInt())