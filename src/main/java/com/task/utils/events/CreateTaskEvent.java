package com.task.utils.events;


import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.task.utils.events.base.TaskEvent;
import com.task.utils.tasks.TaskFile;

/** 任务被创建
 * @author SmallasWater*/

public class CreateTaskEvent extends TaskEvent {

    public CreateTaskEvent(TaskFile file) {
        super(file);
    }

}
