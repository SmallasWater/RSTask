package com.task.utils.task;


import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import com.task.RSTask;
import com.task.utils.tasks.taskitems.PlayerTask;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.events.TaskTimeOutEvent;

/**
 * @author SmallasWater
 */
public class ChunkTaskTask extends Task {
    @Override
    public void onRun(int i) {
        for(PlayerFile file: RSTask.getTask().playerFiles.values()){
            for(PlayerTask task:file.getPlayerTasks()){
                if(task.getTaskFile().getLoadDay() > 0) {
                    if (file.getTimeOutDay(task.getTaskName()) <= 0) {
                        if (task.getTaskClass().getOpen()) {
                            file.closeTask(task.getTaskName());
                            Server.getInstance().getPluginManager().callEvent(new TaskTimeOutEvent(file, task.getTaskFile()));
                        }
                    }
                }
            }

        }
    }
}
