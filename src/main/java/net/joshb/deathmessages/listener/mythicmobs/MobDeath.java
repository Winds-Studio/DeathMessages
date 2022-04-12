package net.joshb.deathmessages.listener.mythicmobs;

import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.joshb.deathmessages.api.EntityManager;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import net.joshb.deathmessages.assets.Assets;
import net.joshb.deathmessages.config.Settings;
import net.joshb.deathmessages.enums.MessageType;
import net.joshb.deathmessages.enums.MobType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.ArrayList;
import java.util.List;

public class MobDeath implements Listener {

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        if (getEntityDeathMessages().getConfigurationSection("Mythic-Mobs-Entities").getKeys(false).isEmpty()) return;
        for (String customMobs : getEntityDeathMessages().getConfigurationSection("Mythic-Mobs-Entities").getKeys(false)) {
            if (e.getMob().getType().getInternalName().equals(customMobs)) {
                EntityManager em = EntityManager.getEntity(e.getEntity().getUniqueId());

                if (em == null || em.getLastPlayerDamager() == null) return;

                PlayerManager damager = em.getLastPlayerDamager();
                TextComponent tx = Assets.entityDeathMessage(em, MobType.MYTHIC_MOB);
                if (tx == null) return;
                BroadcastEntityDeathMessageEvent event = new BroadcastEntityDeathMessageEvent(damager, e.getEntity(), MessageType.ENTITY, tx, getWorlds(e.getEntity()));
                Bukkit.getPluginManager().callEvent(event);
            }
        }
    }

    public static List<World> getWorlds(Entity e){
        List<World> broadcastWorlds = new ArrayList<>();
        if(Settings.getInstance().getConfig().getStringList("Disabled-Worlds").contains(e.getWorld().getName())){
            return broadcastWorlds;
        }
        if (Settings.getInstance().getConfig().getBoolean("Per-World-Messages")) {
            for (String groups : Settings.getInstance().getConfig().getConfigurationSection("World-Groups").getKeys(false)) {
                List<String> worlds = Settings.getInstance().getConfig().getStringList("World-Groups." + groups);
                if(worlds.contains(e.getWorld().getName())){
                    for(String single : worlds){
                        broadcastWorlds.add(Bukkit.getWorld(single));
                    }
                }
            }
            if(broadcastWorlds.isEmpty()){
                broadcastWorlds.add(e.getWorld());
            }
        } else {
            return Bukkit.getWorlds();
        }
        return broadcastWorlds;
    }

    public static FileConfiguration getEntityDeathMessages() {
        return Assets.getEntityDeathMessages();
    }
}
