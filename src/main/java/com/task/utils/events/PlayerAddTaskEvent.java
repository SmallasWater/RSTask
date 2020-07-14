package com.task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.task.utils.tasks.TaskFile;

/**
 * 玩家领取任务事件
 * @author SmallasWater */
public class PlayerAddTaskEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    public TaskFile file;

    public PlayerAddTaskEvent(Player player, TaskFile taskFile){
        this.player = player;
        this.file = taskFile;
    }

    /**
     * 返回任务类
     * @return {@link TaskFile}
     * */
    public TaskFile getFile() {
        return file;
    }
}
