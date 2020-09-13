package com.task.utils.events;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;

import com.task.utils.events.base.TaskEvent;
import com.task.utils.tasks.TaskFile;

/**
 * 判断是否可领取的时候触发事件
 * @author SmallasWater
 */
public class PlayerCanInviteTaskEvent extends TaskEvent {


    private final String playerName;

    public PlayerCanInviteTaskEvent(String playerName,TaskFile task){
        super(task);
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }

}
