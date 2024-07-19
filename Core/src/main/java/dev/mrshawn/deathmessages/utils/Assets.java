package dev.mrshawn.deathmessages.utils;

import com.cryptomorin.xseries.XMaterial;
import com.meowj.langutils.lang.LanguageHelper;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.config.PlayerDeathMessages;
import dev.mrshawn.deathmessages.config.Settings;
import dev.mrshawn.deathmessages.enums.DeathAffiliation;
import dev.mrshawn.deathmessages.enums.DeathModes;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.DragonFireball;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EnderCrystal;
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

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assets {

	// Dreeam TODO - to figure out why the value defined in private static field will not change with the change of the config value
	//private static final CommentedConfiguration config = Settings.getInstance().getConfig();
	//private static final boolean addPrefix = config.getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath());

	public static boolean isClimbable(Material material) {
		final String name = material.name();
		return name.contains("LADDER")
				|| name.contains("VINE")
				|| name.contains("SCAFFOLDING")
				|| name.contains("TRAPDOOR");
	}

	public static boolean isAir(ItemStack i) {
		if (Util.isOlderAndEqual(13, 0)) {
			// From 1.14 org.bukkit.Material.isAir()
			switch (i.getType()) {
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

		return i.getType().isAir();
	}

	public static boolean itemNameIsWeapon(ItemStack itemStack) {
		if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return false;

		String displayName = itemStack.getItemMeta().getDisplayName();

		for (String s : Settings.getInstance().getConfig().getStringList(Config.CUSTOM_ITEM_DISPLAY_NAMES_IS_WEAPON.getPath())) {
			Pattern pattern = Pattern.compile(s);
			Matcher matcher = pattern.matcher(displayName);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}

	public static boolean itemMaterialIsWeapon(ItemStack itemStack) {
		for (String s : Settings.getInstance().getConfig().getStringList(Config.CUSTOM_ITEM_MATERIAL_IS_WEAPON.getPath())) {
			Material material = Material.getMaterial(s);
			if (itemStack.getType().equals(material)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isWeapon(ItemStack itemStack) {
		return isWeapon(itemStack.getType())
				|| itemNameIsWeapon(itemStack)
				|| itemMaterialIsWeapon(itemStack);
	}

	public static boolean isWeapon(Material material) {
		String materialName = material.toString();
		return materialName.contains("SHOVEL")
				|| materialName.contains("PICKAXE")
				|| materialName.contains("AXE")
				|| materialName.contains("HOE")
				|| materialName.contains("SWORD")
				|| materialName.contains("BOW")
				|| materialName.contains("CROSSBOW")
				|| materialName.contains("ARROW")
				|| materialName.contains("TRIDENT");
	}

	public static boolean hasWeapon(LivingEntity mob, EntityDamageEvent.DamageCause damageCause) {
		if (mob.getEquipment() == null || damageCause.equals(EntityDamageEvent.DamageCause.THORNS)) return false;

		return isWeapon(mob.getEquipment().getItemInMainHand());
	}

	public static TextComponent playerDeathMessage(PlayerManager pm, boolean gang) {
		LivingEntity mob = (LivingEntity) pm.getLastEntityDamager();

		if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
			if (pm.getLastExplosiveEntity() instanceof EnderCrystal) {
				return get(gang, pm, mob, "End-Crystal");
			} else if (pm.getLastExplosiveEntity() instanceof TNTPrimed) {
				return get(gang, pm, mob, "TNT");
			} else if (pm.getLastExplosiveEntity() instanceof Firework) {
				return get(gang, pm, mob, "Firework");
			} else {
				return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION));
			}
		}
		if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
			Optional<ExplosionManager> explosionManager = ExplosionManager.getManagerIfEffected(pm.getUUID());
			if (explosionManager.isPresent()) {
				Optional<PlayerManager> pyro = PlayerManager.getPlayer(explosionManager.get().getPyro());
				if (pyro.isPresent()) {
					// Bed kill
					if (explosionManager.get().getMaterial().name().contains("BED")) {
						return get(gang, pm, pyro.get().getPlayer(), "Bed");
					}
					// Respawn Anchor kill
					if (Util.isNewerAndEqual(16, 0)) {
						if (explosionManager.get().getMaterial().equals(Material.RESPAWN_ANCHOR)) {
							return get(gang, pm, pyro.get().getPlayer(), "Respawn-Anchor");
						}
					}
				}
			}
		}

		boolean hasWeapon = hasWeapon(mob, pm.getLastDamage());
		if (hasWeapon) {
			if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
				return getWeapon(gang, pm, mob);
			} else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE) && pm.getLastProjectileEntity() instanceof Arrow) {
				return getProjectile(gang, pm, mob, getSimpleProjectile(pm.getLastProjectileEntity()));
			} else {
				return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
			}
		} else {
			// Dreeam TODO: idk why there is for loop used to if (pm.getLastDamage().equals(dc)), no need, waste performance..
			for (EntityDamageEvent.DamageCause dc : EntityDamageEvent.DamageCause.values()) {
				if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
					return getProjectile(gang, pm, mob, getSimpleProjectile(pm.getLastProjectileEntity()));
				}
				if (pm.getLastDamage().equals(dc)) {
					return get(gang, pm, mob, getSimpleCause(dc));
				}
			}
			return Component.empty();
		}
	}

	public static TextComponent entityDeathMessage(EntityManager em, MobType mobType) {
		Optional<PlayerManager> pm = Optional.ofNullable(em.getLastPlayerDamager());

		if (!pm.isPresent()) return Component.empty();

		Player p = pm.get().getPlayer();
		boolean hasWeapon = hasWeapon(p, pm.get().getLastDamage());

		if (em.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)) {
			if (em.getLastExplosiveEntity() instanceof EnderCrystal) {
				return getEntityDeath(p, em.getEntity(), "End-Crystal", mobType);
			} else if (em.getLastExplosiveEntity() instanceof TNTPrimed) {
				return getEntityDeath(p, em.getEntity(), "TNT", mobType);
			} else if (em.getLastExplosiveEntity() instanceof Firework) {
				return getEntityDeath(p, em.getEntity(), "Firework", mobType);
			} else {
				return getEntityDeath(p, em.getEntity(), getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION), mobType);
			}
		}
		if (em.getLastDamage().equals(EntityDamageEvent.DamageCause.BLOCK_EXPLOSION)) {
			Optional<ExplosionManager> explosionManager = ExplosionManager.getManagerIfEffected(em.getEntityUUID());
			if (explosionManager.isPresent()) {
				Optional<PlayerManager> pyro = PlayerManager.getPlayer(explosionManager.get().getPyro());
				if (pyro.isPresent()) {
					// Bed kill
					if (explosionManager.get().getMaterial().name().contains("BED")) {
						return getEntityDeath(pyro.get().getPlayer(), em.getEntity(), "Bed", mobType);
					}
					// Respawn Anchor kill
					if (Util.isNewerAndEqual(16, 0)) {
						if (explosionManager.get().getMaterial().equals(Material.RESPAWN_ANCHOR)) {
							return getEntityDeath(pyro.get().getPlayer(), em.getEntity(), "Respawn-Anchor", mobType);
						}
					}
				}
			}
		}
		if (hasWeapon) {
			if (em.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
				return getEntityDeathWeapon(p, em.getEntity(), mobType);
			} else if (em.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE) && em.getLastProjectileEntity() instanceof Arrow) {
				return getEntityDeathProjectile(p, em, getSimpleProjectile(em.getLastProjectileEntity()), mobType);
			} else {
				return getEntityDeathWeapon(p, em.getEntity(), mobType);
			}
		} else {
			for (EntityDamageEvent.DamageCause dc : EntityDamageEvent.DamageCause.values()) {
				if (em.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
					return getEntityDeathProjectile(p, em, getSimpleProjectile(em.getLastProjectileEntity()), mobType);
				}
				if (em.getLastDamage().equals(dc)) {
					return getEntityDeath(p, em.getEntity(), getSimpleCause(dc), mobType);
				}
			}
			return Component.empty();
		}
	}

	public static TextComponent getNaturalDeath(PlayerManager pm, String damageCause) {
		List<String> msgs = sortList(getPlayerDeathMessages().getStringList("Natural-Cause." + damageCause), pm.getPlayer(), pm.getPlayer());

		if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
			DeathMessages.LOGGER.warn("node: [Natural-Cause.{}]", damageCause);
		if (msgs.isEmpty()) {
			DeathMessages.LOGGER.warn("Can't find message node: [Natural-Cause.{}] in PlayerDeathMessages.yml", damageCause);
			DeathMessages.LOGGER.warn("This should not happen, please check your config or report issue on Github");
			msgs = sortList(getPlayerDeathMessages().getStringList("Natural-Cause.Unknown"), pm.getPlayer(), pm.getPlayer());
			DeathMessages.LOGGER.warn("Fallback this death to [Natural-Cause.Unknown] message node");
		}

		String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.get(0);

		TextComponent.Builder base = Component.text();
		if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
			TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		List<String> rawEvents = new ArrayList<>();
		msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

		if (msg.contains("%block%") && pm.getLastEntityDamager() instanceof FallingBlock) {
			try {
				FallingBlock fb = (FallingBlock) pm.getLastEntityDamager();
				String material = Util.isNewerAndEqual(12, 0)
						? fb.getBlockData().getMaterial().toString().toLowerCase()
						: fb.getMaterial().toString().toLowerCase();
				String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);

				base.append(Util.convertFromLegacy(msg.replaceAll("%block%", configValue)));
			} catch (NullPointerException e) {
				DeathMessages.LOGGER.error("Could not parse %block%. Please check your config for a wrong value." +
						" Your materials could be spelt wrong or it does not exists in the config. Open a issue if you need help, " + "https://github.com/Winds-Studio/DeathMessages/issues");
				pm.setLastEntityDamager(null);
				return getNaturalDeath(pm, getSimpleCause(EntityDamageEvent.DamageCause.SUFFOCATION));
			}
		} else if (msg.contains("%climbable%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.FALL)) {
			try {
				String material = pm.getLastClimbing().toString().toLowerCase();
				String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);

				base.append(Util.convertFromLegacy(msg.replaceAll("%climbable%", configValue)));
			} catch (NullPointerException e) {
				pm.setLastClimbing(null);
				return getNaturalDeath(pm, getSimpleCause(EntityDamageEvent.DamageCause.FALL));
			}
		} else if (msg.contains("%weapon%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			ItemStack i = pm.getPlayer().getEquipment().getItemInMainHand();

			if (!i.getType().equals(XMaterial.BOW.parseMaterial())) {
				return getNaturalDeath(pm, "Projectile-Unknown");
			}
			if (Util.isNewerAndEqual(14, 0)) {
				if (!i.getType().equals(XMaterial.CROSSBOW.parseMaterial())) {
					return getNaturalDeath(pm, "Projectile-Unknown");
				}
			}

			Component displayName;
			if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
				if (Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED.getPath())) {
					if (!Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_IGNORE_ENCHANTMENTS.getPath())) {
						if (i.getEnchantments().isEmpty()) {
							return getNaturalDeath(pm, "Projectile-Unknown");
						}
					} else {
						return getNaturalDeath(pm, "Projectile-Unknown");
					}
				}
				displayName = getI18nName(i, pm.getPlayer());
			} else {
				displayName = ComponentUtil.getItemStackDisplayName(i);
			}

			TextComponent message = Util.convertFromLegacy(msg);
			Component weapon = ComponentUtil.buildItemHover(pm.getPlayer(), i, displayName);

			base.append(message.replaceText(TextReplacementConfig.builder().matchLiteral("%weapon%").replacement(weapon).build()));
		} else {
			TextComponent message = Util.convertFromLegacy(msg);
			base.append(message);
		}

		Component baseWithEvents = base.build();

		if (!rawEvents.isEmpty()) {
			int index = 0;
			for (String rawEvent : rawEvents) {
				Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, pm, null, null, false, true);
				baseWithEvents = baseWithEvents.replaceText(
						TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
				);
			}
		}

		return (TextComponent) playerDeathPlaceholders(baseWithEvents, pm, null);
	}

	public static TextComponent getWeapon(boolean gang, PlayerManager pm, LivingEntity mob) {
		final boolean basicMode = getPlayerDeathMessages().getBoolean("Basic-Mode.Enabled");
		String entityName = EntityUtil.getConfigNodeByEntity(mob);
		final String mode = basicMode ? DeathModes.BASIC_MODE.getValue() : DeathModes.MOBS.getValue()
				+ "." + entityName;
		final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();
		//Bukkit.broadcastMessage(DeathMessages.getInstance().mythicmobsEnabled + " - " + DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId()));
		List<String> msgs = sortList(getPlayerDeathMessages().getStringList(mode + "." + affiliation + ".Weapon"), pm.getPlayer(), mob);
		if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
			String internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
			//Bukkit.broadcastMessage("is myth - " + internalMobType);
			msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + internalMobType + "." + affiliation + ".Weapon"), pm.getPlayer(), mob);
		}

		if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
			DeathMessages.LOGGER.warn("node: [{}.{}.Weapon]", mode, affiliation);
		if (msgs.isEmpty()) {
			DeathMessages.LOGGER.warn("Can't find message node: [{}.{}.Weapon] in PlayerDeathMessages.yml", mode, affiliation);
			DeathMessages.LOGGER.warn("This should not happen, please check your config or report this issue on Github");
			msgs = sortList(getPlayerDeathMessages().getStringList(DeathModes.BASIC_MODE.getValue() + "." + affiliation + ".Weapon"), pm.getPlayer(), mob);
			DeathMessages.LOGGER.warn("Fallback this death to Basic-Mode of PlayerDeathMessages");
		}

		String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.get(0);

		TextComponent.Builder base = Component.text();

		if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
			TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		List<String> rawEvents = new ArrayList<>();
		msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

		if (msg.contains("%weapon%")) {
			ItemStack i = mob.getEquipment().getItemInMainHand();
			Component displayName;
			if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
				if (FileStore.INSTANCE.getCONFIG().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED)) {
					if (!FileStore.INSTANCE.getCONFIG().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_IGNORE_ENCHANTMENTS)) {
						if (i.getEnchantments().isEmpty()) {
							return get(gang, pm, mob, FileStore.INSTANCE.getCONFIG()
									.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO));
						}
					} else {
						return get(gang, pm, mob, FileStore.INSTANCE.getCONFIG()
								.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO));
					}
				}
				displayName = getI18nName(i, pm.getPlayer());
			} else {
				displayName = ComponentUtil.getItemStackDisplayName(i);
			}

			TextComponent deathMessage = Util.convertFromLegacy(msg);
			Component weaponHover = ComponentUtil.buildItemHover(pm.getPlayer(), i, displayName);

			base.append(deathMessage.replaceText(TextReplacementConfig.builder().matchLiteral("%weapon%").replacement(weaponHover).build()));
		} else {
			TextComponent deathMessage = Util.convertFromLegacy(msg);
			base.append(deathMessage);
		}

		Component baseWithEvents = base.build();

		if (!rawEvents.isEmpty()) {
			int index = 0;
			for (String rawEvent : rawEvents) {
				Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, pm, null, mob, false, true);
				baseWithEvents = baseWithEvents.replaceText(
						TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
				);
			}
		}

		return (TextComponent) playerDeathPlaceholders(baseWithEvents, pm, mob);
	}

	public static TextComponent getEntityDeathWeapon(Player p, Entity e, MobType mobType) {
		String entityName = EntityUtil.getConfigNodeByEntity(e);
		boolean hasOwner = EntityUtil.hasOwner(e);
		List<String> msgs = sortList(getEntityDeathMessages().getStringList("Entities." + entityName + ".Weapon"), p, e);

		if (mobType.equals(MobType.MYTHIC_MOB)) {
			String internalMobType = null;
			if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(e.getUniqueId())) {
				internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(e).getMobType();
			} else {
				// reserved
			}
			msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + internalMobType + ".Weapon"), p, e);
		}

		if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
			DeathMessages.LOGGER.warn("node: [Entities.{}.Weapon]", entityName);
		if (msgs.isEmpty()) {
			// This death message will not be broadcast, since user have not set death message for this entity
			return Component.empty();
		}

		String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.get(0);

		TextComponent.Builder base = Component.text();

		if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
			TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

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

	public static TextComponent get(boolean gang, PlayerManager pm, LivingEntity mob, String damageCause) {
		final boolean basicMode = getPlayerDeathMessages().getBoolean("Basic-Mode.Enabled");
		String entityName = EntityUtil.getConfigNodeByEntity(mob);
		final String mode = basicMode ? DeathModes.BASIC_MODE.getValue() : DeathModes.MOBS.getValue()
				+ "." + entityName;
		final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();
		List<String> msgs = sortList(getPlayerDeathMessages().getStringList(mode + "." + affiliation + "." + damageCause), pm.getPlayer(), mob);

		if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
			String internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
			//System.out.println("is myth - " + internalMobType);
			msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + internalMobType + "." + affiliation + "." + damageCause), pm.getPlayer(), mob);
		}

		if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
			DeathMessages.LOGGER.warn("node: [{}.{}.{}]", mode, affiliation, damageCause);
		if (msgs.isEmpty()) {
			msgs = sortList(getPlayerDeathMessages().getStringList(DeathModes.MOBS.getValue() + ".player." + affiliation + "." + damageCause), pm.getPlayer(), mob);
			if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
				DeathMessages.LOGGER.warn("node2: [{}.player.{}.{}]", DeathModes.MOBS.getValue(), affiliation, damageCause);
			if (msgs.isEmpty()) {
				if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
					DeathMessages.LOGGER.info("Redirected from [{}.player.{}.{}]", DeathModes.MOBS.getValue(), affiliation, damageCause);
				if (Settings.getInstance().getConfig().getBoolean(Config.DEFAULT_NATURAL_DEATH_NOT_DEFINED.getPath()))
					return getNaturalDeath(pm, damageCause);
				if (Settings.getInstance().getConfig().getBoolean(Config.DEFAULT_MELEE_LAST_DAMAGE_NOT_DEFINED.getPath()))
					return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
				DeathMessages.LOGGER.warn("This death message will not be broadcast, unless you enable [Default-Natural-Death-Not-Defined] in Settings.yml");
				return Component.empty();
			}
		}

		String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.get(0);

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
				Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, pm, null, mob, false, true);
				baseWithEvents = baseWithEvents.replaceText(
						TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
				);
			}
		}

		return (TextComponent) playerDeathPlaceholders(baseWithEvents, pm, mob);
	}

	public static TextComponent getProjectile(boolean gang, PlayerManager pm, LivingEntity mob, String projectileDamage) {
		final boolean basicMode = getPlayerDeathMessages().getBoolean("Basic-Mode.Enabled");
		String entityName = EntityUtil.getConfigNodeByEntity(mob);
		final String mode = basicMode ? DeathModes.BASIC_MODE.getValue() : DeathModes.MOBS.getValue()
				+ "." + entityName;
		final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();
		List<String> msgs = sortList(getPlayerDeathMessages().getStringList(mode + "." + affiliation + "." + projectileDamage), pm.getPlayer(), mob);

		if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
			String internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
			msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + internalMobType + "." + affiliation + "." + projectileDamage), pm.getPlayer(), mob);
		}

		if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
			DeathMessages.LOGGER.warn("node: [{}.{}.{}]", mode, affiliation, projectileDamage);
		if (msgs.isEmpty()) {
			msgs = sortList(getPlayerDeathMessages().getStringList(DeathModes.MOBS.getValue() + ".player." + affiliation + "." + projectileDamage), pm.getPlayer(), mob);
			if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
				DeathMessages.LOGGER.warn("node2: [{}.player.{}.{}]", DeathModes.MOBS.getValue(), affiliation, projectileDamage);
			if (msgs.isEmpty()) {
				if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
					DeathMessages.LOGGER.info("Redirected from [{}.player.{}.{}]", DeathModes.MOBS.getValue(), affiliation, projectileDamage);
				if (Settings.getInstance().getConfig().getBoolean(Config.DEFAULT_NATURAL_DEATH_NOT_DEFINED.getPath()))
					return getNaturalDeath(pm, projectileDamage);
				DeathMessages.LOGGER.warn("This death message will not be broadcast, unless you enable [Default-Natural-Death-Not-Defined] in Settings.yml");
				return Component.empty();
			}
		}

		String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.get(0);

		TextComponent.Builder base = Component.text();

		if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
			TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		List<String> rawEvents = new ArrayList<>();
		msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

		if (msg.contains("%weapon%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			Component weaponHover;

			if (!projectileDamage.equals("Projectile-ShulkerBullet")) {
				ItemStack i = mob.getEquipment().getItemInMainHand();
				Component displayName;
				if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
					if (Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED.getPath())) {
						if (!Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO.getPath())
								.equals(projectileDamage)) {
							return getProjectile(gang, pm, mob, Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO.getPath()));
						}
					}
					displayName = getI18nName(i, pm.getPlayer());
				} else {
					displayName = ComponentUtil.getItemStackDisplayName(i);
				}

				weaponHover = ComponentUtil.buildItemHover(pm.getPlayer(), i, displayName);
			} else {
				Entity projectile = pm.getLastProjectileEntity();
				Component projectileName = projectile.getCustomName() != null
						? Component.text(projectile.getCustomName()) // TODO: Get display name using Paper api
						: getI18nName(projectile, pm.getPlayer());

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
				Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, pm, null, mob, false, true);
				baseWithEvents = baseWithEvents.replaceText(
						TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
				);
			}
		}

		return (TextComponent) playerDeathPlaceholders(baseWithEvents, pm, mob);
	}

	public static TextComponent getEntityDeathProjectile(Player p, EntityManager em, String projectileDamage, MobType mobType) {
		String entityName = EntityUtil.getConfigNodeByEntity(em.getEntity());
		boolean hasOwner = EntityUtil.hasOwner(em.getEntity());
		List<String> msgs = sortList(getEntityDeathMessages().getStringList("Entities." + entityName + "." + projectileDamage), p, em.getEntity());

		if (mobType.equals(MobType.MYTHIC_MOB)) {
			String internalMobType = null;
			if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(em.getEntityUUID())) {
				internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(em.getEntity()).getMobType();
			}
			msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + internalMobType + "." + projectileDamage), p, em.getEntity());
		}

		if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
			DeathMessages.LOGGER.warn("node: [Entities.{}.{}]", entityName, projectileDamage);
		if (msgs.isEmpty()) {
			if (Settings.getInstance().getConfig().getBoolean(Config.DEFAULT_MELEE_LAST_DAMAGE_NOT_DEFINED.getPath())) {
				if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
					DeathMessages.LOGGER.warn("node2：: [getEntityDeath]");
				return getEntityDeath(p, em.getEntity(), getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK), mobType);
			}
			// This death message will not be broadcast, since user have not set death message for this entity
			return Component.empty();
		}

		String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.get(0);

		TextComponent.Builder base = Component.text();

		if (Settings.getInstance().getConfig().getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES.getPath())) {
			TextComponent prefix = Util.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		List<String> rawEvents = new ArrayList<>();
		msg = ComponentUtil.sortHoverEvents(msg, rawEvents);

		if (msg.contains("%weapon%") && em.getLastProjectileEntity() instanceof Arrow) {
			ItemStack i = p.getEquipment().getItemInMainHand();
			Component displayName;
			if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
				if (Settings.getInstance().getConfig().getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED.getPath())) {
					if (!Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO.getPath())
							.equals(projectileDamage)) {
						return getEntityDeathProjectile(p, em,
								Settings.getInstance().getConfig().getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO.getPath()), mobType);
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
				Component hoverEvent = ComponentUtil.buildHoverEvents(rawEvent, null, p, em.getEntity(), hasOwner, false);
				baseWithEvents = baseWithEvents.replaceText(
						TextReplacementConfig.builder().match("%hover_event_" + index++ + "%").replacement(hoverEvent).build()
				);
			}
		}

		return (TextComponent) entityDeathPlaceholders(baseWithEvents, p, em.getEntity(), hasOwner);
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
			String internalMobType = null;
			if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(e.getUniqueId())) {
				internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(e).getMobType();
			} else {
				// reserved
			}
			msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + internalMobType + "." + damageCause), player, e);
		}

		if (Settings.getInstance().getConfig().getBoolean(Config.DEBUG.getPath()))
			DeathMessages.LOGGER.warn("node: [Entities.{}.{}]", entityName, damageCause);
		if (msgs.isEmpty()) {
			// This death message will not be broadcast, since user have not set death message for this entity
			return Component.empty();
		}

		String msg = (msgs.size() > 1) ? msgs.get(ThreadLocalRandom.current().nextInt(msgs.size())) : msgs.get(0);

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
				Matcher m = Pattern.compile("PERMISSION\\[(.*?)]").matcher(s);
				while (m.find()) {
					String perm = m.group(1);
					s = victim.hasPermission(perm)
							? s.replace("PERMISSION[" + perm + "]", "") : "";
				}
			}
			// Check for killer permission messages
			if (s.contains("PERMISSION_KILLER[")) {
				Matcher m = Pattern.compile("PERMISSION_KILLER\\[(.*?)]").matcher(s);
				while (m.find()) {
					String perm = m.group(1);
					s = killer.hasPermission(perm)
							? s.replace("PERMISSION_KILLER[" + perm + "]", "") : "";
				}
			}
			// Check for region specific messages
			if (s.contains("REGION[")) {
				Matcher m = Pattern.compile("REGION\\[(.*?)]").matcher(s);
				while (m.find()) {
					String regionID = m.group(1);
					s = DeathMessages.worldGuardExtension.isInRegion(victim, regionID)
							? s.replace("REGION[" + regionID + "]", "") : "";
				}
			}

			// Append messages sorted
			if (!s.isEmpty()) result.add(s);
		}

		return result;
	}

	public static Component entityDeathPlaceholders(Component msg, Player player, Entity entity, boolean hasOwner) {
		msg = msg.replaceText(Util.replace("%entity%", Messages.getInstance().getConfig().getString("Mobs."
						+ entity.getType().toString().toLowerCase())))
				.replaceText(Util.replace("%entity_display%", entity.getCustomName() == null ? Messages.getInstance().getConfig().getString("Mobs."
						+ entity.getType().toString().toLowerCase()) : entity.getCustomName()))
				.replaceText(Util.replace("%killer%", player.getName()))
				.replaceText(Util.replace("%killer_display%", player.getDisplayName()))
				.replaceText(Util.replace("%world%", entity.getLocation().getWorld().getName()))
				.replaceText(Util.replace("%world_environment%", getEnvironment(entity.getLocation().getWorld().getEnvironment())))
				.replaceText(Util.replace("%x%", String.valueOf(entity.getLocation().getBlock().getX())))
				.replaceText(Util.replace("%y%", String.valueOf(entity.getLocation().getBlock().getY())))
				.replaceText(Util.replace("%z%", String.valueOf(entity.getLocation().getBlock().getZ())));

		if (hasOwner) {
			msg = msg.replaceText(Util.replace("%owner%", ((Tameable) entity).getOwner().getName()));
		}

		try {
			msg = msg.replaceText(Util.replace("%biome%", entity.getLocation().getBlock().getBiome().name()));
		} catch (NullPointerException e) {
			DeathMessages.LOGGER.error("Custom Biome detected. Using 'Unknown' for a biome name.");
			DeathMessages.LOGGER.error("Custom Biomes are not supported yet.'");
			msg = msg.replaceText(Util.replace("%biome%", "Unknown"));
		}

		try {
			if (entity != null && entity.getLocation() != null) {
				msg = msg.replaceText(Util.replace("%distance%", String.valueOf((int) Math.round(player.getLocation().distance(entity.getLocation())))));
			}
		} catch (Exception ex) {
			DeathMessages.LOGGER.error("Unknown distance calculated. Using 'Zero' for the distance.");
			msg = msg.replaceText(Util.replace("%distance", "0"));
		}

		if (DeathMessages.getInstance().placeholderAPIEnabled) {
			Matcher identifiers = Pattern.compile("%([^%]+)%").matcher(Util.convertToLegacy(msg));

			while (identifiers.find()) {
				String identifier = identifiers.group(0);
				msg = msg.replaceText(Util.replace(identifier, PlaceholderAPI.setPlaceholders(player, identifier)));
			}
		}

		return msg;
	}

	public static String entityDeathPlaceholders(String msg, Player player, Entity entity, boolean hasOwner) {
		final boolean isSayanVanished = isSayanVanished(player);
		msg = msg
				.replaceAll("%entity%", Messages.getInstance().getConfig().getString("Mobs."
						+ entity.getType().toString().toLowerCase()))
				.replaceAll("%entity_display%", entity.getCustomName() == null ? Messages.getInstance().getConfig().getString("Mobs."
						+ entity.getType().toString().toLowerCase()) : entity.getCustomName())
				.replaceAll("%killer%", isSayanVanished ? getVanishedName() : player.getName())
				.replaceAll("%killer_display%", isSayanVanished ? getVanishedName() : player.getDisplayName())
				.replaceAll("%world%", entity.getLocation().getWorld().getName())
				.replaceAll("%world_environment%", getEnvironment(entity.getLocation().getWorld().getEnvironment()))
				.replaceAll("%x%", String.valueOf(entity.getLocation().getBlock().getX()))
				.replaceAll("%y%", String.valueOf(entity.getLocation().getBlock().getY()))
				.replaceAll("%z%", String.valueOf(entity.getLocation().getBlock().getZ()));

		if (hasOwner) {
			msg = msg.replaceAll("%owner%", ((Tameable) entity).getOwner().getName());
		}

		try {
			msg = msg
					.replaceAll("%biome%", entity.getLocation().getBlock().getBiome().name());
		} catch (NullPointerException e) {
			DeathMessages.LOGGER.error("Custom Biome detected. Using 'Unknown' for a biome name.");
			DeathMessages.LOGGER.error("Custom Biomes are not supported yet.'");
			msg = msg
					.replaceAll("%biome%", "Unknown");
		}

		try {
			if (entity != null && entity.getLocation() != null) {
				msg = msg.replaceAll("%distance%", String.valueOf((int) Math.round(player.getLocation().distance(entity.getLocation()))));
			}
		} catch (Exception ex) {
			DeathMessages.LOGGER.error("Unknown distance calculated. Using 'Zero' for the distance.");
			msg = msg.replaceAll("%distance", "0");
		}


		if (DeathMessages.getInstance().placeholderAPIEnabled) {
			msg = PlaceholderAPI.setPlaceholders(player, msg);
		}

		return msg;
	}

	public static Component playerDeathPlaceholders(Component msg, PlayerManager pm, LivingEntity mob) {
		final boolean isSayanVanished = isSayanVanished(pm.getPlayer());
		msg = msg.replaceText(Util.replace("%player%", isSayanVanished ? getVanishedName() : pm.getName()))
				.replaceText(Util.replace("%player_display%", isSayanVanished ? getVanishedName() : pm.getPlayer().getDisplayName()))
				.replaceText(Util.replace("%world%", pm.getLastLocation().getWorld().getName()))
				.replaceText(Util.replace("%world_environment%", getEnvironment(pm.getLastLocation().getWorld().getEnvironment())))
				.replaceText(Util.replace("%x%", String.valueOf(pm.getLastLocation().getBlock().getX())))
				.replaceText(Util.replace("%y%", String.valueOf(pm.getLastLocation().getBlock().getY())))
				.replaceText(Util.replace("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ())));

		try {
			msg = msg.replaceText(Util.replace("%biome%", pm.getLastLocation().getBlock().getBiome().name()));
		} catch (NullPointerException e) {
			DeathMessages.LOGGER.error("Custom Biome detected. Using 'Unknown' for a biome name.");
			DeathMessages.LOGGER.error("Custom Biomes are not supported yet.'");
			msg = msg.replaceText(Util.replace("%biome%", "Unknown"));
		}

		try {
			if (mob != null && mob.getLocation() != null) {
				msg = msg.replaceText(Util.replace("%distance%", String.valueOf((int) Math.round(pm.getLastLocation().distance(mob.getLocation())))));
			}
		} catch (Exception ex) {
			DeathMessages.LOGGER.error("Unknown distance calculated. Using 'Zero' for the distance.");
			msg = msg.replaceText(Util.replace("%distance", "0"));
		}

		if (mob != null) {
			String mobName = mob.getName();
			if (Settings.getInstance().getConfig().getBoolean(Config.RENAME_MOBS_ENABLED.getPath())) {
				String[] chars = Settings.getInstance().getConfig().getString(Config.RENAME_MOBS_IF_CONTAINS.getPath()).split("(?!^)");
				for (String ch : chars) {
					if (mobName.contains(ch)) {
						mobName = Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase());
						break;
					}
				}
			}

			if (!(mob instanceof Player) && Settings.getInstance().getConfig().getBoolean(Config.DISABLE_NAMED_MOBS.getPath())) {
				mobName = Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase());
			}

			msg = msg.replaceText(Util.replace("%killer%", mobName))
					.replaceText(Util.replace("%killer_type%", Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase())));

			if (mob instanceof Player) {
				Player p = (Player) mob;
				msg = msg.replaceText(Util.replace("%killer_display%", p.getDisplayName()));
			}
		}

		if (DeathMessages.getInstance().placeholderAPIEnabled) {
			Matcher params = Pattern.compile("%([^%]+)%").matcher(Util.convertToLegacy(msg));

			while (params.find()) {
				String param = params.group(0);
				msg = msg.replaceText(Util.replace(param, PlaceholderAPI.setPlaceholders(pm.getPlayer(), param)));
			}
		}

		return msg;
	}

	public static String playerDeathPlaceholders(String msg, PlayerManager pm, LivingEntity mob) {
		final boolean isSayanVanished = isSayanVanished(pm.getPlayer());
		msg = msg.replaceAll("%player%", isSayanVanished ? getVanishedName() : pm.getName())
				.replaceAll("%player_display%", isSayanVanished ? getVanishedName() : pm.getPlayer().getDisplayName())
				.replaceAll("%world%", pm.getLastLocation().getWorld().getName())
				.replaceAll("%world_environment%", getEnvironment(pm.getLastLocation().getWorld().getEnvironment()))
				.replaceAll("%x%", String.valueOf(pm.getLastLocation().getBlock().getX()))
				.replaceAll("%y%", String.valueOf(pm.getLastLocation().getBlock().getY()))
				.replaceAll("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ()));

		try {
			msg = msg.replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name());
		} catch (NullPointerException e) {
			DeathMessages.LOGGER.error("Custom Biome detected. Using 'Unknown' for a biome name.");
			DeathMessages.LOGGER.error("Custom Biomes are not supported yet.'");
			msg = msg.replaceAll("%biome%", "Unknown");
		}

		try {
			if (mob != null && mob.getLocation() != null) {
				msg = msg.replaceAll("%distance%", String.valueOf((int) Math.round(pm.getLastLocation().distance(mob.getLocation()))));
			}
		} catch (Exception ex) {
			DeathMessages.LOGGER.error("Unknown distance calculated. Using 'Zero' for the distance.");
			msg = msg.replaceAll("%distance", "0");
		}

		if (mob != null) {
			String mobName = mob.getName();
			if (Settings.getInstance().getConfig().getBoolean(Config.RENAME_MOBS_ENABLED.getPath())) {
				String[] chars = Settings.getInstance().getConfig().getString(Config.RENAME_MOBS_IF_CONTAINS.getPath()).split("(?!^)");
				for (String ch : chars) {
					if (mobName.contains(ch)) {
						mobName = Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase());
						break;
					}
				}
			}

			if (!(mob instanceof Player) && Settings.getInstance().getConfig().getBoolean(Config.DISABLE_NAMED_MOBS.getPath())) {
				mobName = Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase());
			}

			msg = msg.replaceAll("%killer%", mobName)
					.replaceAll("%killer_type%", Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase()));

			if (mob instanceof Player) {
				Player p = (Player) mob;
				msg = msg.replaceAll("%killer_display%", isSayanVanished(p) ? getVanishedName() : p.getDisplayName());
			}
		}

		if (DeathMessages.getInstance().placeholderAPIEnabled) {
			msg = PlaceholderAPI.setPlaceholders(pm.getPlayer(), msg);
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

		if (Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_ITEM_NAME.getPath()) && !DeathMessages.discordSRVEnabled) {
			if (Util.isNewerAndEqual(12, 0)) {
				// Block: block.minecraft.example
				// Item: item.minecraft.example
				String materialType = i.getType().isBlock() ? "block" : "item";
				String rawTranslatable = "<lang:" + materialType + ".minecraft." + i.getType().name().toLowerCase() + ">";
				i18nName = MiniMessage.miniMessage().deserialize(rawTranslatable);
			} else if (DeathMessages.getInstance().langUtilsEnabled) {
				i18nName = Component.text(LanguageHelper.getItemName(i, p.getLocale()));
			} else {
				String name = capitalize(i.getType().name());
				i18nName = Component.text(name);
			}
		} else {
			String name = capitalize(i.getType().name());
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

		if (Settings.getInstance().getConfig().getBoolean(Config.DISPLAY_I18N_MOB_NAME.getPath()) && !DeathMessages.discordSRVEnabled) {
			if (Util.isNewerAndEqual(12, 0)) {
				// Entity: entity.minecraft.example
				String rawTranslatable = "<lang:entity.minecraft." + mob.getType().name().toLowerCase() + ">";
				i18nName = MiniMessage.miniMessage().deserialize(rawTranslatable);
			} else if (DeathMessages.getInstance().langUtilsEnabled && !(mob instanceof ShulkerBullet)) { // <= 1.12.2 no ShulkerBullet lang key
				i18nName = Component.text(LanguageHelper.getEntityName(mob, p.getLocale()));
			} else {
				String name = capitalize(mob.getType().name());
				i18nName = Component.text(name);
			}
		} else {
			String name = capitalize(mob.getType().name());
			i18nName = Component.text(name);
		}

		return i18nName;
	}

	private static String capitalize(String name) {
		// Split with "_"
		String[] list = name.split("_");
		StringBuilder sb = new StringBuilder();
		int i = 0;

		// To make the first letter of each word capitalized, then append the rest of the string in each word together
		for (String s : list) {
			String fst = s.substring(0, 1);
			String snd = s.substring(1).toLowerCase();

			sb.append(fst).append(snd);

			// Add space between split words
			if (i < list.length - 1) {
				sb.append(" ");
			}

			i++;
		}

		return sb.toString();
	}

	public static String getEnvironment(World.Environment environment) {
		switch (environment) {
			case NORMAL:
				return Messages.getInstance().getConfig().getString("Environment.normal");
			case NETHER:
				return Messages.getInstance().getConfig().getString("Environment.nether");
			case THE_END:
				return Messages.getInstance().getConfig().getString("Environment.the_end");
			default:
				// Dreeam TODO: support all environment
				return Messages.getInstance().getConfig().getString("Environment.unknown");
		}
	}

	public static String getSimpleProjectile(Projectile projectile) {
		if (projectile instanceof Arrow) {
			return "Projectile-Arrow";
		} else if (projectile instanceof DragonFireball) {
			return "Projectile-Dragon-Fireball";
		} else if (projectile instanceof Egg) {
			return "Projectile-Egg";
		} else if (projectile instanceof EnderPearl) {
			return "Projectile-EnderPearl";
		} else if (projectile instanceof WitherSkull) {
			return "Projectile-Fireball";
		} else if (projectile instanceof Fireball) {
			return "Projectile-Fireball";
		} else if (projectile instanceof FishHook) {
			return "Projectile-FishHook";
		} else if (projectile instanceof LlamaSpit) {
			return "Projectile-LlamaSpit";
		} else if (projectile instanceof Snowball) {
			return "Projectile-Snowball";
		} else if (projectile instanceof ShulkerBullet) {
			return "Projectile-ShulkerBullet";
		} else if (projectile instanceof Trident) {
			return "Projectile-Trident";
		} else {
			return "Projectile-Arrow";
		}
	}

	public static String getSimpleCause(EntityDamageEvent.DamageCause damageCause) {
		switch (damageCause) {
			case KILL:
				return "Kill";
			case WORLD_BORDER:
				return "World-Border";
			case CONTACT:
				return "Contact";
			case ENTITY_ATTACK:
			case ENTITY_SWEEP_ATTACK:
				return "Melee";
			case PROJECTILE:
				return "Projectile";
			case SUFFOCATION:
				return "Suffocation";
			case FALL:
				return "Fall";
			case FIRE:
				return "Fire";
			case FIRE_TICK:
				return "Fire-Tick";
			case MELTING:
				return "Melting";
			case LAVA:
				return "Lava";
			case DROWNING:
				return "Drowning";
			case BLOCK_EXPLOSION:
			case ENTITY_EXPLOSION:
				return "Explosion";
			case VOID:
				return "Void";
			case LIGHTNING:
				return "Lightning";
			case SUICIDE:
				return "Suicide";
			case STARVATION:
				return "Starvation";
			case POISON:
				return "Poison";
			case MAGIC:
				return "Magic";
			case WITHER:
				return "Wither";
			case FALLING_BLOCK:
				return "Falling-Block";
			case THORNS:
				return "Thorns";
			case DRAGON_BREATH:
				return "Dragon-Breath";
			case CUSTOM:
				return "Custom";
			case FLY_INTO_WALL:
				return "Fly-Into-Wall";
			case HOT_FLOOR:
				return "Hot-Floor";
			case CRAMMING:
				return "Cramming";
			case DRYOUT:
				return "Dryout";
			case FREEZE:
				return "Freeze";
			case SONIC_BOOM:
				return "Sonic-Boom";
			default:
				return "Unknown";
		}
	}

	public static FileConfiguration getPlayerDeathMessages() {
		return PlayerDeathMessages.getInstance().getConfig();
	}

	public static FileConfiguration getEntityDeathMessages() {
		return EntityDeathMessages.getInstance().getConfig();
	}

	private static boolean isSayanVanished(Player p) {
		if (DeathMessages.getInstance().sayanVanishEnabled) {
			return DeathMessages.getInstance().sayanVanishExtension.isVanished(p.getUniqueId());
		}
		return false;
	}

	private static String getVanishedName() {
		return Settings.getInstance().getConfig().getString(Config.HOOKS_SAYANVANISH_VANISHED_NAME.getPath());
	}
}
