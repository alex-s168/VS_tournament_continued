package org.valkyrienskies.tournament.items

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
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
import net.minecraft.world.level.storage.loot.parameters.LootContextParam
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import net.minecraft.world.level.storage.loot.parameters.LootContextParams
import org.valkyrienskies.tournament.TournamentItems

class GiftBagItem : Item(
    Properties().stacksTo(1).tab(TournamentItems.TAB)
) {

    companion object {
        fun of(lootTable: String, tooltip: String, rarity: Rarity): ItemStack {
            val stack = ItemStack(TournamentItems.GIFT_BAG.get())

            val lootTag = CompoundTag()
            lootTag.putString("table", lootTable)
            stack.addTagElement("loot", lootTag)

            val tooltipTag = CompoundTag()
            tooltipTag.putString("tooltip", tooltip)
            stack.addTagElement("tooltip", tooltipTag)

            val rarityTag = CompoundTag()
            rarityTag.putString("rarity", rarity.name)
            stack.addTagElement("rarity", rarityTag)

            return stack
        }
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (level as? ServerLevel == null) return InteractionResultHolder.fail(player.getItemInHand(usedHand))

        val stack = player.getItemInHand(usedHand)

        stack.getTagElement("loot")?.let {
            val table = ResourceLocation.of(it.getString("table"), ':')
            val ctx = LootContext.Builder(level)
                .withLuck(player.luck)
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .create(LootContextParamSets.PIGLIN_BARTER)
            println(table)
            println(level.server.lootTables.ids.contains(table))
            val loot = level.server.lootTables.get(table).getRandomItems(ctx)
            println(loot)

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
        stack.getTagElement("tooltip")?.let {
            tooltipComponents.add(TranslatableComponent(it.getString("tooltip")))
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