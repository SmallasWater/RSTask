package com.Task.utils.Task;

import AwakenSystem.events.*;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;

/** 想用的话监听...*/
public interface RPGListener extends Listener{

    /** 玩家升级事件*/
    @EventHandler
    void onLevelUpEvent(PlayerLevelUpEvent event);

    /** 玩家增加Buff事件*/
    @EventHandler
    void onAddBuffEvent(PlayerAddBufferEvent event);

    /** 玩家增加经验事件*/
    @EventHandler
    void onAddExpEvent(PlayerAddExpEvent event);

    /** 玩家PVP事件*/
    @EventHandler
    void onAttactEvent(PlayerAttackEvent event);

    /** 玩家装备饰品事件*/
    @EventHandler
    void onOrnamentsEvent(PlayerOrnamentsEvent event);

    /** 玩家重选属性事件*/
    @EventHandler
    void onResetAwakenEvent(PlayerResetAwakenEvent event);

    /**/



}
