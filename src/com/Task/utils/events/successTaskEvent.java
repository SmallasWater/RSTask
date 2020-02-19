package com.Task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

/** 玩家完成任务事件*/
public class successTaskEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private String taskName;

    public successTaskEvent(Player player,String taskName){
        this.player = player;
        this.taskName = taskName;
    }

    public String getTaskName() {
        return taskName;
    }
}
