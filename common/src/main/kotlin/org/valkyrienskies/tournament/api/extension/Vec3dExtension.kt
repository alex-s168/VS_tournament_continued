package org.valkyrienskies.tournament.api.extension

import de.m_marvin.univec.impl.Vec3d
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import org.valkyrienskies.tournament.api.helper.Helper3d

fun Vec3d.toPos(): BlockPos {
    return BlockPos(
        this.x,
        this.y,
        this.z
    )
}

fun Vec3d.toWorldspace(l : Level): Vec3d {
    return Helper3d.MaybeShipToWorldspace(l, this)
}

fun Vec3d.fromPos(pos : BlockPos) : Vec3d {
    this.x = pos.x.toDouble()
    this.y = pos.y.toDouble()
    this.z = pos.z.toDouble()

    return this
}

fun Vec3d.fromVVec(vec : Vec3) : Vec3d {
    this.x = vec.x
    this.y = vec.y
    this.z = vec.z

    return this
}