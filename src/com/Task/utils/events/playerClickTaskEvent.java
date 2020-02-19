package com.Task.utils.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.Task.utils.Tasks.TaskFile;


/** 玩家选中任务事件*/
public class playerClickTaskEvent extends PlayerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlers() {
        return handlers;
    }

    private TaskFile file;

    private boolean show = true;


    public TaskFile getFile() {
        return file;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public playerClickTaskEvent(TaskFile file, Player player){
        this.file = file;
        this.player = player;
    }
    public playerClickTaskEvent(TaskFile file, Player player,boolean showUI){
        this.file = file;
        this.player = player;
        this.show = showUI;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public boolean isShow(){
        return this.show;
    }
}
