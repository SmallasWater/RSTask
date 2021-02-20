package com.task.events;


import cn.nukkit.Player;
import com.task.events.base.PlayerTaskEvent;
import com.task.tasks.TaskFile;

/**
 * 玩家领取任务事件
 * @author SmallasWater */
public class PlayerAddTaskEvent extends PlayerTaskEvent {

    public PlayerAddTaskEvent(Player player, TaskFile file) {
        super(player, file);
    }
}
