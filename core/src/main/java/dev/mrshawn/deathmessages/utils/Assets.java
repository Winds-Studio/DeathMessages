package dev.mrshawn.deathmessages.utils;

import com.cryptomorin.xseries.XMaterial;
import com.meowj.langutils.lang.LanguageHelper;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityCtx;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.config.PlayerDeathMessages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.config.files.Config;
import dev.mrshawn.deathmessages.config.files.FileStore;
import dev.mrshawn.deathmessages.enums.DeathAffiliation;
import dev.mrshawn.deathmessages.enums.DeathModes;
import dev.mrshawn.deathmessages.enums.MobType;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Firework;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.LlamaSpit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.ShulkerBullet;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.entity.Tameable;
import org.bukkit.entity.Trident;
import org.bukkit.entity.WitherSkull;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;

public class Assets {

    // Dreeam TODO - to figure out why the value defined in private static field will not change with the change of the config value
    //private static final CommentedConfiguration config = Settings.getInstance().getConfig();

    public static TextComponent[] playerNatureDeathMessage(PlayerCtx playerCtx, Player player) {
        TextComponent[] components = ComponentUtil.empty();

        if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
            TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
            components[0] = prefix;
        }

        // Natural Death
        if (playerCtx.getLastExplosiveEntity() instanceof EnderCrystal) {
            components[1] = Assets.getNaturalDeath(playerCtx, "End-Crystal");
        } else if (playerCtx.getLastExplosiveEntity() instanceof TNTPrimed) {
            components[1] = Assets.getNaturalDeath(playerCtx, "TNT");
        } else if (playerCtx.getLastExplosiveEntity() instanceof Firework) {
            components[1] = Assets.getNaturalDeath(playerCtx, "Firework");
        } else if (playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            components[1] = Assets.getNaturalDeath(playerCtx, "Climbable");
        } else if (playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            ExplosionManager explosion = ExplosionManager.getManagerIfEffected(player.getUniqueId());
            if (explosion != null) {
                if (explosion.getMaterial().name().contains("BED")) {
                    components[1] = Assets.getNaturalDeath(playerCtx, "Bed");
                }
                if (Util.isNewerAndEqual(16, 0)) {
                    if (explosion.getMaterial().equals(Material.RESPAWN_ANCHOR)) {
                        components[1] = Assets.getNaturalDeath(playerCtx, "Respawn-Anchor");
                    }
                }
                // Dreeam TODO: Check weather needs to handle unknow explosion to prevent potential empty death message
            }
        } else if (playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            components[1] = Assets.getNaturalDeath(playerCtx, Assets.getSimpleProjectile(playerCtx.getLastProjectileEntity()));
        } else if (Util.isNewerAndEqual(9, 0) && Util.isOlderAndEqual(999, 999) && playerCtx.getLastEntityDamager() instanceof AreaEffectCloud cloud) { // Fix MC-84595 - Killed by Dragon's Breath
            if (cloud.getSource() instanceof EnderDragon) {
                playerCtx.setLastDamageCause(
                        Settings.getInstance().getConfig().getBoolean(Config.FIX_MC_84595.getPath())
                                ? EntityDamageEvent.DamageCause.DRAGON_BREATH : EntityDamageEvent.DamageCause.ENTITY_ATTACK
                );
            }

            components[1] = Assets.getNaturalDeath(playerCtx, Assets.getSimpleCause(playerCtx.getLastDamageCause()));
        } else {
            components[1] = Assets.getNaturalDeath(playerCtx, Assets.getSimpleCause(playerCtx.getLastDamageCause()));
        }

        return components;
    }

    public static TextComponent[] playerDeathMessage(PlayerCtx playerCtx, boolean gang) {
        LivingEntity mob = (LivingEntity) playerCtx.getLastEntityDamager();
        TextComponent[] components = ComponentUtil.empty();

        if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
            TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
            components[0] = prefix;
        }

        if (playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            switch (playerCtx.getLastExplosiveEntity()) {
                case EnderCrystal enderCrystal -> {
                    components[1] = get(gang, playerCtx, mob, "End-Crystal");
                    return components;
                }
                case TNTPrimed tntPrimed -> {
                    components[1] = get(gang, playerCtx, mob, "TNT");
                    return components;
                }
                case Firework firework -> {
                    components[1] = get(gang, playerCtx, mob, "Firework");
                    return components;
                }
                default -> {
                }
            }

            components[1] = get(gang, playerCtx, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION));
            return components;
        }

        if (playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            ExplosionManager explosionManager = ExplosionManager.getManagerIfEffected(playerCtx.getUUID());
            if (explosionManager != null) {
                PlayerCtx pyroCtx = PlayerCtx.of(explosionManager.getPyro());
                if (pyroCtx != null) {
                    // Bed kill
                    if (explosionManager.getMaterial().name().contains("BED")) {
                        components[1] = get(gang, playerCtx, pyroCtx.getPlayer(), "Bed");
                        return components;
                    }

                    // Respawn Anchor kill
                    if (Util.isNewerAndEqual(16, 0)) {
                        if (explosionManager.getMaterial().equals(Material.RESPAWN_ANCHOR)) {
                            components[1] = get(gang, playerCtx, pyroCtx.getPlayer(), "Respawn-Anchor");
                            return components;
                        }
                    }
                }
            }
        }

        boolean hasWeapon = MaterialUtil.hasWeapon(mob, playerCtx.getLastDamageCause());

        if (hasWeapon) {
            if (playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                components[1] = getWeapon(gang, playerCtx, mob);
                return components;
            }

            if (playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.PROJECTILE) && playerCtx.getLastProjectileEntity() instanceof Arrow) {
                components[1] = getProjectile(gang, playerCtx, mob, getSimpleProjectile(playerCtx.getLastProjectileEntity()));
                return components;
            }

            components[1] = get(gang, playerCtx, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
            return components;
        } else {
            // Dreeam TODO: idk why there is for loop used to if (playerCtx.getLastDamageCause().equals(dc)), no need, waste performance..
            for (EntityDamageEvent.DamageCause dc : EntityDamageEvent.DamageCause.values()) {
                if (playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                    components[1] = getProjectile(gang, playerCtx, mob, getSimpleProjectile(playerCtx.getLastProjectileEntity()));
                    return components;
                }

                if (playerCtx.getLastDamageCause().equals(dc)) {
                    components[1] = get(gang, playerCtx, mob, getSimpleCause(dc));
                    return components;
                }
            }

            return ComponentUtil.empty();
        }
    }

    public static TextComponent[] entityDeathMessage(EntityCtx entityCtx, MobType mobType) {
        PlayerCtx damagerCtx = entityCtx.getLastPlayerDamager();

        if (damagerCtx == null) return ComponentUtil.empty();

        Player p = damagerCtx.getPlayer();
        TextComponent[] components = ComponentUtil.empty();

        if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
            TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
            components[0] = prefix;
        }

        if (entityCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
            switch (entityCtx.getLastExplosiveEntity()) {
                case EnderCrystal enderCrystal -> {
                    components[1] = getEntityDeath(p, entityCtx.getEntity(), "End-Crystal", mobType);
                    return components;
                }
                case TNTPrimed tntPrimed -> {
                    components[1] = getEntityDeath(p, entityCtx.getEntity(), "TNT", mobType);
                    return components;
                }
                case Firework firework -> {
                    components[1] = getEntityDeath(p, entityCtx.getEntity(), "Firework", mobType);
                    return components;
                }
                default -> {
                }
            }

            components[1] = getEntityDeath(p, entityCtx.getEntity(), getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION), mobType);
            return components;
        }

        if (entityCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
            ExplosionManager explosionManager = ExplosionManager.getManagerIfEffected(entityCtx.getUUID());
            if (explosionManager != null) {
                PlayerCtx pyroCtx = PlayerCtx.of(explosionManager.getPyro());
                if (pyroCtx != null) {
                    // Bed kill
                    if (explosionManager.getMaterial().name().contains("BED")) {
                        components[1] = getEntityDeath(pyroCtx.getPlayer(), entityCtx.getEntity(), "Bed", mobType);
                        return components;
                    }

                    // Respawn Anchor kill
                    if (Util.isNewerAndEqual(16, 0)) {
                        if (explosionManager.getMaterial().equals(Material.RESPAWN_ANCHOR)) {
                            components[1] = getEntityDeath(pyroCtx.getPlayer(), entityCtx.getEntity(), "Respawn-Anchor", mobType);
                            return components;
                        }
                    }
                }
            }
        }

        boolean hasWeapon = MaterialUtil.hasWeapon(p, damagerCtx.getLastDamageCause());

        if (hasWeapon) {
            if (entityCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
                components[1] = getEntityDeathWeapon(p, entityCtx.getEntity(), mobType);
                return components;
            }

            if (entityCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.PROJECTILE) && entityCtx.getLastProjectileEntity() instanceof Arrow) {
                components[1] = getEntityDeathProjectile(p, entityCtx, getSimpleProjectile(entityCtx.getLastProjectileEntity()), mobType);
                return components;
            }

            components[1] = getEntityDeathWeapon(p, entityCtx.getEntity(), mobType);
            return components;
        } else {
            for (EntityDamageEvent.DamageCause dc : EntityDamageEvent.DamageCause.values()) {
                if (entityCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
                    components[1] = getEntityDeathProjectile(p, entityCtx, getSimpleProjectile(entityCtx.getLastProjectileEntity()), mobType);
                    return components;
                }

                if (entityCtx.getLastDamageCause().equals(dc)) {
                    components[1] = getEntityDeath(p, entityCtx.getEntity(), getSimpleCause(dc), mobType);
                    return components;
                }
            }

            return ComponentUtil.empty();
        }
    }

    public static TextComponent getNaturalDeath(PlayerCtx playerCtx, String damageCause) {
        List<String> msgs = sortList(getPlayerDeathMessages().getStringList("Natural-Cause." + damageCause), playerCtx.getPlayer(), playerCtx.getPlayer());

        if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
            DeathMessages.LOGGER.warn("node: [Natural-Cause.{}]", damageCause);
        if (msgs.isEmpty()) {
            DeathMessages.LOGGER.warn("Can't find message node: [Natural-Cause.{}] in PlayerDeathMessages.yml", damageCause);
            DeathMessages.LOGGER.warn("This should not happen, please check your config or report issue on Github");
            msgs = sortList(getPlayerDeathMessages().getStringList("Natural-Cause.Unknown"), playerCtx.getPlayer(), playerCtx.getPlayer());
            DeathMessages.LOGGER.warn("Fallback this death to [Natural-Cause.Unknown] message node");
        }

        String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.getFirst();
        TextComponent.Builder base = Component.text();
        List<String> rawEvents = new ArrayList<>();
        msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

        if (msg.contains("%block%") && playerCtx.getLastEntityDamager() instanceof FallingBlock fb) {
            try {
                // TODO: to NMS hook
                String material = Util.isNewerAndEqual(12, 0)
                        ? fb.getBlockData().getMaterial().toString().toLowerCase()
                        : fb.getMaterial().toString().toLowerCase();
                String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);

                base.append(Util.convertFromLegacy(msg.replaceAll("%block%", configValue)));
            } catch (NullPointerException e) {
                DeathMessages.LOGGER.error("Could not parse %block%. Please check your config for a wrong value." +
                        " Your materials could be spelt wrong or it does not exists in the config. Open a issue if you need help, " + "https://github.com/Winds-Studio/DeathMessages/issues");
                playerCtx.setLastEntityDamager(null);
                return getNaturalDeath(playerCtx, getSimpleCause(EntityDamageEvent.DamageCause.SUFFOCATION));
            }
        } else if (msg.contains("%climbable%") && playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.FALL)) {
            try {
                String material = playerCtx.getLastClimbing().toString().toLowerCase();
                String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);

                base.append(Util.convertFromLegacy(msg.replaceAll("%climbable%", configValue)));
            } catch (NullPointerException e) {
                playerCtx.setLastClimbing(null);
                return getNaturalDeath(playerCtx, getSimpleCause(EntityDamageEvent.DamageCause.FALL));
            }
        } else if (msg.contains("%weapon%") && playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            ItemStack i = playerCtx.getPlayer().getEquipment().getItemInMainHand();

            if (!i.getType().equals(XMaterial.BOW.parseMaterial())) {
                return getNaturalDeath(playerCtx, "Projectile-Unknown");
            }
            if (Util.isNewerAndEqual(14, 0)) {
                if (!i.getType().equals(XMaterial.CROSSBOW.parseMaterial())) {
                    return getNaturalDeath(playerCtx, "Projectile-Unknown");
                }
            }

            Component displayName;
            if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
                if (Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED.getPath())) {
                    if (!Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_IGNORE_ENCHANTMENTS.getPath())) {
                        if (i.getEnchantments().isEmpty()) {
                            return getNaturalDeath(playerCtx, "Projectile-Unknown");
                        }
                    } else {
                        return getNaturalDeath(playerCtx, "Projectile-Unknown");
                    }
                }
                displayName = getI18nName(i, playerCtx.getPlayer());
            } else {
                displayName = ComponentUtil.getItemStackDisplayName(i);
            }

            TextComponent message = Util.convertFromLegacy(msg);
            Component weapon = ComponentUtil.buildItemHover(playerCtx.getPlayer(), i, displayName);

            base.append(message.replaceText(TextReplacementConfig.builder().matchLiteral("%weapon%").replacement(weapon).build()));
        } else {
            TextComponent message = Util.convertFromLegacy(msg);
            base.append(message);
        }

        Component baseWithEvents = base.build();

        if (!rawEvents.isEmpty()) {
            int index = 0;
            for (String rawEvent : rawEvents) {
                Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, playerCtx, null, null, false, true);
                baseWithEvents = baseWithEvents.replaceText(
                        TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
                );
            }
        }

        return (TextComponent) playerDeathPlaceholders(baseWithEvents, playerCtx, null);
    }

    public static TextComponent getWeapon(boolean gang, PlayerCtx playerCtx, LivingEntity mob) {
        final boolean basicMode = getPlayerDeathMessages().getBoolean("Basic-Mode.Enabled");
        String entityName = EntityUtil.getConfigNodeByEntity(mob);
        final String mode = basicMode ? DeathModes.BASIC_MODE.getValue() : DeathModes.MOBS.getValue()
                                                                           + "." + entityName;
        final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();
        //Bukkit.broadcastMessage(DeathMessages.getInstance().mythicmobsEnabled + " - " + DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId()));
        List<String> msgs = sortList(getPlayerDeathMessages().getStringList(mode + "." + affiliation + ".Weapon"), playerCtx.getPlayer(), mob);

        if (DeathMessages.getHooks().mythicmobsEnabled && DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
            String mmMobType = DeathMessages.getHooks().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
            //Bukkit.broadcastMessage("is myth - " + mmMobType);
            msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + mmMobType + "." + affiliation + ".Weapon"), playerCtx.getPlayer(), mob);

            if (msgs.isEmpty()) return Component.empty(); // Don't send mm mob death msg if no configured death msg.
        }

        if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
            DeathMessages.LOGGER.warn("node: [{}.{}.Weapon]", mode, affiliation);
        if (msgs.isEmpty()) {
            DeathMessages.LOGGER.warn("Can't find message node: [{}.{}.Weapon] in PlayerDeathMessages.yml", mode, affiliation);
            DeathMessages.LOGGER.warn("This should not happen, please check your config or report this issue on Github");
            msgs = sortList(getPlayerDeathMessages().getStringList(DeathModes.BASIC_MODE.getValue() + "." + affiliation + ".Weapon"), playerCtx.getPlayer(), mob);
            DeathMessages.LOGGER.warn("Fallback this death to Basic-Mode of PlayerDeathMessages");
        }

        String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.getFirst();
        TextComponent.Builder base = Component.text();
        List<String> rawEvents = new ArrayList<>();
        msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

        if (msg.contains("%weapon%")) {
            ItemStack i = mob.getEquipment().getItemInMainHand();
            Component displayName;
            if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
                if (FileStore.CONFIG.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED)) {
                    if (!FileStore.CONFIG.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_IGNORE_ENCHANTMENTS)) {
                        if (i.getEnchantments().isEmpty()) {
                            return get(gang, playerCtx, mob, FileStore.CONFIG.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO));
                        }
                    } else {
                        return get(gang, playerCtx, mob, FileStore.CONFIG.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO));
                    }
                }
                displayName = getI18nName(i, playerCtx.getPlayer());
            } else {
                displayName = ComponentUtil.getItemStackDisplayName(i);
            }

            TextComponent deathMessage = Util.convertFromLegacy(msg);
            Component weaponHover = ComponentUtil.buildItemHover(playerCtx.getPlayer(), i, displayName);

            base.append(deathMessage.replaceText(TextReplacementConfig.builder().matchLiteral("%weapon%").replacement(weaponHover).build()));
        } else {
            TextComponent deathMessage = Util.convertFromLegacy(msg);
            base.append(deathMessage);
        }

        Component baseWithEvents = base.build();

        if (!rawEvents.isEmpty()) {
            int index = 0;
            for (String rawEvent : rawEvents) {
                Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, playerCtx, null, mob, false, true);
                baseWithEvents = baseWithEvents.replaceText(
                        TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
                );
            }
        }

        return (TextComponent) playerDeathPlaceholders(baseWithEvents, playerCtx, mob);
    }

    public static TextComponent getEntityDeathWeapon(Player p, Entity e, MobType mobType) {
        String entityName = EntityUtil.getConfigNodeByEntity(e);
        boolean hasOwner = EntityUtil.hasOwner(e);
        List<String> msgs = sortList(getEntityDeathMessages().getStringList("Entities." + entityName + ".Weapon"), p, e);

        if (mobType.equals(MobType.MYTHIC_MOB)) {
            String mmMobType = null;
            if (DeathMessages.getHooks().mythicmobsEnabled && DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(e.getUniqueId())) {
                mmMobType = DeathMessages.getHooks().mythicMobs.getAPIHelper().getMythicMobInstance(e).getMobType();
            } else {
                // reserved
            }

            msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + mmMobType + ".Weapon"), p, e);

            if (msgs.isEmpty()) return Component.empty(); // Don't send mm mob death msg if no configured death msg.
        }

        if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
            DeathMessages.LOGGER.warn("node: [Entities.{}.Weapon]", entityName);
        if (msgs.isEmpty()) {
            // This death message will not be broadcast, since user have not set death message for this entity
            return Component.empty();
        }

        String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.getFirst();
        TextComponent.Builder base = Component.text();
        List<String> rawEvents = new ArrayList<>();
        msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

        if (msg.contains("%weapon%")) {
            ItemStack i = p.getEquipment().getItemInMainHand();
            Component displayName;
            if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
                if (Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED.getPath())) {
                    if (!Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_IGNORE_ENCHANTMENTS.getPath())) {
                        if (i.getEnchantments().isEmpty()) {
                            return getEntityDeath(p, e,
                                    Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO.getPath()), mobType);
                        }
                    } else {
                        return getEntityDeath(p, e,
                                Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO.getPath()), mobType);
                    }
                }
                displayName = getI18nName(i, p);
            } else {
                displayName = ComponentUtil.getItemStackDisplayName(i);
            }

            TextComponent deathMessage = Util.convertFromLegacy(msg);
            Component weaponHover = ComponentUtil.buildItemHover(p, i, displayName);

            base.append(deathMessage.replaceText(TextReplacementConfig.builder().matchLiteral("%weapon%").replacement(weaponHover).build()));
        } else {
            TextComponent deathMessage = Util.convertFromLegacy(msg);
            base.append(deathMessage);
        }

        Component baseWithEvents = base.build();

        if (!rawEvents.isEmpty()) {
            int index = 0;
            for (String rawEvent : rawEvents) {
                Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, null, p, e, hasOwner, false);
                baseWithEvents = baseWithEvents.replaceText(
                        TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
                );
            }
        }

        return (TextComponent) entityDeathPlaceholders(baseWithEvents, p, e, hasOwner);
    }

    public static TextComponent get(boolean gang, PlayerCtx playerCtx, LivingEntity mob, String damageCause) {
        final boolean basicMode = getPlayerDeathMessages().getBoolean("Basic-Mode.Enabled");
        String entityName = EntityUtil.getConfigNodeByEntity(mob);
        final String mode = basicMode ? DeathModes.BASIC_MODE.getValue() : DeathModes.MOBS.getValue()
                                                                           + "." + entityName;
        final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();
        List<String> msgs = sortList(getPlayerDeathMessages().getStringList(mode + "." + affiliation + "." + damageCause), playerCtx.getPlayer(), mob);

        if (DeathMessages.getHooks().mythicmobsEnabled && DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
            String mmMobType = DeathMessages.getHooks().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
            //System.out.println("is myth - " + mmMobType);
            msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + mmMobType + "." + affiliation + "." + damageCause), playerCtx.getPlayer(), mob);

            if (msgs.isEmpty()) return Component.empty(); // Don't send mm mob death msg if no configured death msg.
        }

        if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
            DeathMessages.LOGGER.warn("node: [{}.{}.{}]", mode, affiliation, damageCause);
        if (msgs.isEmpty()) {
            msgs = sortList(getPlayerDeathMessages().getStringList(DeathModes.MOBS.getValue() + ".player." + affiliation + "." + damageCause), playerCtx.getPlayer(), mob);
            if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
                DeathMessages.LOGGER.warn("node2: [{}.player.{}.{}]", DeathModes.MOBS.getValue(), affiliation, damageCause);
            if (msgs.isEmpty()) {
                if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
                    DeathMessages.LOGGER.info("Redirected from [{}.player.{}.{}]", DeathModes.MOBS.getValue(), affiliation, damageCause);
                if (Settings.getInstance().getConfig().getBoolean(Config.DEFAULT_NATURAL_DEATH_NOT_DEFINED.getPath()))
                    return getNaturalDeath(playerCtx, damageCause);
                if (Settings.getInstance().getConfig().getBoolean(Config.DEFAULT_MELEE_LAST_DAMAGE_NOT_DEFINED.getPath()))
                    return get(gang, playerCtx, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
                DeathMessages.LOGGER.warn("This death message will not be broadcast, unless you enable [Default-Natural-Death-Not-Defined] in Settings.yml");
                return Component.empty();
            }
        }

        String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.getFirst();
        TextComponent.Builder base = Component.text();
        List<String> rawEvents = new ArrayList<>();
        msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

        base.append(Util.convertFromLegacy(msg));

        Component baseWithEvents = base.build();

        if (!rawEvents.isEmpty()) {
            int index = 0;
            for (String rawEvent : rawEvents) {
                Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, playerCtx, null, mob, false, true);
                baseWithEvents = baseWithEvents.replaceText(
                        TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
                );
            }
        }

        return (TextComponent) playerDeathPlaceholders(baseWithEvents, playerCtx, mob);
    }

    public static TextComponent getProjectile(boolean gang, PlayerCtx playerCtx, LivingEntity mob, String projectileDamage) {
        final boolean basicMode = getPlayerDeathMessages().getBoolean("Basic-Mode.Enabled");
        String entityName = EntityUtil.getConfigNodeByEntity(mob);
        final String mode = basicMode ? DeathModes.BASIC_MODE.getValue() : DeathModes.MOBS.getValue()
                                                                           + "." + entityName;
        final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();
        List<String> msgs = sortList(getPlayerDeathMessages().getStringList(mode + "." + affiliation + "." + projectileDamage), playerCtx.getPlayer(), mob);

        if (DeathMessages.getHooks().mythicmobsEnabled && DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
            String mmMobType = DeathMessages.getHooks().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
            msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + mmMobType + "." + affiliation + "." + projectileDamage), playerCtx.getPlayer(), mob);

            if (msgs.isEmpty()) return Component.empty(); // Don't send mm mob death msg if no configured death msg.
        }

        if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
            DeathMessages.LOGGER.warn("node: [{}.{}.{}]", mode, affiliation, projectileDamage);
        if (msgs.isEmpty()) {
            msgs = sortList(getPlayerDeathMessages().getStringList(DeathModes.MOBS.getValue() + ".player." + affiliation + "." + projectileDamage), playerCtx.getPlayer(), mob);
            if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
                DeathMessages.LOGGER.warn("node2: [{}.player.{}.{}]", DeathModes.MOBS.getValue(), affiliation, projectileDamage);
            if (msgs.isEmpty()) {
                if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
                    DeathMessages.LOGGER.info("Redirected from [{}.player.{}.{}]", DeathModes.MOBS.getValue(), affiliation, projectileDamage);
                if (Settings.getInstance().getConfig().getBoolean(Config.DEFAULT_NATURAL_DEATH_NOT_DEFINED.getPath()))
                    return getNaturalDeath(playerCtx, projectileDamage);
                DeathMessages.LOGGER.warn("This death message will not be broadcast, unless you enable [Default-Natural-Death-Not-Defined] in Settings.yml");
                return Component.empty();
            }
        }

        String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.getFirst();
        TextComponent.Builder base = Component.text();
        List<String> rawEvents = new ArrayList<>();
        msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

        if (msg.contains("%weapon%") && playerCtx.getLastDamageCause().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
            Component weaponHover;
            ItemStack i = mob.getEquipment().getItemInMainHand();

            // If there is no item in killer's main hands, then it can be seen as the victim got attack by projectile
            // So just render that projectile name as the weapon name
            // Death message for killed by skeleton will still show bow, since bow in the main hand, which is also
            // meet the expectation
            // Same logic in getEntityDeathProjectile()
            if (!MaterialUtil.isAir(i)) {
                Component displayName;
                if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
                    if (Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED.getPath())) {
                        if (!Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO.getPath())
                                .equals(projectileDamage)) {
                            return getProjectile(gang, playerCtx, mob, Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO.getPath()));
                        }
                    }
                    displayName = getI18nName(i, playerCtx.getPlayer());
                } else {
                    displayName = ComponentUtil.getItemStackDisplayName(i);
                }

                weaponHover = ComponentUtil.buildItemHover(playerCtx.getPlayer(), i, displayName);
            } else {
                final Entity projectile = playerCtx.getLastProjectileEntity();
                final Component projectileCustomName = DeathMessages.getNMS().entityCustomName(projectile);
                final Component projectileName = projectileCustomName != null
                        ? projectileCustomName
                        : getI18nName(projectile, playerCtx.getPlayer());

                weaponHover = projectileName;
            }

            TextComponent deathMessage = Util.convertFromLegacy(msg);
            base.append(deathMessage.replaceText(TextReplacementConfig.builder().matchLiteral("%weapon%").replacement(weaponHover).build()));
        } else {
            TextComponent deathMessage = Util.convertFromLegacy(msg);
            base.append(deathMessage);
        }

        Component baseWithEvents = base.build();

        if (!rawEvents.isEmpty()) {
            int index = 0;
            for (String rawEvent : rawEvents) {
                Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, playerCtx, null, mob, false, true);
                baseWithEvents = baseWithEvents.replaceText(
                        TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
                );
            }
        }

        return (TextComponent) playerDeathPlaceholders(baseWithEvents, playerCtx, mob);
    }

    public static TextComponent getEntityDeathProjectile(Player p, EntityCtx entityCtx, String projectileDamage, MobType mobType) {
        String entityName = EntityUtil.getConfigNodeByEntity(entityCtx.getEntity());
        boolean hasOwner = EntityUtil.hasOwner(entityCtx.getEntity());
        List<String> msgs = sortList(getEntityDeathMessages().getStringList("Entities." + entityName + "." + projectileDamage), p, entityCtx.getEntity());

        if (mobType.equals(MobType.MYTHIC_MOB)) {
            String mmMobType = null;
            if (DeathMessages.getHooks().mythicmobsEnabled && DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(entityCtx.getUUID())) {
                mmMobType = DeathMessages.getHooks().mythicMobs.getAPIHelper().getMythicMobInstance(entityCtx.getEntity()).getMobType();
            }

            msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + mmMobType + "." + projectileDamage), p, entityCtx.getEntity());

            if (msgs.isEmpty()) return Component.empty(); // Don't send mm mob death msg if no configured death msg.
        }

        if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
            DeathMessages.LOGGER.warn("node: [Entities.{}.{}]", entityName, projectileDamage);
        if (msgs.isEmpty()) {
            if (Settings.getInstance().getConfig().getBoolean(Config.DEFAULT_MELEE_LAST_DAMAGE_NOT_DEFINED.getPath())) {
                if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
                    DeathMessages.LOGGER.warn("node2：: [getEntityDeath]");
                return getEntityDeath(p, entityCtx.getEntity(), getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK), mobType);
            }
            // This death message will not be broadcast, since user have not set death message for this entity
            return Component.empty();
        }

        String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.getFirst();
        TextComponent.Builder base = Component.text();
        List<String> rawEvents = new ArrayList<>();
        msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

        if (msg.contains("%weapon%") && entityCtx.getLastProjectileEntity() instanceof Arrow) {
            Component weaponHover;
            ItemStack i = p.getEquipment().getItemInMainHand();

            if (!MaterialUtil.isAir(i)) {
                Component displayName;
                if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
                    if (Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED.getPath())) {
                        if (!Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO.getPath())
                                .equals(projectileDamage)) {
                            return getEntityDeathProjectile(p, entityCtx,
                                    Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO.getPath()), mobType);
                        }
                    }
                    displayName = getI18nName(i, p);
                } else {
                    displayName = ComponentUtil.getItemStackDisplayName(i);
                }

                weaponHover = ComponentUtil.buildItemHover(p, i, displayName);
            } else {
                final Entity projectile = entityCtx.getLastProjectileEntity();
                final Component projectileCustomName = DeathMessages.getNMS().entityCustomName(projectile);
                final Component projectileName = projectileCustomName != null
                        ? projectileCustomName
                        : getI18nName(projectile, p);

                weaponHover = projectileName;
            }

            TextComponent deathMessage = Util.convertFromLegacy(msg);
            base.append(deathMessage.replaceText(TextReplacementConfig.builder().matchLiteral("%weapon%").replacement(weaponHover).build()));
        } else {
            TextComponent deathMessage = Util.convertFromLegacy(msg);
            base.append(deathMessage);
        }

        Component baseWithEvents = base.build();

        if (!rawEvents.isEmpty()) {
            int index = 0;
            for (String rawEvent : rawEvents) {
                Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, null, p, entityCtx.getEntity(), hasOwner, false);
                baseWithEvents = baseWithEvents.replaceText(
                        TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
                );
            }
        }

        return (TextComponent) entityDeathPlaceholders(baseWithEvents, p, entityCtx.getEntity(), hasOwner);
    }

    public static TextComponent getEntityDeath(Player player, Entity e, String damageCause, MobType mobType) {
        String entityName = EntityUtil.getConfigNodeByEntity(e);
        boolean hasOwner = EntityUtil.hasOwner(e);
        List<String> msgs = sortList(getEntityDeathMessages().getStringList("Entities." +
                entityName + "." + damageCause), player, e);

        if (hasOwner) {
            msgs = sortList(getEntityDeathMessages().getStringList("Entities." +
                    e.getName().toLowerCase() + ".Tamed"), player, e);
        } else if (mobType.equals(MobType.MYTHIC_MOB)) {
            String mmMobType = null;
            if (DeathMessages.getHooks().mythicmobsEnabled && DeathMessages.getHooks().mythicMobs.getAPIHelper().isMythicMob(e.getUniqueId())) {
                mmMobType = DeathMessages.getHooks().mythicMobs.getAPIHelper().getMythicMobInstance(e).getMobType();
            } else {
                // reserved
            }

            msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + mmMobType + "." + damageCause), player, e);

            if (msgs.isEmpty()) return Component.empty(); // Don't send mm mob death msg if no configured death msg.
        }

        if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
            DeathMessages.LOGGER.warn("node: [Entities.{}.{}]", entityName, damageCause);
        if (msgs.isEmpty()) {
            // This death message will not be broadcast, since user have not set death message for this entity
            return Component.empty();
        }

        String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.getFirst();

        TextComponent.Builder base = Component.text();

        if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
            TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
            base.append(prefix);
        }

        List<String> rawEvents = new ArrayList<>();
        msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

        base.append(Util.convertFromLegacy(msg));

        Component baseWithEvents = base.build();

        if (!rawEvents.isEmpty()) {
            int index = 0;
            for (String rawEvent : rawEvents) {
                Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, null, player, e, hasOwner, false);
                baseWithEvents = baseWithEvents.replaceText(
                        TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
                );
            }
        }

        return (TextComponent) entityDeathPlaceholders(base.build(), player, e, hasOwner);
    }

    /*
        To filter death messages based on permissions or world guard regions
        Support multi perm nodes or regions to become more configurable
        e.g. - "PERMISSION[node1]PERMISSION_KILLER[node2]REGION[r1]&2message"
     */
    public static List<String> sortList(List<String> list, Player victim, Entity killer) {
        List<String> result = new ArrayList<>(list.size());

        for (String s : list) {
            // Check for victim permission messages
            if (s.contains("PERMISSION[")) {
                Matcher m = Util.DM_PERM_PATTERN.matcher(s);
                while (m.find()) {
                    String perm = m.group(1);
                    s = victim.hasPermission(perm)
                            ? s.replace("PERMISSION[" + perm + "]", "") : "";
                }
            }
            // Check for killer permission messages
            if (s.contains("PERMISSION_KILLER[")) {
                Matcher m = Util.DM_KILLER_PERM_PATTERN.matcher(s);
                while (m.find()) {
                    String perm = m.group(1);
                    s = killer.hasPermission(perm)
                            ? s.replace("PERMISSION_KILLER[" + perm + "]", "") : "";
                }
            }
            // Check for region specific messages
            if (s.contains("REGION[")) {
                Matcher m = Util.DM_REGION_PATTERN.matcher(s);
                while (m.find()) {
                    String regionID = m.group(1);
                    s = DeathMessages.getHooks().worldGuardExtension.isInRegion(victim, regionID)
                            ? s.replace("REGION[" + regionID + "]", "") : "";
                }
            }

            // Append messages sorted
            if (!s.isEmpty()) result.add(s);
        }

        return result;
    }

    // TODO: Check all component related utils
    public static Component entityDeathPlaceholders(Component msg, Player player, Entity entity, boolean hasOwner) {
        msg = msg.replaceText(Util.replace("%entity%", EntityUtil.getEntityCustomNameComponent(entity)))
                // TODO - to component
                .replaceText(Util.replace("%entity_display%", entity.getCustomName() != null ? entity.getCustomName() : EntityUtil.getEntityCustomName(entity)))
                .replaceText(Util.replace("%killer%", Util.getPlayerName(player)))
                .replaceText(Util.replace("%killer_display%", Util.getPlayerDisplayName(player)))
                .replaceText(Util.replace("%world%", entity.getLocation().getWorld().getName()))
                .replaceText(Util.replace("%world_environment%", getEnvironment(entity.getLocation().getWorld().getEnvironment())))
                .replaceText(Util.replace("%x%", String.valueOf(entity.getLocation().getBlock().getX())))
                .replaceText(Util.replace("%y%", String.valueOf(entity.getLocation().getBlock().getY())))
                .replaceText(Util.replace("%z%", String.valueOf(entity.getLocation().getBlock().getZ())));

        if (hasOwner) {
            msg = msg.replaceText(Util.replace("%owner%", ((Tameable) entity).getOwner().getName()));
        }

        String biomeName;
        try {
            final Biome biome = entity.getLocation().getBlock().getBiome();
            biomeName = Util.getBiomeName(biome);
        } catch (NullPointerException e) {
            DeathMessages.LOGGER.error("Custom Biome detected. Using 'Unknown' for a biome name.");
            DeathMessages.LOGGER.error("Custom Biomes are not supported yet.'");
            biomeName = "Unknown";
        }

        msg = msg.replaceText(Util.replace("%biome%", biomeName));

        if (entity != null && entity.getLocation() != null) {
            try {
                msg = msg.replaceText(Util.replace("%distance%", String.valueOf((int) Math.round(player.getLocation().distance(entity.getLocation())))));
            } catch (Exception ex) {
                DeathMessages.LOGGER.error("Unknown distance calculated. Using 'Zero' for the distance.");
                msg = msg.replaceText(Util.replace("%distance%", "0"));
            }
        }

        if (DeathMessages.getHooks().placeholderAPIEnabled) {
            Matcher identifiers = Util.PAPI_PLACEHOLDER_PATTERN.matcher(Util.convertToLegacy(msg));

            while (identifiers.find()) {
                String identifier = identifiers.group(0);
                msg = msg.replaceText(Util.replace(identifier, PlaceholderAPI.setPlaceholders(player, identifier)));
            }
        }

        return msg;
    }

    // TODO: Check all component related utils
    @Deprecated
    public static String entityDeathPlaceholders(String msg, Player player, Entity entity, boolean hasOwner) {
        final boolean hasBiome = msg.contains("%biome%");
        final boolean hasDistance = msg.contains("%distance%");

        msg = msg.replaceAll("%entity%", EntityUtil.getEntityCustomName(entity))
                .replaceAll("%entity_display%", entity.getCustomName() != null ? entity.getCustomName() : EntityUtil.getEntityCustomName(entity))
                .replaceAll("%killer%", Util.getPlayerName(player))
                .replaceAll("%killer_display%", Util.getPlayerDisplayName(player))
                .replaceAll("%world%", entity.getLocation().getWorld().getName())
                .replaceAll("%world_environment%", getEnvironment(entity.getLocation().getWorld().getEnvironment()))
                .replaceAll("%x%", String.valueOf(entity.getLocation().getBlock().getX()))
                .replaceAll("%y%", String.valueOf(entity.getLocation().getBlock().getY()))
                .replaceAll("%z%", String.valueOf(entity.getLocation().getBlock().getZ()));

        if (hasOwner) {
            msg = msg.replaceAll("%owner%", ((Tameable) entity).getOwner().getName());
        }

        if (hasBiome) {
            String biomeName;
            try {
                final Biome biome = entity.getLocation().getBlock().getBiome();
                biomeName = Util.getBiomeName(biome);
            } catch (NullPointerException e) {
                DeathMessages.LOGGER.error("Custom Biome detected. Using 'Unknown' for a biome name.");
                DeathMessages.LOGGER.error("Custom Biomes are not supported yet.'");
                biomeName = "Unknown";
            }

            msg = msg.replace("%biome%", biomeName);
        }

        if (hasDistance && entity != null && entity.getLocation() != null) {
            try {
                msg = msg.replaceAll("%distance%", String.valueOf((int) Math.round(player.getLocation().distance(entity.getLocation()))));
            } catch (Exception ex) {
                DeathMessages.LOGGER.error("Unknown distance calculated. Using 'Zero' for the distance.");
                msg = msg.replace("%distance%", "0");
            }
        }

        if (DeathMessages.getHooks().placeholderAPIEnabled) {
            msg = PlaceholderAPI.setPlaceholders(player, msg);
        }

        return msg;
    }

    // TODO: Check all component related utils
    public static Component playerDeathPlaceholders(Component msg, PlayerCtx playerCtx, Entity mob) {
        msg = msg.replaceText(Util.replace("%player%", Util.getPlayerName(playerCtx)))
                .replaceText(Util.replace("%player_display%", Util.getPlayerDisplayName(playerCtx)))
                .replaceText(Util.replace("%world%", playerCtx.getLastLocation().getWorld().getName()))
                .replaceText(Util.replace("%world_environment%", getEnvironment(playerCtx.getLastLocation().getWorld().getEnvironment())))
                .replaceText(Util.replace("%x%", String.valueOf(playerCtx.getLastLocation().getBlock().getX())))
                .replaceText(Util.replace("%y%", String.valueOf(playerCtx.getLastLocation().getBlock().getY())))
                .replaceText(Util.replace("%z%", String.valueOf(playerCtx.getLastLocation().getBlock().getZ())));

        String biomeName;
        try {
            final Biome biome = playerCtx.getLastLocation().getBlock().getBiome();
            biomeName = Util.getBiomeName(biome);
        } catch (NullPointerException e) {
            DeathMessages.LOGGER.error("Custom Biome detected. Using 'Unknown' for a biome name.");
            DeathMessages.LOGGER.error("Custom Biomes are not supported yet.'");
            biomeName = "Unknown";
        }

        msg = msg.replaceText(Util.replace("%biome%", biomeName));

        if (mob != null && mob.getLocation() != null) {
            try {
                msg = msg.replaceText(Util.replace("%distance%", String.valueOf((int) Math.round(playerCtx.getLastLocation().distance(mob.getLocation())))));
            } catch (Exception ex) {
                DeathMessages.LOGGER.error("Unknown distance calculated. Using 'Zero' for the distance.");
                msg = msg.replaceText(Util.replace("%distance%", "0"));
            }
        }

        if (mob != null) {
            String mobNameStr = mob.getName();
            Component mobName = Component.text(mobNameStr);
            if (Settings.getInstance().getConfig().getBoolean(Config.RENAME_MOBS_ENABLED.getPath())) {
                String[] chars = Settings.getInstance().getConfig().getString(Config.RENAME_MOBS_IF_CONTAINS.getPath()).split("(?!^)");
                for (String ch : chars) {
                    if (mobNameStr.contains(ch)) {
                        mobName = EntityUtil.getEntityCustomNameComponent(mob);
                        break;
                    }
                }
            }

            if (!(mob instanceof Player) && Settings.getInstance().getConfig().getBoolean(Config.DISABLE_NAMED_MOBS.getPath())) {
                mobName = EntityUtil.getEntityCustomNameComponent(mob);
            } else if (mob instanceof Player p) {
                mobName = Util.getPlayerNameComponent(p);
            }

            msg = msg.replaceText(Util.replace("%killer%", mobName))
                    .replaceText(Util.replace("%killer_type%", EntityUtil.getEntityCustomNameComponent(mob)));

            if (mob instanceof Player p) {
                msg = msg.replaceText(Util.replace("%killer_display%", Util.getPlayerDisplayName(p)));
            } else {
                msg = msg.replaceText(Util.replace("%killer_display%", mobName)); // Fallback to mob name if not player
            }
        }

        if (DeathMessages.getHooks().placeholderAPIEnabled) {
            Matcher params = Util.PAPI_PLACEHOLDER_PATTERN.matcher(Util.convertToLegacy(msg));

            while (params.find()) {
                String param = params.group(0);
                msg = msg.replaceText(Util.replace(param, PlaceholderAPI.setPlaceholders(playerCtx.getPlayer(), param)));
            }
        }

        return msg;
    }

    // TODO: Check all component related utils
    @Deprecated
    public static String playerDeathPlaceholders(String msg, PlayerCtx playerCtx, Entity mob) {
        final boolean hasBiome = msg.contains("%biome%");
        final boolean hasDistance = msg.contains("%distance%");

        msg = msg.replaceAll("%player%", Util.getPlayerName(playerCtx))
                .replaceAll("%player_display%", Util.getPlayerDisplayName(playerCtx))
                .replaceAll("%world%", playerCtx.getLastLocation().getWorld().getName())
                .replaceAll("%world_environment%", getEnvironment(playerCtx.getLastLocation().getWorld().getEnvironment()))
                .replaceAll("%x%", String.valueOf(playerCtx.getLastLocation().getBlock().getX()))
                .replaceAll("%y%", String.valueOf(playerCtx.getLastLocation().getBlock().getY()))
                .replaceAll("%z%", String.valueOf(playerCtx.getLastLocation().getBlock().getZ()));

        if (hasBiome) {
            String biomeName;
            try {
                final Biome biome = playerCtx.getLastLocation().getBlock().getBiome();
                biomeName = Util.getBiomeName(biome);
            } catch (NullPointerException e) {
                DeathMessages.LOGGER.error("Custom Biome detected. Using 'Unknown' for a biome name.");
                DeathMessages.LOGGER.error("Custom Biomes are not supported yet.'");
                biomeName = "Unknown";
            }

            msg = msg.replace("%biome%", biomeName);
        }

        if (hasDistance && mob != null && mob.getLocation() != null) {
            try {
                msg = msg.replaceAll("%distance%", String.valueOf((int) Math.round(playerCtx.getLastLocation().distance(mob.getLocation()))));
            } catch (Exception ex) {
                DeathMessages.LOGGER.error("Unknown distance calculated. Using 'Zero' for the distance.");
                msg = msg.replace("%distance%", "0");
            }
        }

        if (mob != null) {
            String mobName = mob.getName();
            if (Settings.getInstance().getConfig().getBoolean(Config.RENAME_MOBS_ENABLED.getPath())) {
                String[] chars = Settings.getInstance().getConfig().getString(Config.RENAME_MOBS_IF_CONTAINS.getPath()).split("(?!^)");
                for (String ch : chars) {
                    if (mobName.contains(ch)) {
                        mobName = EntityUtil.getEntityCustomName(mob);
                        break;
                    }
                }
            }

            if (!(mob instanceof Player) && Settings.getInstance().getConfig().getBoolean(Config.DISABLE_NAMED_MOBS.getPath())) {
                mobName = EntityUtil.getEntityCustomName(mob);
            } else if (mob instanceof Player p) {
                mobName = Util.getPlayerName(p);
            }

            msg = msg.replaceAll("%killer%", mobName)
                    .replaceAll("%killer_type%", EntityUtil.getEntityCustomName(mob));

            if (mob instanceof Player p) {
                msg = msg.replaceAll("%killer_display%", Util.getPlayerDisplayName(p));
            } else {
                msg = msg.replaceAll("%killer_display%", mobName); // Fallback to mob name if not player
            }
        }

        if (DeathMessages.getHooks().placeholderAPIEnabled) {
            msg = PlaceholderAPI.setPlaceholders(playerCtx.getPlayer(), msg);
        }

        return msg;
    }

    /*
        Use MiniMessage feature to send translatable component to player.
         Thus, able to display item name for players based on player's client locale.
         But I think maybe there is a better way to display localized item name to player
     */
    private static Component getI18nName(ItemStack i, Player p) {
        Component i18nName;

        if (!DeathMessages.getHooks().disableI18nDisplay) {
            if (Util.isNewerAndEqual(12, 0)) {
                // Block: block.minecraft.example
                // Item: item.minecraft.example
                String materialType = i.getType().isBlock() ? "block" : "item";
                String rawTranslatable = "<lang:" + materialType + ".minecraft." + i.getType().name().toLowerCase() + ">";
                i18nName = MiniMessage.miniMessage().deserialize(rawTranslatable);
            } else if (DeathMessages.getHooks().langUtilsEnabled) {
                i18nName = Component.text(LanguageHelper.getItemName(i, p.getLocale()));
            } else {
                String name = Util.capitalize(i.getType().name());
                i18nName = Component.text(name);
            }
        } else {
            String name = Util.capitalize(i.getType().name());
            i18nName = Component.text(name);
        }

        return i18nName;
    }

    /*
         Use MiniMessage feature to send translatable component to player.
         Thus, able to display entity name for players based on player's client locale.
     */
    private static Component getI18nName(Entity mob, Player p) {
        Component i18nName;

        if (Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_MOB_NAME.getPath()) && !DeathMessages.getHooks().discordSRVEnabled) {
            if (Util.isNewerAndEqual(12, 0)) {
                // Entity: entity.minecraft.example
                String rawTranslatable = "<lang:entity.minecraft." + mob.getType().name().toLowerCase() + ">";
                i18nName = MiniMessage.miniMessage().deserialize(rawTranslatable);
            } else if (DeathMessages.getHooks().langUtilsEnabled && !(mob instanceof ShulkerBullet)) { // <= 1.12.2 no ShulkerBullet lang key
                i18nName = Component.text(LanguageHelper.getEntityName(mob, p.getLocale()));
            } else {
                String name = Util.capitalize(mob.getType().name());
                i18nName = Component.text(name);
            }
        } else {
            String name = Util.capitalize(mob.getType().name());
            i18nName = Component.text(name);
        }

        return i18nName;
    }

    public static String getEnvironment(World.Environment environment) {
        return switch (environment) {
            case NORMAL -> Messages.getInstance().getConfig().getString("Environment.normal");
            case NETHER -> Messages.getInstance().getConfig().getString("Environment.nether");
            case THE_END -> Messages.getInstance().getConfig().getString("Environment.the_end");
            default ->
                // Dreeam TODO: support all environment
                    Messages.getInstance().getConfig().getString("Environment.unknown");
        };
    }

    public static String getSimpleProjectile(Projectile projectile) {
        return switch (projectile) {
            case Arrow arrow -> "Projectile-Arrow";
            case DragonFireball dragonFireball -> "Projectile-Dragon-Fireball";
            case Egg egg -> "Projectile-Egg";
            case EnderPearl enderPearl -> "Projectile-EnderPearl";
            case WitherSkull witherSkull -> "Projectile-Fireball";
            case Fireball fireball -> "Projectile-Fireball";
            case FishHook fishHook -> "Projectile-FishHook";
            case LlamaSpit llamaSpit -> "Projectile-LlamaSpit";
            case Snowball snowball -> "Projectile-Snowball";
            case ShulkerBullet shulkerBullet -> "Projectile-ShulkerBullet";
            case Trident trident -> "Projectile-Trident";
            case null, default -> "Projectile-Arrow";
        };
    }

    public static String getSimpleCause(EntityDamageEvent.DamageCause damageCause) {
        return switch (damageCause) {
            case KILL -> "Kill";
            case WORLD_BORDER -> "World-Border";
            case CONTACT -> "Contact";
            case ENTITY_ATTACK, ENTITY_SWEEP_ATTACK -> "Melee";
            case PROJECTILE -> "Projectile";
            case SUFFOCATION -> "Suffocation";
            case FALL -> "Fall";
            case CAMPFIRE, FIRE -> "Fire";
            case FIRE_TICK -> "Fire-Tick";
            case MELTING -> "Melting";
            case LAVA -> "Lava";
            case DROWNING -> "Drowning";
            case BLOCK_EXPLOSION, ENTITY_EXPLOSION -> "Explosion";
            case VOID -> "Void";
            case LIGHTNING -> "Lightning";
            case SUICIDE -> "Suicide";
            case STARVATION -> "Starvation";
            case POISON -> "Poison";
            case MAGIC -> "Magic";
            case WITHER -> "Wither";
            case FALLING_BLOCK -> "Falling-Block";
            case THORNS -> "Thorns";
            case DRAGON_BREATH -> "Dragon-Breath";
            case CUSTOM -> "Custom";
            case FLY_INTO_WALL -> "Fly-Into-Wall";
            case HOT_FLOOR -> "Hot-Floor";
            case CRAMMING -> "Cramming";
            case DRYOUT -> "Dryout";
            case FREEZE -> "Freeze";
            case SONIC_BOOM -> "Sonic-Boom";
            default -> "Unknown";
        };
    }

    public static FileConfiguration getPlayerDeathMessages() {
        return PlayerDeathMessages.getInstance().getConfig();
    }

    public static FileConfiguration getEntityDeathMessages() {
        return EntityDeathMessages.getInstance().getConfig();
    }
}
