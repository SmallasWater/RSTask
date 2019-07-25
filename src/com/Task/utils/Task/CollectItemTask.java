package com.Task.utils.Task;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.TaskItems.ItemClass;
import com.Task.utils.Tasks.TaskItems.TaskItem;
import com.Task.utils.Tasks.TaskItems.playerTask;
import com.Task.utils.Tasks.playerFile;

import java.util.LinkedList;


//玩家收集
public class CollectItemTask{

    public static void onRun(Player player) {
        playerFile file = new playerFile(player.getName());
        LinkedList<playerTask> tasks = file.getPlayerTasks();
        for(playerTask task:tasks){
            if(task.getTaskFile().getType() == TaskFile.TaskType.CollectItem){
                add(player,task.getTaskName(),task.getTaskClass().getValue());
            }
        }
    }

    private static void add(Player player,String taskName,TaskItem[] items){
        playerFile file = new playerFile(player.getName());
        for(TaskItem item:items){
            if(item != null){
                ItemClass itemClass = ItemClass.toItem(item);
                if(itemClass != null){
                    item.setEndCount(getCount(player,itemClass));
                    file.setTaskValue(taskName,item);
                }
            }
        }
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

}
