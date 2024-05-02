package org.valkyrienskies.tournament.blockentity.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.world.level.block.DirectionalBlock
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.valkyrienskies.tournament.TournamentModels
import org.valkyrienskies.tournament.blockentity.PropellerBlockEntity
import org.valkyrienskies.tournament.util.extension.pose

class PropellerBlockEntityRender<T: PropellerBlockEntity<T>>(
    val model: TournamentModels.Model
): BlockEntityRenderer<T> {

    override fun render(
        be: T,
        partial: Float,
        pose: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        pose.pose {
            translate(0.5, 0.5, 0.5)
            mulPose(be.blockState.getValue(DirectionalBlock.FACING).opposite.rotation)
            pose.mulPose(Quaternionf(AxisAngle4f(Math.toRadians(be.rotation).toFloat(), 0f, 1f, 0f)))
            translate(-0.5, -0.5, -0.5)
            model.renderer.render(
                pose,
                be,
                bufferSource,
                packedLight,
                packedOverlay
            )
        }
    }

}