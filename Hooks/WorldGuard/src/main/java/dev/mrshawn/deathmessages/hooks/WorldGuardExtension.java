package dev.mrshawn.deathmessages.hooks;

import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.Collection;

public interface WorldGuardExtension {

    StateFlag BROADCAST_PLAYER = new StateFlag("broadcast-deathmessage-player", true);
    StateFlag BROADCAST_MOBS = new StateFlag("broadcast-deathmessage-mobs", true);
    StateFlag BROADCAST_NATURAL = new StateFlag("broadcast-deathmessage-natural", true);
    StateFlag BROADCAST_ENTITY = new StateFlag("broadcast-deathmessage-entity", true);
    Collection<Flag<?>> stateFlags = Arrays.asList(BROADCAST_PLAYER, BROADCAST_MOBS, BROADCAST_NATURAL, BROADCAST_ENTITY);

    void registerFlags();

    boolean denyFromRegion(final Player p, String region);

    boolean isInRegion(Player p, String regionID);
}
