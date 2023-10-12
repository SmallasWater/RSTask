package com.task.events.base;

import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.task.utils.tasks.TaskFile;

/**
 * @author SmallasWater
 * @create 2020/9/13 11:28
 */
public class TaskEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private final TaskFile file;
    public TaskEvent(TaskFile file){
        this.file = file;
    }

    /**
     * 返回任务类
     * @return {@link TaskFile}
     * */
    public TaskFile getFile() {
        return file;
    }
}
