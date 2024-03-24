package dev.mrshawn.deathmessages.hooks;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collection;

public interface WorldGuardExtension {

    String version = Bukkit.getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();

    StateFlag BROADCAST_PLAYER = new StateFlag("broadcast-deathmessage-player", true);
    StateFlag BROADCAST_MOBS = new StateFlag("broadcast-deathmessage-mobs", true);
    StateFlag BROADCAST_NATURAL = new StateFlag("broadcast-deathmessage-natural", true);
    StateFlag BROADCAST_ENTITY = new StateFlag("broadcast-deathmessage-entity", true);

    default void registerFlags() {
        FlagRegistry registry = null;

        try {
            if (version.startsWith("6")) {
                Field field = WorldGuardPlugin.class.getDeclaredField("registry");

                field.setAccessible(true);

                WorldGuardPlugin inst = WorldGuardPlugin.inst();

                registry = (FlagRegistry) field.get(inst);
            } else if (version.startsWith("7")) {
                Class<?> WorldGuard = Class.forName("com.sk89q.worldguard.WorldGuard");
                Field field = WorldGuard.getDeclaredField("flagRegistry");

                field.setAccessible(true);

                Method inst = WorldGuard.getMethod("getInstance");

                Object worldGuardInstance = inst.invoke(null);

                registry = (FlagRegistry) field.get(worldGuardInstance);
            }
        } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | InvocationTargetException |
                 NoSuchMethodException e) {
            LogManager.getLogger("DeathMessages").error(e);
        }

        final Collection<Flag<?>> stateFlags = Arrays.asList(BROADCAST_PLAYER, BROADCAST_MOBS, BROADCAST_NATURAL, BROADCAST_ENTITY);

        registry.registerAll(stateFlags);
    }

    StateFlag.State getRegionState(final Player p, String type);

    boolean isInRegion(Player p, String regionID);
}
