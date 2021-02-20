package com.task.events;


import com.task.events.base.TaskEvent;
import com.task.utils.tasks.TaskFile;

/** 删除任务事件
 * @author SmallasWater*/
public class DelTaskEvent extends TaskEvent {

    public DelTaskEvent(TaskFile file) {
        super(file);
    }
}
