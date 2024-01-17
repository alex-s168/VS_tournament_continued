package org.valkyrienskies.tournament.items

import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import org.valkyrienskies.tournament.TournamentItems

class GiftBagItem : Item(
    Properties().stacksTo(1).tab(TournamentItems.TAB)
) {

    companion object {
        fun of(tooltip: String, rarity: Rarity): ItemStack {
            val stack = ItemStack(TournamentItems.GIFT_BAG.get())

            val tooltipTag = CompoundTag()
            tooltipTag.putString("tooltip", tooltip)
            stack.addTagElement("tooltip", tooltipTag)

            val rarityTag = CompoundTag()
            rarityTag.putString("rarity", rarity.name)
            stack.addTagElement("rarity", rarityTag)

            return stack
        }

        fun of(lootTable: String, tooltip: String, rarity: Rarity): ItemStack {
            val stack = of(tooltip, rarity)

            val lootTag = CompoundTag()
            lootTag.putString("table", lootTable)
            stack.addTagElement("loot", lootTag)

            return stack
        }

        fun of(items: List<ItemStack>, tooltip: String, rarity: Rarity): ItemStack {
            val stack = of(tooltip, rarity)

            val itemTag = CompoundTag()
            itemTag.putInt("size", items.size)
            items.forEachIndexed { i, item ->
                itemTag.put("elem_$i", item.save(CompoundTag()))
            }
            stack.addTagElement("items", itemTag)

            return stack
        }
    }


    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        val stack = player.getItemInHand(usedHand)

        if (!(stack.getTagElement("items") != null || stack.getTagElement("loot") != null)) {
            /*
            if (level.isClientSide) {
                val cont = SimpleContainer(9)
                cont.startOpen(player)
                Minecraft.getInstance().setScreen(GiftBagScreen(cont, -1, player.inventory) {
                    println("close")
                    println(cont.getItem(0).item.builtInRegistryHolder().key().registry().toString())
                    cont.stopOpen(player)
                })
            }

             */
            return InteractionResultHolder.success(stack)
        }

        if (level as? ServerLevel == null) return InteractionResultHolder.fail(player.getItemInHand(usedHand))

        stack.getTagElement("items")?.let { tag ->
            for (i in 0 until tag.getInt("size")) {
                val s = ItemStack.of(tag.getCompound("elem_$i"))
                if (!player.inventory.add(s)) {
                    player.drop(s, false)
                }
            }
        }

        stack.getTagElement("loot")?.let {
            val table = ResourceLocation.of(it.getString("table"), ':')
            val ctx = LootContext.Builder(level)
                .withLuck(player.luck)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.PIGLIN_BARTER)
            val loot = level.server.lootTables.get(table).getRandomItems(ctx)

            loot.forEach { itemStack ->
                if (!player.inventory.add(itemStack)) {
                    player.drop(itemStack, false)
                }
            }
        }

        stack.shrink(1)
        return InteractionResultHolder.consume(stack)
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        val tag = stack.getTagElement("tooltip")
        if (tag == null) {
            tooltipComponents.add(TranslatableComponent("tooltip.vs_tournament.gift.none"))
        }
        else {
            tooltipComponents.add(TranslatableComponent(tag.getString("tooltip")))
        }
    }

    override fun getRarity(stack: ItemStack): Rarity {
        stack.getTagElement("rarity")?.let {
            return Rarity.valueOf(it.getString("rarity"))
        }

        return Rarity.COMMON
    }

    override fun isEnchantable(stack: ItemStack): Boolean = false

}