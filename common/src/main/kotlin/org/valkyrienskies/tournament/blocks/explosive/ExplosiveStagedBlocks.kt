package org.valkyrienskies.tournament.blocks.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Explosion
import org.valkyrienskies.tournament.api.extension.explodeShip

class ExplosiveStagedBlockSmall : AbstractExplosiveBlock() {

    override fun explosionTicks(): Int = (3..11).random()

    override fun explodeTick(level: ServerLevel, pos: BlockPos) {
        println("explosion")
        level.explodeShip(
            level,
            pos.x + 0.5 + (-20..20).random(),
            pos.y + 0.5 + (-7..4).random(),
            pos.z + 0.5 + (-20..20).random(),
            (7..15).random().toFloat(),
            Explosion.BlockInteraction.BREAK
        )
    }

}