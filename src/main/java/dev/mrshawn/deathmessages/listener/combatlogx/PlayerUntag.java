package dev.mrshawn.deathmessages.listener.combatlogx;

import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import dev.mrshawn.deathmessages.listener.EntityDeath;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastDeathMessageEvent;
import dev.mrshawn.deathmessages.assets.Assets;
import dev.mrshawn.deathmessages.config.Gangs;
import dev.mrshawn.deathmessages.enums.MessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class PlayerUntag implements Listener {

    @EventHandler
    public void untagPlayer(PlayerUntagEvent e) {
        Player p = e.getPlayer();
        PlayerManager pm = PlayerManager.getPlayer(p);
        if (pm == null) {
            pm = new PlayerManager(p);
        }
        UntagReason reason = e.getUntagReason();

        if(!reason.equals(UntagReason.QUIT)) return;
        int radius = Gangs.getInstance().getConfig().getInt("Gang.Mobs.player.Radius");
        int amount = Gangs.getInstance().getConfig().getInt("Gang.Mobs.player.Amount");
        boolean gangKill = false;

        if (Gangs.getInstance().getConfig().getBoolean("Gang.Enabled")) {
            int totalMobEntities = 0;
            for (Entity entities : p.getNearbyEntities(radius, radius, radius)) {
                if (entities.getType().equals(EntityType.PLAYER)) {
                    totalMobEntities++;
                }
            }
            if (totalMobEntities >= amount) {
                gangKill = true;
            }
        }
        TextComponent tx = Assets.get(gangKill, pm, e.getPreviousEnemy(), "CombatLogX-Quit");
        if (tx == null) return;
        BroadcastDeathMessageEvent event = new BroadcastDeathMessageEvent(p, e.getPreviousEnemy(), MessageType.PLAYER, tx, EntityDeath.getWorlds(p), gangKill);
        Bukkit.getPluginManager().callEvent(event);
    }
}
