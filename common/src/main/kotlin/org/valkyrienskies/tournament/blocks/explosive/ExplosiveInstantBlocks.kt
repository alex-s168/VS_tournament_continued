package org.valkyrienskies.tournament.blocks.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Explosion
import org.valkyrienskies.tournament.api.extension.*

class ExplosiveInstantBlockSmall : AbstractExplosiveBlock() {

    override fun explode(level: ServerLevel, pos: BlockPos) {
        level.explodeShip(level, pos.x+0.5, pos.y+0.5, pos.z+0.5, 3.0f, Explosion.BlockInteraction.BREAK)
    }

}

class ExplosiveInstantBlockMedium : AbstractExplosiveBlock() {

    override fun explode(level: ServerLevel, pos: BlockPos) {
        level.explodeShip(level, pos.x+0.5, pos.y+0.5, pos.z+0.5, 6.0f, Explosion.BlockInteraction.BREAK)
    }

}

class ExplosiveInstantBlockLarge : AbstractExplosiveBlock() {

    override fun explode(level: ServerLevel, pos: BlockPos) {
        level.explodeShip(level, pos.x+0.5, pos.y+0.5, pos.z+0.5, 12.0f, Explosion.BlockInteraction.BREAK)
    }

}