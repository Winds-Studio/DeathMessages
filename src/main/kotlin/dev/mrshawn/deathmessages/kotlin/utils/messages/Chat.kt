package dev.mrshawn.deathmessages.kotlin.utils.messages

import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

object Chat {

	private fun tell(toWhom: CommandSender?, message: String?) {
		if (toWhom != null && message != null) toWhom.sendMessage(message)
	}

	fun tell(toWhom: CommandSender, messages: Array<String?>) = run {
		for (message in messages) {
			tell(toWhom, message)
		}
	}

	fun tell(toWhom: CommandSender, messages: ArrayList<String>) = run {
		for (message in messages) {
			tell(toWhom, message)
		}
	}

	private fun log(message: String?) {
		if (message != null) Bukkit.getConsoleSender().sendMessage(message)
	}

	fun error(message: String?) {
		if (message != null) log("&4[ERROR] $message")
	}

	fun broadcast(message: Component?) {
		if (message != null) Bukkit.broadcast(message)
	}

	fun clearChat() = run {
		for (i in 0..100) {
			Bukkit.broadcast(Component.text(" "))
		}
	}
}
