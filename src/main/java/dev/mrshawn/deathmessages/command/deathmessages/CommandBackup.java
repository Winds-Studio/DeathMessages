package dev.mrshawn.deathmessages.command.deathmessages;

import dev.mrshawn.deathmessages.config.ConfigManager;
import dev.mrshawn.deathmessages.enums.Permission;
import dev.mrshawn.deathmessages.utils.Assets;
import org.bukkit.command.CommandSender;

public class CommandBackup extends DeathMessagesCommand {


	@Override
	public String command() {
		return "backup";
	}

	@Override
	public void onCommand(CommandSender sender, String[] args) {
		if (!sender.hasPermission(Permission.DEATHMESSAGES_COMMAND_BACKUP.getValue())) {
			sender.sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.No-Permission")));
			return;
		}
		if (args.length == 0) {
			sender.sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Backup.Usage")));
		} else {
			boolean b = Boolean.parseBoolean(args[0]);
			String code = ConfigManager.getInstance().backup(b);
			sender.sendMessage(Assets.convertFromLegacy(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Backup.Backed-Up")
					.replaceAll("%backup-code%", code)));
		}
	}
}