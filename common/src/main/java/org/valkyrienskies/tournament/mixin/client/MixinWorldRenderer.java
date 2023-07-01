package org.valkyrienskies.tournament.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.valkyrienskies.tournament.TournamentDebugHelper;
import org.valkyrienskies.tournament.api.debug.DebugLine;
import org.valkyrienskies.tournament.api.helper.Helper3d;

import java.awt.*;

@Mixin(LevelRenderer.class)
public abstract class MixinWorldRenderer {

    @Inject(method = "renderLevel", at = @At(value = "CONSTANT", args = "stringValue=blockentities", ordinal = 0))
    private void afterEntities(PoseStack poseStack, float partialTick, long finishNanoTime, boolean renderBlockOutline, Camera camera, GameRenderer gameRenderer, LightTexture lightTexture, Matrix4f projectionMatrix, CallbackInfo ci) {
        final MultiBufferSource.BufferSource bufferSource =
                MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());

        TournamentDebugHelper.Companion.list().forEach((k, v)->{
            VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.lines());

            Vector3d cam = new Vector3d(camera.getPosition().x, camera.getPosition().y, camera.getPosition().z);

            if (v instanceof DebugLine line && v.getAlways()) {
                assert Minecraft.getInstance().level != null;
                Vector3d A = Helper3d.INSTANCE.convertShipToWorldSpace(Minecraft.getInstance().level, line.getA()).sub(cam);
                Vector3d B = Helper3d.INSTANCE.convertShipToWorldSpace(Minecraft.getInstance().level, line.getB()).sub(cam);
                Vector3f normal = new Vector3f((Vector3fc) A.sub(B).normalize());

                Color c = line.getColor();

                vertexConsumer.vertex(A.x, A.y, A.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).normal(normal.x, normal.y, normal.z).endVertex();
                vertexConsumer.vertex(B.x, B.y, B.z).color(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha()).normal(normal.x, normal.y, normal.z).endVertex();
            }
        });
        bufferSource.endBatch();
    }

}
