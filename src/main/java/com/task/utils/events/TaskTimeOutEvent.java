package com.task.utils.events;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.task.utils.events.base.TaskEvent;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.tasks.TaskFile;

/**
 * 任务超时事件
 * @author SmallasWater
 */
public class TaskTimeOutEvent extends TaskEvent {


    private final PlayerFile player;

    public TaskTimeOutEvent(PlayerFile player, TaskFile file){
        super(file);
        this.player = player;
    }

    /**
     * 获取超时的任务
     * @return {@link TaskFile}
     * */
    @Override
    public TaskFile getFile() {
        return super.getFile();
    }

    /**
     * 获取超时的玩家类
     * @return {@link PlayerFile}
     * */
    public PlayerFile getPlayer() {
        return player;
    }
}
