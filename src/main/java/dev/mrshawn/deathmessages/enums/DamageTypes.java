package dev.mrshawn.deathmessages.enums;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum DamageTypes {

	BED("Bed"),
	RESPAWN_ANCHOR("Respawn-Anchor"),
	PROJECTILE_ARROW("Projectile-Arrow"),
	PROJECTILE_DRAGON_FIREBALL("Projectile-Dragon-Fireball"),
	PROJECTILE_EGG("Projectile-Egg"),
	PROJECTILE_ENDERPEARL("Projectile-EnderPearl"),
	PROJECTILE_FIREBALL("Projectile-Fireball"),
	PROJECTILE_FISHHOOK("Projectile-FishHook"),
	PROJECTILE_LLAMASPIT("Projectile-LlamaSpit"),
	PROJECTILE_SNOWBALL("Projectile-Snowball"),
	PROJECTILE_TRIDENT("Projectile-Trident"),
	PROJECTILE_WITHERSKULL("Projectile-WitherSkull"),
	PROJECTILE_SHULKERBULLET("Projectile-ShulkerBullet"),
	CONTACT("Contact"),
	MELEE("Melee"),
	SUFFOCATION("Suffocation"),
	FALL("Fall"),
	CLIMBABLE("Climbable"),
	FIRE("Fire"),
	FIRE_TICK("Fire-Tick"),
	MELTING("Melting"),
	LAVA("Lava"),
	DROWNING("Drowning"),
	EXPLOSION("Explosion"),
	TNT("Tnt"),
	FIREWORK("Firework"),
	END_CRYSTAL("End-Crystal"),
	VOID("Void"),
	LIGHTNING("Lightning"),
	SUICIDE("Suicide"),
	STARVATION("Starvation"),
	POISON("Poison"),
	MAGIC("Magic"),
	WITHER("Wither"),
	FALLING_BLOCK("Falling-Block"),
	DRAGON_BREATH("Dragon-Breath"),
	CUSTOM("Custom"),
	FLY_INTO_WALL("Fly-Into-Wall"),
	HOT_FLOOR("Hot-Floor"),
	CRAMMING("Cramming"),
	DRYOUT("Dryout"),
	UNKNOWN("Unknown"),
	COMBATLOGX_QUIT("CombatLogX-Quit");

	private final String name;

	DamageTypes(String name) {
		this.name = name;
	}

	public static List<String> getFriendlyNames() {
		return Arrays.stream(DamageTypes.values())
				.map(type -> type.name)
				.collect(Collectors.toList());
	}
}
