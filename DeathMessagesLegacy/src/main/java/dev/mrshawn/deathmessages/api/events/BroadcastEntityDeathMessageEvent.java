package dev.mrshawn.deathmessages.api.events;

import dev.mrshawn.deathmessages.api.PlayerManager;
import dev.mrshawn.deathmessages.enums.MessageType;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;


public class BroadcastEntityDeathMessageEvent extends Event implements Cancellable {

    // The killer
    private final PlayerManager player;
    // The entity that was killed by a player
    private final Entity entity;
    private final MessageType messageType;
    private final TextComponent textComponent;
    private final TextComponent[] textComponents; // 0: Prefix, 1: Message body
    private final List<World> broadcastedWorlds;
    private boolean isCancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public BroadcastEntityDeathMessageEvent(PlayerManager pm, Entity entity, MessageType messageType, TextComponent textComponent, TextComponent[] textComponents,
                                            List<World> broadcastedWorlds) {
        this.player = pm;
        this.entity = entity;
        this.messageType = messageType;
        this.textComponent = textComponent;
        this.textComponents = textComponents;
        this.broadcastedWorlds = broadcastedWorlds;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return this.isCancelled;
    }

    @Override
    public void setCancelled(boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    @Override
    public @NotNull HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlayerManager getPlayer() {
        return this.player;
    }

    public Entity getEntity() {
        return this.entity;
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

    public List<World> getBroadcastedWorlds() {
        return this.broadcastedWorlds;
    }
}
