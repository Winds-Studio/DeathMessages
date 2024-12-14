package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.utils.Util;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnInteract implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInteract(PlayerInteractEvent e) {
        Block getBlock = e.getClickedBlock();

        if (getBlock == null || !e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || isAir(getBlock.getType()))
            return; // Dreeam - No NPE

        World.Environment environment = getBlock.getWorld().getEnvironment();
        if (environment.equals(World.Environment.NETHER) || environment.equals(World.Environment.THE_END)) {
            if (getBlock.getType().name().endsWith("BED") || getBlock.getType().name().endsWith("BED_BLOCK")) {
                Util.getExplosionNearbyEffected(e.getPlayer(), getBlock);
            }
        } else if (Util.isNewerAndEqual(16, 0)) {
            if (!getBlock.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
                if (getBlock.getType().equals(Material.RESPAWN_ANCHOR)) {
                    Util.getExplosionNearbyEffected(e.getPlayer(), getBlock);
                }
            }
        }
    }

    private boolean isAir(Material material) {
        if (Util.isOlderAndEqual(13, 2)) {
            // From 1.14 org.bukkit.Material.isAir()
            switch (material) {
                //<editor-fold defaultstate="collapsed" desc="isAir">
                case AIR:
                case CAVE_AIR:
                case VOID_AIR:
                    // ----- Legacy Separator -----
                case LEGACY_AIR:
                    //</editor-fold>
                    return true;
                default:
                    return false;
            }
        }
        return material.isAir();
    }
}
