package org.valkyrienskies.tournament.util.extension

import com.mojang.blaze3d.vertex.PoseStack

fun PoseStack.pose(block: PoseStack.() -> Unit) {
    pushPose()
    block()
    popPose()
}