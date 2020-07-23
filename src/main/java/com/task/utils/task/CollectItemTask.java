package com.task.utils.task;


import cn.nukkit.Player;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.PluginTask;
import cn.nukkit.scheduler.Task;
import com.task.RsTask;
import com.task.utils.DataTool;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.ItemClass;
import com.task.utils.tasks.taskitems.TaskItem;
import com.task.utils.tasks.taskitems.PlayerTask;
import com.task.utils.tasks.PlayerFile;

import java.util.LinkedList;


/**
 * 玩家收集物品任务
 *
 * @author SmallasWater*/
public class CollectItemTask extends PluginTask<RsTask> {

    private Player player;
    public CollectItemTask(RsTask plugin,Player player){
        super(plugin);
        this.player = player;
    }

    public static void onRun(Player player) {
        PlayerFile file = PlayerFile.getPlayerFile(player.getName());
        LinkedList<PlayerTask> tasks = file.getPlayerTasks();
        for(PlayerTask task:tasks){
            if(task.getTaskFile().getType() == TaskFile.TaskType.CollectItem){
                add(player,task.getTaskName(),task.getTaskClass().getValue());
            }
        }
    }

    private static void add(Player player, String taskName, TaskItem[] items){
        PlayerFile file = PlayerFile.getPlayerFile(player.getName());
        for(TaskItem item:items){
            if(item != null){
                ItemClass itemClass = ItemClass.toItem(item);
                if(itemClass != null){
                    item.setEndCount(DataTool.getCount(player,itemClass));
                    file.setTaskValue(taskName,item);
                }

            }
        }
    }



    @Override
    public void onRun(int i) {
        onRun(player);
    }
}
