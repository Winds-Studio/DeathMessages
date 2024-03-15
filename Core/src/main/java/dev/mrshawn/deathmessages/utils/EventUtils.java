package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

public class EventUtils {

	public static void registerEvents(Listener... listeners) {
		PluginManager pluginManager = Bukkit.getPluginManager();

		for (Listener listener : listeners) {
			pluginManager.registerEvents(listener, DeathMessages.getInstance());
		}
	}
}