package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.Config;
import dev.mrshawn.deathmessages.config.legacy.EntityDeathMessages;
import dev.mrshawn.deathmessages.enums.MobType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;

import java.util.Optional;
import java.util.Set;

public class EntityDamageByBlock implements Listener {

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDeath(EntityDamageByBlockEvent e) {
		if (e.getEntity() instanceof Player && Bukkit.getServer().getOnlinePlayers().contains((Player) e.getEntity())) {
			Player p = (Player) e.getEntity();
			Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(p);
			getPlayer.ifPresent(pm -> pm.setLastDamageCause(e.getCause()));
		} else {
			if (EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Entities") == null) return;
			Set<String> listenedMobs = EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Entities")
					.getKeys(false);
			if (EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Mythic-Mobs-Entities") != null
					&& DeathMessages.getInstance().mythicmobsEnabled) {
				listenedMobs.addAll(EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Mythic-Mobs-Entities")
						.getKeys(false));
			}

			if (listenedMobs.isEmpty()) return;

			for (String listened : listenedMobs) {
				if (listened.contains(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase())) {
					Optional<EntityManager> getEntity = EntityManager.getEntity(e.getEntity().getUniqueId());
					if (Config.settings.DEBUG()) System.out.println(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase());
					getEntity.ifPresent(em -> em.setLastDamageCause(e.getCause()));
					if (!getEntity.isPresent()) {
						MobType mobType = MobType.VANILLA;
						if (DeathMessages.getInstance().mythicmobsEnabled
								&& DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
							mobType = MobType.MYTHIC_MOB;
						}
						new EntityManager(e.getEntity(), e.getEntity().getUniqueId(), mobType);
					}
				}
			}
		}
	}
}

