package org.valkyrienskies.tournament.screen

import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ChestMenu
import net.minecraft.world.inventory.MenuType

@Environment(EnvType.CLIENT)
class GiftBagScreen(
    cont: Container,
    contId: Int,
    inv: Inventory,
    val closeFun: () -> Unit
): AbstractContainerScreen<ChestMenu>(
    ChestMenu(
        MenuType.GENERIC_9x1,
        contId,
        inv,
        cont,
        1
    ),
    inv,
    TranslatableComponent("screen.vs_tournament.gift_bag.title")
) {

    private val TEXUTRE_LOCATION = ResourceLocation("vs_tournament", "textures/gui/gift_bag.png")


    override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
        this.renderBackground(poseStack)
        super.render(poseStack, mouseX, mouseY, partialTick)
        this.renderTooltip(poseStack, mouseX, mouseY)
    }

    override fun renderBg(poseStack: PoseStack, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionTexShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXUTRE_LOCATION)
        val i = (width - imageWidth) / 2
        val j = (height - imageHeight) / 2
        this.blit(poseStack, i, j, 0, 0, imageWidth, imageHeight)
    }

    override fun onClose() {
        super.onClose()
        closeFun()
    }

}