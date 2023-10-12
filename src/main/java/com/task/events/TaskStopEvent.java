package com.task.events;

import cn.nukkit.event.HandlerList;
import cn.nukkit.event.plugin.PluginEvent;
import cn.nukkit.plugin.Plugin;

/**
 * @author SmallasWater
 * Create on 2021/2/26 14:19
 * Package com.task.events
 */
public class TaskStopEvent extends PluginEvent {
    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlers() {
        return HANDLERS;
    }

    private Runnable runnable;
    public TaskStopEvent(Plugin plugin,Runnable runnable) {
        super(plugin);
        this.runnable = runnable;
    }

    public Runnable getRunnable() {
        return runnable;
    }
}
