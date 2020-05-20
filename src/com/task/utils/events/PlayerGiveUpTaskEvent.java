package com.task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.task.utils.tasks.TaskFile;

/** 玩家放弃任务事件
 * @author SmallasWater*/
public class PlayerGiveUpTaskEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TaskFile file;


    /**
     * 返回任务类
     * @return {@link TaskFile}
     * */
    public TaskFile getFile() {
        return file;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public PlayerGiveUpTaskEvent(TaskFile file,Player player){
        this.file = file;
        this.player = player;
    }
}
