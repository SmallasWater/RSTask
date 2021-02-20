package com.task.events;


import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;
import com.task.utils.tasks.TaskFile;


/**
 * 玩家选中任务事件
 *
 * @author SmallasWater
 */
public class PlayerClickTaskEvent extends PlayerEvent implements Cancellable {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private final TaskFile file;

    private boolean show = true;


    /**
     * 返回任务类
     *
     * @return {@link TaskFile}
     */
    public TaskFile getFile() {
        return file;
    }

    @Override
    public Player getPlayer() {
        return player;
    }

    public PlayerClickTaskEvent(TaskFile file, Player player) {
        this.file = file;
        this.player = player;
    }

    public PlayerClickTaskEvent(TaskFile file, Player player, boolean showUi) {
        this.file = file;
        this.player = player;
        this.show = showUi;
    }

    /**
     * 设置此事件是否给玩家发送GUI
     *
     * @param show 是否显示
     */
    public void setShow(boolean show) {
        this.show = show;
    }

    /**
     * 判断此事件是否给玩家发送GUI
     *
     * @return 是否显示GUI
     */
    public boolean isShow() {
        return this.show;
    }
}
