package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.EntityManager;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.config.EntityDeathMessages;
import net.joshb.deathmessages.enums.MobType;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;

import java.util.Set;

public class EntityDamageByBlock implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDamageByBlockEvent e) {
        if (e.getEntity() instanceof Player && Bukkit.getOnlinePlayers().contains(e.getEntity())) {
            Player p = (Player) e.getEntity();
            PlayerManager pm = PlayerManager.getPlayer(p);
            pm.setLastDamageCause(e.getCause());
        } else {
            if(EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Entities") == null) return;
            Set<String> listenedMobs = EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Entities")
                    .getKeys(false);
            if(EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Mythic-Mobs-Entities") != null
                        && DeathMessages.plugin.mythicmobsEnabled){
                listenedMobs.addAll(EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Mythic-Mobs-Entities")
                        .getKeys(false));
            }
            if(listenedMobs.isEmpty()) return;
            for (String listened : listenedMobs) {
                if(listened.contains(e.getEntity().getType().getEntityClass().getSimpleName().toLowerCase())){
                    EntityManager em;
                    if(EntityManager.getEntity(e.getEntity().getUniqueId()) == null){
                        MobType mobType = MobType.VANILLA;
                        if(DeathMessages.plugin.mythicmobsEnabled
                                && DeathMessages.plugin.mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId())){
                            mobType = MobType.MYTHIC_MOB;
                        }
                        em = new EntityManager(e.getEntity(), e.getEntity().getUniqueId(), mobType);
                    } else {
                        em = EntityManager.getEntity(e.getEntity().getUniqueId());
                    }
                    em.setLastDamageCause(e.getCause());
                }
            }
        }
    }

}

