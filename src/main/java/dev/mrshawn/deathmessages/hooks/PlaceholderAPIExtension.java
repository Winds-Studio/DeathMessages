package dev.mrshawn.deathmessages.hooks;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
	 * Because this is a internal class, this check is not needed
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
	 * <br>For convienience do we return the author from the plugin.yml
	 *
	 * @return The name of the author as a String.
	 */
	@Override
	public @NotNull String getAuthor() {
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
	public @NotNull String getIdentifier() {
		return "deathmessages";
	}

	/**
	 * This is the version of the expansion.
	 * <br>You don't have to use numbers, since it is set as a String.
	 * <p>
	 * For convienience do we return the version from the plugin.yml
	 *
	 * @return The version as a String.
	 */
	@Override
	public @NotNull String getVersion() {
		return plugin.getDescription().getVersion();
	}

	/**
	 * This is the method called when a placeholder with our identifier
	 * is found and needs a value.
	 * <br>We specify the value identifier in this method.
	 * <br>Since version 2.9.1 can you use OfflinePlayers in your requests.
	 *
	 * @param identifier A String containing the identifier/value.
	 * @return possibly-null String of the requested identifier.
	 */
	@Override
	public String onPlaceholderRequest(Player player, @NotNull String identifier) {

		if (player == null) return null; // Dreeam - No NPE

		PlayerManager pm = PlayerManager.getPlayer(player);

		if (pm == null) return null; // Dreeam - No NPE

		switch (identifier) {
			case "messages_enabled":
				return String.valueOf(pm.getMessagesEnabled());
			case "is_blacklisted":
				return String.valueOf(pm.isBlacklisted());
			case "victim_name":
				return pm.getName();
			case "victim_display_name":
				return LegacyComponentSerializer.legacyAmpersand().serialize(pm.getPlayer().displayName());
			case "killer_name":
				if (pm.getLastEntityDamager() == null) return null; // Dreeam - No NPE
				return pm.getLastEntityDamager().getName();
			case "killer_display_name":
				if (pm.getLastEntityDamager() == null) return null; // Dreeam - No NPE
				Component customname = pm.getLastEntityDamager().customName();
				if (customname == null) return null; // Dreeam - No NPE
				return LegacyComponentSerializer.legacyAmpersand().serialize(customname);
			default:
				return null;
		}
	}
}