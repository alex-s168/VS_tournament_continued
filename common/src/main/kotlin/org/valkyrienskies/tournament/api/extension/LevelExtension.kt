package org.valkyrienskies.tournament.api.extension

import de.m_marvin.univec.impl.Vec3d
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.PrimedTnt
import net.minecraft.world.level.Explosion
import org.valkyrienskies.tournament.api.helper.Helper3d

fun ServerLevel.explodeShip(level : ServerLevel, x : Double, y: Double, z: Double, radius: Float, interaction: Explosion.BlockInteraction) {
    level.explodeShip(level, Vec3d(x,y,z), radius, interaction)
}

fun ServerLevel.explodeShip(level: ServerLevel, pos: Vec3d, radius: Float, interaction: Explosion.BlockInteraction) {
    level.explode(level, pos, radius, interaction)
    level.explode(level, Helper3d.MaybeShipToWorldspace(level, pos), radius, interaction)
}

fun ServerLevel.explode(level: ServerLevel,pos: Vec3d,radius: Float,interaction: Explosion.BlockInteraction) {
    level.explode(PrimedTnt(EntityType.TNT, level), pos.x, pos.y, pos.z, radius, interaction)
}