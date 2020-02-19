package com.Task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

import com.Task.utils.Tasks.TaskItems.playerTask;

/** 玩家执行任务事件 */
public class useTaskEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private playerTask taskItem;

    public useTaskEvent(Player player,playerTask item){
        this.player = player;
        this.taskItem = item;
    }

    public playerTask getTaskItem() {
        return taskItem;
    }
}
