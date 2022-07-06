package dev.mrshawn.deathmessages.kotlin.files

import dev.mrshawn.deathmessages.DeathMessages
import dev.mrshawn.deathmessages.files.Config
import dev.mrshawn.deathmessages.files.FileSettings

object FileStore {

	val CONFIG: FileSettings =
		FileSettings(DeathMessages.getInstance(), "Settings.yml").loadSettings(Config::class.java)

}