package com.Task.utils.Tasks;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.utils.Config;
import com.Task.RSTask;
import com.Task.utils.DataTool;
import com.Task.utils.ItemIDSunName;
import com.Task.utils.Tasks.TaskItems.*;
import com.Task.utils.events.successTaskEvent;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;


public class playerFile {

    private String playerName;

    private LinkedList<playerTask> playerTasks = new LinkedList<>();


    public playerFile(String playerName){
        this.playerName = playerName;
        this.init();
        if(canSyncTaskItem()){
            this.init(true);
        }
    }

    private boolean canSyncTaskItem(){
        for(playerTask task:playerTasks){
            if(task.getTaskFile() == null){
                playerTasks.remove(task);
                continue;
            }
            TaskItem[] items = task.getTaskFile().getTaskItem();
            if(items.length == task.getTaskClass().getValue().length){
                for(TaskItem item:items){
                    if(item != null){
                        if(!canIn_ArrayTaskItem(task,item)){
                            return true;
                        }
                    }
                }
            }else{
                return true;
            }
        }
        return false;
    }


    //* 初始化
    private void init(){
        this.init(false);
    }

    private void init(boolean sync){
        if(!sync){
            Config config = RSTask.getTask().getPlayerConfig(playerName);
            LinkedList<playerTask> tasks = new LinkedList<>();
            Map map = (Map) config.get("Task");
            if(map != null){
                for(Object name:map.keySet()){
                    tasks.add(new playerTask
                            (PlayerTaskClass.toPlayerTaskClass((String)name,(Map)map.get(name))));
                }
            }
            this.playerTasks = tasks;
        }else{
            LinkedList<playerTask> newTask = new LinkedList<>();
            for(playerTask task:playerTasks){
                newTask.add(new playerTask(TaskFile.getTask(task.getTaskName())));
            }
            this.playerTasks = newTask;
        }
    }

    /** 玩家是否有这个任务 */
    public boolean issetTask(String taskName){
        init();
        for(playerTask T:playerTasks){
            if(T.getTaskName().equals(taskName)){
                return true;
            }
        }
        return false;
    }

    /** 玩家是否有这个任务 */
    public boolean issetTask(playerTask task){
        return issetTask(task.getTaskName());
    }

    /** 玩家是否有这个任务 */
    public boolean issetTask(TaskFile task){
        return issetTask(task.getTaskName());
    }



    /** 给玩家增加一个任务 */
    public void addTask(String taskName){
        this.addTask(new playerTask(taskName));
    }

    /** 给玩家增加一个任务 */
    public void addTask(TaskFile taskName){
        this.addTask(new playerTask(taskName));

    }


    /** 给玩家增加一个任务 */
    public void addTask(playerTask task){
        if(!issetTask(task)){
            task.getTaskClass().setOpen(true);
            playerTasks.add(task);
            toSaveConfig(defaultConfig());
        }else{
            playerTask t = getTaskByName(task.getTaskName());
            PlayerTaskClass playerTaskClass = t.getTaskClass();
            playerTaskClass.setOpen(true);
            playerTaskClass.setTime(new Date());
            t.setTaskClass(playerTaskClass);
            toSaveConfig(defaultConfig());
        }
    }




    /** 给玩家移除一个任务 */
    public boolean delTask(String taskName){
        if(issetTask(getTaskByName(taskName))){
            LinkedList<playerTask> tasks = new LinkedList<>();
            for(playerTask task:playerTasks){
                if(!task.getTaskName().equals(taskName)){
                    tasks.add(task);
                }
            }
            playerTasks = tasks;
            toSaveConfig(defaultConfig());
            return true;
        }
        return false;
    }


    /** 获取玩家任务分支*/
    public TaskItem[] getTaskItems(String taskName){
        PlayerTaskClass playerTaskClass = getTaskByName(taskName).getTaskClass();
        return playerTaskClass.getValue();
    }


    /** 给玩家任务的分支加点*/
    public boolean addTaskValue(String taskName,String valueName,int value){
        TaskItem[] taskItems = getTaskByName(taskName).getTaskClass().getValue();
        for(TaskItem item:taskItems){
            if(item != null) {
                if (item.getTask().equals(valueName)) {
                    item.addEndCount(value);
                    setTaskValue(taskName, item);
                    return true;
                }
            }

        }
        return false;
    }

    /** 关闭一个任务*/
    public boolean closeTask(String taskName){
        if(getTaskByName(taskName) != null) {
            playerTask file = getTaskByName(taskName);
            if(isSuccessed(taskName)){
                file.getTaskClass().setOpen(false);
                PlayerTaskClass playerTaskClass = file.getTaskClass();
                TaskItem[] items = playerTaskClass.getValue();
                for(TaskItem item:items){
                    item.setEndCount(0);
                }
                file.setTaskClass(playerTaskClass);
                setPlayerTask(file);
            }else{
                delTask(taskName);
            }
            toSaveConfig(defaultConfig());
            return true;
        }
        return false;
    }

    /** 给玩家任务的分支设置任务点*/
    public boolean setTaskValue(String taskName,String valueName,int value){
        TaskItem[] taskItems = getTaskByName(taskName).getTaskClass().getValue();
        for(TaskItem item:taskItems){
            if(item != null){
                if(item.getTask().equals(valueName)){
                    item.setEndCount(value);
                    setTaskValue(taskName,item);
                    return true;
                }
            }

        }
        return false;
    }

    /** 判断任务分支是否存在于此任务*/
    public boolean canIn_ArrayTaskItem(playerTask task,TaskItem item){
        TaskItem[] items = task.getTaskClass().getValue();
        for(TaskItem item1:items){
            if(item1.equals(item)){
                return true;
            }
        }
        return false;
    }


    /**  设置玩家任务状态*/
    public void setPlayerTask(playerTask task){
        playerTask tasks = getTaskByName(task.getTaskName());
        if(tasks != null){
            tasks.sync(task);
        }else{
            addTask(task);
            init();
            if(!issetTask(task)){
                Server.getInstance().getLogger().warning("读取玩家任务出现异常");
            }
        }
        toSaveConfig(defaultConfig());
    }



    /** 根据任务名获取分支任务 */
    public playerTask getTaskByName(String taskName){
        init();
        for(playerTask task:playerTasks){
            if(task.getTaskName().equals(taskName)){
                return task;
            }
        }
        return null;
    }

    /** 是否能领取 */
    public boolean can_Invite(String taskName){
        TaskFile file = TaskFile.getTask(taskName);
        if(file != null){
            if(!issetTask(taskName)){
                String last = file.getLastTask();
                if(last != null && !last.equals("null")){
                    if(!issetTask(last))
                        return false;
                    else if(!isSuccessed(last))
                            return false;
                }
            }else {
                if (!inDay(taskName)) {
                   return false;
                }
                if (file.getSuccessItem().getCount() == 0
                        && isSuccessed(taskName)) {
                    return false;
                }
                if(isRunning(taskName))
                    return false;
            }
        }
            return true;
    }



    /** 是否完成过 */
    public boolean isSuccessed(String taskName){
        if(issetTask(taskName)){
            playerTask playerTask = getTaskByName(taskName);
            return (playerTask.getTaskClass().getCount() > 0);
        }
        return false;
    }



    /** 任务是否冷却结束 */
    public boolean inDay(String taskName){
        playerTask task = getTaskByName(taskName);
        TaskFile file = TaskFile.getTask(taskName);
        if(task != null && file != null){
            Date date = task.getTaskClass().getTime();
            int t = file.getDay();
            if(t >= 0 && DataTool.getTime(date) >= t){
                return true;
            }
        }
        return false;
    }


    /** 任务是否进行中 */
    public boolean isRunning(String taskName) {
        playerTask task = this.getTaskByName(taskName);
        return task != null && task.getTaskClass().getOpen();
    }


    /** 获取任务状态 */
    public PlayerTaskType getTaskType(TaskFile taskFile){
        if(issetTask(taskFile.getTaskName())){
            if(isSuccess(taskFile)){
                    return PlayerTaskType.Success;
            }else if(can_Invite(taskFile.getTaskName())){
                return PlayerTaskType.can_Invite;
            }else if(isRunning(taskFile.getTaskName())){
                return PlayerTaskType.Running;
            }else if(isSuccessed(taskFile.getTaskName()) && !can_Invite(taskFile.getTaskName())){
                return PlayerTaskType.isSuccess_noInvite;
            }else if(isSuccessed(taskFile.getTaskName()) && can_Invite(taskFile.getTaskName())){
                return PlayerTaskType.isSuccess_canInvite;
            }else {
                return PlayerTaskType.No_Invite;
            }
        }else{
            String last = taskFile.getLastTask();
            if(last != null && !last.equals("null")){
                if(issetTask(last) && isSuccessed(last)){
                    return PlayerTaskType.can_Invite;
                }else{
                    return PlayerTaskType.No_Invite;
                }
            }else{
                return PlayerTaskType.can_Invite;
            }
        }
    }

    /** 获取完成过可领取任务 */
    public LinkedList<playerTask> getisSuccessedAndCanInvite(){
        return getTasksByType(PlayerTaskType.isSuccess_canInvite);
    }

    /** 获取完成过不可领取任务 */
    public LinkedList<playerTask> getisSuccessedAndNotInvite(){
        return getTasksByType(PlayerTaskType.isSuccess_noInvite);
    }

    /** 获取可以领取任务 */
    public LinkedList<playerTask> getCanInviteTasks(int star){
        LinkedList<playerTask> tasks = new LinkedList<>();
        LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(star);
        for(TaskFile file:taskFiles){
            if(can_Invite(file.getTaskName())){
                tasks.add(new playerTask(file));
            }
        }
        return tasks;
    }


    /** 获取不能领取任务 */
    public LinkedList<playerTask> getNoInviteTasks(int star){
        LinkedList<playerTask> tasks = new LinkedList<>();
        LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(star);
        for(TaskFile file:taskFiles){
            if(file.getSuccessItem().getCount() == 0){
                tasks.add(new playerTask(file));
            }else if(isSuccess(file.getTaskName()) && !inDay(file.getTaskName())){
                tasks.add(new playerTask(file));
            }
        }

        return tasks;
    }


    /** 获取进行任务（不包括完成与不可领取的） */
    public LinkedList<playerTask> getInviteTasks(){
        return getTasksByType(PlayerTaskType.Running);
    }

    public LinkedList<playerTask> getInviteTasks(int level){
        return getTasksByType(PlayerTaskType.Running,level);
    }

    /** 获取已完成(达到要求)任务 */
    public LinkedList<playerTask> getSuccessTasks(){
        return getTasksByType(PlayerTaskType.Success);
    }

    public LinkedList<playerTask> getSuccessTasks(int level){
        return getTasksByType(PlayerTaskType.Success,level);
    }




    public LinkedList<playerTask> getTasksByType(PlayerTaskType taskType,int level){
        LinkedList<playerTask> tasks = new LinkedList<>();
        for(playerTask task:playerTasks){
            if(task.getTaskFile().getStar() == level){
                switch (taskType){
                    case Success:
                        if(isSuccess(task.getTaskName()))
                            tasks.add(task);
                        break;
                    case Running:
                        if(isRunning(task.getTaskName()) && !isSuccess(task.getTaskName()))
                            tasks.add(task);
                        break;
                    case isSuccess_noInvite:
                        if(isSuccessed(task.getTaskName()) && !can_Invite(task.getTaskName()))
                            tasks.add(task);
                        break;

                    case isSuccess_canInvite:
                        if(isSuccessed(task.getTaskName()) && can_Invite(task.getTaskName()))
                            tasks.add(task);
                        break;
                    case can_Invite:
                        if(can_Invite(task.getTaskName()))
                            tasks.add(task);
                        break;
                }
            }
        }
        return tasks;
    }

    public enum PlayerTaskType{
        can_Invite, Success,No_Invite,Running,isSuccess_canInvite,isSuccess_noInvite,
    }

    private LinkedList<playerTask> getTasksByType(PlayerTaskType taskType){
        init();
        LinkedList<playerTask> tasks = new LinkedList<>();
        for(playerTask task:playerTasks) {
            switch (taskType) {
                case Success:
                    if(isSuccess(task.getTaskName()))
                        tasks.add(task);
                    break;
                case Running:
                    if(isRunning(task.getTaskName()) && !isSuccess(task.getTaskName()))
                        tasks.add(task);
                    break;
                case isSuccess_noInvite:
                    if(isSuccessed(task.getTaskName()) && !can_Invite(task.getTaskName()))
                        tasks.add(task);
                    break;

                case isSuccess_canInvite:
                    if(isSuccessed(task.getTaskName()) && can_Invite(task.getTaskName()))
                        tasks.add(task);
                    break;
            }
        }
        return tasks;
    }

    /** 判断此难度是否解锁(需开启积分验证)
     1级难度默认解锁 */
    public boolean canLock(int star) {
        return !RSTask.getTask().getConfig().getBoolean("是否开启积分验证") || (getCount() >= RSTask.starNeed(star));
    }


    /** 是否第一次领取*/
    public boolean isFrist(TaskFile file){
        if(!issetTask(file)){
            return true;
        }else{
            if(this.getTaskByName(file.getTaskName()).getTaskClass().getCount() == 0)
                return true;
        }
        return false;
    }


    /** 任务是否完成 */
    public boolean isSuccess(String taskName){
        if(issetTask(taskName)){
            TaskFile file = TaskFile.getTask(taskName);
            return isSuccess(file);
        }
        return false;
    }


    /** 任务是否完成 */
    public boolean isSuccess(TaskFile taskName){
        init();
        if(issetTask(taskName.getTaskName())){
            PlayerTaskClass use = getTaskByName(taskName.getTaskName()).getTaskClass();
            if(use.getOpen()){
                int i = 0;
                for(TaskItem item:use.getValue()){
                    if(item != null)
                        if(TaskItemSuccess(item,taskName)){
                            i++;
                        }
                }
                if(i == use.getValue().length){
                    return true;
                }
            }
        }
        return false;
    }

    /** 输入玩家 */
    private boolean TaskItemSuccess(TaskItem item,TaskFile file){
        for(TaskItem item1:file.getTaskItem()){
            if(item.equals(item1)){
                if(item.getEndCount() >= item1.getEndCount())
                    return true;
            }
        }
        return false;
    }



    /** 获取玩家所有任务 */
    public LinkedList<playerTask> getPlayerTasks() {
        return playerTasks;
    }



    /** 减少任务分支 */
    public boolean delTaskItemByTask(String taskName,TaskItem item){
        playerTask task = getTaskByName(taskName);
        if(canIn_ArrayTaskItem(task,item)){
            try{
                TaskItem[] taskItems = task.getTaskClass().getValue();
                TaskItem[] newItem = new TaskItem[taskItems.length-1];
                int i = 1;
                for(TaskItem item1:taskItems){
                    if(!item1.equals(item)){
                        newItem[i] = item1;
                    }
                    i++;
                }
                task.getTaskClass().setValue(newItem);
                return true;
            }catch (ArrayIndexOutOfBoundsException e){
                return false;
            }
        }
        return false;
    }

    /** 添加任务分支 */
    public void addTaskItemByTask(String taskName,TaskItem item){
        playerTask task = getTaskByName(taskName);
        if(task != null){
            if(!canIn_ArrayTaskItem(task,item)){
                TaskItem[] items = task.getTaskClass().getValue();
                TaskItem[] newItem = new TaskItem[items.length+1];
                System.arraycopy(items,0,newItem,0,newItem.length);
                newItem[items.length] = item;
                task.getTaskClass().setValue(newItem);
            }
        }
    }


    /** 设置玩家任务分支 */
    public void setTaskValue(String taskName,TaskItem item){
        playerTask task =  getTaskByName(taskName);
        PlayerTaskClass playerTaskClass = task.getTaskClass();
        playerTaskClass.setLoad(item);
        task.setTaskClass(playerTaskClass);
        toSaveConfig(defaultConfig());
//
    }

    /** 获取玩家积分 */
    public int getCount(){
        Config config = RSTask.getTask().getPlayerConfig(playerName);
        return config.getInt("Count");
    }

    /** 设置玩家积分 */
    public void setCount(int value){
        toSaveConfig(defaultConfig(playerTasks,value));
    }


    /** 玩家完成任务 */
    public static void givePlayerSuccessItems(Player player, String taskName){
        if(TaskFile.isFileTask(taskName)){
            successTaskEvent event = new successTaskEvent(player,taskName);
            Server.getInstance().getPluginManager().callEvent(event);
        }
    }


    public static playerFile getPlayerFile(String playerName){
        return new playerFile(playerName);
    }



    private void toSaveConfig(LinkedHashMap<String,Object> maps){
        Config config = RSTask.getTask().getPlayerConfig(playerName);
        config.setAll(maps);
        config.save();
    }


    private LinkedHashMap<String,Object> defaultConfig(){
        return defaultConfig(playerTasks,RSTask.getTask().getPlayerConfig(playerName).getInt("Count"));
    }


    private LinkedHashMap<String,Object> defaultConfig(LinkedList<playerTask> tasks){
        return defaultConfig(tasks,RSTask.getTask().getPlayerConfig(playerName).getInt("Count"));
    }


    private LinkedHashMap<String,Object> defaultConfig(LinkedList<playerTask> tasks,int count){
        LinkedHashMap<String,Object> playerTasks = new LinkedHashMap<>();
        for(playerTask task:tasks){
            playerTasks.putAll(task.toSaveConfig());
        }
        return new LinkedHashMap<String,Object>(){{
            put("Task",playerTasks);
            put("Count",count);
        }};
    }


}
