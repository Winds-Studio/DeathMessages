package dev.mrshawn.deathmessages.listeners.combatlogx;

import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Gangs;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class PlayerUntag implements Listener {

    @EventHandler
    public void untagPlayer(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        PlayerManager getPlayer = PlayerManager.getPlayer(player);
        if (getPlayer != null) {
            UntagReason reason = e.getUntagReason();

            if (!reason.equals(UntagReason.QUIT)) return;

            boolean gangKill = false;

            if (Gangs.getInstance().getConfig().getBoolean("Gang.Enabled")) {
                int radius = Gangs.getInstance().getConfig().getInt("Gang.Mobs.player.Radius");
                int amount = Gangs.getInstance().getConfig().getInt("Gang.Mobs.player.Amount");

                int totalMobEntities = 0;
                List<Entity> nearbyEntities = player.getNearbyEntities(radius, radius, radius);

                for (Entity entity : nearbyEntities) {
                    if (entity.toString().contains("EnderDragonPart")) { // Exclude EnderDragonPart
                        continue;
                    }

                    if (entity instanceof Player) {
                        if (++totalMobEntities >= amount) {
                            gangKill = true;
                            break;
                        }
                    }
                }
            }

            TextComponent deathMessageBody = Assets.get(gangKill, getPlayer, (LivingEntity) e.getPreviousEnemies().get(0), "CombatLogX-Quit");
            TextComponent[] deathMessage = new TextComponent[2];

            if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
                TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
                deathMessage[0] = prefix;
            }

            deathMessage[1] = deathMessageBody;

            TextComponent oldDeathMessage = deathMessage[0].append(deathMessage[1]); // Dreeam TODO: Remove in 1.4.21

            if (!ComponentUtil.isMessageEmpty(deathMessage)) {
                BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(
                        player,
                        (LivingEntity) e.getPreviousEnemies().get(0),
                        MessageType.PLAYER,
                        oldDeathMessage,
                        deathMessage,
                        Util.getBroadcastWorlds(player),
                        gangKill
                );
                Bukkit.getPluginManager().callEvent(event);
            }
        } else {
            new PlayerManager(player);
        }
    }
}
