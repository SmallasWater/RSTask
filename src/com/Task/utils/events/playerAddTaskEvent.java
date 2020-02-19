package com.Task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.Task.utils.Tasks.TaskFile;

/** */
public class playerAddTaskEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public TaskFile file;

    public playerAddTaskEvent(Player player, TaskFile taskFile){
        this.player = player;
        this.file = taskFile;
    }

    public TaskFile getFile() {
        return file;
    }
}
