package dev.mrshawn.deathmessages.kotlin.utils.items

import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

class ItemBuilder(material: Material, amount: Int = 1) {

	private val item = ItemStack(material, amount)
	private val meta = item.itemMeta
	private val lore = mutableListOf<String>()

	companion object {
		fun glow(item: ItemStack): ItemStack {
			val meta = item.itemMeta
			meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS)
			meta?.addEnchant(Enchantment.DURABILITY, 1, true)
			item.itemMeta = meta
			return item
		}
	}

	fun name(name: String): ItemBuilder {
		meta?.setDisplayName(name)
		return this
	}

	fun setNoName(): ItemBuilder {
		name(" ")
		return this
	}

	fun addLoreLine(line: String): ItemBuilder {
		lore.add(line)
		return this
	}

	fun addLoreLines(lines: Array<String>): ItemBuilder {
		lore.addAll(lines.map { it })
		return this
	}

	fun addLoreLines(lines: MutableList<String>): ItemBuilder {
		lore.addAll(lines.map { it })
		return this
	}

	private fun addEnchantment(enchantment: Enchantment, level: Int): ItemBuilder {
		meta?.addEnchant(enchantment, level, true)
		return this
	}

	private fun glow(): ItemBuilder {
		if (meta?.hasEnchants() == false) {
			hideAttributes()
			addEnchantment(Enchantment.DURABILITY, 1)
		}
		return this
	}

	fun glowIf(condition: () -> Boolean): ItemBuilder {
		if (condition.invoke()) glow()
		return this
	}

	fun addData(key: NamespacedKey, value: String): ItemBuilder {
		val container = meta?.persistentDataContainer
		container?.set(key, PersistentDataType.STRING, value)
		return this
	}

	fun addData(key: NamespacedKey, value: Int): ItemBuilder {
		val container = meta?.persistentDataContainer
		container?.set(key, PersistentDataType.INTEGER, value)
		return this
	}

	fun setCustomModelData(data: Int): ItemBuilder {
		meta?.setCustomModelData(data)
		return this
	}

	private fun addItemFlag(flag: ItemFlag): ItemBuilder {
		meta?.addItemFlags(flag)
		return this
	}

	private fun hideAttributes(): ItemBuilder = addItemFlag(ItemFlag.HIDE_ATTRIBUTES)

	fun build(): ItemStack {
		meta?.lore = lore
		item.itemMeta = meta
		return item
	}

}