package com.Task.utils.events;


import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.Task.utils.Tasks.TaskFile;

/** 删除任务事件*/
public class delTaskEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public TaskFile taskName;

    public delTaskEvent(TaskFile taskName){
        this.taskName = taskName;
    }

    public TaskFile getTask() {
        return taskName;
    }
}
