package dev.mrshawn.deathmessages.kotlin.utils.messages

class TextReplacement {

	constructor(key: String, value: String) {
		addReplacement(key, value)
	}

	constructor(vararg replacements: Pair<String, String>) {
		replacements.forEach { addReplacement(it) }
	}

	private val replacements: MutableMap<String, String> = HashMap()

	fun of(vararg replacements: Pair<String, String>): TextReplacement {
		replacements.forEach {
			addReplacement(it)
		}
		return this
	}

	fun addReplacement(key: String, value: String): TextReplacement {
		replacements[key] = value
		return this
	}

	fun addReplacement(replacement: Pair<String, String>): TextReplacement {
		replacements[replacement.first] = replacement.second
		return this
	}

	fun replace(input: String): String {
		var output = input
		replacements.forEach {
			output = output.replace(it.key, it.value)
		}
		return output
	}

}