package com.task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

/** 玩家完成任务事件
 * @author SmallasWater*/
public class SuccessTaskEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private final String taskName;

    public SuccessTaskEvent(Player player, String taskName){
        this.player = player;
        this.taskName = taskName;
    }

    /**
     * 返回任务名称
     * @return 任务名
     * */
    public String getTaskName() {
        return taskName;
    }
}
