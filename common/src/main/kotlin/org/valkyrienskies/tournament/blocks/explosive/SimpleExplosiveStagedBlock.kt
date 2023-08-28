package org.valkyrienskies.tournament.blocks.explosive

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Explosion
import org.valkyrienskies.tournament.api.extension.explodeShip

abstract class SimpleExplosiveStagedBlock(
    private val tickCount: IntRange,
    private val expRadius: IntRange,
    private val spreadX: IntRange,
    private val spreadY: IntRange,
    private val spreadZ: IntRange,
    private val expBlockInteraction: Explosion.BlockInteraction

) : AbstractExplosiveBlock() {

    override fun explosionTicks(): Int = tickCount.random()

    override fun explodeTick(level: ServerLevel, pos: BlockPos) {
        level.explodeShip(
            level,
            pos.x + 0.5 + spreadX.random(),
            pos.y + 0.5 + spreadY.random(),
            pos.z + 0.5 + spreadZ.random(),
            expRadius.random().toFloat(),
            expBlockInteraction
        )
    }

}