package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.entity.AreaEffectCloud;
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
		if (e.getEntity() instanceof Player && Bukkit.getServer().getOnlinePlayers().contains((Player) e.getEntity())) {
			Player p = (Player) e.getEntity();
			Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(p);
			getPlayer.ifPresent(pm -> {
				if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
					if (e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager().getUniqueId())) {
						pm.setLastEntityDamager(explosions.get(e.getDamager().getUniqueId()));
						pm.setLastExplosiveEntity(e.getDamager());
					} else if (e.getDamager() instanceof TNTPrimed) { // For <= 1.20.2, because TNT explosion became BLOCK_EXPLOSION since 1.20.3
						TNTPrimed tnt = (TNTPrimed) e.getDamager();
						if (tnt.getSource() instanceof LivingEntity) {
							pm.setLastEntityDamager(tnt.getSource());
						}
						pm.setLastExplosiveEntity(e.getDamager());
					} else if (Util.isNewerAndEqual(16, 0) && e.getDamager() instanceof Firework) { // Firework extends Entity under <= 1.15
						Firework firework = (Firework) e.getDamager();
						try {
							if (firework.getShooter() instanceof LivingEntity) {
								pm.setLastEntityDamager((LivingEntity) firework.getShooter());
							}
							pm.setLastExplosiveEntity(e.getDamager());
						} catch (NoSuchMethodError e2) {
							// McMMO ability
							DeathMessages.LOGGER.error(e2);
						}
					} else {
						pm.setLastEntityDamager(e.getDamager());
						pm.setLastExplosiveEntity(e.getDamager());
					}
				} else if (e.getDamager() instanceof Projectile) {
					Projectile projectile = (Projectile) e.getDamager();
					if (projectile.getShooter() instanceof LivingEntity) {
						pm.setLastEntityDamager((LivingEntity) projectile.getShooter());
					}
					pm.setLastProjectileEntity(projectile);
				} else if (e.getDamager() instanceof FallingBlock) {
					pm.setLastEntityDamager(e.getDamager());
				} else if (e.getDamager().getType().isAlive()) {
					pm.setLastEntityDamager(e.getDamager());
				} else if (e.getDamager() instanceof EvokerFangs) {
					EvokerFangs evokerFangs = (EvokerFangs) e.getDamager();
					pm.setLastEntityDamager(evokerFangs.getOwner());
				} else if (e.getDamager() instanceof AreaEffectCloud) {
					pm.setLastEntityDamager(e.getDamager());
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
					getEntity.ifPresent(em -> {
						if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
							System.out.println(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase());
						if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
							if (e.getDamager() instanceof EnderCrystal && explosions.containsKey(e.getDamager())) {
								if (explosions.get(e.getDamager().getUniqueId()) instanceof Player) {
									Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) explosions.get(e.getDamager().getUniqueId()));
									getPlayer.ifPresent(em::setLastPlayerDamager);
									em.setLastExplosiveEntity(e.getDamager());
								}
							} else if (e.getDamager() instanceof TNTPrimed) {
								TNTPrimed tnt = (TNTPrimed) e.getDamager();
								if (tnt.getSource() instanceof Player) {
									Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) tnt.getSource());
									getPlayer.ifPresent(em::setLastPlayerDamager);
								}
								em.setLastExplosiveEntity(e.getDamager());
							} else if (Util.isNewerAndEqual(16, 0) && e.getDamager() instanceof Firework) {
								Firework firework = (Firework) e.getDamager();
								try {
									if (firework.getShooter() instanceof Player) {
										Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) firework.getShooter());
										getPlayer.ifPresent(em::setLastPlayerDamager);
									}
									em.setLastExplosiveEntity(e.getDamager());
								} catch (NoSuchMethodError e3) {
									// McMMO ability
									DeathMessages.LOGGER.error(e3);
								}
							} else {
								Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) e.getDamager());
								getPlayer.ifPresent(em::setLastPlayerDamager);
								em.setLastExplosiveEntity(e.getDamager());
							}
						} else if (e.getDamager() instanceof Projectile) {
							Projectile projectile = (Projectile) e.getDamager();
							if (projectile.getShooter() instanceof Player) {
								Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) projectile.getShooter());
								getPlayer.ifPresent(em::setLastPlayerDamager);
							}
							em.setLastProjectileEntity(projectile);
						} else if (e.getDamager() instanceof Player) {
							Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) e.getDamager());
							getPlayer.ifPresent(em::setLastPlayerDamager);
						}
					});
					if (!getEntity.isPresent()) {
						MobType mobType = MobType.VANILLA;
						if (DeathMessages.getInstance().mythicmobsEnabled
								&& DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
							mobType = MobType.MYTHIC_MOB;
						}
						if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath())) System.out.println(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase());
						new EntityManager(e.getEntity(), e.getEntity().getUniqueId(), mobType);
					}
				}
			}
		}
		if (e.getEntity() instanceof EnderCrystal) {
			if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath())) System.out.println(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase());
			if (e.getDamager().getType().isAlive()) {
				explosions.put(e.getEntity().getUniqueId(), e.getDamager());
			} else if (e.getDamager() instanceof Projectile) {
				Projectile projectile = (Projectile) e.getDamager();
				if (projectile.getShooter() instanceof LivingEntity) {
					explosions.put(e.getEntity().getUniqueId(), (LivingEntity) projectile.getShooter());
				}
			}

		}
	}
}
