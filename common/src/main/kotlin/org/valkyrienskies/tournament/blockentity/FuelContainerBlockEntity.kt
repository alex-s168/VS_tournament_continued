package org.valkyrienskies.tournament.blockentity

import com.google.common.util.concurrent.AtomicDouble
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.WorldlyContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.valkyrienskies.core.api.ships.ServerShip
import org.valkyrienskies.mod.common.getShipManagingPos
import org.valkyrienskies.tournament.TournamentBlockEntities
import org.valkyrienskies.tournament.storage.ShipFuelStorage
import org.valkyrienskies.tournament.util.getThrusterFuelValue
import java.util.concurrent.CopyOnWriteArrayList

class FuelContainerBlockEntity(
    pos: BlockPos,
    state: BlockState,
    val cap: Int
): BlockEntity(
    TournamentBlockEntities.FUEL_CONTAINER_FULL.get(),
    pos,
    state
) {

    var amount = AtomicDouble(0.0)

    override fun getUpdateTag(): CompoundTag {
        val tag = CompoundTag()
        saveAdditional(tag)
        return tag
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket? {
        return ClientboundBlockEntityDataPacket.create(this)
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.putDouble("amount", amount.get())
    }

    override fun load(tag: CompoundTag) {
        amount.set(tag.getDouble("amount"))
        val ship = level!!.getShipManagingPos(worldPosition)
        ship?.let {
            ship as ServerShip
            val li = ShipFuelStorage.ships.getOrPut(ship) { CopyOnWriteArrayList() }
            if (li.contains(amount))
                li[li.indexOf(amount)] = amount
            else
                li.add(amount)
        }
    }

    companion object {
        fun tick(level: Level, pos: BlockPos, state: BlockState, be: BlockEntity) {
            be as FuelContainerBlockEntity
        }
    }

    fun getContainer(): CustomContainer {
        return CustomContainer(this)
    }

    fun sendUpdate() =
        level!!.sendBlockUpdated(worldPosition, level!!.getBlockState(worldPosition), level!!.getBlockState(worldPosition), 3)

    class CustomContainer(
        val be: FuelContainerBlockEntity
    ): WorldlyContainer {
        override fun clearContent() {
            be.amount.set(0.0)
        }

        override fun getContainerSize(): Int =
            be.cap

        override fun isEmpty(): Boolean =
            (be.amount.get() == 0.0)

        override fun getItem(slot: Int): ItemStack =
            ItemStack(Items.AIR)

        override fun removeItem(slot: Int, amount: Int): ItemStack {
            be.amount.set(be.amount.get() - amount)
            be.sendUpdate()
            return ItemStack(Items.AIR)
        }

        override fun removeItemNoUpdate(slot: Int): ItemStack {
            be.amount.set(be.amount.get() - 1)
            return ItemStack(Items.AIR)
        }

        override fun setItem(slot: Int, stack: ItemStack) {
            be.amount.set(be.amount.get() + stack.getThrusterFuelValue()!!)
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
                return (be.amount.get() + it <= be.cap)
            }
            return false
        }

        override fun canTakeItemThroughFace(index: Int, stack: ItemStack, direction: Direction): Boolean =
            false

    }
}