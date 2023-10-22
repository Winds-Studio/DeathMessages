package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.enums.MobType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

public class EntityDamage implements Listener {


	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.isCancelled()) return;
		if (e.getEntity() instanceof Player p && Bukkit.getOnlinePlayers().contains(e.getEntity())) {
            PlayerManager pm = PlayerManager.getPlayer(p);
			pm.setLastDamageCause(e.getCause());
			// for fall large if ppl want it float dist = e.getEntity().getFallDistance();
		} else if (!(e.getEntity() instanceof Player)) {
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
					EntityManager em;
					if (EntityManager.getEntity(e.getEntity().getUniqueId()) == null) {
						MobType mobType = MobType.VANILLA;
						if (DeathMessages.getInstance().mythicmobsEnabled
								&& DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
							mobType = MobType.MYTHIC_MOB;
						}
						em = new EntityManager(e.getEntity(), e.getEntity().getUniqueId(), mobType);
					} else {
						em = EntityManager.getEntity(e.getEntity().getUniqueId());
					}
					em.setLastDamageCause(e.getCause());
				}
			}
		}
	}

}