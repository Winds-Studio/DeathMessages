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
import dev.mrshawn.deathmessages.enums.MobType;
import dev.mrshawn.deathmessages.enums.PDMode;
import dev.mrshawn.deathmessages.files.Config;
import dev.mrshawn.deathmessages.files.FileSettings;
import dev.mrshawn.deathmessages.kotlin.files.FileStore;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Assets {

	private static final FileSettings<Config> config = FileStore.INSTANCE.getCONFIG();

	private static final boolean addPrefix = config.getBoolean(Config.ADD_PREFIX_TO_ALL_MESSAGES);

	public static boolean isNumeric(String s) {
		for (char c : s.toCharArray()) {
			if (Character.isDigit(c))
				return true;
		}
		return false;
	}

	public static final HashMap<String, String> addingMessage = new HashMap<>();

	public static String formatMessage(String path) {
		return Messages.getInstance().getConfig().getString(path)
				.replaceAll("%prefix%", Messages.getInstance().getConfig().getString("Prefix"));
	}

	public static String formatString(String string) {
		return string
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

	public static TextComponent convertLegacy(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
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
		String displayName = LegacyComponentSerializer.legacyAmpersand().serialize(itemStack.getItemMeta().displayName());

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
			// Bed kill
			ExplosionManager explosionManager = ExplosionManager.getManagerIfEffected(pm.getUUID());
			PlayerManager pyro = PlayerManager.getPlayer(explosionManager.getPyro());
			if (explosionManager.getMaterial().name().contains("BED")) {
				if (pyro != null) return get(gang, pm, pyro.getPlayer(), "Bed");
			}
			// Respawn Anchor kill
			if (DeathMessages.majorVersion() >= 16 && explosionManager.getMaterial().equals(Material.RESPAWN_ANCHOR)) {
				if (pyro != null) return get(gang, pm, pyro.getPlayer(), "Respawn-Anchor");
			}
		}
		if (hasWeapon) {
			if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.ENTITY_ATTACK)) {
				return getWeapon(gang, pm, mob);
			} else if (pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE) && pm.getLastProjectileEntity() instanceof Arrow) {
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
		PlayerManager pm = em.getLastPlayerDamager();
		Player p = pm.getPlayer();
		boolean hasWeapon = hasWeapon(p, pm.getLastDamage());

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
			// Bed kill
			ExplosionManager explosionManager = ExplosionManager.getManagerIfEffected(em.getEntityUUID());
			if (explosionManager.getMaterial().name().contains("BED")) {
				PlayerManager pyro = PlayerManager.getPlayer(explosionManager.getPyro());
				return getEntityDeath(pyro.getPlayer(), em.getEntity(), "Bed", mobType);
			}
			// Respawn Anchor kill
			if (DeathMessages.majorVersion() >= 16 && explosionManager.getMaterial().equals(Material.RESPAWN_ANCHOR)) {
				PlayerManager pyro = PlayerManager.getPlayer(explosionManager.getPyro());
				return getEntityDeath(pyro.getPlayer(), em.getEntity(), "Respawn-Anchor", mobType);
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
		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		TextComponent.Builder tc = Component.text();
		if (addPrefix) {
			TextComponent prefix = Assets.convertLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			tc.append(prefix);
		}
		if (msg.contains("%block%") && pm.getLastEntityDamager() instanceof FallingBlock) {
			try {
				FallingBlock fb = (FallingBlock) pm.getLastEntityDamager();
				String material = fb.getBlockData().getMaterial().toString().toLowerCase();
				String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);
				String mssa = msg.replaceAll("%block%", configValue);
				tc.append(Assets.convertLegacy(mssa));
			} catch (NullPointerException e) {
				DeathMessages.getInstance().getLogger().severe("Could not parse %block%. Please check your config for a wrong value." +
						" Your materials could be spelt wrong or it does not exists in the config. If this problem persist, contact support" +
						" on the discord https://discord.gg/dhJnq7R");
				pm.setLastEntityDamager(null);
				return getNaturalDeath(pm, getSimpleCause(EntityDamageEvent.DamageCause.SUFFOCATION));
			}
		} else if (msg.contains("%climbable%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.FALL)) {
			try {
				String material = pm.getLastClimbing().toString().toLowerCase();
				String configValue = Messages.getInstance().getConfig().getString("Blocks." + material);
				String mssa = msg.replaceAll("%climbable%", configValue);
				tc.append(Assets.convertLegacy(mssa));
			} catch (NullPointerException e) {
				DeathMessages.getInstance().getLogger().severe("Could not parse %climbable%. Please check your config for a wrong value." +
						" Your materials could be spelt wrong or it does not exists in the config. If this problem persist, contact support" +
						" on the discord https://discord.gg/dhJnq7R - Parsed block: " + pm.getLastClimbing().toString());
				pm.setLastClimbing(null);
				return getNaturalDeath(pm, getSimpleCause(EntityDamageEvent.DamageCause.FALL));
			}
		} else if (msg.contains("%weapon%") && pm.getLastDamage().equals(EntityDamageEvent.DamageCause.PROJECTILE)) {
			ItemStack i = pm.getPlayer().getEquipment().getItemInMainHand();

			if (!i.getType().equals(XMaterial.BOW.parseMaterial())) {
				return getNaturalDeath(pm, "Projectile-Unknown");
			}
			if (DeathMessages.majorVersion() >= 14 && !i.getType().equals(XMaterial.CROSSBOW.parseMaterial())) {
				return getNaturalDeath(pm, "Projectile-Unknown");
			}
			String displayName;
			if (i.getItemMeta() != null && !i.getItemMeta().hasDisplayName() || i.getItemMeta().displayName() == Component.empty()) {
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
				displayName = LegacyComponentSerializer.legacyAmpersand().serialize(i.displayName());
			}
			String[] spl = msg.split("%weapon%");
			if (spl.length != 0 && spl[0] != null && !spl[0].isEmpty()) {
				displayName = spl[0] + displayName;
			}
			if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].isEmpty()) {
				displayName = displayName + spl[1];
			}
			HoverEvent.ShowItem hoverEventComponents = HoverEvent.ShowItem.showItem(i.getType().key(), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().getAsString()));
			tc.append(Assets.convertLegacy(displayName).hoverEvent(HoverEvent.showItem(hoverEventComponents)));
		} else {
			TextComponent tx = Assets.convertLegacy(playerDeathPlaceholders(msg, pm, null) + " ");
			tc.append(tx);
		}
		// TODO: need to re-write the logic of death message click event & hover text.
//		if (msg.length() >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertLegacy(playerDeathPlaceholders(msg[1], pm, null))));
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
		return tc.build();
	}

	public static TextComponent getWeapon(boolean gang, PlayerManager pm, LivingEntity mob) {
		final boolean basicMode = PlayerDeathMessages.getInstance().getConfig().getBoolean("Basic-Mode.Enabled");
		final String cMode = basicMode ? PDMode.BASIC_MODE.getValue() : PDMode.MOBS.getValue()
				+ "." + mob.getType().getEntityClass().getSimpleName().toLowerCase();
		final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();
		//List<String> msgs = sortList(getPlayerDeathMessages().getStringList(cMode + "." + affiliation + ".Weapon"), pm.getPlayer());

		//Bukkit.broadcastMessage(DeathMessages.getInstance().mythicmobsEnabled + " - " + DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId()));
		List<String> msgs;
		if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
			String internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
			//Bukkit.broadcastMessage("is myth - " + internalMobType);
			msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + internalMobType + "." + affiliation + ".Weapon"), pm.getPlayer(), mob);
		} else {
			msgs = sortList(getPlayerDeathMessages().getStringList(cMode + "." + affiliation + ".Weapon"), pm.getPlayer(), mob);
		}

		if (msgs.isEmpty()) return null;
		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		TextComponent.Builder tc = Component.text();
		if (addPrefix) {
			TextComponent tx = Assets.convertLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			tc.append(tx);
		}
		if (msg.contains("%weapon%")) {
			ItemStack i = mob.getEquipment().getItemInMainHand();
			String displayName;
			if ((i.getItemMeta() != null) && !i.getItemMeta().hasDisplayName() || i.getItemMeta().displayName() == Component.empty()) {
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
				displayName = LegacyComponentSerializer.legacyAmpersand().serialize(i.displayName());
			}
			String[] spl = msg.split("%weapon%");
			if (spl.length != 0 && spl[0] != null && !spl[0].isEmpty()) {
				displayName = spl[0] + displayName;
			}
			if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].isEmpty()) {
				displayName = displayName + spl[1];
			}
			HoverEvent.ShowItem hoverEventComponents = HoverEvent.ShowItem.showItem(i.getType().key(), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().getAsString()));
			tc.append(Assets.convertLegacy(displayName).hoverEvent(HoverEvent.showItem(hoverEventComponents)));
		} else {
			TextComponent tx = Assets.convertLegacy(playerDeathPlaceholders(msg, pm, mob) + " ");
			tc.append(tx);
		}
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertLegacy(playerDeathPlaceholders(sec[1], pm, mob))));
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
		return tc.build();
	}

	public static TextComponent getEntityDeathWeapon(Player p, Entity e, MobType mobType) {
		String entityName = e.getType().getEntityClass().getSimpleName().toLowerCase();
		List<String> msgs;
		if (mobType.equals(MobType.MYTHIC_MOB)) {
			String internalMobType = null;
			if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(e.getUniqueId())) {
				internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(e).getMobType();
			} else {
				// reserved
			}
			msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + internalMobType + ".Weapon"), p, e);
		} else {
			msgs = sortList(getEntityDeathMessages().getStringList("Entities." + entityName + ".Weapon"), p, e);
		}

		if (msgs.isEmpty()) return null;
		boolean hasOwner = false;
		if (e instanceof Tameable tameable) {
			if (tameable.getOwner() != null) hasOwner = true;
		}

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		TextComponent.Builder tc = Component.text();
		if (addPrefix) {
			TextComponent tx = Assets.convertLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			tc.append(tx);
		}
		if (msg.contains("%weapon%")) {
			ItemStack i = p.getEquipment().getItemInMainHand();
			String displayName;
			if ((i.getItemMeta() != null) && !i.getItemMeta().hasDisplayName() || i.getItemMeta().displayName() == Component.empty()) {
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
				displayName = LegacyComponentSerializer.legacySection().serialize(i.displayName());
			}
			String[] spl = msg.split("%weapon%");
			if (spl.length != 0 && spl[0] != null && !spl[0].isEmpty()) {
				displayName = spl[0] + displayName;
			}
			if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].isEmpty()) {
				displayName = displayName + spl[1];
			}
			HoverEvent.ShowItem hoverEventComponents = HoverEvent.ShowItem.showItem(i.getType().key(), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().getAsString()));
			tc.append(Assets.convertLegacy(displayName).hoverEvent(HoverEvent.showItem(hoverEventComponents)));
		} else {
			TextComponent tx = Assets.convertLegacy(entityDeathPlaceholders(msg, p, e, hasOwner) + " ");
			tc.append(tx);
		}
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertLegacy(entityDeathPlaceholders(sec[1], p, e, hasOwner))));
//		}
//		if (sec.length == 3) {
//			if (sec[2].startsWith("COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.runCommand("/" + entityDeathPlaceholders(cmd, p, e, hasOwner)));
//			} else if (sec[2].startsWith("SUGGEST_COMMAND:")) {
//				String cmd = sec[2].split(":")[1];
//				tc.clickEvent(ClickEvent.suggestCommand("/" + entityDeathPlaceholders(cmd, p, e, hasOwner)));
//			}
		return tc.build();
	}

	public static TextComponent get(boolean gang, PlayerManager pm, LivingEntity mob, String damageCause) {
		final boolean basicMode = PlayerDeathMessages.getInstance().getConfig().getBoolean("Basic-Mode.Enabled");
		final String cMode = basicMode ? PDMode.BASIC_MODE.getValue() : PDMode.MOBS.getValue()
				+ "." + mob.getType().getEntityClass().getSimpleName().toLowerCase();
		final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();

		// List<String> msgs = sortList(getPlayerDeathMessages().getStringList(cMode + "." + affiliation + "." + damageCause), pm.getPlayer());

		List<String> msgs;
		if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
			String internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
			//Bukkit.broadcastMessage("is myth - " + internalMobType);
			msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + internalMobType + "." + affiliation + "." + damageCause), pm.getPlayer(), mob);
		} else {
			msgs = sortList(getPlayerDeathMessages().getStringList(cMode + "." + affiliation + "." + damageCause), pm.getPlayer(), mob);
		}

		if (msgs.isEmpty()) {
			if (config.getBoolean(Config.DEFAULT_NATURAL_DEATH_NOT_DEFINED))
				return getNaturalDeath(pm, damageCause);
			if (config.getBoolean(Config.DEFAULT_MELEE_LAST_DAMAGE_NOT_DEFINED))
				return get(gang, pm, mob, getSimpleCause(EntityDamageEvent.DamageCause.ENTITY_ATTACK));
			return null;
		}

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		TextComponent.Builder tc = Component.text();
		if (addPrefix) {
			TextComponent tx = Assets.convertLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			tc.append(tx);
		}
		TextComponent tx = Assets.convertLegacy(playerDeathPlaceholders(msg, pm, mob) + " ");
		tc.append(tx);
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertLegacy(playerDeathPlaceholders(sec[1], pm, mob))));
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
		return tc.build();
	}

	public static TextComponent getProjectile(boolean gang, PlayerManager pm, LivingEntity mob, String projectileDamage) {
		final boolean basicMode = PlayerDeathMessages.getInstance().getConfig().getBoolean("Basic-Mode.Enabled");
		final String cMode = basicMode ? PDMode.BASIC_MODE.getValue() : PDMode.MOBS.getValue() + "." + LegacyComponentSerializer.legacyAmpersand().serialize(mob.customName()).toLowerCase();
		final String affiliation = gang ? DeathAffiliation.GANG.getValue() : DeathAffiliation.SOLO.getValue();

		//List<String> msgs = sortList(getPlayerDeathMessages().getStringList(cMode + "." + affiliation + "." + projectileDamage), pm.getPlayer());

		List<String> msgs;
		if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(mob.getUniqueId())) {
			String internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(mob).getMobType();
			msgs = sortList(getPlayerDeathMessages().getStringList("Custom-Mobs.Mythic-Mobs." + internalMobType + "." + affiliation + "." + projectileDamage), pm.getPlayer(), mob);
		} else {
			msgs = sortList(getPlayerDeathMessages().getStringList(cMode + "." + affiliation + "." + projectileDamage), pm.getPlayer(), mob);
		}
		if (msgs.isEmpty()) {
			return null;
		}
		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		TextComponent.Builder tc = Component.text();
		if (addPrefix) {
			TextComponent tx = Assets.convertLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			tc.append(tx);
		}
		if (msg.contains("%weapon%") && pm.getLastProjectileEntity() instanceof Arrow) {
			ItemStack i = mob.getEquipment().getItemInMainHand();
			String displayName;
			if ((i.getItemMeta() != null) && !i.getItemMeta().hasDisplayName() || i.getItemMeta().displayName() == Component.empty()) {
				if (config.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED)) {
					if (!config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO)
							.equals(projectileDamage)) {
						return getProjectile(gang, pm, mob, config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO));
					}
				}
				displayName = Assets.convertString(i.getType().name());
			} else {
				displayName = LegacyComponentSerializer.legacySection().serialize(i.displayName());
			}
			String[] spl = msg.split("%weapon%");
			if (spl.length != 0 && spl[0] != null && !spl[0].isEmpty()) {
				displayName = spl[0] + "&r" + displayName;
			}
			if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].isEmpty()) {
				displayName = displayName + "&r" + spl[1];
			}
			HoverEvent.ShowItem hoverEventComponents = HoverEvent.ShowItem.showItem(i.getType().key(), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().getAsString()));
			tc.append(Assets.convertLegacy(displayName).hoverEvent(HoverEvent.showItem(hoverEventComponents)));
		} else {
			TextComponent tx = Assets.convertLegacy(playerDeathPlaceholders(msg, pm, mob) + " ");
			tc.append(tx);
		}
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertLegacy(playerDeathPlaceholders(sec[1], pm, mob))));
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
		return tc.build();
	}

	public static TextComponent getEntityDeathProjectile(Player p, EntityManager em, String projectileDamage, MobType mobType) {
		String entityName = em.getEntity().getType().getEntityClass().getSimpleName().toLowerCase();
		List<String> msgs;
		if (mobType.equals(MobType.MYTHIC_MOB)) {
			String internalMobType = null;
			if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(em.getEntityUUID())) {
				internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(em.getEntity()).getMobType();
			}
			msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + internalMobType + "." + projectileDamage), p, em.getEntity());
		} else {
			msgs = sortList(getEntityDeathMessages().getStringList("Entities." + entityName + "." + projectileDamage), p, em.getEntity());
		}
		if (msgs.isEmpty()) {
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
		TextComponent.Builder tc = Component.text();
		if (addPrefix) {
			TextComponent tx = Assets.convertLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			tc.append(tx);
		}
		if (msg.contains("%weapon%") && em.getLastProjectileEntity() instanceof Arrow) {
			ItemStack i = p.getEquipment().getItemInMainHand();
			String displayName;
			if ((i.getItemMeta() != null) && !i.getItemMeta().hasDisplayName() || i.getItemMeta().displayName() == Component.empty()) {
				if (config.getBoolean(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_ENABLED)) {
					if (!config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO)
							.equals(projectileDamage)) {
						return getEntityDeathProjectile(p, em,
								config.getString(Config.DISABLE_WEAPON_KILL_WITH_NO_CUSTOM_NAME_SOURCE_PROJECTILE_DEFAULT_TO), mobType);
					}
				}
				displayName = Assets.convertString(i.getType().name());
			} else {
				displayName = LegacyComponentSerializer.legacySection().serialize(i.displayName());
			}
			String[] spl = msg.split("%weapon%");
			if (spl.length != 0 && spl[0] != null && !spl[0].isEmpty()) {
				displayName = spl[0] + "&r" + displayName;
			}
			if (spl.length != 0 && spl.length != 1 && spl[1] != null && !spl[1].isEmpty()) {
				displayName = displayName + "&r" + spl[1];
			}
			HoverEvent.ShowItem hoverEventComponents = HoverEvent.ShowItem.showItem(i.getType().key(), i.getAmount(), BinaryTagHolder.binaryTagHolder(i.getItemMeta().getAsString()));
			tc.append(Assets.convertLegacy(displayName).hoverEvent(HoverEvent.showItem(hoverEventComponents)));
		} else {
			TextComponent tx = Assets.convertLegacy(entityDeathPlaceholders(msg, p, em.getEntity(), hasOwner) + " ");
			tc.append(tx);
		}
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertLegacy(entityDeathPlaceholders(sec[1], p, em.getEntity(), hasOwner))));
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
		return tc.build();
	}

	public static TextComponent getEntityDeath(Player player, Entity entity, String damageCause, MobType mobType) {
		boolean hasOwner = false;
		if (entity instanceof Tameable tameable) {
			if (tameable.getOwner() != null) hasOwner = true;
		}
		List<String> msgs;
		if (hasOwner) {
			msgs = sortList(getEntityDeathMessages().getStringList("Entities." +
					entity.getName().toLowerCase() + ".Tamed"), player, entity);
		} else {
			if (mobType.equals(MobType.MYTHIC_MOB)) {
				String internalMobType = null;
				if (DeathMessages.getInstance().mythicmobsEnabled && DeathMessages.getInstance().mythicMobs.getAPIHelper().isMythicMob(entity.getUniqueId())) {
					internalMobType = DeathMessages.getInstance().mythicMobs.getAPIHelper().getMythicMobInstance(entity).getMobType();
				} else {
					// reserved
				}
				msgs = sortList(getEntityDeathMessages().getStringList("Mythic-Mobs-Entities." + internalMobType + "." + damageCause), player, entity);
			} else {
				msgs = sortList(getEntityDeathMessages().getStringList("Entities." +
						entity.getType().getEntityClass().getSimpleName().toLowerCase() + "." + damageCause), player, entity);
			}
		}
		if (msgs.isEmpty()) return null;

		String msg = msgs.get(ThreadLocalRandom.current().nextInt(msgs.size()));
		TextComponent.Builder tc = Component.text();
		if (addPrefix) {
			TextComponent tx = Assets.convertLegacy(Messages.getInstance().getConfig().getString("Prefix"));
			tc.append(tx);
		}
		TextComponent tx = Assets.convertLegacy(entityDeathPlaceholders(msg, player, entity, hasOwner) + " ");
		tc.append(tx);
//		if (sec.length >= 2) {
//			tc.hoverEvent(HoverEvent.showText(Assets.convertLegacy(entityDeathPlaceholders(sec[1], player, entity, hasOwner))));
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
		return tc.build();
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
					if (DeathMessages.worldGuardExtension == null) {
						continue;
					}
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
				.replaceAll("%entity_display%", entity.customName() == null ? Messages.getInstance().getConfig().getString("Mobs."
						+ entity.getType().toString().toLowerCase()) : LegacyComponentSerializer.legacySection().serialize(entity.customName()))
				.replaceAll("%killer%", player.getName())
				.replaceAll("%killer_display%", LegacyComponentSerializer.legacySection().serialize(player.displayName()))
				.replaceAll("%world%", entity.getLocation().getWorld().getName())
				.replaceAll("%world_environment%", getEnvironment(entity.getLocation().getWorld().getEnvironment()))
				.replaceAll("%x%", String.valueOf(entity.getLocation().getBlock().getX()))
				.replaceAll("%y%", String.valueOf(entity.getLocation().getBlock().getY()))
				.replaceAll("%z%", String.valueOf(entity.getLocation().getBlock().getZ()));
		if (owner) {
			if (entity instanceof Tameable tameable) {
				if (tameable.getOwner() != null && tameable.getOwner().getName() != null) {
					msg = msg.replaceAll("%owner%", tameable.getOwner().getName());
				}
			}
		}
		try {
			msg = msg.replaceAll("%biome%", entity.getLocation().getBlock().getBiome().name());
		} catch (NullPointerException e) {
			DeathMessages.getInstance().getLogger().severe("Custom Biome detected. Using 'Unknown' for a biome name.");
			DeathMessages.getInstance().getLogger().severe("Custom Biomes are not supported yet.'");
			msg = msg.replaceAll("%biome%", "Unknown");
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
					.replaceAll("%player_display%", LegacyComponentSerializer.legacySection().serialize(pm.getPlayer().displayName()))
					.replaceAll("%world%", pm.getLastLocation().getWorld().getName())
					.replaceAll("%world_environment%", getEnvironment(pm.getLastLocation().getWorld().getEnvironment()))
					.replaceAll("%x%", String.valueOf(pm.getLastLocation().getBlock().getX()))
					.replaceAll("%y%", String.valueOf(pm.getLastLocation().getBlock().getY()))
					.replaceAll("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ()));
			try {
				msg = msg.replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name());
			} catch (NullPointerException e) {
				DeathMessages.getInstance().getLogger().severe("Custom Biome detected. Using 'Unknown' for a biome name.");
				DeathMessages.getInstance().getLogger().severe("Custom Biomes are not supported yet.'");
				msg = msg.replaceAll("%biome%", "Unknown");
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
					.replaceAll("%player_display%", LegacyComponentSerializer.legacySection().serialize(pm.getPlayer().displayName()))
					.replaceAll("%killer%", mobName)
					.replaceAll("%killer_type%", Messages.getInstance().getConfig().getString("Mobs."
							+ mob.getType().toString().toLowerCase()))
					.replaceAll("%world%", pm.getLastLocation().getWorld().getName())
					.replaceAll("%world_environment%", getEnvironment(pm.getLastLocation().getWorld().getEnvironment()))
					.replaceAll("%x%", String.valueOf(pm.getLastLocation().getBlock().getX()))
					.replaceAll("%y%", String.valueOf(pm.getLastLocation().getBlock().getY()))
					.replaceAll("%z%", String.valueOf(pm.getLastLocation().getBlock().getZ()));
			try {
				msg = msg.replaceAll("%biome%", pm.getLastLocation().getBlock().getBiome().name());
			} catch (NullPointerException e) {
				DeathMessages.getInstance().getLogger().severe("Custom Biome detected. Using 'Unknown' for a biome name.");
				DeathMessages.getInstance().getLogger().severe("Custom Biomes are not supported yet.'");
				msg = msg.replaceAll("%biome%", "Unknown");
			}

			if (mob instanceof Player p) {
				msg = msg.replaceAll("%killer_display%", LegacyComponentSerializer.legacySection().serialize(p.displayName()));
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
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < spl.length; i++) {
			if (i == spl.length - 1) {
				sb.append(StringUtils.capitalize(spl[i]));
			} else {
				sb.append(StringUtils.capitalize(spl[i])).append(" ");
			}
		}
		return sb.toString();
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
		} else if (projectile instanceof Trident) {
			return "Projectile-Trident";
		} else if (projectile instanceof ShulkerBullet) {
			return "Projectile-ShulkerBullet";
		} else {
			return "Projectile-Arrow";
		}
	}

	public static String getSimpleCause(EntityDamageEvent.DamageCause damageCause) {
		return switch (damageCause) {
//			case KILL -> "Kill";
//			case WORLD_BORDER -> "World-Border";
			case CONTACT -> "Contact";
			case ENTITY_ATTACK -> "Melee";
//			case ENTITY_SWEEP_ATTACK -> "Entity-Sweep-Attack";
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
