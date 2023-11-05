package dev.mrshawn.deathmessages.worldguard;

import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.apache.logging.log4j.LogManager;
import org.bukkit.entity.Player;

public interface WorldGuardExtension {

	StateFlag BROADCAST_PLAYER = new StateFlag("broadcast-deathmessage-player", true);
	StateFlag BROADCAST_MOBS = new StateFlag("broadcast-deathmessage-mobs", true);
	StateFlag BROADCAST_NATURAL = new StateFlag("broadcast-deathmessage-natural", true);
	StateFlag BROADCAST_ENTITY = new StateFlag("broadcast-deathmessage-entity", true);

	default void registerFlags() {
		final FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
		final StateFlag[] stateFlags = {BROADCAST_PLAYER, BROADCAST_MOBS, BROADCAST_NATURAL, BROADCAST_ENTITY};

		for (StateFlag flag : stateFlags) {
			try {
				registry.register(flag);
			} catch (FlagConflictException e) {
				LogManager.getLogger().error(e);
			}
		}
	}

	StateFlag.State getRegionState(final Player p, String type);

	boolean isInRegion(Player p, String regionID);
}
