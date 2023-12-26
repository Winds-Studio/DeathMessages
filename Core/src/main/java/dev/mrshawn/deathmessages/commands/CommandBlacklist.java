package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.config.UserData;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.Assets;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class CommandBlacklist extends DeathMessagesCommand {

	@Override
	public String command() {
		return "blacklist";
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_BLACKLIST.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.No-Permission")));
			return;
		}
		if (args.length == 0) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Help")));
		} else {
			TextReplacementConfig player = TextReplacementConfig.builder()
					.match("%player%")
					.replacement(args[0])
					.build();

			// Saved-User-Data disabled
			if (!Settings.getInstance().getConfig().getBoolean(Config.SAVED_USER_DATA.getPath())) {
				Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(Bukkit.getPlayer(args[0]));
				getPlayer.ifPresentOrElse(pm -> {
							if (pm.isBlacklisted()) {
								pm.setBlacklisted(false);
								DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Remove"))
										.replaceText(player));
							} else {
								pm.setBlacklisted(true);
								DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Add"))
										.replaceText(player));
							}
						}, () -> DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Username-None-Existent"))
								.replaceText(player))
				);
				return;
			}

			// Saved-User-Data enabled
			for (Map.Entry<String, Object> entry : UserData.getInstance().getConfig().getValues(false).entrySet()) {
				Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(UUID.fromString(entry.getKey()));
				getPlayer.ifPresentOrElse(pm -> {
					String username = UserData.getInstance().getConfig().getString(entry.getKey() + ".username");
					if (username.equalsIgnoreCase(args[0])) {
						boolean blacklisted = UserData.getInstance().getConfig().getBoolean(entry.getKey() + ".is-blacklisted");
						if (blacklisted) {
							pm.setBlacklisted(false);
							UserData.getInstance().getConfig().set(entry.getKey() + ".is-blacklisted", false);
							UserData.getInstance().save();
							DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Remove"))
									.replaceText(player));
						} else {
							pm.setBlacklisted(true);
							UserData.getInstance().getConfig().set(entry.getKey() + ".is-blacklisted", true);
							UserData.getInstance().save();
							DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Add"))
									.replaceText(player));
						}

					}
				}, () -> DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Username-None-Existent"))
						.replaceText(player)));
				return;
			}
		}
	}
}
