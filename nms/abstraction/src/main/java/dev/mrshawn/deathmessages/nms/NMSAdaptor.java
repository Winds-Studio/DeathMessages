package dev.mrshawn.deathmessages.nms;

import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface NMSAdaptor {

    void sendMessage(CommandSender sender, Component message);

    void sendMessage(Player player, Component message);

    void sendConsoleMessage(Component message);

    void adventure(Object instance);

    void shutdownAdventure();

    BukkitAudiences adventure();

    Component itemDisplayName(ItemStack i);

    HoverEvent<HoverEvent.ShowItem> itemHoverEvent(ItemStack i);

    Component entityCustomName(Entity entity);

    boolean showDeathMessages(World world);

    void showDeathMessages(World world, boolean show);

    String biomeKeyName(Biome biome);

    //Material getFallingBlockMaterial();
}
