package dev.mrshawn.deathmessages.api.events;

import dev.mrshawn.deathmessages.enums.MessageType;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;


public class BroadcastDeathMessageEvent extends Event implements Cancellable {

    private final Player player;
    private final LivingEntity livingEntity;
    private final MessageType messageType;
    private final TextComponent textComponent;
    private final TextComponent[] textComponents; // 0: Prefix, 1: Message body
    private final boolean isGangDeath;
    private final List<World> broadcastedWorlds;
    private boolean isCancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public BroadcastDeathMessageEvent(Player player, LivingEntity livingEntity, MessageType messageType, TextComponent textComponent, TextComponent[] textComponents,
                                      List<World> broadcastedWorlds, boolean isGangDeath) {
        this.player = player;
        this.livingEntity = livingEntity;
        this.messageType = messageType;
        this.textComponent = textComponent;
        this.textComponents = textComponents;
        this.broadcastedWorlds = broadcastedWorlds;
        this.isGangDeath = isGangDeath;
        this.isCancelled = false;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public LivingEntity getLivingEntity() {
        return this.livingEntity;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    // forRemoval = true, since = "1.4.21"
    @Deprecated
    public TextComponent getTextComponent() {
        return this.textComponent;
    }

    public TextComponent[] getTextComponents() {
        return this.textComponents;
    }

    public boolean isGangDeath() {
        return this.isGangDeath;
    }

    public List<World> getBroadcastedWorlds() {
        return this.broadcastedWorlds;
    }
}
