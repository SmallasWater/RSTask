package com.smallaswater.task.utils.events;


import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.smallaswater.task.utils.tasks.TaskFile;

/** 任务被创建
 * @author SmallasWater*/

public class CreateTaskEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    public TaskFile file;
    public CreateTaskEvent(TaskFile file){
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
