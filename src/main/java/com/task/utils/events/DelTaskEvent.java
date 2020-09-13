package com.task.utils.events;


import cn.nukkit.event.Cancellable;
import cn.nukkit.event.Event;
import cn.nukkit.event.HandlerList;
import com.task.utils.events.base.TaskEvent;
import com.task.utils.tasks.TaskFile;

/** 删除任务事件
 * @author SmallasWater*/
public class DelTaskEvent extends TaskEvent {

    public DelTaskEvent(TaskFile file) {
        super(file);
    }
}
