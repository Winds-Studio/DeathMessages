package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Gangs;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import dev.mrshawn.deathmessages.utils.Assets;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class EntityDeath implements Listener {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	void onEntityDeath(EntityDeathEvent e) {
		// Player death
		if (e.getEntity() instanceof Player && Bukkit.getServer().getOnlinePlayers().contains((Player) e.getEntity())) {
			Player p = (Player) e.getEntity();
			Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(p);
			getPlayer.ifPresent(pm -> {
				if (e.getEntity().getLastDamageCause() == null) {
					pm.setLastDamageCause(EntityDamageEvent.DamageCause.CUSTOM);
				} else if (pm.isCommandDeath()) { // If died by using suicide like command
					pm.setLastDamageCause(EntityDamageEvent.DamageCause.SUICIDE);
					pm.setCommandDeath(false);
				} else { // Reset lastDamageCause
					pm.setLastDamageCause(e.getEntity().getLastDamageCause().getCause());
				}

				if (pm.isBlacklisted()) return;

				if (!(pm.getLastEntityDamager() instanceof LivingEntity) || pm.getLastEntityDamager() == e.getEntity()) {
					// Natural Death
					TextComponent naturalDeath = Component.empty();
					if (pm.getLastExplosiveEntity() instanceof EnderCrystal) {
						naturalDeath = Assets.getNaturalDeath(pm, "End-Crystal");
					} else if (pm.getLastExplosiveEntity() instanceof TNTPrimed) {
						naturalDeath = Assets.getNaturalDeath(pm, "TNT");
					} else if (pm.getLastExplosiveEntity() instanceof Firework) {
						naturalDeath = Assets.getNaturalDeath(pm, "Firework");
					} else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.FALL)) {
						naturalDeath = Assets.getNaturalDeath(pm, "Climbable");
					} else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
						Optional<ExplosionManager> explosion = ExplosionManager.getManagerIfEffected(p.getUniqueId());
						if (explosion.isPresent()) {
							if (explosion.get().getMaterial().name().contains("BED")) {
								naturalDeath = Assets.getNaturalDeath(pm, "Bed");
							}
							if (DeathMessages.majorVersion >= 16) {
								if (explosion.get().getMaterial().equals(Material.RESPAWN_ANCHOR)) {
									naturalDeath = Assets.getNaturalDeath(pm, "Respawn-Anchor");
								}
							}
						}
					} else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
						naturalDeath = Assets.getNaturalDeath(pm, Assets.getSimpleProjectile(pm.getLastProjectileEntity()));
					} else if (DeathMessages.majorVersion >= 9 && DeathMessages.majorVersion < 999 && pm.getLastEntityDamager() instanceof AreaEffectCloud) { // Fix MC-84595 - Killed by Dragon's Breath
						AreaEffectCloud cloud = (AreaEffectCloud) pm.getLastEntityDamager();
						if (cloud.getSource() instanceof EnderDragon) {
							pm.setLastDamageCause(
									Settings.getInstance().getConfig().getBoolean(Config.FIX_MC_84595.getPath())
											? EntityDamageEvent.DamageCause.DRAGON_BREATH : EntityDamageEvent.DamageCause.ENTITY_ATTACK
							);
						}
						naturalDeath = Assets.getNaturalDeath(pm, Assets.getSimpleCause(pm.getLastDamage()));
					} else {
						naturalDeath = Assets.getNaturalDeath(pm, Assets.getSimpleCause(pm.getLastDamage()));
					}
					BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, null, MessageType.NATURAL, naturalDeath, getWorlds(p), false);
					Bukkit.getPluginManager().callEvent(event);
				} else {
					// Killed by mob
					Entity ent = pm.getLastEntityDamager();
					String mobName = ent.getType().getEntityClass().getSimpleName().toLowerCase();
					int radius = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Radius");
					int amount = Gangs.getInstance().getConfig().getInt("Gang.Mobs." + mobName + ".Amount");

					boolean gangKill = false;

					if (Gangs.getInstance().getConfig().getBoolean("Gang.Enabled")) {
						int totalMobEntities = 0;
						// Dreeam TODO: need move to EntityUtil
						Predicate<Entity> isNotDragonParts = entity -> !entity.toString().contains("EnderDragonPart"); // Exclude EnderDragonPart
						List<Entity> entities = p.getNearbyEntities(radius, radius, radius).stream()
								.filter(isNotDragonParts).collect(Collectors.toList());

						for (Entity entity : entities) {
							if (entity.getType().equals(ent.getType())) {
								totalMobEntities++;
							}
						}
						if (totalMobEntities >= amount) {
							gangKill = true;
						}
					}
					TextComponent playerDeath = Assets.playerDeathMessage(pm, gangKill);
					if (ent instanceof Player) {
						BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p,
								(LivingEntity) pm.getLastEntityDamager(), MessageType.PLAYER, playerDeath, getWorlds(p), gangKill);
						Bukkit.getPluginManager().callEvent(event);
						return;
					}
					BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p,
							(LivingEntity) pm.getLastEntityDamager(), MessageType.MOB, playerDeath, getWorlds(p), gangKill);
					Bukkit.getPluginManager().callEvent(event);
				}
			});
			if (!getPlayer.isPresent()) {
				new PlayerManager(p);
			}
		} else {
			// Entity killed by Player
			Optional<EntityManager> getEntity = EntityManager.getEntity(e.getEntity().getUniqueId());
			getEntity.ifPresent(em -> {
				MobType mobType = MobType.VANILLA;
				if (DeathMessages.getInstance().mythicmobsEnabled) {
					if (DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
						mobType = MobType.MYTHIC_MOB;
					}
				}

				PlayerManager damager = em.getLastPlayerDamager();
				if (damager == null) return; // Entity killed by Entity should not include in DM

				TextComponent entityDeath = Assets.entityDeathMessage(em, mobType);
				BroadcastEntityDeathMessageEvent event = new BroadcastEntityDeathMessageEvent(damager, e.getEntity(), MessageType.ENTITY, entityDeath, getWorlds(e.getEntity()));
				Bukkit.getPluginManager().callEvent(event);
			});
		}
	}

	public static List<World> getWorlds(Entity e) {
		List<World> broadcastWorlds = new ArrayList<>();
		if (config.getStringList(Config.DISABLED_WORLDS).contains(e.getWorld().getName())) {
			return broadcastWorlds;
		}
		if (config.getBoolean(Config.PER_WORLD_MESSAGES)) {
			// TODO: Add support for Map in FileSettings
			for (String groups : Settings.getInstance().getConfig().getConfigurationSection("World-Groups").getKeys(false)) {
				List<String> worlds = Settings.getInstance().getConfig().getStringList("World-Groups." + groups);
				if (worlds.contains(e.getWorld().getName())) {
					for (String single : worlds) {
						World world = Bukkit.getWorld(single);
						if (world != null) {
							broadcastWorlds.add(world);
						} else {
							DeathMessages.LOGGER.warn("Can't find world with name: {}, in World-Groups", single);
						}
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

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath_LOWEST(EntityDeathEvent e) {
		if (DeathMessages.getEventPriority().equals(EventPriority.LOWEST)) {
			onEntityDeath(e);
		}
	}

	@EventHandler(priority = EventPriority.LOW)
	public void onEntityDeath_LOW(EntityDeathEvent e) {
		if (DeathMessages.getEventPriority().equals(EventPriority.LOW)) {
			onEntityDeath(e);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onEntityDeath_NORMAL(EntityDeathEvent e) {
		if (DeathMessages.getEventPriority().equals(EventPriority.NORMAL)) {
			onEntityDeath(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onEntityDeath_HIGH(EntityDeathEvent e) {
		if (DeathMessages.getEventPriority().equals(EventPriority.HIGH)) {
			onEntityDeath(e);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDeath_HIGHEST(EntityDeathEvent e) {
		if (DeathMessages.getEventPriority().equals(EventPriority.HIGHEST)) {
			onEntityDeath(e);
		}
	}
}
