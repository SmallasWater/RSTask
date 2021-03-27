package com.task.utils.task;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import com.task.RsTask;
import com.task.events.TaskStopEvent;
import com.task.utils.tasks.taskitems.TaskBook;
import com.task.utils.tasks.taskitems.PlayerTask;
import com.task.utils.tasks.PlayerFile;

/**
 * 检查玩家背包任务书
 * @author SmallasWater
 */
public class ChunkPlayerInventoryBookTask implements Runnable {

    private RsTask owner;

    private RsTask getOwner() {
        return owner;
    }

    public ChunkPlayerInventoryBookTask(RsTask owner) {
        this.owner = owner;
    }



    @Override
    public void run() {
        while (true) {
            //遍历检测玩家背包
            for (Player player : Server.getInstance().getOnlinePlayers().values()) {
                for (Item item : player.getInventory().getContents().values()) {
                    if (item instanceof ItemBookWritten) {
                        if (TaskBook.isBook((ItemBookWritten) item)) {
                            TaskBook book = TaskBook.getTaskBookByItem((ItemBookWritten) item);
                            String taskName = book.title;
                            PlayerFile file = PlayerFile.getPlayerFile(player.getName());
                            if (file != null) {
                                PlayerTask playerTask = file.getTaskByName(taskName);
                                if (playerTask != null) {
                                    if (!playerTask.getTaskClass().getOpen()) {
                                        player.getInventory().remove(item);
                                    }
                                } else {
                                    player.getInventory().remove(item);
                                }
                            }
                        } else {
                            if (item.hasCompoundTag()) {
                                if (item.getNamedTag().contains("bookTaskName")) {
                                    player.getInventory().remove(item);
                                }
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
