package com.task.utils.task;


import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.scheduler.Task;
import com.task.RsTask;
import com.task.utils.tasks.taskitems.PlayerTask;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.events.TaskTimeOutEvent;

/**
 * 检查任务状态
 * @author SmallasWater
 */
public class ChunkTaskTask extends PluginTask<RsTask> {
    public ChunkTaskTask(RsTask owner) {
        super(owner);
    }

    @Override
    public void onRun(int i) {
        for(PlayerFile file: RsTask.getTask().playerFiles.values()){
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
