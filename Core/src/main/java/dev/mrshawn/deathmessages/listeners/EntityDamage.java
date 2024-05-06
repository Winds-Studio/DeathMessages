package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Optional;
import java.util.Set;

public class EntityDamage implements Listener {


	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.isCancelled()) return;

		if (e.getEntity() instanceof Player && Bukkit.getServer().getOnlinePlayers().contains((Player) e.getEntity())) {
			Player p = (Player) e.getEntity();
			Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(p);
			getPlayer.ifPresent(pm -> {
				pm.setLastDamageCause(e.getCause());

				if ((DeathMessages.majorVersion >= 20 && DeathMessages.minorVersion >= 3) && e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
					// For >= 1.20.3, because TNT explosion became BLOCK_EXPLOSION since 1.20.3
					if (e.getDamageSource().getDirectEntity() instanceof TNTPrimed) {
						TNTPrimed tnt = (TNTPrimed) e.getDamageSource().getDirectEntity();
						if (tnt.getSource() instanceof LivingEntity) {
							pm.setLastEntityDamager(tnt.getSource());
						}
						pm.setLastExplosiveEntity(tnt);
					}
				}
			});
			// for fall large if ppl want it float dist = e.getEntity().getFallDistance();
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
					if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath())) System.out.println(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase());
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