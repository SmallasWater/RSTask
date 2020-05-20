package com.task.utils.task;


import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.Task;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.ItemClass;
import com.task.utils.tasks.taskitems.TaskItem;
import com.task.utils.tasks.taskitems.PlayerTask;
import com.task.utils.tasks.PlayerFile;

import java.util.LinkedList;


//玩家收集
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
                    item.setEndCount(getCount(player,itemClass));
                    file.setTaskValue(taskName,item);
                }

            }
        }
        file.toSave();
    }

    /** 获取物品数量 */
    static int getCount(Player player, ItemClass item){
        int i = 0;
        for(Item playerItem:player.getInventory().getContents().values()){
            ItemClass itemClass = new ItemClass(playerItem);
            if(item != null){
                if(itemClass.equals(item)){
                    i += playerItem.getCount();
                }
            }
        }
        return i;
    }

    @Override
    public void onRun(int i) {
        onRun(player);
    }
}
