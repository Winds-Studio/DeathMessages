package dev.mrshawn.deathmessages.utils;

import com.cryptomorin.xseries.XMaterial;
import dev.mrshawn.deathmessages.DeathMessages;
import dev.mrshawn.deathmessages.api.EntityManager;
import dev.mrshawn.deathmessages.api.ExplosionManager;
import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.config.EntityDeathMessages;
import dev.mrshawn.deathmessages.config.Messages;
import dev.mrshawn.deathmessages.config.PlayerDeathMessages;
import dev.mrshawn.deathmessages.enums.DeathAffiliation;
import dev.mrshawn.deathmessages.enums.DeathModes;
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
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
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assets {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();
	private static final boolean addPrefix = config.getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES);
	public static final HashMap<String, String> addingMessage = new HashMap<>();

	public static boolean isNumeric(String s) {
		for (char c : s.toCharArray()) {
			if (Character.isDigit(c))
				return true;
		}
		return false;
	}

	public static String formatMessage(String path) {
		return Messages.getInstance().getConfig().getString(path)
				.replaceAll("%prefix%", Messages.getInstance().getConfig().getString("Prefix"));
	}

	public static String formatString(String s) {
		return s
				.replaceAll("%prefix%", Messages.getInstance().getConfig().getString("Prefix"));
	}

	public static List<String> formatMessage(List<String> list) {
		List<String> newList = new ArrayList<>();
		for (String s : list) {
			newList.add(s
					.replaceAll("%prefix%", Messages.getInstance().getConfig().getString("Prefix")));
		}
		return newList;
	}

	public static TextComponent convertFromLegacy(String s) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(s);
	}

	public static boolean isClimbable(Block block) {
		return isClimbable(block.getType());
	}

	public static boolean isClimbable(Material material) {
		final String name = material.name();
		return name.contains("LADDER")
				|| name.contains("VINE")
				|| name.contains("SCAFFOLDING")
				|| name.contains("TRAPDOOR");
	}

	public static boolean itemNameIsWeapon(ItemStack itemStack) {
		if (itemStack == null || !itemStack.hasItemMeta() || !itemStack.getItemMeta().hasDisplayName()) return false;

		String displayName = itemStack.getItemMeta().getDisplayName();

		for (String s : config.getStringList(Config.CUSTOM_ITEM_DISPLAY_NAMES_IS_WEAPON)) {
			Pattern pattern = Pattern.compile(s);
			Matcher matcher = pattern.matcher(displayName);
			if (matcher.find()) {
				return true;
			}
		}
		return false;
	}

	public static boolean itemMaterialIsWeapon(ItemStack itemStack) {
		for (String s : config.getStringList(Config.CUSTOM_ITEM_MATERIAL_IS_WEAPON)) {
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
				|| materialName.contains("BOW");
	}

	public static boolean hasWeapon(LivingEntity mob, EntityDamageEvent.DamageCause damageCause) {
		if (mob.getEquipment() == null || damageCause.equals(EntityDamageEvent.DamageCause.THORNS)) return false;

		return isWeapon(mob.getEquipment().getItemInMainHand());
	}

	public static TextComponent playerDeathMessage(PlayerManager pm, boolean gang) {
		LivingEntity mob = (LivingEntity) pm.getLastEntityDamager();
		boolean hasWeapon = hasWeapon(mob, pm.getLastDamage());

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
					if (DeathMessages.majorVersion() >= 16) {
						if (explosionManager.get().getMaterial().equals(Material.RESPAWN_ANCHOR)) {
							return get(gang, pm, pyro.get().getPlayer(), "Respawn-Anchor");
						}
					}
				}
			}
		}
		if (hasWeapon) {
			if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
				return getWeapon(gang, pm, mob);
			} else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE) && pm.getLastProjectileEntity() instanceof Arrow) { // Dreeam TODO
				return getProjectile(gang, pm, mob, getSimpleProjectile(pm.getLastProjectileEntity()));
			} else {
				return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
			}
		} else {
			for (EntityDamageEvent.DamageCause dc : EntityDamageEvent.DamageCause.values()) {
				if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
					return getProjectile(gang, pm, mob, getSimpleProjectile(pm.getLastProjectileEntity()));
				}
				if (pm.getLastDamage().equals(dc)) {
					return get(gang, pm, mob, getSimpleCause(dc));
				}
			}
			return null;
		}
	}

	public static TextComponent entityDeathMessage(EntityManager em, MobType mobType) {
		Optional<PlayerManager> pm = Optional.ofNullable(em.getLastPlayerDamager());

		if (pm.isEmpty()) return null;

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
					if (DeathMessages.majorVersion() >= 16) {
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
			return null;
		}
	}

	public static TextComponent getNaturalDeath(PlayerManager pm, String damageCause) {
		List<String> msgs = sortList(getPlayerDeathMessages().getStringList("Natural-Cause." + damageCause), pm.getPlayer(), pm.getPlayer());

		if (config.getBoolean(Config.DEBUG)) System.out.println("Natural-Cause." + damageCause); // Dreeam - debug

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		msg = playerDeathPlaceholders(msg, pm, null);

		TextComponent.Builder base = Component.text();

		if (addPrefix) {
			TextComponent prefix = Assets.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		if (msg.contains("%block%") && pm.getLastEntityDamager() instanceof FallingBlock) {
			try {
				FallingBlock fb = (FallingBlock) pm.getLastEntityDamager();
				String material = fb.getBlockData().getMaterial().toString().toLowerCase();
				String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);

				base.append(Assets.convertFromLegacy(msg.replaceAll("%block%", configValue)));
			} catch (NullPointerException e) {
				LogManager.getLogger().error("Could not parse %block%. Please check your config for a wrong value." +
						" Your materials could be spelt wrong or it does not exists in the config. Open a issue if you need help, " + "https://github.com/Winds-Studio/DeathMessages/issues");
				pm.setLastEntityDamager(null);
				return getNaturalDeath(pm, getSimpleCause(EntityDamageEvent.DamageCause.SUFFOCATION));
			}
		} else if (msg.contains("%climbable%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.FALL)) {
			try {
				String material = pm.getLastClimbing().toString().toLowerCase();
				String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);

				base.append(Assets.convertFromLegacy(msg.replaceAll("%climbable%", configValue)));
			} catch (NullPointerException e) {
				pm.setLastClimbing(null);
				return getNaturalDeath(pm, getSimpleCause(EntityDamageEvent.DamageCause.FALL));
			}
		} else if (msg.contains("%weapon%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			ItemStack i = pm.getPlayer().getEquipment().getItemInMainHand();

			if (!i.getType().equals(XMaterial.BOW.parseMaterial())) {
				return getNaturalDeath(pm, "Projectile-Unknown");
			}
			if (DeathMessages.majorVersion() >= 14) {
				if (!i.getType().equals(XMaterial.CROSSBOW.parseMaterial())) {
					return getNaturalDeath(pm, "Projectile-Unknown");
				}
			}

			String displayName;
			if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
				if (config.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED)) {
					if (!config.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_IGNORE_ENCHANTMENTS)) {
						if (i.getEnchantments().isEmpty()) {
							return getNaturalDeath(pm, "Projectile-Unknown");
						}
					} else {
						return getNaturalDeath(pm, "Projectile-Unknown");
					}
				}
				displayName = Assets.convertString(i.getType().name());
			} else {
				displayName = i.getItemMeta().getDisplayName();
			}

			HoverEvent<HoverEvent.ShowItem> showItem = HoverEvent.showItem(Key.key(i.getType().name().toLowerCase()), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().toString()));

			Component weapon = Component.text()
					.append(Assets.convertFromLegacy(displayName))
					.build()
					.hoverEvent(showItem);

			Component deathMessage = Component.text()
					.append(convertFromLegacy(msg))
					.build()
					.replaceText(TextReplacementConfig.builder()
							.match("%weapon%")
							.replacement(weapon)
							.build());

			base.append(deathMessage);
		} else {
			TextComponent deathMessage = Assets.convertFromLegacy(msg);
			base.append(deathMessage);
		}
		// TODO: need to re-write the logic of death message click event & hover text.
//		if (msg.length() >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertFromLegacy(playerDeathPlaceholders(msg[1], pm, null))));
//		}
//		if (msg.length() == 3) {
//			if (msg[2].startsWith("COMMAND:")) {
//				String cmd = msg[2].split(":")[1];
//				tc.clickEvent(ClickEvent.runCommand( "/" + playerDeathPlaceholders(cmd, pm, null)));
//			} else if (msg[2].startsWith("SUGGEST_COMMAND:")) {
//				String cmd = msg[2].split(":")[1];
//				tc.clickEvent(ClickEvent.suggestCommand("/" + playerDeathPlaceholders(cmd, pm, null)));
//			}
//		}
		return base.build();
	}

	public static TextComponent getWeapon(boolean gang, PlayerManager pm, LivingEntity mob) {
		final boolean basicMode = PlayerDeathMessages.getInstance().getConfig().getBoolean("Basic-Mode.Enabled");
		String entityName = mob.getType().getEntityClass().getSimpleName().toLowerCase();
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

		if (config.getBoolean(Config.DEBUG)) System.out.println(mode + "." + affiliation + ".Weapon"); // Dreeam - debug

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		msg = playerDeathPlaceholders(msg, pm, mob);

		TextComponent.Builder base = Component.text();

		if (addPrefix) {
			TextComponent prefix = Assets.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		if (msg.contains("%weapon%")) {
			ItemStack i = mob.getEquipment().getItemInMainHand();
			String displayName;
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
				displayName = Assets.convertString(i.getType().name());
			} else {
				displayName = i.getItemMeta().getDisplayName();
			}

			HoverEvent<HoverEvent.ShowItem> showItem = HoverEvent.showItem(Key.key(i.getType().name().toLowerCase()), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().toString()));

			Component weapon = Component.text()
					.append(Assets.convertFromLegacy(displayName))
					.build()
					.hoverEvent(showItem);

			Component deathMessage = Component.text()
					.append(convertFromLegacy(msg))
					.build()
					.replaceText(TextReplacementConfig.builder()
							.match("%weapon%")
							.replacement(weapon)
							.build());

			base.append(deathMessage);
		} else {
			TextComponent deathMessage = Assets.convertFromLegacy(msg);
			base.append(deathMessage);
		}
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertFromLegacy(playerDeathPlaceholders(sec[1], pm, mob))));
//		}
//		if (sec.length == 3) {
//			if (sec[2].startsWith("COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.runCommand("/" + playerDeathPlaceholders(cmd, pm, mob)));
//			} else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.suggestCommand("/" + playerDeathPlaceholders(cmd, pm, mob)));
//			}
//		}
		return base.build();
	}

	public static TextComponent getEntityDeathWeapon(Player p, Entity e, MobType mobType) {
		String entityName = e.getType().getEntityClass().getSimpleName().toLowerCase();
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

		boolean hasOwner = false;
		if (e instanceof Tameable tameable) {
			if (tameable.getOwner() != null) hasOwner = true;
		}

		if (config.getBoolean(Config.DEBUG)) System.out.println("Entities." + entityName + ".Weapon"); // Dreeam - debug

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		msg = entityDeathPlaceholders(msg, p, e, hasOwner);

		TextComponent.Builder base = Component.text();

		if (addPrefix) {
			TextComponent prefix = Assets.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		if (msg.contains("%weapon%")) {
			String displayName;
			ItemStack i = p.getEquipment().getItemInMainHand();
			if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
				if (config.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED)) {
					if (!config.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_IGNORE_ENCHANTMENTS)) {
						if (i.getEnchantments().isEmpty()) {
							return getEntityDeath(p, e,
									config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO), mobType);
						}
					} else {
						return getEntityDeath(p, e,
								config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_WEAPON_DEFAULT_TO), mobType);
					}
				}
				displayName = Assets.convertString(i.getType().name());
			} else {
				displayName = i.getItemMeta().getDisplayName();
			}

			HoverEvent<HoverEvent.ShowItem> showItem = HoverEvent.showItem(Key.key(i.getType().name().toLowerCase()), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().toString()));

			Component weapon = Component.text()
					.append(Assets.convertFromLegacy(displayName))
					.build()
					.hoverEvent(showItem);

			Component deathMessage = Component.text()
					.append(convertFromLegacy(msg))
					.build()
					.replaceText(TextReplacementConfig.builder()
							.match("%weapon%")
							.replacement(weapon)
							.build());

			base.append(deathMessage);
		} else {
			TextComponent deathMessage = Assets.convertFromLegacy(msg);
			base.append(deathMessage);
		}
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertFromLegacy(entityDeathPlaceholders(sec[1], p, e, hasOwner))));
//		}
//		if (sec.length == 3) {
//			if (sec[2].startsWith("COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.runCommand("/" + entityDeathPlaceholders(cmd, p, e, hasOwner)));
//			} else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.suggestCommand("/" + entityDeathPlaceholders(cmd, p, e, hasOwner)));
//			}
		return base.build();
	}

	public static TextComponent get(boolean gang, PlayerManager pm, LivingEntity mob, String damageCause) {
		final boolean basicMode = PlayerDeathMessages.getInstance().getConfig().getBoolean("Basic-Mode.Enabled");
		String entityName = mob.getType().getEntityClass().getSimpleName().toLowerCase();
		final String mode = basicMode ? DeathModes.BASIC_MODE.getValue() : DeathModes.MOBS.getValue()
				+ "." + entityName;
		final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();
		List<String> msgs = sortList(getPlayerDeathMessages().getStringList(mode + "." + affiliation + "." + damageCause), pm.getPlayer(), mob);
		if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
			String internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
			//Bukkit.broadcastMessage("is myth - " + internalMobType);
			msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + internalMobType + "." + affiliation + "." + damageCause), pm.getPlayer(), mob);
		}

		if (config.getBoolean(Config.DEBUG)) System.out.println(mode + "." + affiliation + "." + damageCause); // Dreeam - debug

		if (msgs.isEmpty()) {
			if (config.getBoolean(Config.DEFAULT_NATURAL_DEATH_NOT_DEFINED))
				return getNaturalDeath(pm, damageCause);
			if (config.getBoolean(Config.DEFAULT_MELEE_LAST_DAMAGE_NOT_DEFINED))
				return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
			return null;
		}

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));

		TextComponent.Builder base = Component.text();

		if (addPrefix) {
			TextComponent prefix = Assets.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		TextComponent deathMessage = Assets.convertFromLegacy(playerDeathPlaceholders(msg, pm, mob));
		base.append(deathMessage);
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertFromLegacy(playerDeathPlaceholders(sec[1], pm, mob))));
//		}
//		if (sec.length == 3) {
//			if (sec[2].startsWith("COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.runCommand("/" + playerDeathPlaceholders(cmd, pm, mob)));
//			} else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.suggestCommand("/" + playerDeathPlaceholders(cmd, pm, mob)));
//			}
//		}
		return base.build();
	}

	public static TextComponent getProjectile(boolean gang, PlayerManager pm, LivingEntity mob, String projectileDamage) {
		final boolean basicMode = PlayerDeathMessages.getInstance().getConfig().getBoolean("Basic-Mode.Enabled");
		String entityName = mob.getType().getEntityClass().getSimpleName().toLowerCase();
		final String mode = basicMode ? DeathModes.BASIC_MODE.getValue() : DeathModes.MOBS.getValue()
				+ "." + entityName;
		final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();
		List<String> msgs = sortList(getPlayerDeathMessages().getStringList(mode + "." + affiliation + "." + projectileDamage), pm.getPlayer(), mob);
		if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
			String internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
			msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + internalMobType + "." + affiliation + "." + projectileDamage), pm.getPlayer(), mob);
		}

		if (config.getBoolean(Config.DEBUG)) System.out.println(mode + "." + affiliation + "." + projectileDamage); // Dreeam - debug

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		msg = playerDeathPlaceholders(msg, pm, mob);

		TextComponent.Builder base = Component.text();

		if (addPrefix) {
			TextComponent prefix = Assets.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		if (msg.contains("%weapon%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			ItemStack i = mob.getEquipment().getItemInMainHand();
			String displayName;
			HoverEvent<HoverEvent.ShowItem> showItem;
			if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
				if (config.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED)) {
					if (!config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO)
							.equals(projectileDamage)) {
						return getProjectile(gang, pm, mob, config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO));
					}
				}
				displayName = Assets.convertString(i.getType().name());
				showItem = HoverEvent.showItem(Key.key(i.getType().name().toLowerCase()), i.getAmount());
			} else {
				displayName = i.getItemMeta().getDisplayName();
				showItem = HoverEvent.showItem(Key.key(i.getType().name().toLowerCase()), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().toString()));
			}

			Component weapon = Component.text()
					.append(Assets.convertFromLegacy(displayName))
					.build()
					.hoverEvent(showItem);

			Component deathMessage = Component.text()
					.append(convertFromLegacy(msg))
					.build()
					.replaceText(TextReplacementConfig.builder()
							.match("%weapon%")
							.replacement(weapon)
							.build());

			base.append(deathMessage);
		} else {
			TextComponent deathMessage = Assets.convertFromLegacy(msg);
			base.append(deathMessage);
		}
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertFromLegacy(playerDeathPlaceholders(sec[1], pm, mob))));
//		}
//		if (sec.length == 3) {
//			if (sec[2].startsWith("COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.runCommand("/" + playerDeathPlaceholders(cmd, pm, mob)));
//			} else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.suggestCommand("/" + playerDeathPlaceholders(cmd, pm, mob)));
//			}
//		}
		return base.build();
	}

	public static TextComponent getEntityDeathProjectile(Player p, EntityManager em, String projectileDamage, MobType mobType) {
		String entityName = em.getEntity().getType().getEntityClass().getSimpleName().toLowerCase();
		List<String> msgs = sortList(getEntityDeathMessages().getStringList("Entities." + entityName + "." + projectileDamage), p, em.getEntity());
		if (mobType.equals(MobType.MYTHIC_MOB)) {
			String internalMobType = null;
			if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(em.getEntityUUID())) {
				internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(em.getEntity()).getMobType();
			}
			msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + internalMobType + "." + projectileDamage), p, em.getEntity());
		}

		if (msgs.isEmpty()) {
			if (config.getBoolean(Config.DEBUG)) System.out.println("Entities." + entityName + "." + projectileDamage); // Dreeam - debug
			if (config.getBoolean(Config.DEFAULT_MELEE_LAST_DAMAGE_NOT_DEFINED)) {
				return getEntityDeath(p, em.getEntity(), getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK), mobType);
			}
			return null;
		}

		boolean hasOwner = false;
		if (em.getEntity() instanceof Tameable tameable) {
			if (tameable.getOwner() != null) hasOwner = true;
		}

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		msg = entityDeathPlaceholders(msg, p, em.getEntity(), hasOwner);

		TextComponent.Builder base = Component.text();

		if (addPrefix) {
			TextComponent prefix = Assets.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		if (msg.contains("%weapon%") && em.getLastProjectileEntity() instanceof Arrow) {
			ItemStack i = p.getEquipment().getItemInMainHand();
			String displayName;
			if (i.getItemMeta() == null || !i.getItemMeta().hasDisplayName() || i.getItemMeta().getDisplayName().isEmpty()) {
				if (config.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED)) {
					if (!config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO)
							.equals(projectileDamage)) {
						return getEntityDeathProjectile(p, em,
								config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO), mobType);
					}
				}
				displayName = Assets.convertString(i.getType().name());
			} else {
				displayName = i.getItemMeta().getDisplayName();
			}

			HoverEvent<HoverEvent.ShowItem> showItem = HoverEvent.showItem(Key.key(i.getType().name().toLowerCase()), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().toString()));

			Component weapon = Component.text()
					.append(Assets.convertFromLegacy(displayName))
					.build()
					.hoverEvent(showItem);

			Component deathMessage = Component.text()
					.append(convertFromLegacy(msg))
					.build()
					.replaceText(TextReplacementConfig.builder()
							.match("%weapon%")
							.replacement(weapon)
							.build());

			base.append(deathMessage);
		} else {
			TextComponent deathMessage = Assets.convertFromLegacy(msg);
			base.append(deathMessage);
		}
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertFromLegacy(entityDeathPlaceholders(sec[1], p, em.getEntity(), hasOwner))));
//		}
//		if (sec.length == 3) {
//			if (sec[2].startsWith("COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.runCommand("/" + entityDeathPlaceholders(cmd, p, em.getEntity(), hasOwner)));
//			} else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.suggestCommand("/" + entityDeathPlaceholders(cmd, p, em.getEntity(), hasOwner)));
//			}
//		}
		return base.build();
	}

	public static TextComponent getEntityDeath(Player player, Entity e, String damageCause, MobType mobType) {
		String entityName = e.getType().getEntityClass().getSimpleName().toLowerCase();

		boolean hasOwner = false;
		if (e instanceof Tameable tameable) {
			if (tameable.getOwner() != null) hasOwner = true;
		}

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

		if (config.getBoolean(Config.DEBUG)) System.out.println("Entities." + entityName + "." + damageCause); // Dreeam - debug

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));

		TextComponent.Builder base = Component.text();

		if (addPrefix) {
			TextComponent prefix = Assets.convertFromLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			base.append(prefix);
		}

		TextComponent deathMessage = Assets.convertFromLegacy(entityDeathPlaceholders(msg, player, e, hasOwner));
		base.append(deathMessage);
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertFromLegacy(entityDeathPlaceholders(sec[1], player, entity, hasOwner))));
//		}
//		if (sec.length == 3) {
//			if (sec[2].startsWith("COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.runCommand("/" + entityDeathPlaceholders(cmd, player, entity, hasOwner)));
//			} else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.suggestCommand("/" + entityDeathPlaceholders(cmd, player, entity, hasOwner)));
//			}
//		}
		return base.build();
	}

	public static List<String> sortList(List<String> list, Player player, Entity killer) {
		List<String> newList = list;
		List<String> returnList = new ArrayList<>();
		for (String s : list) {
			// Check for permission messages
			if (s.contains("PERMISSION[")) {
				Matcher m = Pattern.compile("PERMISSION\\[([^)]+)]").matcher(s);
				while (m.find()) {
					String perm = m.group(1);
					if (player.getPlayer().hasPermission(perm)) {
						returnList.add(s.replace("PERMISSION[" + perm + "]", ""));
					}
				}
			}
			if (s.contains("PERMISSION_KILLER[")) {
				Matcher m = Pattern.compile("PERMISSION_KILLER\\[([^)]+)]").matcher(s);
				while (m.find()) {
					String perm = m.group(1);
					if (killer.hasPermission(perm)) {
						returnList.add(s.replace("PERMISSION_KILLER[" + perm + "]", ""));
					}
				}
			}
			// Check for region specific messages
			if (s.contains("REGION[")) {
				Matcher m = Pattern.compile("REGION\\[([^)]+)]").matcher(s);
				while (m.find()) {
					String regionID = m.group(1);
					if (DeathMessages.worldGuardExtension.isInRegion(player.getPlayer(), regionID)) {
						returnList.add(s.replace("REGION[" + regionID + "]", ""));
					}
				}
			}
		}
		if (!returnList.isEmpty()) {
			newList = returnList;
		} else {
			newList.removeIf(s -> s.contains("PERMISSION[") || s.contains("REGION[") || s.contains("PERMISSION_KILLER["));
		}
		return newList;
	}

	public static String entityDeathPlaceholders(String msg, Player player, Entity entity, boolean owner) {
		msg = msg
				.replaceAll("%entity%", Messages.getInstance().getConfig().getString("Mobs."
						+ entity.getType().toString().toLowerCase()))
				.replaceAll("%entity_display%", entity.getCustomName() == null ? Messages.getInstance().getConfig().getString("Mobs."
						+ entity.getType().toString().toLowerCase()) : entity.getCustomName())
				.replaceAll("%killer%", player.getName())
				.replaceAll("%killer_display%", player.getDisplayName())
				.replaceAll("%world%", entity.getLocation().getWorld().getName())
				.replaceAll("%world_environment%", getEnvironment(entity.getLocation().getWorld().getEnvironment()))
				.replaceAll("%x%", String.valueOf(entity.getLocation().getBlock().getX()))
				.replaceAll("%y%", String.valueOf(entity.getLocation().getBlock().getY()))
				.replaceAll("%z%", String.valueOf(entity.getLocation().getBlock().getZ()));
		if (owner && entity instanceof Tameable tameable && tameable.getOwner() != null && tameable.getOwner().getName() != null) {
			msg = msg
					.replaceAll("%owner%", tameable.getOwner().getName());
		}
		try {
			msg = msg
					.replaceAll("%biome%", entity.getLocation().getBlock().getBiome().name());
		} catch (NullPointerException e) {
			LogManager.getLogger().error("Custom Biome detected. Using 'Unknown' for a biome name.");
			LogManager.getLogger().error("Custom Biomes are not supported yet.'");
			msg = msg
					.replaceAll("%biome%", "Unknown");
		}
		if (DeathMessages.getInstance().placeholderAPIEnabled) {
			msg = PlaceholderAPI.setPlaceholders(player.getPlayer(), msg);
		}
		return msg;
	}

	public static String playerDeathPlaceholders(String msg, PlayerManager pm, LivingEntity mob) {
		if (mob == null) {
			msg = msg
					.replaceAll("%player%", pm.getName())
					.replaceAll("%player_display%", pm.getPlayer().getDisplayName())
					.replaceAll("%world%", pm.getLastLocation().getWorld().getName())
					.replaceAll("%world_environment%", getEnvironment(pm.getLastLocation().getWorld().getEnvironment()))
					.replaceAll("%x%", String.valueOf(pm.getLastLocation().getBlock().getX()))
					.replaceAll("%y%", String.valueOf(pm.getLastLocation().getBlock().getY()))
					.replaceAll("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ()));
			try {
				msg = msg
						.replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name());
			} catch (NullPointerException e) {
				LogManager.getLogger().error("Custom Biome detected. Using 'Unknown' for a biome name.");
				LogManager.getLogger().error("Custom Biomes are not supported yet.'");
				msg = msg
						.replaceAll("%biome%", "Unknown");
			}
		} else {
			String mobName = mob.getName();
			if (config.getBoolean(Config.RENAME_MOBS_ENABLED)) {
				String[] chars = config.getString(Config.RENAME_MOBS_IF_CONTAINS).split("(?!^)");
				for (String ch : chars) {
					if (mobName.contains(ch)) {
						mobName = Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase());
						break;
					}
				}
			}
			if (!(mob instanceof Player) && config.getBoolean(Config.DISABLE_NAMED_MOBS)) {
				mobName = Messages.getInstance().getConfig().getString("Mobs." + mob.getType().toString().toLowerCase());
			}
			msg = msg
					.replaceAll("%player%", pm.getName())
					.replaceAll("%player_display%", pm.getPlayer().getDisplayName())
					.replaceAll("%killer%", mobName)
					.replaceAll("%killer_type%", Messages.getInstance().getConfig().getString("Mobs."
							+ mob.getType().toString().toLowerCase()))
					.replaceAll("%world%", pm.getLastLocation().getWorld().getName())
					.replaceAll("%world_environment%", getEnvironment(pm.getLastLocation().getWorld().getEnvironment()))
					.replaceAll("%x%", String.valueOf(pm.getLastLocation().getBlock().getX()))
					.replaceAll("%y%", String.valueOf(pm.getLastLocation().getBlock().getY()))
					.replaceAll("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ()));
			try {
				msg = msg
						.replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name());
			} catch (NullPointerException e) {
				LogManager.getLogger().error("Custom Biome detected. Using 'Unknown' for a biome name.");
				LogManager.getLogger().error("Custom Biomes are not supported yet.'");
				msg = msg
						.replaceAll("%biome%", "Unknown");
			}

			if (mob instanceof Player p) {
				msg = msg
						.replaceAll("%killer_display%", p.getDisplayName());
			}
		}
		if (DeathMessages.getInstance().placeholderAPIEnabled) {
			msg = PlaceholderAPI.setPlaceholders(pm.getPlayer(), msg);
		}
		return msg;
	}

	public static String convertString(String string) {
		string = string.replaceAll("_", " ").toLowerCase();
		String[] spl = string.split(" ");
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < spl.length; i++) {
			if (i == spl.length - 1) {
				builder.append(StringUtils.capitalize(spl[i]));
			} else {
				builder.append(StringUtils.capitalize(spl[i])).append(" ");
			}
		}
		return builder.toString();
	}

	public static String getEnvironment(World.Environment environment) {
		return switch (environment) {
			case NORMAL -> Messages.getInstance().getConfig().getString("Environment.normal");
			case NETHER -> Messages.getInstance().getConfig().getString("Environment.nether");
			case THE_END -> Messages.getInstance().getConfig().getString("Environment.the_end");
			default -> Messages.getInstance().getConfig().getString("Environment.unknown");
		};
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
		return switch (damageCause) {
			//case KILL -> "Kill";
			//case WORLD_BORDER -> "World-Border";
			case CONTACT -> "Contact";
			case ENTITY_ATTACK -> "Melee";
			//case ENTITY_SWEEP_ATTACK -> "Entity-Sweep-Attack";
			case PROJECTILE -> "Projectile";
			case SUFFOCATION -> "Suffocation";
			case FALL -> "Fall";
			case FIRE -> "Fire";
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
