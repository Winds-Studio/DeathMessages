package net.joshb.deathmessages.listener.combatlogx;

import com.github.sirblobman.combatlogx.api.event.PlayerUntagEvent;
import com.github.sirblobman.combatlogx.api.object.UntagReason;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.BroadcastDeathMessageEvent;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Gangs;
import net.joshb.deathmessages.enums.MessageType;
import net.joshb.deathmessages.listener.EntityDeath;
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
