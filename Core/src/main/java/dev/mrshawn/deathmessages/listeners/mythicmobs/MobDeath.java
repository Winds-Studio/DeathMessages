package dev.mrshawn.deathmessages.listeners.mythicmobs;

import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Config;
import dev.mrshawn.deathmessages.config.legacy.Settings;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.utils.Assets;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MobDeath implements Listener {

	@EventHandler
	public void onMythicMobDeath(MythicMobDeathEvent e) {
		if (getEntityDeathMessages().getConfigurationSection("Mythic-Mobs-Entities").getKeys(false).isEmpty()) return;
		for (String customMobs : getEntityDeathMessages().getConfigurationSection("Mythic-Mobs-Entities").getKeys(false)) {
			if (e.getMob().getType().getInternalName().equals(customMobs)) {
				Optional<EntityManager> getEntity = EntityManager.getEntity(e.getEntity().getUniqueId());
				getEntity.ifPresent(em -> {
					PlayerManager damager = em.getLastPlayerDamager();
					TextComponent mythicDeath = Assets.entityDeathMessage(em, MobType.MYTHIC_MOB);
					BroadcastEntityDeathMessageEvent event = new BroadcastEntityDeathMessageEvent(damager, e.getEntity(), MessageType.ENTITY, mythicDeath, getWorlds(e.getEntity()));
					Bukkit.getPluginManager().callEvent(event);
				});
			}
		}
	}

	public static List<World> getWorlds(Entity e) {
		List<World> broadcastWorlds = new ArrayList<>();
		if (Config.settings.DISABLED_WORLDS().contains(e.getWorld().getName())) {
			return broadcastWorlds;
		}
		if (Config.settings.PER_WORLD_MESSAGES()) {
			for (String groups : Settings.getInstance().getConfig().getConfigurationSection("World-Groups").getKeys(false)) {
				List<String> worlds = Settings.getInstance().getConfig().getStringList("World-Groups." + groups);
				if (worlds.contains(e.getWorld().getName())) {
					for (String single : worlds) {
						broadcastWorlds.add(Bukkit.getWorld(single));
					}
				}
			}
			if (broadcastWorlds.isEmpty()) {
				broadcastWorlds.add(e.getWorld());
			}
		} else {
			return Bukkit.getWorlds();
		}
		return broadcastWorlds;
	}

	public static FileConfiguration getEntityDeathMessages() {
		return Assets.getEntityDeathMessages();
	}
}
