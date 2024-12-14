package dev.mrshawn.deathmessages.hooks;

import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.entity.Player;

public final class WorldGuard6Extension implements WorldGuardExtension {

    @Override
    public void registerFlags() {
        final FlagRegistry registry = WorldGuardPlugin.inst().getFlagRegistry();

        registry.registerAll(stateFlags);
    }

    private StateFlag.State getRegionState(final Player player, String messageType) {
        final RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        final RegionManager regions = container.get(player.getWorld());

        if (regions == null) return null;

        final ApplicableRegionSet set = regions.getApplicableRegions(player.getLocation());
        final LocalPlayer localPlayer = WorldGuardPlugin.inst().wrapPlayer(player);

        switch (messageType) {
            case "player":
                return set.queryState(localPlayer, BROADCAST_PLAYER);
            case "mob":
                return set.queryState(localPlayer, BROADCAST_MOBS);
            case "natural":
                return set.queryState(localPlayer, BROADCAST_NATURAL);
            case "entity":
                return set.queryState(localPlayer, BROADCAST_ENTITY);
            default:
                return StateFlag.State.ALLOW;
        }
    }

    @Override
    public boolean denyFromRegion(Player player, String messageType) {
        return getRegionState(player, messageType).equals(StateFlag.State.DENY);
    }

    @Override
    public boolean isInRegion(Player player, String regionID) {
        final RegionContainer container = WorldGuardPlugin.inst().getRegionContainer();
        final RegionManager regions = container.get(player.getWorld());

        if (regions == null) return false;

        final ApplicableRegionSet applicableRegionSet = regions.getApplicableRegions(player.getLocation());

        for (ProtectedRegion region : applicableRegionSet) {
            if (region.getId().equals(regionID)) {
                return true;
            }
        }

        return false;
    }
}
