package com.task.utils.events;

import cn.nukkit.Player;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.TaskBook;

/**
 * 玩家打开任务书事件
 * @author SmallasWater
 */
public class PlayerOpenBookEvent extends PlayerEvent {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TaskBook bookWritten;

    public PlayerOpenBookEvent(Player player, TaskBook written){
        this.player = player;
        this.bookWritten = written;
    }

    /**
     * 返回任务书
     * @return {@link TaskBook}
     * */
    public TaskBook getBookWritten() {
        return bookWritten;
    }

    @Override
    public Player getPlayer() {
        return super.getPlayer();
    }
}
