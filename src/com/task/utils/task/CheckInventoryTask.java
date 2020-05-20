package com.task.utils.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.scheduler.AsyncTask;
import com.task.utils.API;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.ItemClass;
import com.task.utils.tasks.taskitems.TaskItem;
import com.task.utils.tasks.taskitems.PlayerTask;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.events.UseTaskEvent;

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
                    if (file.isRunning(task.getTaskName())
                            && task.getTaskClass().getOpen() || file.isSuccess(task.getTaskName())) {
                        TaskItem[] items = task.getTaskClass().getValue();
                        for (TaskItem item : items) {
                            ItemClass itemClass = ItemClass.toItem(item);
                            if (itemClass != null) {
                                if (oldItem.equals(itemClass.getItem()) || newItem.equals(itemClass.getItem())) {
                                    int c = CollectItemTask.getCount(player, itemClass);
                                    if (c < 0) {
                                        c = 0;
                                    }
                                    if (c != item.getEndCount()) {
                                        UseTaskEvent event1 = new UseTaskEvent(player, task);
                                        Server.getInstance().getPluginManager().callEvent(event1);
                                        if(event1.isCancelled()){
                                            return;
                                        }
                                        if (file.setTaskValue(task.getTaskName(), item.getTask(), c)) {
                                            file.toSave();

                                        }
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


}
