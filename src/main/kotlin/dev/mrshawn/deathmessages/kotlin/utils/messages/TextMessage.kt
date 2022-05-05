package dev.mrshawn.deathmessages.kotlin.utils.messages

import org.bukkit.command.CommandSender

class TextMessage {

	val messages: ArrayList<String> = ArrayList()
	private val replacements: ArrayList<TextReplacement> = ArrayList()

	fun addMessages(vararg messages: String?): TextMessage {
		messages.forEach {
			if (it != null) this.messages.add(it)
		}
		return this
	}

	fun addMessages(vararg messages: List<String>?): TextMessage {
		messages.forEach { if (it != null) this.messages.addAll(it) }
		return this
	}

	fun addReplacements(vararg replacements: TextReplacement?): TextMessage {
		replacements.forEach { if (it != null) this.replacements.add(it) }
		return this
	}

	private fun replaceAll(input: String): String {
		var output = input
		replacements.forEach {
			output = it.replace(output)
		}
		return output
	}

	fun getMessage(): ArrayList<String> {
		val output = ArrayList<String>()
		messages.forEach {
			output.add(replaceAll(it))
		}
		return output
	}

}

fun Chat.tell(toWhom: CommandSender, message: TextMessage) {
	tell(toWhom, message.getMessage())
}