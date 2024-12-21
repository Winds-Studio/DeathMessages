package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.Component;
import org.sayandev.sayanvanish.api.SayanVanishAPI;
import org.sayandev.sayanvanish.bukkit.api.BukkitUser;
import org.sayandev.sayanvanish.bukkit.api.SayanVanishBukkitAPI;
import org.bukkit.entity.Player;

import java.util.UUID;

public class SayanVanishExtension {

    public SayanVanishAPI<BukkitUser> getSayanVanishAPI() {
        return SayanVanishBukkitAPI.getInstance();
    }

    public boolean isVanished(UUID uuid) {
        return getSayanVanishAPI().isVanished(uuid);
    }

    public static boolean isSayanVanished(Player p) {
        return DeathMessages.getHooks().sayanVanishEnabled && DeathMessages.getHooks().sayanVanishExtension.isVanished(p.getUniqueId());
    }

    public static Component getVanishedName() {
        final String name = Settings.getInstance().getConfig().getString(Config.HOOKS_SAYANVANISH_VANISHED_NAME.getPath());

        return Util.convertFromLegacy(name != null ? name : "");
    }
}
