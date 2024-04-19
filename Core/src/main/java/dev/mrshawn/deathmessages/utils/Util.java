package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.DMBlockExplodeEvent;
import dev.mrshawn.deathmessages.enums.MobType;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Util {

    public void getExplosionNearbyEffected(Player p, Block b) {
        List<UUID> effected = new ArrayList<>();
        List<Entity> getNearby = new ArrayList<>((DeathMessages.majorVersion > 12) ? b.getWorld().getNearbyEntities(BoundingBox.of(b).expand(100)) : b.getWorld().getNearbyEntities(b.getLocation(), 100, 100, 100));

        getNearby.forEach(ent -> {
                    if (ent instanceof Player) {
                        Optional<PlayerManager> getPlayer = PlayerManager.getPlayer((Player) ent);
                        getPlayer.ifPresent(effect -> {
                            effected.add(ent.getUniqueId());
                            effect.setLastEntityDamager(p);
                        });
                    } else {
                        Optional<EntityManager> getEntity = EntityManager.getEntity(ent.getUniqueId());
                        Optional<PlayerManager> getPlayer = PlayerManager.getPlayer(p);
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

        new ExplosionManager(p.getUniqueId(), b.getType(), b.getLocation(), effected);
        DMBlockExplodeEvent explodeEvent = new DMBlockExplodeEvent(p, b);
        Bukkit.getPluginManager().callEvent(explodeEvent);
    }
}
