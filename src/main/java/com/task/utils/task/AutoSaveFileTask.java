package com.task.utils.task;

import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.LogLevel;
import com.task.RsTask;
import com.task.events.TaskStopEvent;
import com.task.utils.tasks.*;


/**
 * @author SmallasWater
 * Create on 2021/1/23 22:18
 *
 */
public class AutoSaveFileTask implements Runnable {

    private RsTask owner;

    private RsTask getOwner() {
        return owner;
    }

    public AutoSaveFileTask(RsTask owner) {
        this.owner = owner;
    }



    private int saveCount = 0;

    private int errorCount = 0;


    @Override
    public void run() {
        while (true){
            getOwner().getLogger().info("[任务] 正在保存玩家任务数据");
            RsTask.executor.submit(() -> {
                try{
                    for (PlayerFile file : getOwner().playerFiles.values()) {
                        if (file.toSave()) {
                            saveCount++;
                        } else {
                            getOwner().getLogger().log(LogLevel.ERROR, "玩家 " + file.getPlayerName() + " 任务文件保存失败!");
                            errorCount++;
                        }
                    }
                    getOwner().getLogger().info("[任务] 保存完成 "+saveCount+"个玩家任务数据保存成功 "+errorCount+" 个玩家任务数据保存失败");
                    saveCount = 0;
                    errorCount  = 0;
                }catch (Exception e){
                    getOwner().getLogger().info("[任务] 保存出现异常 "+saveCount+"个玩家任务数据保存成功 "+errorCount+" 个玩家任务数据保存失败");
                }

            });
            try {
                Thread.sleep(getOwner().getConfig().getInt("auto-save-task.time") * 60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Server.getInstance().getPluginManager().callEvent(new TaskStopEvent(getOwner(),this));
                return;
            }
        }

    }
}
