package org.valkyrienskies.tournament.blockentity.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.world.level.block.DirectionalBlock
import org.valkyrienskies.tournament.TournamentModels
import org.valkyrienskies.tournament.blockentity.PropellerBlockEntity
import org.valkyrienskies.tournament.util.extension.pose

class PropellerBlockEntityRender:
    BlockEntityRenderer<PropellerBlockEntity>
{

    override fun render(
        be: PropellerBlockEntity,
        partial: Float,
        pose: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int
    ) {
        pose.pose {
            pose.translate(0.5, 0.5, 0.5)
            pose.mulPose(be.blockState.getValue(DirectionalBlock.FACING).opposite.rotation)
            // pose.mulPose(Vector3f.ZP.rotationDegrees(20.0f))
            pose.translate(-0.5, -0.5, -0.5)
            TournamentModels.PROP_BIG.renderer.render(
                pose,
                be,
                bufferSource,
                packedLight,
                packedOverlay
            )
        }
    }

}