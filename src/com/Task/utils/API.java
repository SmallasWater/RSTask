package com.Task.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import com.Task.RSTask;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.playerFile;
import com.Task.utils.events.delTaskEvent;
import com.Task.utils.events.playerClickTaskEvent;

public class API {


    /**
     * 创建任务
     * @param task 任务 {@link TaskFile}
     * @return 是否创建成功
     * */
    public static boolean createTask(TaskFile task) {
        if(task != null){
            RSTask.getTask().createTask(task);
        }
        return false;
    }

    /**
     * 删除任务
     * @param taskName 任务名称
     * @return 是否删除成功
     * */
    public static boolean deleteTask(String taskName) {
       TaskFile task = TaskFile.getTask(taskName);
       if(task != null){
           delTaskEvent event = new delTaskEvent(task);
           Server.getInstance().getPluginManager().callEvent(event);
           return task.close();
       }
        return false;
    }


    /**
     * 玩家领取任务
     * @param player 玩家
     * @param task 任务名称
     * @return 是否成功领取
     * */
    public static boolean playerSeeTask(Player player, TaskFile task){
        playerFile pf = playerFile.getPlayerFile(player.getName());
        if(pf.canInvite(task.getTaskName())){
            playerClickTaskEvent event = new playerClickTaskEvent(task,player);
            Server.getInstance().getPluginManager().callEvent(event);
            return true;
        }
        return false;
    }

    /**
     * 增加玩家进度
     * @param player 玩家
     * @param task 任务名称
     * @param load 任务分支
     * @param value 数量
     * @return 是否成功领取
     * */
    public static boolean playerAddRunTask(String player,String task,String load,int value){
        playerFile f = playerFile.getPlayerFile(player);
        if(f != null){
            return f.addTaskValue(task,load,value);
        }
        return false;
    }

    /**
     * 设置玩家进度
     * @param player 玩家
     * @param task 任务名称
     * @param load 任务分支
     * @param value 数量
     * @return 是否成功领取
     * */
    public static boolean playerSetRunTask(String player,String task,String load,int value){
        playerFile f = playerFile.getPlayerFile(player);
        if(f != null){
            return f.setTaskValue(task,load,value);
        }
        return false;
    }



}
