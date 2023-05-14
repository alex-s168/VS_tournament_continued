package org.valkyrienskies.tournament.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import de.m_marvin.univec.impl.Vec3d;
import de.m_marvin.univec.impl.Vec3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.tournament.TournamentDebugHelper;
import org.valkyrienskies.tournament.api.debug.DebugLine;
import org.valkyrienskies.tournament.api.debug.DebugObject;
import org.valkyrienskies.tournament.api.helper.Helper3d;

import java.awt.*;

@Mixin(DebugRenderer.class)
public class MixinDebugRenderer {

    @Inject(method = "render", at = @At("HEAD"))
    private void postRender(final PoseStack matricesIgnore, final MultiBufferSource.BufferSource vertexConsumersIgnore,
                            final double cameraX, final double cameraY, final double cameraZ, final CallbackInfo ci) {
        final MultiBufferSource.BufferSource bufferSource =
                MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        if (Minecraft.getInstance().getEntityRenderDispatcher().shouldRenderHitBoxes()) {
            for (DebugObject obj : TournamentDebugHelper.Companion.query()) {
                VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

                Vec3d cam = new Vec3d(cameraX, cameraY, cameraZ);

                if (obj instanceof DebugLine line) {
                    assert Minecraft.getInstance().level != null;
                    Vec3d A = Helper3d.INSTANCE.MaybeShipToWorldspace(Minecraft.getInstance().level, line.getA()).sub(cam);
                    Vec3d B = Helper3d.INSTANCE.MaybeShipToWorldspace(Minecraft.getInstance().level, line.getB()).sub(cam);
                    Vec3f normal = new Vec3f(A.sub(B).normalize());

                    Color c = line.getColor();

                    vertexConsumer.vertex(A.x, A.y, A.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).normal(normal.x, normal.y, normal.z).endVertex();
                    vertexConsumer.vertex(B.x, B.y, B.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).normal(normal.x, normal.y, normal.z).endVertex();
                }
            }
            bufferSource.endBatch();
        }
    }

}
