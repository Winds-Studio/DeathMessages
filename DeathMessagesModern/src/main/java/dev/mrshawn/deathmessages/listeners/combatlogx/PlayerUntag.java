package dev.mrshawn.deathmessages.listeners.combatlogx;

import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.config.Gangs;
import dev.mrshawn.deathmessages.enums.MessageType;
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
            TextComponent deathMessage = Assets.get(gangKill, pm, (LivingEntity) e.getPreviousEnemies().get(0), "CombatLogX-Quit");
            BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(player, (LivingEntity) e.getPreviousEnemies().get(0), MessageType.PLAYER, deathMessage, Util.getBroadcastWords(player), gangKill);
            Bukkit.getPluginManager().callEvent(event);
        });
        if (!getPlayer.isPresent()) {
            new PlayerManager(player);
        }
    }
}
