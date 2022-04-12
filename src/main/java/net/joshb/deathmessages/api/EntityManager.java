package net.joshb.deathmessages.api;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.config.Settings;
import net.joshb.deathmessages.enums.MobType;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//Class designed to keep track of damage and data to mobs that were damaged by players

public class EntityManager {

    private Entity entity;
    private UUID entityUUID;
    private MobType mobType;
    private DamageCause damageCause;
    private PlayerManager lastPlayerDamager;
    private Entity lastExplosiveEntity;
    private Projectile lastPlayerProjectile;
    private Location lastLocation;

    private BukkitTask lastPlayerTask;

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

    public UUID getEntityUUID(){
        return entityUUID;
    }

    public void setLastDamageCause(DamageCause dc) {
        this.damageCause = dc;
    }

    public DamageCause getLastDamage() {
        return damageCause;
    }

    public void setLastPlayerDamager(PlayerManager pm) {
        setLastExplosiveEntity(null);
        setLastProjectileEntity(null);
        this.lastPlayerDamager = pm;
        if (pm == null) return;
        if (lastPlayerTask != null) {
            lastPlayerTask.cancel();
        }
        lastPlayerTask = new BukkitRunnable() {
            @Override
            public void run() {
                destroy();
            }
        }.runTaskLater(DeathMessages.plugin, Settings.getInstance().getConfig().getInt("Expire-Last-Damage.Expire-Entity") * 20L);
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

