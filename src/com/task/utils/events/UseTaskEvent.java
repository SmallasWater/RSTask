package com.task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

import com.task.utils.tasks.taskitems.PlayerTask;

/** 玩家执行任务事件
 * @author SmallasWater*/
public class UseTaskEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private PlayerTask taskItem;

    public UseTaskEvent(Player player, PlayerTask item){
        this.player = player;
        this.taskItem = item;
    }

    /**
     * 返回玩家任务
     * @return {@link PlayerTask}
     * */
    public PlayerTask getTaskItem() {
        return taskItem;
    }
}
