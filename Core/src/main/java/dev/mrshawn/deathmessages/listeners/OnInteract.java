package dev.mrshawn.deathmessages.listeners;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.DMBlockExplodeEvent;
import dev.mrshawn.deathmessages.enums.MobType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class OnInteract implements Listener {

	@EventHandler(priority = EventPriority.MONITOR)
	public void onInteract(PlayerInteractEvent e) {
		Block getBlock = e.getClickedBlock();

		if (getBlock == null || !e.getAction().equals(Action.RIGHT_CLICK_BLOCK) || isAir(getBlock.getType()))
			return; // Dreeam - No NPE

		World.Environment environment = getBlock.getWorld().getEnvironment();
		if (environment.equals(World.Environment.NETHER) || environment.equals(World.Environment.THE_END)) {
			if (getBlock.getType().name().contains("BED") && !getBlock.getType().equals(Material.BEDROCK)) {
				callEvent(e, getBlock);
			}
		} else if (DeathMessages.majorVersion >= 16) {
			if (!getBlock.getWorld().getEnvironment().equals(World.Environment.NETHER)) {
				if (getBlock.getType().equals(Material.RESPAWN_ANCHOR)) {
					callEvent(e, getBlock);
				}
			}
		}
	}

	private void callEvent(PlayerInteractEvent e, Block b) {
		List<UUID> effected = new ArrayList<>();
		List<Entity> getNearby = new ArrayList<>((DeathMessages.majorVersion > 12) ? b.getWorld().getNearbyEntities(BoundingBox.of(b).expand(100)) : b.getWorld().getNearbyEntities(b.getLocation(), 100, 100, 100));

		getNearby.forEach(ent -> {
					if (ent instanceof Player) {
						Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) ent);
						getPlayer.ifPresent(effect -> {
							effected.add(ent.getUniqueId());
							effect.setLastEntityDamager(e.getPlayer());
						});
					} else {
						Optional<EntityManager> getEntity = EntityManager.getEntity(ent.getUniqueId());
						Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(e.getPlayer());
						getEntity.ifPresent(em -> {
							effected.add(ent.getUniqueId());
							getPlayer.ifPresent(em::setLastPlayerDamager);
						});
						if (!getEntity.isPresent()) {
							new EntityManager(ent, ent.getUniqueId(), MobType.VANILLA);
						}
					}
				}
		);

		new ExplosionManager(e.getPlayer().getUniqueId(), b.getType(), b.getLocation(), effected);
		DMBlockExplodeEvent explodeEvent = new DMBlockExplodeEvent(e.getPlayer(), b);
		Bukkit.getPluginManager().callEvent(explodeEvent);
	}

	private boolean isAir(Material material) {
		if (DeathMessages.majorVersion <= 13) {
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
