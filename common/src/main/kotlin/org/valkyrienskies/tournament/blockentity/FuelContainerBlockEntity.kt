package org.valkyrienskies.tournament.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.Packet
import net.minecraft.network.protocol.game.ClientGamePacketListener
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket
import net.minecraft.network.protocol.game.ClientboundUpdateTagsPacket
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.datafix.fixes.BlockEntityKeepPacked
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.blocks.FuelContainerBlock
import org.valkyrienskies.tournament.util.getThrusterFuelValue

class FuelContainerBlockEntity(
    pos: BlockPos,
    state: BlockState,
    val cap: Int
): BlockEntity(
    TournamentBlockEntities.FUEL_CONTAINER_FULL.get(),
    pos,
    state
) {

    var amount: Double = 0.0

    override fun saveAdditional(tag: CompoundTag) {
        tag.putDouble("amount", amount)

        println("saved: $amount")

        super.saveAdditional(tag)
    }

    override fun load(tag: CompoundTag) {
        amount = tag.getDouble("amount")

        println("loaded: $amount")

        super.load(tag)
    }

    fun getContainer(): CustomContainer {
        return CustomContainer(this)
    }

    override fun getUpdatePacket(): Packet<ClientGamePacketListener> =
        ClientboundBlockEntityDataPacket.create(this)

    override fun getUpdateTag(): CompoundTag =
        CompoundTag().also { saveAdditional(it) }

    fun sendUpdate() {
        level as ServerLevel
        level!!.sendBlockUpdated(
            worldPosition,
            level!!.getBlockState(worldPosition),
            level!!.getBlockState(worldPosition),
            3
        )
        level!!.getChunk(worldPosition).isUnsaved = true
    }

    class CustomContainer(
        val be: FuelContainerBlockEntity
    ): WorldlyContainer {
        override fun clearContent() {
            be.amount = 0.0
        }

        override fun getContainerSize(): Int =
            be.cap

        override fun isEmpty(): Boolean =
            (be.amount == 0.0)

        override fun getItem(slot: Int): ItemStack =
            ItemStack(Items.AIR)

        override fun removeItem(slot: Int, amount: Int): ItemStack {
            be.amount -= amount
            be.sendUpdate()
            return ItemStack(Items.AIR)
        }

        override fun removeItemNoUpdate(slot: Int): ItemStack {
            be.amount --
            return ItemStack(Items.AIR)
        }

        override fun setItem(slot: Int, stack: ItemStack) {
            be.amount += stack.getThrusterFuelValue() ?: return
            be.sendUpdate()
        }

        override fun setChanged() {
            be.sendUpdate()
        }

        override fun stillValid(player: Player): Boolean =
            true

        override fun getSlotsForFace(side: Direction): IntArray =
            (0 until be.cap).toList().toIntArray()

        override fun canPlaceItemThroughFace(index: Int, stack: ItemStack, direction: Direction?): Boolean {
            stack.getThrusterFuelValue() ?.let {
                return (be.amount + it <= be.cap)
            }
            return false
        }

        override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean =
            false

    }
}