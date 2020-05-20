package com.task.utils.events;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

import com.task.utils.tasks.TaskFile;

/**
 * 判断是否可领取的时候触发事件
 * @author SmallasWater
 */
public class PlayerCanInviteTaskEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private TaskFile task;

    private String playerName;

    public PlayerCanInviteTaskEvent(String playerName,TaskFile task){
        this.playerName = playerName;
        this.task = task;
    }

    public String getPlayerName() {
        return playerName;
    }

    public TaskFile getTask() {
        return task;
    }
}
