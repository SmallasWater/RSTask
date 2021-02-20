package com.task.events;

import com.task.events.base.TaskEvent;
import com.task.tasks.TaskFile;

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
