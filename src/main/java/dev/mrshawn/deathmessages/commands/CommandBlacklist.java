package dev.mrshawn.deathmessages.commands;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.UserData;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
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
			for (Map.Entry<String, Object> entry : UserData.getInstance().getConfig().getValues(false).entrySet()) {
				String username = UserData.getInstance().getConfig().getString(entry.getKey() + ".username");
				if (username.equalsIgnoreCase(args[0])) {
					boolean blacklisted = UserData.getInstance().getConfig().getBoolean(entry.getKey() + ".is-blacklisted");
					if (blacklisted) {
						if (Bukkit.getPlayer(UUID.fromString(entry.getKey())) != null) {
							Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(UUID.fromString(entry.getKey()));
							getPlayer.ifPresent(pm -> pm.setBlacklisted(false));
						}
						UserData.getInstance().getConfig().set(entry.getKey() + ".is-blacklisted", false);
						UserData.getInstance().save();
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Remove")
								.replaceAll("%player%", args[0])));
					} else {
						if (Bukkit.getPlayer(UUID.fromString(entry.getKey())) != null) {
							Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(UUID.fromString(entry.getKey()));
							getPlayer.ifPresent(pm -> pm.setBlacklisted(true));
						}
						UserData.getInstance().getConfig().set(entry.getKey() + ".is-blacklisted", true);
						UserData.getInstance().save();
						DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Blacklist-Add")
								.replaceAll("%player%", args[0])));
					}
					return;
				}
			}
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Blacklist.Username-None-Existent")
					.replaceAll("%player%", args[0])));
		}

	}
}
