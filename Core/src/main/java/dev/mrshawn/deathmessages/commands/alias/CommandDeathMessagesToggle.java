package dev.mrshawn.deathmessages.commands.alias;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Util;
import org.jetbrains.annotations.NotNull;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandDeathMessagesToggle implements CommandExecutor {

	@Override
	public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String cmdLabel, String[] args) {
		if (sender instanceof Player && !sender.hasPermission(Permission.DEATHMESSAGES_COMMAND.getValue())) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Util.formatMessage("Commands.DeathMessages.No-Permission"));
			return false;
		}
		if (!(sender instanceof Player)) {
			DeathMessages.getInstance().adventure().sender(sender).sendMessage(Util.formatMessage("Commands.DeathMessages.Player-Only-Command"));
			return false;
		}
		Player player = (Player) sender;
		if (!player.hasPermission(Permission.DEATHMESSAGES_COMMAND_TOGGLE.getValue())) {
			DeathMessages.getInstance().adventure().player(player).sendMessage(Util.formatMessage("Commands.DeathMessages.No-Permission"));
			return false;
		}
		Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(player);
		getPlayer.ifPresent(pm -> {
			boolean msg = pm.getMessagesEnabled();
			if (msg) {
				pm.setMessagesEnabled(false);
				DeathMessages.getInstance().adventure().player(player).sendMessage(Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-Off"));
			} else {
				pm.setMessagesEnabled(true);
				DeathMessages.getInstance().adventure().player(player).sendMessage(Util.formatMessage("Commands.DeathMessages.Sub-Commands.Toggle.Toggle-On"));
			}
		});
		return false;
	}
}
