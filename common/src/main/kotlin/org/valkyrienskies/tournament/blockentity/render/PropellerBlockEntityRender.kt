package org.valkyrienskies.tournament.blockentity.render

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Vector3f
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.world.level.block.DirectionalBlock
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
            pose.mulPose(Vector3f.YP.rotationDegrees(be.rotation.toFloat()))
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