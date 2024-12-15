package dev.mrshawn.deathmessages.api;

import dev.mrshawn.deathmessages.DeathMessages;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ExplosionManager {

    private final UUID pyro;
    private final Material material;
    private Location location;
    private final List<UUID> effected;

    private static final Map<UUID, ExplosionManager> explosions = new ConcurrentHashMap<>();
    private static final Map<Location, UUID> locs = new ConcurrentHashMap<>();

    public ExplosionManager(UUID pyro, Material material, Location location, List<UUID> effected) {
        this.pyro = pyro;
        this.material = material;
        this.location = location;
        this.effected = effected;
        explosions.put(pyro, this);
        locs.put(location, pyro);

        // Destroys class. Won't need the info anymore
        DeathMessages.getInstance().foliaLib.getScheduler().runLater(this::destroy, 5 * 20L);
    }

    @NotNull
    public UUID getPyro() {
        return this.pyro;
    }

    public Material getMaterial() {
        return this.material;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public List<UUID> getEffected() {
        return this.effected;
    }

    public static Optional<ExplosionManager> getExplosion(Location location) {
        UUID uuid = locs.get(location);
        if (uuid != null) {
            return Optional.ofNullable(explosions.get(uuid));
        }

        return Optional.empty();
    }

    public static Optional<ExplosionManager> getManagerIfEffected(UUID uuid) {
        return Optional.ofNullable(explosions.get(uuid));
    }

    private void destroy() {
        explosions.remove(this.pyro);
        locs.remove(this.location);
    }
}
