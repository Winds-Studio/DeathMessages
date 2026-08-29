package dev.mrshawn.deathmessages.api;

import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.enums.MobType;
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
    private @Nullable Entity lastExplosiveEntity;
    private @Nullable Projectile lastPlayerProjectile;
    private Location lastLocation;
    private long lastDamagerTimestamp = 0;

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
        this.lastDamagerTimestamp = damagerCtx != null ? System.currentTimeMillis() : 0;
        this.damageCause = DamageCause.CUSTOM;
    }

    public @Nullable PlayerCtx getLastPlayerDamager() {
        return lastPlayerDamager;
    }

    public void setLastExplosiveEntity(@Nullable Entity entity) {
        this.lastExplosiveEntity = entity;
    }

    public @Nullable Entity getLastExplosiveEntity() {
        return lastExplosiveEntity;
    }

    public void setLastProjectileEntity(@Nullable Projectile projectile) {
        this.lastPlayerProjectile = projectile;
    }

    public @Nullable Projectile getLastProjectileEntity() {
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

    /**
     * Called by the global ticker. Removes EntityCtx entries whose player damage attribution has expired.
     */
    public static void cleanExpiredEntities() {
        long now = System.currentTimeMillis();
        long expireMillis = FileStore.CONFIG.getInt(Config.EXPIRE_LAST_DAMAGE_EXPIRE_ENTITY) * 1000L;
        ENTITY_CONTEXTS.entrySet().removeIf(entry -> {
            EntityCtx ctx = entry.getValue();
            return ctx.lastPlayerDamager != null && ctx.lastDamagerTimestamp > 0
                    && (now - ctx.lastDamagerTimestamp) >= expireMillis;
        });
    }
}
