package dev.mrshawn.deathmessages.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;

public final class WorldGuard7Extension implements WorldGuardExtension {

	@Override
	public StateFlag.State getRegionState(final Player p, String type) {
		final Location loc = new Location(BukkitAdapter.adapt(p.getLocation().getWorld()), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
		final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
		final ApplicableRegionSet set = rc.createQuery().getApplicableRegions(loc);
		final LocalPlayer player = WorldGuardPlugin.inst().wrapPlayer(p);
		switch (type) {
			case "player":
				return set.queryState(player, BROADCAST_PLAYER);
			case "mob":
				return set.queryState(player, BROADCAST_MOBS);
			case "natural":
				return set.queryState(player, BROADCAST_NATURAL);
			case "entity":
				return set.queryState(player, BROADCAST_ENTITY);
			default:
				return StateFlag.State.ALLOW;
		}
	}

	@Override
	public boolean isInRegion(Player p, String regionID) {
		final Location loc = new Location(BukkitAdapter.adapt(p.getLocation().getWorld()), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
		final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
		final ApplicableRegionSet set = rc.createQuery().getApplicableRegions(loc);
		for (ProtectedRegion region : set) {
			return region.getId().equals(regionID);
		}
		return false;
	}
}
