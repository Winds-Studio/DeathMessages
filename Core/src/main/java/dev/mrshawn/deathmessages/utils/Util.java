package dev.mrshawn.deathmessages.utils;

import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.api.events.DMBlockExplodeEvent;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.enums.MobType;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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

    public static TextReplacementConfig prefix = TextReplacementConfig.builder()
            .matchLiteral("%prefix%")
            .replacement(convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix")))
            .build();

    public static TextReplacementConfig replace(String match, String replace) {
        return TextReplacementConfig.builder()
                .matchLiteral(match)
                .replacement((replace != null) ? replace : match) // Prevent null replacement
                .build();
    }

    public static Component formatMessage(String path) {
        return convertFromLegacy(Messages.getInstance().getConfig().getString(path)).replaceText(prefix);
    }

    public static TextComponent convertFromLegacy(String s) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
    }

    public static String convertToLegacy(Component component) {
        return LegacyComponentSerializer.legacyAmpersand().serialize(component);
    }

    public static boolean isNumeric(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }

        return false;
    }

    public static void getExplosionNearbyEffected(Player p, Block b) {
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
