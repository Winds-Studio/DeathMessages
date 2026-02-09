package dev.mrshawn.deathmessages.api.events;

import dev.mrshawn.deathmessages.api.PlayerCtx;
import dev.mrshawn.deathmessages.enums.MessageType;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;

public class BroadcastEntityDeathMessageEvent extends Event implements Cancellable {

    // The killer context
    private final PlayerCtx playerCtx;
    // The entity that was killed by a player
    private final Entity entity;
    private final MessageType messageType;
    // 0: Prefix, 1: Message body
    private final TextComponent[] textComponents;
    private final List<World> broadcastedWorlds;
    private boolean isCancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public BroadcastEntityDeathMessageEvent(PlayerCtx playerCtx, Entity entity, MessageType messageType, TextComponent[] textComponents,
                                            List<World> broadcastedWorlds) {
        this.playerCtx = playerCtx;
        this.entity = entity;
        this.messageType = messageType;
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
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public PlayerCtx getPlayerContext() {
        return this.playerCtx;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public MessageType getMessageType() {
        return this.messageType;
    }

    public TextComponent[] getTextComponents() {
        return this.textComponents;
    }

    public List<World> getBroadcastedWorlds() {
        return this.broadcastedWorlds;
    }
}
