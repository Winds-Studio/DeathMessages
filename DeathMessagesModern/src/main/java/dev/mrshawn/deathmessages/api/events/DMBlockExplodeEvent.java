package dev.mrshawn.deathmessages.api.events;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DMBlockExplodeEvent extends Event {

    private final Player player;
    private final Block block;

    private static final HandlerList HANDLERS = new HandlerList();

    public DMBlockExplodeEvent(Player pyro, Block block) {
        this.player = pyro;
        this.block = block;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public Player getPlayer() {
        return this.player;
    }

    public Block getBlock() {
        return this.block;
    }
}
