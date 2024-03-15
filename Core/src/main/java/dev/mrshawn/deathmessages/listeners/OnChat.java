package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.config.legacy.EntityDeathMessages;
import dev.mrshawn.deathmessages.config.legacy.PlayerDeathMessages;
import dev.mrshawn.deathmessages.utils.Assets;
import net.kyori.adventure.text.TextReplacementConfig;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.List;

public class OnChat implements Listener {

	@EventHandler
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (Assets.addingMessage.containsKey(p.getName())) {
			e.setCancelled(true);
			String args = Assets.addingMessage.get(p.getName());
			Assets.addingMessage.remove(p.getName());
			String[] spl = args.split(":");
			if (spl[0].equalsIgnoreCase("Gang") || spl[0].equalsIgnoreCase("Solo")) {
				String mode = spl[0];
				String mobName = spl[1];
				String damageType = spl[2];
				List<String> list = PlayerDeathMessages.getInstance().getConfig().getStringList("Mobs." + mobName + "." + mode + "." + damageType);
				list.add(e.getMessage());
				PlayerDeathMessages.getInstance().getConfig().set("Mobs." + mobName + "." + mode + "." + damageType, list);
				PlayerDeathMessages.getInstance().save();
				PlayerDeathMessages.getInstance().reload();
				DeathMessages.getInstance().adventure().player(p).sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Added-Message")
						.replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(e.getMessage()).build())
						.replaceText(TextReplacementConfig.builder().matchLiteral("%mob_name%").replacement(mobName).build())
						.replaceText(TextReplacementConfig.builder().matchLiteral("%mode%").replacement(mode).build())
						.replaceText(TextReplacementConfig.builder().matchLiteral("%damage_type%").replacement(damageType).build()));
			} else {
				String mobName = spl[0];
				String damageType = spl[1];
				List<String> list = EntityDeathMessages.getInstance().getConfig().getStringList("Entities." + mobName + "." + damageType);
				list.add(e.getMessage());
				EntityDeathMessages.getInstance().getConfig().set("Entities." + mobName + "." + damageType, list);
				EntityDeathMessages.getInstance().save();
				EntityDeathMessages.getInstance().reload();
				DeathMessages.getInstance().adventure().player(p).sendMessage(Assets.formatMessage("Commands.DeathMessages.Sub-Commands.Edit.Added-Message")
						.replaceText(TextReplacementConfig.builder().matchLiteral("%message%").replacement(e.getMessage()).build())
						.replaceText(TextReplacementConfig.builder().matchLiteral("%mob_name%").replacement(mobName).build())
						.replaceText(TextReplacementConfig.builder().matchLiteral("%damage_type%").replacement(damageType).build()));
			}
		}
	}
}
