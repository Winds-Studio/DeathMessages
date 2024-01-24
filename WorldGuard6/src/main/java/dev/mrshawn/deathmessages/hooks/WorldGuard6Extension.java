package dev.mrshawn.deathmessages.hooks;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public final class WorldGuard6Extension implements WorldGuardExtension {

	@Override
	public StateFlag.State getRegionState(final Player p, String type) {
		final RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
		final RegionManager regions = container.get(p.getWorld());
		if (regions == null) return null;
		final ApplicableRegionSet set = regions.getApplicableRegions(p.getLocation());
		final LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);
		switch (type) {
			case "player":
				return set.queryState(lp, BROADCAST_PLAYER);
			case "mob":
				return set.queryState(lp, BROADCAST_MOBS);
			case "natural":
				return set.queryState(lp, BROADCAST_NATURAL);
			case "entity":
				return set.queryState(lp, BROADCAST_ENTITY);
			default:
				return StateFlag.State.ALLOW;
		}
	}

	@Override
	public boolean isInRegion(Player p, String regionID) {
		final RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
		final RegionManager regions = container.get(p.getWorld());
		if (regions == null) return false;
		final ApplicableRegionSet applicableRegionSet = regions.getApplicableRegions(p.getLocation());
		for (ProtectedRegion region : applicableRegionSet) {
			return region.getId().equals(regionID);
		}
		return false;
	}
}
