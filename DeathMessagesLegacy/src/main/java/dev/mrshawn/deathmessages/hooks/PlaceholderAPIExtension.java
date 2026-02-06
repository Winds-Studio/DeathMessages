package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.jspecify.annotations.NullMarked;
import org.bukkit.entity.Player;

@NullMarked
public class PlaceholderAPIExtension extends PlaceholderExpansion {

    private final DeathMessages plugin;

    /**
     * Since we register the expansion inside our own plugin, we
     * can simply use this method here to get an instance of our
     * plugin.
     *
     * @param plugin The instance of our plugin.
     */
    public PlaceholderAPIExtension(DeathMessages plugin) {
        this.plugin = plugin;
    }

    /**
     * Because this is an internal class,
     * you must override this method to let PlaceholderAPI know to not unregister your expansion class when
     * PlaceholderAPI is reloaded
     *
     * @return true to persist through reloads
     */
    @Override
    public boolean persist() {
        return true;
    }

    /**
     * Because this is an internal class, this check is not needed
     * and we can simply return {@code true}
     *
     * @return Always true since it's an internal class.
     */
    @Override
    public boolean canRegister() {
        return true;
    }

    /**
     * The name of the person who created this expansion should go here.
     * <br>For convenience do we return the author from the plugin.yml
     *
     * @return The name of the author as a String.
     */
    @Override
    public String getAuthor() {
        return plugin.getDescription().getAuthors().toString();
    }

    /**
     * The placeholder identifier should go here.
     * <br>This is what tells PlaceholderAPI to call our onRequest
     * method to obtain a value if a placeholder starts with our
     * identifier.
     * <br>This must be unique and can not contain % or _
     *
     * @return The identifier in {@code %<identifier>_<value>%} as String.
     */
    @Override
    public String getIdentifier() {
        return "deathmessages";
    }

    /**
     * This is the version of the expansion.
     * <br>You don't have to use numbers, since it is set as a String.
     * <p>
     * For convenience do we return the version from the plugin.yml
     *
     * @return The version as a String.
     */
    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    /**
     * This is the method called when a placeholder with our identifier
     * is found and needs a value.
     * <br>We specify the value identifier in this method.
     * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
     *
     * @param params A String containing the identifier/value.
     * @return possibly-null String of the requested identifier.
     */
    @Override
    public String onPlaceholderRequest(Player player, String params) {
        PlayerManager getPlayer = PlayerManager.getPlayer(player);

        if (getPlayer == null) {
            return "null";
        }

        switch (params) {
                case "messages_enabled":
                    return String.valueOf(getPlayer.getMessagesEnabled());
                case "is_blacklisted":
                    return String.valueOf(getPlayer.isBlacklisted());
                case "victim_name":
                    return getPlayer.getName();
                case "victim_display_name":
                    return getPlayer.getPlayer().getDisplayName();
                case "killer_name":
                    return (getPlayer.getLastEntityDamager() != null) ? getPlayer.getLastEntityDamager().getName() : "null";
                case "killer_display_name":
                    return (getPlayer.getLastEntityDamager() != null) ? getPlayer.getLastEntityDamager().getCustomName() : "null";
                default:
                    return "null";
            }
    }
}
