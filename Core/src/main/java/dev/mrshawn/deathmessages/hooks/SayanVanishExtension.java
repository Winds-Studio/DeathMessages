package dev.mrshawn.deathmessages.hooks;

import org.sayandev.sayanvanish.api.SayanVanishAPI;
import org.sayandev.sayanvanish.bukkit.api.BukkitUser;
import org.sayandev.sayanvanish.bukkit.api.SayanVanishBukkitAPI;

import java.util.UUID;

public class SayanVanishExtension {
    public SayanVanishAPI<BukkitUser> getSayanVanishAPI() {
        return SayanVanishBukkitAPI.getInstance();
    }

    public boolean isVanished(UUID uuid) {
        return getSayanVanishAPI().isVanished(uuid);
    }
}
