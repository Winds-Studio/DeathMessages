package dev.mrshawn.deathmessages.hooks;

import io.lumine.mythic.bukkit.BukkitAPIHelper;
import io.lumine.mythic.bukkit.MythicBukkit;

public class MythicMobsHook {

    private final MythicBukkit instance = MythicBukkit.inst();

    public BukkitAPIHelper get() {
        return instance.getAPIHelper();
    }
}
