package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityCtx;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.utils.EntityUtil;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByBlockEvent;

import java.util.Set;

public class EntityDamageByBlock implements Listener {

    @EventHandler(priority = EventPriority.HIGH)
    public void onEntityDeath(EntityDamageByBlockEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PlayerCtx playerCtx = PlayerCtx.of(p.getUniqueId());
            if (playerCtx != null) {
                playerCtx.setLastDamageCause(e.getCause());
            }
        } else {
            ConfigurationSection entityConfig = EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Entities");

            if (entityConfig == null) return;

            Set<String> listenedMobs = entityConfig.getKeys(false);
            ConfigurationSection mobConfig = EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Mythic-Mobs-Entities");

            if (mobConfig != null && DeathMessages.getHooks().mythicmobsEnabled && !DeathMessages.getHooks().useMythicMobsDeathMessages) {
                listenedMobs.addAll(mobConfig.getKeys(false));
            }

            if (listenedMobs.isEmpty()) return;

            for (String listened : listenedMobs) {
                if (listened.contains(EntityUtil.getConfigNodeByEntity(e.getEntity()))) {
                    EntityCtx entityCtx = EntityCtx.of(e.getEntity().getUniqueId());

                    if (entityCtx != null) {
                        entityCtx.setLastDamageCause(e.getCause());
                    } else {
                        MobType mobType = MobType.VANILLA;
                        if (DeathMessages.getHooks().mythicmobsEnabled
                                && DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
                            mobType = MobType.MYTHIC_MOB;
                        }
                        EntityCtx.create(new EntityCtx(e.getEntity(), mobType));
                    }
                }
            }
        }
    }
}
