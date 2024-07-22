package org.valkyrienskies.tournament.blockentity.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
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
        pose.pose {
            translate(0.1, 0.1, 0.1)
            scale(0.8f, 0.8f * be.wholeShipFillLevelSynced, 0.8f)
            TournamentModels.SOLID_FUEL.renderer.render(
                this,
                be,
                bufferSource,
                packedLight,
                packedOverlay
            )
        }
    }

}