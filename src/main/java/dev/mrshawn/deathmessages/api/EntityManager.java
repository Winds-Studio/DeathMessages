package dev.mrshawn.deathmessages.api;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// Class designed to keep track of damage and data to mobs that were damaged by players

public class EntityManager {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	private final Entity entity;
	private final UUID entityUUID;
	private final MobType mobType;
	private DamageCause damageCause;
	private PlayerManager lastPlayerDamager;
	private Entity lastExplosiveEntity;
	private Projectile lastPlayerProjectile;
	private Location lastLocation;

	private ScheduledTask lastPlayerTask;

	private static final List<EntityManager> entities = new ArrayList<>();

	public EntityManager(Entity entity, UUID entityUUID, MobType mobType) {
		this.entity = entity;
		this.entityUUID = entityUUID;
		this.mobType = mobType;
		entities.add(this);
	}

	public Entity getEntity() {
		return entity;
	}

	public UUID getEntityUUID() {
		return entityUUID;
	}

	public MobType getMobType() {
		return mobType;
	}

	public void setLastDamageCause(DamageCause damageCause) {
		this.damageCause = damageCause;
	}

	public DamageCause getLastDamage() {
		return damageCause;
	}

	public void setLastPlayerDamager(PlayerManager pm) {
		setLastExplosiveEntity(null);
		setLastProjectileEntity(null);
		this.lastPlayerDamager = pm;
		if (pm == null) return; // Dreeam - No NPE
		if (lastPlayerTask != null) {
			lastPlayerTask.cancel();
		}
		lastPlayerTask = Bukkit.getGlobalRegionScheduler().runDelayed(DeathMessages.getInstance(),
				task -> destroy(), config.getInt(Config.EXPIRE_LAST_DAMAGE_EXPIRE_ENTITY) * 20L);
		this.damageCause = DamageCause.CUSTOM;
	}

	public PlayerManager getLastPlayerDamager() {
		return lastPlayerDamager;
	}

	public void setLastExplosiveEntity(Entity e) {
		this.lastExplosiveEntity = e;
	}

	public Entity getLastExplosiveEntity() {
		return lastExplosiveEntity;
	}

	public void setLastProjectileEntity(Projectile projectile) {
		this.lastPlayerProjectile = projectile;
	}

	public Projectile getLastProjectileEntity() {
		return lastPlayerProjectile;
	}

	public void setLastLocation(Location location) {
		this.lastLocation = location;
	}

	public Location getLastLocation() {
		return lastLocation;
	}

	public static EntityManager getEntity(UUID uuid) {
		for (EntityManager em : entities) {
			if (em.getEntityUUID().equals(uuid)) {
				return em;
			}
		}
		return null;
	}

	public void destroy() {
		entities.remove(this);
	}
}

