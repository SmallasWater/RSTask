package com.smallaswater.task.utils.task;


import cn.nukkit.Player;
import cn.nukkit.scheduler.Task;
import com.smallaswater.task.utils.DataTool;
import com.smallaswater.task.utils.tasks.TaskFile;
import com.smallaswater.task.utils.tasks.taskitems.ItemClass;
import com.smallaswater.task.utils.tasks.taskitems.TaskItem;
import com.smallaswater.task.utils.tasks.taskitems.PlayerTask;
import com.smallaswater.task.utils.tasks.PlayerFile;

import java.util.LinkedList;


/**
 * 玩家收集物品任务
 *
 * @author SmallasWater*/
public class CollectItemTask extends Task {

    private Player player;
    public CollectItemTask(Player player){
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

    private static void add(Player player,String taskName,TaskItem[] items){
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
