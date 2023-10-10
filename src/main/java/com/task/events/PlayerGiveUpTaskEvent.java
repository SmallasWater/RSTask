package com.task.events;


import cn.nukkit.Player;
import com.task.events.base.PlayerTaskEvent;
import com.task.utils.tasks.TaskFile;

/** 玩家放弃任务事件
 * @author SmallasWater*/
public class PlayerGiveUpTaskEvent extends PlayerTaskEvent {


    public PlayerGiveUpTaskEvent(Player player, TaskFile file) {
        super(player, file);
    }
}
