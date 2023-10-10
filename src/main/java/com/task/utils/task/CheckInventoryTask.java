package com.task.utils.task;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.AsyncTask;
import com.task.items.ItemLib;
import com.task.utils.API;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.ItemClass;
import com.task.utils.tasks.taskitems.TaskItem;
import com.task.utils.DataTool;
import com.task.utils.tasks.taskitems.PlayerTask;

import java.util.LinkedList;

/**
 * 线程池处理玩家任务
 *
 * @author SmallasWater*/
public class CheckInventoryTask implements Runnable {
    private Player player;
    private LinkedList<PlayerTask> getTasks;
    private PlayerFile file;
    private boolean cancel;
    private Item oldItem,newItem;

    CheckInventoryTask(Player player, LinkedList<PlayerTask> getTasks, PlayerFile file, Item oldItem, Item newItem, boolean cancel) {

        this.player = player;
        this.getTasks = getTasks;
        this.file = file;
        this.cancel = cancel;
        this.oldItem = oldItem;
        this.newItem = newItem;
    }

    public void setCancel(boolean cancel) {
        this.cancel = cancel;
    }

    @Override
    public void run() {
        try {
            for (PlayerTask task : getTasks) {
                if (task.getTaskFile().getType() == TaskFile.TaskType.CollectItem || task.getTaskFile().getType() == TaskFile.TaskType.DIY) {
                    if (isNext(task)) {
                        TaskItem[] items = task.getTaskClass().getValue();
                        for (TaskItem item : items) {
                            ItemClass itemClass = ItemClass.toItem(item);

                            if (itemClass != null) {
                                if(itemClass instanceof ItemLib){
                                    if(((ItemLib) itemClass).hasItem(oldItem) || ((ItemLib) itemClass).hasItem(newItem)){

                                        int c = ((ItemLib) itemClass).getPlayerAllItemCount(player);
                                        if (c != item.getEndCount()) {
                                            file.setTaskValue(task.getTaskName(), item.getTask(), c);
                                        }
                                    }

                                }else if (oldItem.equals(itemClass.getItem()) || newItem.equals(itemClass.getItem())) {
                                    int c = DataTool.getCount(player, itemClass);
                                    if (c < 0) {
                                        c = 0;
                                    }
                                    if (c != item.getEndCount()) {
                                        file.setTaskValue(task.getTaskName(), item.getTask(), c);
                                    }
                                }
                            }
                        }
                    }
                }
                if (!cancel) {
                    if (task.getTaskFile().getType() == TaskFile.TaskType.GetItem) {
                        Item item = newItem;
                        API.addItem(player, item, TaskFile.TaskType.GetItem);
                    }
                }

            }
        }catch (Exception ignore){}

    }

    private boolean isNext(PlayerTask task){
        return (task.getTaskClass().getOpen()) || file.isSuccess(task.getTaskName());
    }


}
