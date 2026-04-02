package dev.mrshawn.deathmessages.nms.spigot.v1_12_2;

import com.cryptomorin.xseries.XMaterial;
import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import dev.mrshawn.deathmessages.nms.NMSAdaptor;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.nbt.api.BinaryTagHolder;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

public class NMSAdaptorImpl implements NMSAdaptor {

    private BukkitAudiences adventure;
    private Audience console;

    @Override
    public void sendMessage(CommandSender sender, Component message) {
        this.adventure().sender(sender).sendMessage(message);
    }

    @Override
    public void sendMessage(Player player, Component message) {
        this.adventure().player(player).sendMessage(message);
    }

    @Override
    public void sendConsoleMessage(Component message) {
        this.console.sendMessage(message);
    }

    @Override
    public void adventure(Object instance) {
        this.adventure = BukkitAudiences.create((Plugin) instance);
        this.console = adventure.console();
    }

    @Override
    public void shutdownAdventure() {
        if (this.adventure != null) {
            this.adventure.close();
            this.adventure = null;
            this.console = null;
        }
    }

    @Override
    public BukkitAudiences adventure() {
        if (this.adventure == null) {
            throw new IllegalStateException("Tried to access Adventure when the plugin was disabled!");
        }

        return this.adventure;
    }

    @Override
    public Component itemDisplayName(ItemStack i) {
        // Only legacy color code exists for <= 1.16.5
        return LegacyComponentSerializer.legacyAmpersand().deserialize(i.getItemMeta().getDisplayName());
    }

    @Override
    public HoverEvent<HoverEvent.ShowItem> itemHoverEvent(ItemStack i) {
        //noinspection PatternValidation
        final String iNamespace = XMaterial.matchXMaterial(i.getType().name()).get().name().toLowerCase();
        //noinspection PatternValidation - Dreeam: we can sure that this namespace above can build a valid adventure key
        final Key itemKey = Key.key(iNamespace);
        final ReadWriteNBT nbt = NBT.itemStackToNBT(i).getCompound("tag");

        return i.hasItemMeta() && nbt != null && !nbt.toString().isEmpty()
                // Item with NBT
                // TODO: Note - Not working on Spigot 1.20.5+ I guess
                ? HoverEvent.showItem(itemKey, i.getAmount(), BinaryTagHolder.binaryTagHolder(nbt.toString()))
                // Item without NBT (tag compound)
                : HoverEvent.showItem(itemKey, i.getAmount());
    }

    @Override
    public Component entityCustomName(Entity entity) {
        final String customName = entity.getCustomName();
        return customName == null ? null : Component.text(entity.getCustomName());
    }

    @Override
    public boolean showDeathMessages(World world) {
        // Deprecated since Spigot 1.13
        return world.getGameRuleValue("showDeathMessages").equals("true");
    }

    @Override
    public void showDeathMessages(World world, boolean show) {
        // Deprecated since Spigot 1.13
        world.setGameRuleValue("showDeathMessages", String.valueOf(show));
    }

    @Override
    public String biomeKeyName(Biome biome) {
        // Deprecated since 1.21.3, Planned to remove since 1.22
        return biome.name();
    }
}
