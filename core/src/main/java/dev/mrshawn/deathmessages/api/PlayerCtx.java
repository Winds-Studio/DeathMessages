package dev.mrshawn.deathmessages.api;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.UserData;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import org.jspecify.annotations.Nullable;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class for storing player's death cotext information, functions as a "wrapper" of Player instance
 */
public class PlayerCtx {

    private final UUID uuid;
    private final String name;
    private final Player player;
    private boolean isMessageEnabled;
    private boolean isBlacklisted;
    private boolean isCommandDeath;
    private DamageCause damageCause;
    private Entity lastEntityDamager;
    private Entity lastExplosiveEntity;
    private Projectile lastProjectileEntity;
    private Material climbingBlock;
    //private Location location; // Uncomment if we really need to track it and put it in onMove
    private Inventory inventory;
    private int cooldown = 0;
    private @Nullable WrappedTask cooldownTask;
    private @Nullable WrappedTask lastEntityTask;

    private static final Map<UUID, PlayerCtx> PLAYER_CONTEXTS = new ConcurrentHashMap<>();

    public final boolean saveUserData = FileStore.CONFIG.getBoolean(Config.SAVED_USER_DATA);

    public PlayerCtx(Player p) {
        this.uuid = p.getUniqueId();
        this.name = p.getName();
        this.player = p;
        this.damageCause = DamageCause.CUSTOM;
        this.isCommandDeath = false;

        if (saveUserData) {
            final FileConfiguration config = UserData.getInstance().getConfig();
            if (!config.contains(uuid.toString())) {
                config.set(uuid + ".username", name);
                config.set(uuid + ".messages-enabled", true);
                config.set(uuid + ".is-blacklisted", false);
                UserData.getInstance().save();
            }

            isMessageEnabled = config.getBoolean(uuid + ".messages-enabled");
            isBlacklisted = config.getBoolean(uuid + ".is-blacklisted");
        } else {
            isMessageEnabled = true;
            isBlacklisted = false;
        }
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    // TODO: check all call chain
    @Deprecated
    public Player getPlayer() {
        return player;
    }

    public boolean isMessageEnabled() {
        return isMessageEnabled;
    }

    public void setMessageEnabled(boolean isMessageEnabled) {
        this.isMessageEnabled = isMessageEnabled;
        if (saveUserData) {
            UserData.getInstance().getConfig().set(uuid + ".messages-enabled", isMessageEnabled);
            UserData.getInstance().save();
        }
    }

    public boolean isBlacklisted() {
        return isBlacklisted;
    }

    public void setBlacklisted(boolean isBlacklisted) {
        this.isBlacklisted = isBlacklisted;
        if (saveUserData) {
            UserData.getInstance().getConfig().set(uuid + ".is-blacklisted", isBlacklisted);
            UserData.getInstance().save();
        }
    }

    public DamageCause getLastDamageCause() {
        return this.damageCause;
    }

    public void setLastDamageCause(DamageCause damageCause) {
        this.damageCause = damageCause;
    }

    public boolean isCommandDeath() {
        return this.isCommandDeath;
    }

    public void setCommandDeath(boolean isCommandDeath) {
        this.isCommandDeath = isCommandDeath;
    }

    public Entity getLastEntityDamager() {
        return lastEntityDamager;
    }

    public void setLastEntityDamager(Entity damager) {
        setLastExplosiveEntity(null);
        setLastProjectileEntity(null);
        this.lastEntityDamager = damager;

        if (lastEntityTask != null) {
            lastEntityTask.cancel();
        }
        lastEntityTask = DeathMessages.getInstance().foliaLib.getScheduler().runLater(() -> setLastEntityDamager(null), FileStore.CONFIG.getInt(Config.EXPIRE_LAST_DAMAGE_EXPIRE_PLAYER) * 20L);
    }

    public Entity getLastExplosiveEntity() {
        return lastExplosiveEntity;
    }

    public void setLastExplosiveEntity(Entity e) {
        this.lastExplosiveEntity = e;
    }

    public Projectile getLastProjectileEntity() {
        return lastProjectileEntity;
    }

    public void setLastProjectileEntity(Projectile lastProjectileEntity) {
        this.lastProjectileEntity = lastProjectileEntity;
    }

    public Material getLastClimbing() {
        return climbingBlock;
    }

    public void setLastClimbing(Material climbingBlock) {
        this.climbingBlock = climbingBlock;
    }

    // Note: Actually is player current location
    public Location getLastLocation() {
        return player.getLocation();
    }

    public boolean isInCooldown() {
        return cooldown > 0;
    }

    public void setCooldown() {
        cooldown = FileStore.CONFIG.getInt(Config.COOLDOWN);
        cooldownTask = DeathMessages.getInstance().foliaLib.getScheduler().runTimer(() -> {
            if (cooldown <= 0) {
                cooldownTask.cancel();
            }
            cooldown--;
        }, 1, 20);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    // Note: Only for online players
    public static @Nullable PlayerCtx of(UUID uuid) {
        return PLAYER_CONTEXTS.get(uuid);
    }

    public static void create(PlayerCtx playerCtx) {
        UUID uuid = playerCtx.uuid;
        PLAYER_CONTEXTS.put(uuid, playerCtx);
    }

    public static void remove(UUID uuid) {
        PLAYER_CONTEXTS.remove(uuid);
    }
}
