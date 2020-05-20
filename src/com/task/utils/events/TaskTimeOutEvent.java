package com.task.utils.events;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.PlayerFile;

/**
 * 任务超时事件
 * @author SmallasWater
 */
public class TaskTimeOutEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TaskFile file;

    private PlayerFile player;

    public TaskTimeOutEvent(PlayerFile player, TaskFile file){
        this.player = player;
        this.file = file;
    }

    /**
     * 获取超时的任务
     * @return {@link TaskFile}
     * */
    public TaskFile getFile() {
        return file;
    }

    /**
     * 获取超时的玩家类
     * @return {@link PlayerFile}
     * */
    public PlayerFile getPlayer() {
        return player;
    }
}
