package dev.mrshawn.deathmessages.hooks;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.util.Location;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.entity.Player;

public final class WorldGuard7Extension implements WorldGuardExtension {

    public void registerFlags() {
        final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();

        registry.registerAll(stateFlags);
    }

    private StateFlag.State getRegionState(final Player player, String messageType) {
        final Location loc = new Location(BukkitAdapter.adapt(player.getLocation().getWorld()), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
        final RegionContainer rc = WorldGuard.getInstance().getPlatform().getRegionContainer();
        final ApplicableRegionSet set = rc.createQuery().getApplicableRegions(loc);
        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        return switch (messageType) {
            case "player" -> set.queryState(localPlayer, BROADCAST_PLAYER);
            case "mob" -> set.queryState(localPlayer, BROADCAST_MOBS);
            case "natural" -> set.queryState(localPlayer, BROADCAST_NATURAL);
            case "entity" -> set.queryState(localPlayer, BROADCAST_ENTITY);
            default -> StateFlag.State.ALLOW;
        };
    }

    @Override
    public boolean denyFromRegion(Player player, String messageType) {
        return getRegionState(player, messageType).equals(StateFlag.State.DENY);
    }

    @Override
    public boolean isInRegion(Player player, String regionID) {
        final Location loc = new Location(BukkitAdapter.adapt(player.getLocation().getWorld()), player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ());
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
