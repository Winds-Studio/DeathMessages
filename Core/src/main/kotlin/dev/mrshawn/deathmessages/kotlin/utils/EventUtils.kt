package dev.mrshawn.deathmessages.kotlin.utils

import dev.mrshawn.deathmessages.DeathMessages
import org.bukkit.Bukkit
import org.bukkit.event.Listener

object EventUtils {

	fun registerEvents(vararg listeners: Listener) {
		val pluginManager = Bukkit.getPluginManager()
		listeners.forEach { listener ->
			pluginManager.registerEvents(listener, DeathMessages.getInstance())
		}
	}

}