package org.valkyrienskies.tournament.util.extension

import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Explosion
import org.joml.Vector3d
import org.valkyrienskies.tournament.util.helper.Helper3d

fun ServerLevel.explodeShip(x : Double, y: Double, z: Double, radius: Float, interaction: Explosion.BlockInteraction) =
    explodeShip(Vector3d(x,y,z), radius, interaction)

fun ServerLevel.explodeShip(pos: Vector3d, radius: Float, interaction: Explosion.BlockInteraction) {
    explode(pos, radius, interaction)
    explode(Helper3d.convertShipToWorldSpace(level, pos), radius, interaction)
}

fun ServerLevel.explode(pos: Vector3d, radius: Float, interaction: Explosion.BlockInteraction) =
    explode(PrimedTnt(EntityType.TNT, this), pos.x, pos.y, pos.z, radius, interaction).void()