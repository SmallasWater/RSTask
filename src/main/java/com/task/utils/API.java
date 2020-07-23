package com.task.utils;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import com.task.RsTask;
import com.task.utils.events.DelTaskEvent;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.ItemClass;
import com.task.utils.tasks.taskitems.PlayerTask;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.events.PlayerClickTaskEvent;
import com.task.utils.task.ListerEvents;

import java.util.LinkedList;

import static com.task.utils.task.ListerEvents.defaultUseTask;

/**
 * @author SmallasWater
 */
public class API {


    /**
     * 创建任务
     * @param task 任务 {@link TaskFile}
     * @return 是否创建成功
     * */
    public static boolean createTask(TaskFile task) {
        if(task != null){
            DataTool.createTask(task);
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
           DelTaskEvent event = new DelTaskEvent(task);
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
        PlayerFile pf = PlayerFile.getPlayerFile(player.getName());
        if(pf.canInvite(task.getTaskName())){
            PlayerClickTaskEvent event = new PlayerClickTaskEvent(task,player);
            Server.getInstance().getPluginManager().callEvent(event);
            return true;
        }
        return false;
    }
    /**
     * 增加玩家任务进度
     * @param player 玩家
     * @param task 任务名称
     * @param load 任务分支
     * @param value 数量
     * @return 是否成功领取
     * */
    public static boolean addPlayerTaskValue(String player,String task,String load,int value){
        PlayerFile f = PlayerFile.getPlayerFile(player);
        if(f != null){
            return f.addTaskValue(task,load,value);
        }
        return false;
    }

    /**
     * 设置玩家任务进度
     * @param player 玩家
     * @param task 任务名称
     * @param load 任务分支
     * @param value 数量
     * @return 是否成功领取
     * */
    public static boolean setPlayerTaskValue(String player,String task,String load,int value){
        PlayerFile f = PlayerFile.getPlayerFile(player);
        if(f != null){
            return f.setTaskValue(task,load,value);
        }
        return false;
    }

    /**
     * 任务增加物品进度
     * */
    public static void addItem(Player player, Item item, TaskFile.TaskType type){
        if(item != null){
            ItemClass itemClass = new ItemClass(item);
            if(!RsTask.getTask().canExisteItemClass(itemClass)){
                ListerEvents.defaultUseTask(player.getName(),itemClass.toTaskItem(false), type,false);
            }else{
                ListerEvents.defaultUseTask(player.getName(),itemClass.toTaskItem(true),type,false);

            }
        }
    }

    /**
     * 增加玩家进行中任务进度
     * @param player 玩家
     * @param task 任务名称
     * @param load 任务分支
     * @param value 数量
     * @return 是否成功增加
     * */
    public static boolean addPlayerRunTask(String player,String task,String load,int value){
        PlayerFile f = PlayerFile.getPlayerFile(player);
        if(f != null){
            if(f.isRunning(task)){
                return f.addTaskValue(task,load,value);
            }
        }
        return false;
    }

    /**
     * 设置玩家进度
     * @param player 玩家
     * @param task 任务名称
     * @param load 任务分支
     * @param value 数量
     * @return 是否成功设置
     * */
    public static boolean setPlayerRunTask(String player,String task,String load,int value){
        PlayerFile f = PlayerFile.getPlayerFile(player);
        if(f != null){
            if(f.isRunning(task)) {
                return f.setTaskValue(task, load, value);
            }
        }
        return false;
    }



    /**
     * 获取玩家全部进行中的任务
     * @param player 玩家
     * @return 进行中的任务
     * */
    public LinkedList<PlayerTask> getAllRunTasks(Player player){
        //获取玩家全部进行的任务
        LinkedList<PlayerTask> tasks = new LinkedList<>();
        PlayerFile file = PlayerFile.getPlayerFile(player.getName());
        if(file != null){
            for(PlayerTask task:file.getPlayerTasks()){
                if(file.isRunning(task.getTaskName())){
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }


}
