package com.task.events;

import com.task.events.base.TaskEvent;
import com.task.tasks.PlayerFile;
import com.task.tasks.TaskFile;

/**
 * 玩家放弃任务事件
 * @author SmallasWater
 */
public class PlayerTaskCloseEvent extends TaskEvent {


    private final PlayerFile player;

    public PlayerTaskCloseEvent(PlayerFile player, TaskFile file){
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
