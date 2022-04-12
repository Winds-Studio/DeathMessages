package net.joshb.deathmessages.listener;

import net.joshb.deathmessages.DeathMessages;
import net.joshb.deathmessages.api.EntityManager;
import net.joshb.deathmessages.api.ExplosionManager;
import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.api.events.DMBlockExplodeEvent;
import net.joshb.deathmessages.enums.MobType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.RespawnAnchor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class InteractEvent implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent e) {
        Block b = e.getClickedBlock();
        if(b == null) return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if (b.getType().equals(Material.AIR)) return;
        World.Environment environment = b.getWorld().getEnvironment();
        if (environment.equals(World.Environment.NETHER) || environment.equals(World.Environment.THE_END)) {
            if (b.getType().name().contains("BED") && !b.getType().equals(Material.BEDROCK)) {
                List<UUID> effected = new ArrayList<>();
                for (Player p : e.getClickedBlock().getWorld().getPlayers()) {
                    PlayerManager effect = PlayerManager.getPlayer(p);
                    if (p.getLocation().distanceSquared(b.getLocation()) < 100) {
                        effected.add(p.getUniqueId());
                        effect.setLastEntityDamager(e.getPlayer());
                    }
                }
                for (Entity ent : e.getClickedBlock().getWorld().getEntities()) {
                    if(ent instanceof Player) continue;
                    if (ent.getLocation().distanceSquared(b.getLocation()) < 100) {
                        EntityManager em;
                        if (EntityManager.getEntity(ent.getUniqueId()) == null) {
                            em = new EntityManager(ent, ent.getUniqueId(), MobType.VANILLA);
                        } else {
                            em = EntityManager.getEntity(ent.getUniqueId());
                        }
                        effected.add(ent.getUniqueId());
                        em.setLastPlayerDamager(PlayerManager.getPlayer(e.getPlayer()));
                    }
                }
                new ExplosionManager(e.getPlayer().getUniqueId(), b.getType(), b.getLocation(), effected);
                DMBlockExplodeEvent explodeEvent = new DMBlockExplodeEvent(e.getPlayer(), b);
                Bukkit.getPluginManager().callEvent(explodeEvent);
            }
        } else if (!b.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
            if (DeathMessages.majorVersion() >= 16) {
                if (b.getType().equals(Material.RESPAWN_ANCHOR)) {
                    RespawnAnchor anchor = (RespawnAnchor) b.getBlockData();
                    
                    if (!(anchor.getCharges() == anchor.getMaximumCharges()) && !e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.GLOWSTONE)) return;
                    List<UUID> effected = new ArrayList<>();
                    for (Player p : e.getClickedBlock().getWorld().getPlayers()) {
                        if (p.getLocation().distanceSquared(b.getLocation()) < 100) {
                            PlayerManager effect = PlayerManager.getPlayer(p);
                            effected.add(p.getUniqueId());
                            effect.setLastEntityDamager(e.getPlayer());
                        }
                    }
                    for (Entity ent : e.getClickedBlock().getWorld().getEntities()) {
                        if(ent instanceof Player) continue;
                        if (ent.getLocation().distanceSquared(b.getLocation()) < 100) {
                            EntityManager em;
                            if (EntityManager.getEntity(ent.getUniqueId()) == null) {
                                em = new EntityManager(ent, ent.getUniqueId(), MobType.VANILLA);
                            } else {
                                em = EntityManager.getEntity(ent.getUniqueId());
                            }
                            effected.add(ent.getUniqueId());
                            em.setLastPlayerDamager(PlayerManager.getPlayer(e.getPlayer()));
                        }
                    }
                    new ExplosionManager(e.getPlayer().getUniqueId(), b.getType(), b.getLocation(), effected);
                    DMBlockExplodeEvent explodeEvent = new DMBlockExplodeEvent(e.getPlayer(), b);
                    Bukkit.getPluginManager().callEvent(explodeEvent);
                }
            }
        }
    }
}
