package com.Task.utils.events;


import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.Task.utils.Tasks.TaskFile;

/** 创建任务事件*/

public class createTaskEvent extends Event implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    public TaskFile file;
    public createTaskEvent(TaskFile file){
        this.file = file;
    }

    public TaskFile getFile() {
        return file;
    }
}
