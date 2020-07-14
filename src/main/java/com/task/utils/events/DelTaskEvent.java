package com.task.utils.events;


import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.task.utils.tasks.TaskFile;

/** 删除任务事件
 * @author SmallasWater*/
public class DelTaskEvent extends Event implements Cancellable {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    public TaskFile taskName;

    public DelTaskEvent(TaskFile taskName){
        this.taskName = taskName;
    }

    /**
     * 返回任务类
     * @return {@link TaskFile}
     * */
    public TaskFile getTask() {
        return taskName;
    }
}
