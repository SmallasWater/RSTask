package com.task.utils.task;


import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;
import com.task.RsTask;
import com.task.events.TaskStopEvent;
import com.task.utils.tasks.taskitems.PlayerTask;
import com.task.utils.tasks.PlayerFile;
import com.task.events.TaskTimeOutEvent;

/**
 * 检查任务状态
 * @author SmallasWater
 */
public class ChunkTaskTask implements Runnable {
    private RsTask owner;

    private RsTask getOwner() {
        return owner;
    }

    public ChunkTaskTask(RsTask owner) {
        this.owner = owner;
    }

    @Override
    public void run() {
        while (true) {
            for (PlayerFile file : RsTask.getTask().playerFiles.values()) {
                for (PlayerTask task : file.getPlayerTasks()) {
                    if (task.getTaskFile().getLoadDay() > 0) {
                        if (file.getTimeOutDay(task.getTaskName()) <= 0) {
                            if (task.getTaskClass().getOpen()) {
                                file.closeTask(task.getTaskName());
                                Server.getInstance().getPluginManager().callEvent(new TaskTimeOutEvent(file, task.getTaskFile()));
                            }
                        }
                    }
                }

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Server.getInstance().getPluginManager().callEvent(new TaskStopEvent(getOwner(),this));
                return;
            }
        }
    }
}
