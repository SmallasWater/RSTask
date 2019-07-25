package com.Task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.Task.utils.Tasks.TaskFile;

/** 玩家放弃任务事件*/
public class playerGiveUpTaskEvent extends PlayerEvent{

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TaskFile file;


    public TaskFile getFile() {
        return file;
    }

    public Player getPlayer() {
        return player;
    }

    public playerGiveUpTaskEvent(TaskFile file,Player player){
        this.file = file;
        this.player = player;
    }
}
