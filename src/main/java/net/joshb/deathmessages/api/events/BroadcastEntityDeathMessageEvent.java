package net.joshb.deathmessages.api.events;

import net.joshb.deathmessages.api.PlayerManager;
import net.joshb.deathmessages.enums.MessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.List;


public class BroadcastEntityDeathMessageEvent extends Event implements Cancellable {

    //The killer
    private final PlayerManager player;
    //The entity that was killed by a player
    private final Entity entity;
    private final MessageType messageType;
    private final TextComponent textComponent;
    private final List<World> broadcastedWorlds;
    private boolean isCancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public BroadcastEntityDeathMessageEvent(PlayerManager pm, Entity entity, MessageType messageType, TextComponent textComponent,
                                            List<World> broadcastedWorlds) {
        this.player = pm;
        this.entity = entity;
        this.messageType = messageType;
        this.textComponent = textComponent;
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

    public PlayerManager getPlayer() {
        return this.player;
    }

    public Entity getEntity() { return this.entity; }

    public MessageType getMessageType(){ return this.messageType; }

    public TextComponent getTextComponent() {
        return this.textComponent;
    }

    public List<World> getBroadcastedWorlds(){
        return this.broadcastedWorlds;
    }

}
