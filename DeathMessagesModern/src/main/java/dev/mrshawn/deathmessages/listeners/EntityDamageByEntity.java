package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.utils.EntityUtil;
import dev.mrshawn.deathmessages.utils.Util;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EvokerFangs;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;

import java.util.Set;

public class EntityDamageByEntity implements Listener {

    @EventHandler
    public void entityDamageByEntity(EntityDamageByEntityEvent e) {
        // Get the damager of ender crystal
        Util.loadCrystalDamager(e.getEntity(), e.getDamager());

        if (e.getEntity() instanceof Player && Bukkit.getServer().getOnlinePlayers().contains((Player) e.getEntity())) {
            Player p = (Player) e.getEntity();
            PlayerManager getPlayer = PlayerManager.getPlayer(p);
            if (getPlayer != null) {
                if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                    Entity lastCrystalDamager = Util.crystalDeathData.get(e.getDamager().getUniqueId());

                    if (e.getDamager() instanceof EnderCrystal && lastCrystalDamager != null) {
                        getPlayer.setLastEntityDamager(lastCrystalDamager);
                        getPlayer.setLastExplosiveEntity(e.getDamager());
                    } else if (e.getDamager() instanceof TNTPrimed) { // For <= 1.20.2, because TNT explosion became BLOCK_EXPLOSION since 1.20.3
                        TNTPrimed tnt = (TNTPrimed) e.getDamager();
                        if (tnt.getSource() instanceof LivingEntity) {
                            getPlayer.setLastEntityDamager(tnt.getSource());
                        }
                        getPlayer.setLastExplosiveEntity(e.getDamager());
                    } else if (e.getDamager() instanceof Firework) { // Firework extends Entity under <= 1.15
                        Firework firework = (Firework) e.getDamager();
                        try {
                            if (firework.getShooter() instanceof LivingEntity) {
                                getPlayer.setLastEntityDamager((LivingEntity) firework.getShooter());
                            }
                            getPlayer.setLastExplosiveEntity(e.getDamager());
                        } catch (NoSuchMethodError e2) {
                            // McMMO ability
                            DeathMessages.LOGGER.error(e2);
                        }
                    } else {
                        getPlayer.setLastEntityDamager(e.getDamager());
                        getPlayer.setLastExplosiveEntity(e.getDamager());
                    }
                } else if (e.getDamager() instanceof Projectile) {
                    Projectile projectile = (Projectile) e.getDamager();
                    if (projectile.getShooter() instanceof LivingEntity) {
                        getPlayer.setLastEntityDamager((LivingEntity) projectile.getShooter());
                    }
                    getPlayer.setLastProjectileEntity(projectile);
                } else if (e.getDamager() instanceof FallingBlock) {
                    getPlayer.setLastEntityDamager(e.getDamager());
                } else if (e.getDamager().getType().isAlive()) {
                    getPlayer.setLastEntityDamager(e.getDamager());
                } else if (e.getDamager() instanceof EvokerFangs) {
                    EvokerFangs evokerFangs = (EvokerFangs) e.getDamager();
                    getPlayer.setLastEntityDamager(evokerFangs.getOwner());
                } else if (e.getDamager() instanceof AreaEffectCloud) {
                    getPlayer.setLastEntityDamager(e.getDamager());
                }
            }
        } else if (!(e.getEntity() instanceof Player) && e.getDamager() instanceof Player) {
            // Cleanup this part below
            // listenedMobs should not use loop
            // should use listenedMobs.contains(EntityUtil.getConfigNodeByEntity(e.getEntity()))
            ConfigurationSection entityConfig = EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Entities");

            if (entityConfig == null) return;

            Set<String> listenedMobs = entityConfig.getKeys(false);
            ConfigurationSection mobsConfig = EntityDeathMessages.getInstance().getConfig().getConfigurationSection("Mythic-Mobs-Entities");

            if (mobsConfig != null && DeathMessages.getHooks().mythicmobsEnabled) {
                listenedMobs.addAll(mobsConfig.getKeys(false));
            }

            if (listenedMobs.isEmpty()) return;

            for (String listened : listenedMobs) {
                if (listened.contains(EntityUtil.getConfigNodeByEntity(e.getEntity()))
                        || (DeathMessages.getHooks().mythicmobsEnabled && DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId()))) {
                    EntityManager getEntity = EntityManager.getEntity(e.getEntity().getUniqueId());
                    if (getEntity != null) {
                        if (e.getCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
                            Entity lastCrystalDamager = Util.crystalDeathData.get(e.getDamager().getUniqueId());

                            if (e.getDamager() instanceof EnderCrystal && lastCrystalDamager != null) {
                                if (lastCrystalDamager instanceof Player) {
                                    PlayerManager getPlayer = PlayerManager.getPlayer((Player) lastCrystalDamager);
                                    if (getPlayer != null) {
                                        getEntity.setLastPlayerDamager(getPlayer);
                                    }
                                    getEntity.setLastExplosiveEntity(e.getDamager());
                                }
                            } else if (e.getDamager() instanceof TNTPrimed) {
                                TNTPrimed tnt = (TNTPrimed) e.getDamager();
                                if (tnt.getSource() instanceof Player) {
                                    PlayerManager getPlayer = PlayerManager.getPlayer((Player) tnt.getSource());
                                    if (getPlayer != null) {
                                        getEntity.setLastPlayerDamager(getPlayer);
                                    }
                                }
                                getEntity.setLastExplosiveEntity(e.getDamager());
                            } else if (e.getDamager() instanceof Firework) {
                                Firework firework = (Firework) e.getDamager();
                                try {
                                    if (firework.getShooter() instanceof Player) {
                                        PlayerManager getPlayer = PlayerManager.getPlayer((Player) firework.getShooter());
                                        if (getPlayer != null) {
                                            getEntity.setLastPlayerDamager(getPlayer);
                                        }
                                    }
                                    getEntity.setLastExplosiveEntity(e.getDamager());
                                } catch (NoSuchMethodError e3) {
                                    // McMMO ability
                                    DeathMessages.LOGGER.error(e3);
                                }
                            } else {
                                PlayerManager getPlayer = PlayerManager.getPlayer((Player) e.getDamager());
                                if (getPlayer != null) {
                                    getEntity.setLastPlayerDamager(getPlayer);
                                }
                                getEntity.setLastExplosiveEntity(e.getDamager());
                            }
                        } else if (e.getDamager() instanceof Projectile) {
                            Projectile projectile = (Projectile) e.getDamager();
                            if (projectile.getShooter() instanceof Player) {
                                PlayerManager getPlayer = PlayerManager.getPlayer((Player) projectile.getShooter());
                                if (getPlayer != null) {
                                    getEntity.setLastPlayerDamager(getPlayer);
                                }
                            }
                            getEntity.setLastProjectileEntity(projectile);
                        } else if (e.getDamager() instanceof Player) {
                            PlayerManager getPlayer = PlayerManager.getPlayer((Player) e.getDamager());
                            if (getPlayer != null) {
                                getEntity.setLastPlayerDamager(getPlayer);
                            }
                        }
                    } else {
                        MobType mobType = MobType.VANILLA;
                        if (DeathMessages.getHooks().mythicmobsEnabled
                                && DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(e.getEntity().getUniqueId())) {
                            mobType = MobType.MYTHIC_MOB;
                        }

                        new EntityManager(e.getEntity(), e.getEntity().getUniqueId(), mobType);
                    }
                }
            }
        }
    }
}
