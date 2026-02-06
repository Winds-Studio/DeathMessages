package dev.mrshawn.deathmessages.listeners.mythicmobs;

import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.BroadcastEntityDeathMessageEvent;
import dev.mrshawn.deathmessages.enums.MessageType;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.utils.Assets;
import dev.mrshawn.deathmessages.utils.ComponentUtil;
import dev.mrshawn.deathmessages.utils.Util;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Set;

public class MobDeath implements Listener {

    @EventHandler
    public void onMythicMobDeath(MythicMobDeathEvent e) {
        ConfigurationSection mobsConfig = getEntityDeathMessages().getConfigurationSection("Mythic-Mobs-Entities");

        if (mobsConfig == null) return;

        Set<String> mobs = mobsConfig.getKeys(false);

        if (mobs.isEmpty()) return;

        for (String customMob : mobs) {
            if (e.getMob().getType().getInternalName().equals(customMob)) {
                EntityManager getEntity = EntityManager.getEntity(e.getEntity().getUniqueId());
                if (getEntity != null) {
                    PlayerManager damager = getEntity.getLastPlayerDamager();
                    TextComponent[] mythicDeath = Assets.entityDeathMessage(getEntity, MobType.MYTHIC_MOB);
                    TextComponent oldMythicDeath = mythicDeath[0].append(mythicDeath[1]); // Dreeam TODO: Remove in 1.4.21

                    if (!ComponentUtil.isMessageEmpty(mythicDeath)) {
                        BroadcastEntityDeathMessageEvent event = new BroadcastEntityDeathMessageEvent(
                                damager,
                                e.getEntity(),
                                MessageType.ENTITY,
                                oldMythicDeath,
                                mythicDeath,
                                Util.getBroadcastWorlds(e.getEntity())
                        );
                        Bukkit.getPluginManager().callEvent(event);
                    }
                }
            }
        }
    }

    public static FileConfiguration getEntityDeathMessages() {
        return Assets.getEntityDeathMessages();
    }
}
