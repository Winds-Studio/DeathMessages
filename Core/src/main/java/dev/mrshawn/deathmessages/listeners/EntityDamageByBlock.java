package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.EntityUtil;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
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
			ConfigurationSection entityConfig = EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Entities");

			if (entityConfig == null) return;

			Set<String> listenedMobs = entityConfig.getKeys(false);
			ConfigurationSection mobConfig = EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Mythic-Mobs-Entities");

			if (mobConfig != null && DeathMessages.getInstance().mythicmobsEnabled) {
				listenedMobs.addAll(mobConfig.getKeys(false));
			}

			if (listenedMobs.isEmpty()) return;

			for (String listened : listenedMobs) {
				if (listened.contains(EntityUtil.getConfigNodeByEntity(e.getEntity()))) {
					Optional<EntityManager> getEntity = EntityManager.getEntity(e.getEntity().getUniqueId());

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

