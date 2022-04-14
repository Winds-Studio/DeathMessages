package dev.mrshawn.deathmessages.kotlin.utils

import dev.mrshawn.deathmessages.DeathMessages
import org.bukkit.Bukkit
import org.bukkit.entity.LivingEntity
import org.bukkit.event.Event
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent

object EventUtils {

	fun registerEvents(vararg listeners: Listener) {
		val pluginManager = Bukkit.getPluginManager()
		listeners.forEach { listener ->
			pluginManager.registerEvents(listener, DeathMessages.getInstance())
		}
	}

}