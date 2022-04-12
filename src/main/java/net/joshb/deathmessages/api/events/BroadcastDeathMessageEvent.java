package net.joshb.deathmessages.api.events;

import net.joshb.deathmessages.enums.MessageType;
import net.md_5.bungee.api.chat.TextComponent;
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
    private final boolean isGangDeath;
    private final List<World> broadcastedWorlds;
    private boolean isCancelled;

    private static final HandlerList HANDLERS = new HandlerList();

    public BroadcastDeathMessageEvent(Player player, LivingEntity livingEntity, MessageType messageType, TextComponent textComponent,
                                      List<World> broadcastedWorlds, boolean isGangDeath) {
        this.player = player;
        this.livingEntity = livingEntity;
        this.messageType = messageType;
        this.textComponent = textComponent;
        this.isGangDeath = isGangDeath;
        this.broadcastedWorlds = broadcastedWorlds;
        this.isCancelled = false;
    }

    @Override
    public HandlerList getHandlers() {
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

    public LivingEntity getLivingEntity(){
        return this.livingEntity;
    }

    public MessageType getMessageType(){ return this.messageType; }

    public TextComponent getTextComponent() {
        return this.textComponent;
    }

    public boolean isGangDeath(){
        return this.isGangDeath;
    }

    public List<World> getBroadcastedWorlds(){
        return this.broadcastedWorlds;
    }
}
