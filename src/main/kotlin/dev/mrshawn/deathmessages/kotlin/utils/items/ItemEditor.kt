package dev.mrshawn.deathmessages.kotlin.utils.items

import dev.mrshawn.deathmessages.kotlin.utils.messages.Chat
import org.bukkit.Bukkit
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType

object ItemEditor {

	private fun getItemMeta(item: ItemStack): ItemMeta {
		return item.itemMeta ?: Bukkit.getItemFactory().getItemMeta(item.type)!!
	}

	fun rename(item: ItemStack, name: String): ItemStack {
		val meta = getItemMeta(item)
		meta.setDisplayName(Chat.colorize(name))
		item.itemMeta = meta
		return item
	}

	fun relore(item: ItemStack, lore: Array<String>): ItemStack {
		val meta = getItemMeta(item)
		meta.lore = lore.map { Chat.colorize(it) }.toMutableList()
		item.itemMeta = meta
		return item
	}

	fun amount(item: ItemStack, amount: Int): ItemStack {
		item.amount = amount
		return item
	}

	fun glow(item: ItemStack): ItemStack {
		val meta = getItemMeta(item)
		meta.addEnchant(Enchantment.DURABILITY, 1, true)
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS)
		item.itemMeta = meta
		return item
	}

	fun unglow(item: ItemStack): ItemStack {
		val meta = getItemMeta(item)
		meta.removeEnchant(Enchantment.DURABILITY)
		meta.removeItemFlags(ItemFlag.HIDE_ENCHANTS)
		item.itemMeta = meta
		return item
	}

	fun getData(item: ItemStack, key: NamespacedKey): String? {
		return getItemMeta(item).persistentDataContainer
			.get(key, PersistentDataType.STRING)
	}

	fun getAllKeys(item: ItemStack) = item.itemMeta?.persistentDataContainer?.keys

}
