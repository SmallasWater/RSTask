package com.task.utils.task;

import cn.nukkit.scheduler.AsyncTask;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.utils.LogLevel;
import com.task.RsTask;
import com.task.tasks.*;


/**
 * @author SmallasWater
 * Create on 2021/1/23 22:18
 *
 */
public class AutoSaveFileTask extends PluginTask<RsTask> {

    public AutoSaveFileTask(RsTask owner) {
        super(owner);
    }

    private int saveCount = 0;

    private int errorCount = 0;


    @Override
    public void onRun(int i) {
        getOwner().getLogger().info("[任务] 正在保存玩家任务数据");
        getOwner().getServer().getScheduler().scheduleAsyncTask(getOwner(), new AsyncTask() {
            @Override
            public void onRun() {
                for(PlayerFile file: getOwner().playerFiles.values()){
                    if(file.toSave()){
                        saveCount++;
                    }else{
                        getOwner().getLogger().log(LogLevel.ERROR,"玩家 "+file.getPlayerName()+" 任务文件保存失败!");
                        errorCount++;
                    }
                }

                getOwner().getLogger().info("[任务] 保存完成 "+saveCount+"个玩家任务数据保存成功 "+errorCount+" 个玩家任务数据保存失败");
                saveCount = 0;
                errorCount  = 0;
            }
        });
    }
}
