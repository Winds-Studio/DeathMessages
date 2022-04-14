package dev.mrshawn.deathmessages.worldguard;

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
        final LocalPlayer lp = WorldGuardPlugin.inst().wrapPlayer(p);
        if (type.equals("player")) {
            return set.queryState(lp, BROADCAST_PLAYER);
        } else if (type.equals("mob")) {
            return set.queryState(lp, BROADCAST_MOBS);
        } else if (type.equals("natural")) {
            return set.queryState(lp, BROADCAST_NATURAL);
        } else if (type.equals("entity")) {
            return set.queryState(lp, BROADCAST_ENTITY);
        }
        return StateFlag.State.ALLOW;
    }

    @Override
    public boolean isInRegion(Player p, String regionID) {
        final Location loc = new Location(BukkitAdapter.adapt(p.getLocation().getWorld()), p.getLocation().getX(), p.getLocation().getY(), p.getLocation().getZ());
        final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final ApplicableRegionSet set = rc.createQuery().getApplicableRegions(loc);
        for (ProtectedRegion region : set) {
            if (region.getId().equals(regionID)) {
                return true;
            }
        }
        return false;
    }
}
