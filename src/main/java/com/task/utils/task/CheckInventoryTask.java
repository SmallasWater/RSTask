package com.task.utils.task;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.AsyncTask;
import com.task.utils.API;
import com.task.tasks.PlayerFile;
import com.task.tasks.TaskFile;
import com.task.tasks.taskitems.ItemClass;
import com.task.tasks.taskitems.TaskItem;
import com.task.utils.DataTool;
import com.task.tasks.taskitems.PlayerTask;

import java.util.LinkedList;

/**
 * 线程池处理玩家任务
 *
 * @author SmallasWater*/
public class CheckInventoryTask extends AsyncTask {
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



    @Override
    public void onRun() {
        try {
            for (PlayerTask task : getTasks) {
                if (task.getTaskFile().getType() == TaskFile.TaskType.CollectItem) {
                    if (isNext(task)) {
                        TaskItem[] items = task.getTaskClass().getValue();
                        for (TaskItem item : items) {
                            ItemClass itemClass = ItemClass.toItem(item);
                            if (itemClass != null) {
                                if (oldItem.equals(itemClass.getItem()) || newItem.equals(itemClass.getItem())) {
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
