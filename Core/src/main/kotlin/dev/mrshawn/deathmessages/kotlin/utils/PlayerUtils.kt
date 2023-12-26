package dev.mrshawn.deathmessages.kotlin.utils

import org.bukkit.Bukkit
import org.bukkit.entity.Player

object PlayerUtils {

	fun isPlayer(name: String): Boolean = Bukkit.getPlayer(name) != null

	fun getPlayer(name: String): Player? = Bukkit.getPlayer(name)

}
