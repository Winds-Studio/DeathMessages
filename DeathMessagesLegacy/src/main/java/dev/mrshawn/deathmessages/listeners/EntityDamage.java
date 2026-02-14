package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityCtx;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.utils.EntityUtil;
import dev.mrshawn.deathmessages.utils.Util;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

public class EntityDamage implements Listener {

    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.isCancelled()) return;

        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            PlayerCtx playerCtx = PlayerCtx.of(p.getUniqueId());
            if (playerCtx != null) {
                playerCtx.setLastDamageCause(e.getCause());

                if (Util.isNewerAndEqual(20, 3) && e.getCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
                    // For >= 1.20.3, because TNT explosion became BLOCK_EXPLOSION since 1.20.3
                    if (e.getDamageSource().getDirectEntity() instanceof TNTPrimed) {
                        TNTPrimed tnt = (TNTPrimed) e.getDamageSource().getDirectEntity();
                        if (tnt.getSource() instanceof LivingEntity) {
                            playerCtx.setLastEntityDamager(tnt.getSource());
                        }
                        playerCtx.setLastExplosiveEntity(tnt);
                    }
                }
            }
            // for fall large if ppl want it float dist = e.getEntity().getFallDistance();
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
