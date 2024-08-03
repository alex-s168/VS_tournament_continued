package org.valkyrienskies.tournament.blockentity.render

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.core.Direction
import org.valkyrienskies.tournament.TournamentModels
import org.valkyrienskies.tournament.blockentity.FuelTankBlockEntity
import org.valkyrienskies.tournament.util.extension.pose

class TransparentFuelTankBlockEntityRender:
    BlockEntityRenderer<FuelTankBlockEntity>
{

    override fun render(
        be: FuelTankBlockEntity,
        partial: Float,
        pose: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        RenderSystem.disableCull()

        if (be.wholeShipFillLevelSynced > 0.05f) {
            val byDir = List(Direction.entries.size) { index ->
                if (be.neighborsTransparent[index]) {
                    0.0
                } else {
                    0.01
                }
            }

            val x = byDir[Direction.WEST.ordinal]
            val y = byDir[Direction.DOWN.ordinal]
            val z = byDir[Direction.NORTH.ordinal]

            val nx = byDir[Direction.EAST.ordinal]
            val ny = byDir[Direction.UP.ordinal]
            val nz = byDir[Direction.SOUTH.ordinal]

            pose.pose {
                translate(x, y, z)

                scale(
                    1.0f - (x + nx).toFloat(),
                    (1.0f - (y + ny).toFloat()) * be.wholeShipFillLevelSynced,
                    1.0f - (z + nz).toFloat()
                )

                TournamentModels.SOLID_FUEL.renderer.render(
                    pose,
                    be,
                    bufferSource,
                    packedLight,
                    packedOverlay
                )
            }
        }

        TournamentModels.FUEL_TANK_FULL_TRANSPARENT.renderer.render(
            pose,
            be,
            bufferSource,
            packedLight,
            packedOverlay
        )

        RenderSystem.enableCull()
    }

}