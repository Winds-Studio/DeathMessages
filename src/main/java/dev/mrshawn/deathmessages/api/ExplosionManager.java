package dev.mrshawn.deathmessages.api;

import dev.mrshawn.deathmessages.DeathMessages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExplosionManager {

	private final UUID pyro;
	private final Material material;
	private Location location;
	private final List<UUID> effected;

	public static final List<ExplosionManager> explosions = new ArrayList<>();

	public ExplosionManager(UUID pyro, Material material, Location location, List<UUID> effected) {
		this.pyro = pyro;
		this.material = material;
		this.location = location;
		this.effected = effected;
		explosions.add(this);

		//  Destroys class. Won't need the info anymore
		Bukkit.getGlobalRegionScheduler().runDelayed(DeathMessages.getInstance(),
				task -> destroy(), 5L * 20L);
	}

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

	public static ExplosionManager getExplosion(Location location) {
		for (ExplosionManager ex : explosions) {
			if (ex.getLocation().equals(location)) {
				return ex;
			}
		}
		return null;
	}

	public static ExplosionManager getManagerIfEffected(UUID uuid) {
		for (ExplosionManager ex : explosions) {
			if (ex.getEffected().contains(uuid)) {
				return ex;
			}
		}
		return null;
	}

	private void destroy() {
		explosions.remove(this);
	}
}