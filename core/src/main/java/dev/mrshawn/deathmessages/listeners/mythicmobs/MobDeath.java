package dev.mrshawn.deathmessages.listeners.mythicmobs;

import dev.mrshawn.deathmessages.api.EntityCtx;
import dev.mrshawn.deathmessages.api.PlayerCtx;
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
                EntityCtx entityCtx = EntityCtx.of(e.getEntity().getUniqueId());
                if (entityCtx != null) {
                    PlayerCtx damagerCtx = entityCtx.getLastPlayerDamager();
                    TextComponent[] mythicDeath = Assets.entityDeathMessage(entityCtx, MobType.MYTHIC_MOB);

                    if (!ComponentUtil.isMessageEmpty(mythicDeath)) {
                        BroadcastEntityDeathMessageEvent event = new BroadcastEntityDeathMessageEvent(
                                damagerCtx,
                                e.getEntity(),
                                MessageType.ENTITY,
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
