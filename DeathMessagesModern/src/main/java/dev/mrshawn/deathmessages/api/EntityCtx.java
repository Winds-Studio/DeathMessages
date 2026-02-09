package dev.mrshawn.deathmessages.api;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import org.jspecify.annotations.Nullable;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for storing entity's death cotext information, for tracking entity that is killed players
 */
public class EntityCtx {

    private final Entity entity;
    private final UUID uuid;
    private final MobType mobType;
    private DamageCause damageCause;
    private @Nullable PlayerCtx lastPlayerDamager;
    private Entity lastExplosiveEntity;
    private Projectile lastPlayerProjectile;
    private Location lastLocation;
    private @Nullable WrappedTask lastPlayerTask;

    private static final Map<UUID, EntityCtx> ENTITY_CONTEXTS = new ConcurrentHashMap<>();

    public EntityCtx(Entity entity, MobType mobType) {
        this.entity = entity;
        this.uuid = entity.getUniqueId();
        this.mobType = mobType;
    }

    public Entity getEntity() {
        return entity;
    }

    public UUID getUUID() {
        return uuid;
    }

    public MobType getMobType() {
        return mobType;
    }

    public void setLastDamageCause(DamageCause damageCause) {
        this.damageCause = damageCause;
    }

    public DamageCause getLastDamageCause() {
        return damageCause;
    }

    public void setLastPlayerDamager(PlayerCtx damagerCtx) {
        setLastExplosiveEntity(null);
        setLastProjectileEntity(null);

        this.lastPlayerDamager = damagerCtx;

        if (lastPlayerTask != null) {
            lastPlayerTask.cancel();
        }
        lastPlayerTask = DeathMessages.getInstance().foliaLib.getScheduler().runLater(() -> EntityCtx.remove(this.getUUID()), FileStore.CONFIG.getInt(Config.EXPIRE_LAST_DAMAGE_EXPIRE_ENTITY) * 20L);
        this.damageCause = DamageCause.CUSTOM;
    }

    public @Nullable PlayerCtx getLastPlayerDamager() {
        return lastPlayerDamager;
    }

    public void setLastExplosiveEntity(Entity entity) {
        this.lastExplosiveEntity = entity;
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

    public static @Nullable EntityCtx of(UUID uuid) {
        return ENTITY_CONTEXTS.get(uuid);
    }

    public static void create(EntityCtx entityCtx) {
        UUID uuid = entityCtx.getUUID();
        ENTITY_CONTEXTS.put(uuid, entityCtx);
    }

    public static void remove(UUID uuid) {
        ENTITY_CONTEXTS.remove(uuid);
    }
}
