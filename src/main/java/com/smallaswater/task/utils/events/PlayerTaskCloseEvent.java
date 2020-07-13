package com.smallaswater.task.utils.events;

import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.smallaswater.task.utils.tasks.PlayerFile;
import com.smallaswater.task.utils.tasks.TaskFile;

/**
 * 玩家放弃任务事件
 * @author SmallasWater
 */
public class PlayerTaskCloseEvent extends Event {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private TaskFile file;

    private PlayerFile player;

    public PlayerTaskCloseEvent(PlayerFile player, TaskFile file){
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
