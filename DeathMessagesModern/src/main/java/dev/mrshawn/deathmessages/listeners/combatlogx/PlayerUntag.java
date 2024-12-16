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
import dev.mrshawn.deathmessages.utils.Util;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PlayerUntag implements Listener {

    @EventHandler
    public void untagPlayer(PlayerUntagEvent e) {
        Player player = e.getPlayer();
        Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(player);
        getPlayer.ifPresent(pm -> {
            UntagReason reason = e.getUntagReason();

            if (!reason.equals(UntagReason.QUIT)) return;

            int radius = Gangs.getInstance().getConfig().getInt("Gang.Mobs.player.Radius");
            int amount = Gangs.getInstance().getConfig().getInt("Gang.Mobs.player.Amount");
            boolean gangKill = false;

            if (Gangs.getInstance().getConfig().getBoolean("Gang.Enabled")) {
                int totalMobEntities = 0;
                Predicate<Entity> isNotDragonParts = entity -> !entity.toString().contains("EnderDragonPart"); // Exclude EnderDragonPart
                List<Entity> entities = player.getNearbyEntities(radius, radius, radius).stream()
                        .filter(isNotDragonParts).collect(Collectors.toList());

                for (Entity entity : entities) {
                    if (entity.getType().equals(EntityType.PLAYER)) {
                        totalMobEntities++;
                    }
                }

                if (totalMobEntities >= amount) {
                    gangKill = true;
                }
            }

            TextComponent deathMessageBody = Assets.get(gangKill, pm, (LivingEntity) e.getPreviousEnemies().get(0), "CombatLogX-Quit");
            TextComponent[] deathMessage = new TextComponent[2];

            if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
                TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
                deathMessage[0] = prefix;
            }

            deathMessage[1] = deathMessageBody;

            TextComponent oldDeathMessage = deathMessage[0] != null ? deathMessage[0].append(deathMessage[1]) : deathMessage[1]; // Dreeam TODO: Remove in 1.4.21

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
        });

        if (getPlayer.isEmpty()) {
            new PlayerManager(player);
        }
    }
}
