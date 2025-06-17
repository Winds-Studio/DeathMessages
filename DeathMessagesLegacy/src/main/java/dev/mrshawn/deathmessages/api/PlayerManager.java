package dev.mrshawn.deathmessages.api;

import com.tcoded.folialib.wrapper.task.WrappedTask;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.UserData;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileStore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.Inventory;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PlayerManager {

    private final UUID playerUUID;
    private final String playerName;
    private boolean messagesEnabled;
    private boolean isBlacklisted;
    private boolean isCommandDeath;
    private DamageCause damageCause;
    private Entity lastEntityDamager;
    private Entity lastExplosiveEntity;
    private Projectile lastProjectileEntity;
    private Material climbing;
    private Location explosionCauser;
    private Location location;
    private AtomicInteger cooldown = new AtomicInteger(0);
    private WrappedTask cooldownTask;
    private Inventory cachedInventory;

    private WrappedTask lastEntityTask;

    private static final Map<UUID, PlayerManager> players = new ConcurrentHashMap<>();

    public final boolean saveUserData = FileStore.CONFIG.getBoolean(Config.SAVED_USER_DATA);

    public PlayerManager(Player p) {
        this.playerUUID = p.getUniqueId();
        this.playerName = p.getName();

        if (saveUserData && !UserData.getInstance().getConfig().contains(playerUUID.toString())) {
            UserData.getInstance().getConfig().set(playerUUID + ".username", playerName);
            UserData.getInstance().getConfig().set(playerUUID + ".messages-enabled", true);
            UserData.getInstance().getConfig().set(playerUUID + ".is-blacklisted", false);
            UserData.getInstance().save();
        }

        if (saveUserData) {
            messagesEnabled = UserData.getInstance().getConfig().getBoolean(playerUUID + ".messages-enabled");
            isBlacklisted = UserData.getInstance().getConfig().getBoolean(playerUUID + ".is-blacklisted");
        } else {
            messagesEnabled = true;
            isBlacklisted = false;
        }

        this.damageCause = DamageCause.CUSTOM;
        this.isCommandDeath = false;
        players.put(p.getUniqueId(), this);
    }

    public Player getPlayer() {
        return Bukkit.getPlayer(playerUUID);
    }

    public UUID getUUID() {
        return playerUUID;
    }

    public String getName() {
        return playerName;
    }

    public boolean getMessagesEnabled() {
        return messagesEnabled;
    }

    public void setMessagesEnabled(boolean b) {
        this.messagesEnabled = b;
        if (saveUserData) {
            UserData.getInstance().getConfig().set(playerUUID.toString() + ".messages-enabled", b);
            UserData.getInstance().save();
        }
    }

    public boolean isBlacklisted() {
        return isBlacklisted;
    }

    public void setBlacklisted(boolean b) {
        this.isBlacklisted = b;
        if (saveUserData) {
            UserData.getInstance().getConfig().set(playerUUID.toString() + ".is-blacklisted", b);
            UserData.getInstance().save();
        }
    }

    public void setLastDamageCause(DamageCause dc) {
        this.damageCause = dc;
    }

    public DamageCause getLastDamage() {
        return this.damageCause;
    }

    public void setCommandDeath(boolean isTrue) {
        this.isCommandDeath = isTrue;
    }

    public boolean isCommandDeath() {
        return this.isCommandDeath;
    }

    public void setLastEntityDamager(Entity e) {
        setLastExplosiveEntity(null);
        setLastProjectileEntity(null);
        this.lastEntityDamager = e;

        if (lastEntityTask != null) {
            lastEntityTask.cancel();
        }
        lastEntityTask = DeathMessages.getInstance().foliaLib.getScheduler().runLater(() -> setLastEntityDamager(null), FileStore.CONFIG.getInt(Config.EXPIRE_LAST_DAMAGE_EXPIRE_PLAYER) * 20L);
    }

    public Entity getLastEntityDamager() {
        return lastEntityDamager;
    }

    public void setLastExplosiveEntity(Entity e) {
        this.lastExplosiveEntity = e;
    }

    public Entity getLastExplosiveEntity() {
        return lastExplosiveEntity;
    }

    public Projectile getLastProjectileEntity() {
        return lastProjectileEntity;
    }

    public void setLastProjectileEntity(Projectile lastProjectileEntity) {
        this.lastProjectileEntity = lastProjectileEntity;
    }

    public Material getLastClimbing() {
        return climbing;
    }

    public void setLastClimbing(Material climbing) {
        this.climbing = climbing;
    }

    public void setExplosionCauser(Location location) {
        this.explosionCauser = location;
    }

    public Location getExplosionCauser() {
        return explosionCauser;
    }

    public Location getLastLocation() {
        return getPlayer().getLocation();
    }

    public boolean isInCooldown() {
        return cooldown.get() > 0;
    }

    public int getCooldown() {
        return cooldown.get();
    }

    public void setCooldown() {
        cooldown.set(FileStore.CONFIG.getInt(Config.COOLDOWN));
        cooldownTask = DeathMessages.getInstance().foliaLib.getScheduler().runTimer(() -> {
            if (cooldown.get() <= 0) {
                cooldownTask.cancel();
            }
            cooldown.decrementAndGet();
        }, 1, 20);
    }

    public void setCachedInventory(Inventory inventory) {
        cachedInventory = inventory;
    }

    public Inventory getCachedInventory() {
        return cachedInventory;
    }

    public static Optional<PlayerManager> getPlayer(Player p) {
        return getPlayer(p.getUniqueId());
    }

    public static Optional<PlayerManager> getPlayer(UUID uuid) {
        return Optional.ofNullable(players.get(uuid));
    }

    public static boolean isEmpty(Player p) {
        return !getPlayer(p).isPresent();
    }

    public void removePlayer() {
        players.remove(this.playerUUID);
    }
}
