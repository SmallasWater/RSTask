package com.task.events.base;

import cn.nukkit.Player;
import com.task.tasks.TaskFile;

/**
 * @author SmallasWater
 * @create 2020/9/13 11:28
 */
public class PlayerTaskEvent extends TaskEvent{

    private final Player player;

    public PlayerTaskEvent(Player player,TaskFile file) {
        super(file);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }
}
