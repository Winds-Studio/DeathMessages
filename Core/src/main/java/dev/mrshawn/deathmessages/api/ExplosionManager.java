package dev.mrshawn.deathmessages.api;

import dev.mrshawn.deathmessages.DeathMessages;
import org.jetbrains.annotations.NotNull;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

public class ExplosionManager {

	private final UUID pyro;
	private final Material material;
	private Location location;
	private final List<UUID> effected;

	public static final List<ExplosionManager> explosions = new CopyOnWriteArrayList<>();

	public ExplosionManager(UUID pyro, Material material, Location location, List<UUID> effected) {
		this.pyro = pyro;
		this.material = material;
		this.location = location;
		this.effected = effected;
		explosions.add(this);

		// Destroys class. Won't need the info anymore
		DeathMessages.getInstance().foliaLib.getImpl().runLater(this::destroy, 5 * 20L);
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
		return explosions.stream()
				.filter(ex -> ex.getLocation() == location)
				.findAny();
	}

	public static Optional<ExplosionManager> getManagerIfEffected(UUID uuid) {
		return explosions.stream()
				.filter(ex -> ex.getEffected().contains(uuid))
				.findFirst();
	}

	private void destroy() {
		explosions.remove(this);
	}
}