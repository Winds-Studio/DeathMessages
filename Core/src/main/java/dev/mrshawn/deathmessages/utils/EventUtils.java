package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import org.bukkit.event.Listener;

public class EventUtils {

	public static void registerEvents(Listener... listeners) {
		for (Listener listener : listeners) {
			DeathMessages.getInstance().getServer().getPluginManager().registerEvents(listener, DeathMessages.getInstance());
		}
	}
}