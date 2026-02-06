package dev.mrshawn.deathmessages.api;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileStore;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

// Class designed to keep track of damage and data to mobs that were damaged by players

public class EntityManager {

    private final Entity entity;
    private final UUID entityUUID;
    private final MobType mobType;
    private DamageCause damageCause;
    private PlayerManager lastPlayerDamager;
    private Entity lastExplosiveEntity;
    private Projectile lastPlayerProjectile;
    private Location lastLocation;

    private WrappedTask lastPlayerTask;

    private static final Map<UUID, EntityManager> entities = new ConcurrentHashMap<>();

    public EntityManager(Entity entity, UUID entityUUID, MobType mobType) {
        this.entity = entity;
        this.entityUUID = entityUUID;
        this.mobType = mobType;
        entities.put(entityUUID, this);
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

        if (lastPlayerTask != null) {
            lastPlayerTask.cancel();
        }
        lastPlayerTask = DeathMessages.getInstance().foliaLib.getScheduler().runLater(this::destroy, FileStore.CONFIG.getInt(Config.EXPIRE_LAST_DAMAGE_EXPIRE_ENTITY) * 20L);
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
        return entities.get(uuid);
    }

    public void destroy() {
        entities.remove(this.entityUUID);
    }
}
