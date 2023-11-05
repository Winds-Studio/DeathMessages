package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.enums.MobType;
import org.bukkit.Bukkit;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EntityDamageByEntity implements Listener {

	public static final Map<UUID, Entity> explosions = new HashMap<>();

	@EventHandler
	public void entityDamageByEntity(EntityDamageByEntityEvent e) {
		if (e.getEntity() instanceof Player p && Bukkit.getOnlinePlayers().contains(e.getEntity())) {
            Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(p);
			getPlayer.ifPresent(pm -> {
				if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
					if (e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager().getUniqueId())) {
						pm.setLastEntityDamager(explosions.get(e.getDamager().getUniqueId()));
						pm.setLastExplosiveEntity(e.getDamager());
					} else if (e.getDamager() instanceof TNTPrimed tnt) {
						if (tnt.getSource() instanceof LivingEntity) {
							pm.setLastEntityDamager(tnt.getSource());
						}
						pm.setLastExplosiveEntity(e.getDamager());
					} else if (e.getDamager() instanceof Firework firework) {
						try {
							if (firework.getShooter() instanceof LivingEntity) {
								pm.setLastEntityDamager((LivingEntity) firework.getShooter());
							}
							pm.setLastExplosiveEntity(e.getDamager());
						} catch (NoSuchMethodError e2) {
							//McMMO ability
							e2.printStackTrace();
						}
					} else {
						pm.setLastEntityDamager(e.getDamager());
						pm.setLastExplosiveEntity(e.getDamager());
					}
				} else if (e.getDamager() instanceof Projectile projectile) {
					if (projectile.getShooter() instanceof LivingEntity) {
						pm.setLastEntityDamager((LivingEntity) projectile.getShooter());
					}
					pm.setLastProjectileEntity(projectile);
				} else if (e.getDamager() instanceof FallingBlock) {
					pm.setLastEntityDamager(e.getDamager());
				} else if (e.getDamager().getType().isAlive()) {
					pm.setLastEntityDamager(e.getDamager());
				} else if (e.getDamager() instanceof EvokerFangs evokerFangs) {
					pm.setLastEntityDamager(evokerFangs.getOwner());
				}
			});
		} else if (!(e.getEntity() instanceof Player) && e.getDamager() instanceof Player) {
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
				if (listened.contains(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase())
						|| (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId()))) {
					Optional<EntityManager> getEntity = EntityManager.getEntity(e.getEntity().getUniqueId());
					getEntity.ifPresentOrElse(em -> {
						if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
							if (e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager())) {
								if (explosions.get(e.getDamager().getUniqueId()) instanceof Player) {
									Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) explosions.get(e.getDamager().getUniqueId()));
									getPlayer.ifPresent(em::setLastPlayerDamager);
									em.setLastExplosiveEntity(e.getDamager());
								}
							} else if (e.getDamager() instanceof TNTPrimed tnt) {
								if (tnt.getSource() instanceof Player) {
									Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) tnt.getSource());
									getPlayer.ifPresent(em::setLastPlayerDamager);
								}
								em.setLastExplosiveEntity(e.getDamager());
							} else if (e.getDamager() instanceof Firework firework) {
								try {
									if (firework.getShooter() instanceof Player) {
										Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) firework.getShooter());
										getPlayer.ifPresent(em::setLastPlayerDamager);
									}
									em.setLastExplosiveEntity(e.getDamager());
								} catch (NoSuchMethodError e3) {
									//McMMO ability
									e3.printStackTrace();
								}
							} else {
								Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) e.getDamager());
								getPlayer.ifPresent(em::setLastPlayerDamager);
								em.setLastExplosiveEntity(e.getDamager());
							}
						} else if (e.getDamager() instanceof Projectile projectile) {
							if (projectile.getShooter() instanceof Player) {
								Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) projectile.getShooter());
								getPlayer.ifPresent(em::setLastPlayerDamager);
							}
							em.setLastProjectileEntity(projectile);
						} else if (e.getDamager() instanceof Player) {
							Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) e.getDamager());
							getPlayer.ifPresent(em::setLastPlayerDamager);
						}
					}, () -> {
						MobType mobType = MobType.VANILLA;
						if (DeathMessages.getInstance().mythicmobsEnabled
								&& DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
							mobType = MobType.MYTHIC_MOB;
						}
						new EntityManager(e.getEntity(), e.getEntity().getUniqueId(), mobType);
					});
				}
			}
		}
		if (e.getEntity() instanceof EnderCrystal) {
			if (e.getDamager().getType().isAlive()) {
				explosions.put(e.getEntity().getUniqueId(), e.getDamager());
			} else if (e.getDamager() instanceof Projectile projectile) {
                if (projectile.getShooter() instanceof LivingEntity) {
					explosions.put(e.getEntity().getUniqueId(), (LivingEntity) projectile.getShooter());
				}
			}

		}
	}
}
