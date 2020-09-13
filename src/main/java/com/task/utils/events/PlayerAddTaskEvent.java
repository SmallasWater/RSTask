package com.task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.task.utils.events.base.PlayerTaskEvent;
import com.task.utils.tasks.TaskFile;

/**
 * 玩家领取任务事件
 * @author SmallasWater */
public class PlayerAddTaskEvent extends PlayerTaskEvent {

    public PlayerAddTaskEvent(Player player, TaskFile file) {
        super(player, file);
    }
}
