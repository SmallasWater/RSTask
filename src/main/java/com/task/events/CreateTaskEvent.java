package com.task.events;


import com.task.events.base.TaskEvent;
import com.task.tasks.TaskFile;

/** 任务被创建
 * @author SmallasWater*/

public class CreateTaskEvent extends TaskEvent {

    public CreateTaskEvent(TaskFile file) {
        super(file);
    }

}
