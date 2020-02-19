package com.Task.utils.events;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.Task.utils.Tasks.TaskItems.TaskBook;

public class playerOpenBookEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TaskBook bookWritten;

    public playerOpenBookEvent(Player player,TaskBook written){
        this.player = player;
        this.bookWritten = written;
    }

    public TaskBook getBookWritten() {
        return bookWritten;
    }

    @Override
    public Player getPlayer() {
        return super.getPlayer();
    }
}
