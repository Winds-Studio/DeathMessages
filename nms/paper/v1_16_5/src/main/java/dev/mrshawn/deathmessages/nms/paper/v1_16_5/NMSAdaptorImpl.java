package dev.mrshawn.deathmessages.nms.paper.v1_16_5;

import dev.mrshawn.deathmessages.nms.NMSAdaptor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class NMSAdaptorImpl implements NMSAdaptor {

    private static final ConsoleCommandSender CONSOLE = Bukkit.getConsoleSender();

    @Override
    public void sendMessage(CommandSender sender, Component message) {
        sender.sendMessage(message);
    }

    @Override
    public void sendMessage(Player player, Component message) {
        player.sendMessage(message);
    }

    @Override
    public void sendConsoleMessage(Component message) {
        CONSOLE.sendMessage(message);
    }

    @Override
    public void adventure(Object instance) {
    }

    @Override
    public void shutdownAdventure() {
    }

    @Override
    public BukkitAudiences adventure() {
        return null;
    }

    @Override
    public Component itemDisplayName(ItemStack i) {
        return i.getItemMeta().displayName();
    }

    @Override
    public HoverEvent<HoverEvent.ShowItem> itemHoverEvent(ItemStack i) {
        return i.asHoverEvent();
    }

    @Override
    public Component entityCustomName(Entity entity) {
       return entity.customName();
    }

    @Override
    public boolean showDeathMessages(World world) {
        return Boolean.TRUE.equals(world.getGameRuleValue(GameRule.SHOW_DEATH_MESSAGES));
    }

    @Override
    public void showDeathMessages(World world, boolean show) {
        world.setGameRule(GameRule.SHOW_DEATH_MESSAGES, show);
    }

    @Override
    public String biomeKeyName(Biome biome) {
        // Deprecated since 1.21.3, Planned to remove since 1.22
        return biome.name();
    }
}
