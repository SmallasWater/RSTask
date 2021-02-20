package com.task.utils.tasks;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import cn.nukkit.utils.Config;
import com.task.RsTask;
import com.task.utils.ItemIDSunName;
import com.task.utils.tasks.taskitems.*;
import com.task.utils.DataTool;
import com.task.events.PlayerCanInviteTaskEvent;
import com.task.events.PlayerTaskCloseEvent;
import com.task.events.SuccessTaskEvent;
import com.task.events.UseTaskEvent;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;


/**
 * @author 若水
 */
public class PlayerFile {

    private String playerName;

    private int count;


    private LinkedList<PlayerTask> playerTasks = new LinkedList<>();

    public static PlayerFile getPlayerFile(String playerName){
        if(!RsTask.getTask().playerFiles.containsKey(playerName)){
            RsTask.getTask().playerFiles.put(playerName,new PlayerFile(playerName));
        }
        return RsTask.getTask().playerFiles.get(playerName);
    }


    public String getPlayerName() {
        return playerName;
    }

    private PlayerFile(String playerName){
        this.playerName = playerName;
        this.init();
    }

    private boolean canSyncTaskItem(){

        for(PlayerTask task:playerTasks){
            if(task.getTaskFile() == null){
                playerTasks.remove(task);
                continue;
            }
            TaskItem[] items = task.getTaskFile().getTaskItem();
            if(items.length == task.getTaskClass().getValue().length){
                for(TaskItem item:items){
                    if(item != null){
                        if(!canInArrayTaskItem(task,item)){
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
        Config config = RsTask.getTask().getPlayerConfig(playerName);
        LinkedList<PlayerTask> tasks = new LinkedList<>();
        Map map = (Map) config.get("Task");
        if(map != null){
            for(Object name:map.keySet()){
                if(TaskFile.isFileTask((String) name)){
                    PlayerTaskClass taskClass = PlayerTaskClass.toPlayerTaskClass((String)name,(Map)map.get(name));
                    if(taskClass != null){
                        tasks.add(new PlayerTask(taskClass));
                    }
                }
            }
        }
        this.playerTasks = tasks;
        this.count = config.getInt("Count");

    }

    /** 玩家是否有这个任务
     * @param taskName 任务名称
     *
     * @return 是否拥有
     * */
    public boolean issetTask(String taskName){
        for(PlayerTask t:playerTasks){
            if(t.getTaskName().equals(taskName)){
                return true;
            }
        }
        return false;
    }

    /** 玩家是否有这个任务
     * @param task 玩家任务类
     *
     * @return 是否拥有
     * */
    public boolean issetTask(PlayerTask task){
        return issetTask(task.getTaskName());
    }

    /** 玩家是否有这个任务
     * @param task 任务文件
     *
     * @return 是否拥有
     * */
    public boolean issetTask(TaskFile task){
        return issetTask(task.getTaskName());
    }



    /**
     * 给玩家增加一个任务
     * @param taskName 任务名称
     * */
    public void addTask(String taskName){
        this.addTask(new PlayerTask(taskName));
    }

    /**
     * 给玩家增加一个任务
     * @param taskName 任务文件
     * */
    public void addTask(TaskFile taskName){
        this.addTask(new PlayerTask(taskName));

    }


    /**
     * 给玩家增加一个任务
     * @param task 玩家任务
     * */
    public void addTask(PlayerTask task){
        if(!issetTask(task)){
            task.getTaskClass().setOpen(true);
            playerTasks.add(task);

        }else{
            PlayerTask t = getTaskByName(task.getTaskName());
            PlayerTaskClass playerTaskClass = t.getTaskClass();
            playerTaskClass.setOpen(true);
            playerTaskClass.setTime(new Date());
            t.setTaskClass(playerTaskClass);
        }
    }




    /**
     * 给玩家移除一个任务
     * @param taskName 任务名称
     *
     * @return 是否移除成功
     * */
    public boolean delTask(String taskName){
        if(issetTask(getTaskByName(taskName))){
            LinkedList<PlayerTask> tasks = new LinkedList<>();
            for(PlayerTask task:playerTasks){
                if(!task.getTaskName().equals(taskName)){
                    tasks.add(task);
                }
            }
            playerTasks = tasks;
            return true;
        }
        return false;
    }


    /** 获取玩家任务分支
     * @param taskName 任务名
     * @return 获取任务进度*/
    public TaskItem[] getTaskItems(String taskName){
        PlayerTaskClass playerTaskClass = getTaskByName(taskName).getTaskClass();
        return playerTaskClass.getValue();
    }


    /** 给玩家任务的分支加点
     * @param taskName 任务名
     * @param valueName 进度名称
     * @param value 进度大小
     *
     * @return 是否加点成功*/
    public boolean addTaskValue(String taskName,String valueName,int value){
        PlayerTask task = getTaskByName(taskName);
        if(task != null){
            TaskItem[] taskItems = task.getTaskClass().getValue();
            int i = 0;
            for(TaskItem item:taskItems){
                if(item != null) {
                    if (item.getTask().equals(valueName)) {
                        i++;
                        item.addEndCount(value);
                        Boolean event = getBoolean(taskName, task, item);
                        if (!event) {
                            return false;
                        }
                        break;
                    }
                }
            }
            return i > 0;

        }
        return false;
    }

    private Boolean getBoolean(String taskName, PlayerTask task, TaskItem item) {
        setTaskValue(taskName, item);
        Player pl = Server.getInstance().getPlayer(playerName);
        if(pl != null) {
            UseTaskEvent event = new UseTaskEvent(pl,task);
            Server.getInstance().getPluginManager().callEvent(event);
            return !event.isCancelled();
        }
        return true;
    }

    /** 关闭一个任务
     * @param taskName 任务名称
     * @return 是否关闭成功
     * */
    public boolean closeTask(String taskName){
        if(getTaskByName(taskName) != null) {
            PlayerTask file = getTaskByName(taskName);
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
            TaskFile file1 = TaskFile.getTask(taskName);
            if(file1 != null) {
                PlayerTaskCloseEvent event = new PlayerTaskCloseEvent(this, file1);
                Server.getInstance().getPluginManager().callEvent(event);
            }
            return true;
        }
        return false;
    }

    /** 给玩家任务的分支设置任务点
     * @param taskName 任务名称
     * @param valueName 进度名称
     * @param value 进度大小
     *
     * @return 是否设置成功*/
    public boolean setTaskValue(String taskName,String valueName,int value){
        PlayerTask task = getTaskByName(taskName);
        if(task != null){
            TaskItem[] taskItems = task.getTaskClass().getValue();
            for(TaskItem item:taskItems){
                if(item != null){
                    if(item.getTask().equalsIgnoreCase(valueName)){
                        item.setEndCount(value);
                        return getBoolean(taskName, task, item);
                    }
                }

            }
        }

        return false;
    }

    /** 判断任务分支是否存在于此任务
     *
     * @param task 玩家任务
     * @param item 任务进度类
     *
     * @return 是否存在*/
    public boolean canInArrayTaskItem(PlayerTask task, TaskItem item){
        TaskItem[] items = task.getTaskClass().getValue();
        for(TaskItem item1:items){
            if(item1.equals(item)){
                return true;
            }
        }
        return false;
    }


    /**  设置玩家任务状态
     * @param task 玩家任务类
     * */
    public void setPlayerTask(PlayerTask task){
        PlayerTask tasks = getTaskByName(task.getTaskName());
        if(tasks != null){
            tasks.sync(task);
        }else{
            addTask(task);
            if(!issetTask(task)){
                Server.getInstance().getLogger().warning("读取玩家任务出现异常");
            }
        }
    }



    /** 根据任务名获取分支任务 */
    public PlayerTask getTaskByName(String taskName){
        for(PlayerTask task:playerTasks){
            if(task.getTaskName().equals(taskName)){
                return task;
            }
            if(task.getTaskFile().getShowName().equalsIgnoreCase(taskName)){
                return task;
            }
        }
        return null;
    }

    private LinkedList<PlayerTask> getSucceed(){
        LinkedList<PlayerTask> tasks = new LinkedList<>();
        for(PlayerTask task:getPlayerTasks()){
            if(isSuccessed(task.getTaskName())){
                tasks.add(task);
            }
        }
        return tasks;
    }

    private boolean isHaveNotInviteTask(String taskName){
        TaskFile file = TaskFile.getTask(taskName);
        if(getSucceed().size() > 0) {
            for (PlayerTask task : getSucceed()) {
                if (file.getNotInviteTasks().contains(task.getTaskName())) {
                    return true;
                }
            }
        }
        if(getPlayerTasks().size() > 0) {
            for (PlayerTask task : getPlayerTasks()) {
                if (file.getNotToInviteTasks().contains(task.getTaskName())) {
                    return true;
                }
            }
        }
        return false;
    }

    /** 是否能领取 */
    public boolean canInvite(String taskName){
        TaskFile file = TaskFile.getTask(taskName);
        if(file != null) {
            PlayerCanInviteTaskEvent event = new PlayerCanInviteTaskEvent(playerName,file);
            Server.getInstance().getPluginManager().callEvent(event);
            if(event.isCancelled()){
                return false;
            }
            if(isHaveNotInviteTask(taskName)){
                return false;
            }
            // 任务上一级
            String last = file.getLastTask();
            //判断玩家是否做过这个
            if (issetTask(taskName)) {

                if (isRunning(taskName)) {
                    return false;
                }
                if(isSuccess(taskName)){
                    return false;
                }
                if(file.getSuccessCount() != -1){
                    if(file.getSuccessCount() <= getSuccessedCount(taskName)){
                        return false;
                    }
                }
                if (file.getDay() > 0) {
                    return inDay(taskName);
                }else{
                    return true;
                }

                //计算冷却时间
            } else {
                if (last != null && !"null".equals(last)) {
                    return isSuccessed(last);
                }else{
                    return true;
                }
            }
        }
        return false;
    }



    /** 是否完成过 */
    public boolean isSuccessed(String taskName){
        if(issetTask(taskName)){
            PlayerTask playerTask = getTaskByName(taskName);
            return (playerTask.getTaskClass().getCount() > 0);
        }
        return false;
    }

    /** 获取完成次数 */
    public int getSuccessedCount(String taskName){
        int count = 0;
        if(issetTask(taskName)){
            PlayerTask playerTask = getTaskByName(taskName);
            count = playerTask.getTaskClass().getCount();
        }
        return count;
    }



    /** 任务是否冷却结束 */
    public boolean inDay(String taskName){
        PlayerTask task = getTaskByName(taskName);
        TaskFile file = TaskFile.getTask(taskName);
        if(task != null && file != null){
            Date date = task.getTaskClass().getTime();
            int t = file.getDay();
            if(t > 0){
                return DataTool.getTime(date) >= t;
            }

        }
        return true;
    }


    /** 任务是否进行中 */
    public boolean isRunning(String taskName) {
        PlayerTask task = this.getTaskByName(taskName);
        return !isSuccess(taskName) && task != null && task.getTaskClass().getOpen();
    }


    /** 获取任务状态 */
    public PlayerTaskType getTaskType(TaskFile taskFile){
        if(issetTask(taskFile.getTaskName())){
            if(canInvite(taskFile.getTaskName()) && !isSuccessed(taskFile.getTaskName())){
                return PlayerTaskType.can_Invite;
            }else{
                if(isSuccessed(taskFile.getTaskName()) && canInvite(taskFile.getTaskName())){
                    return PlayerTaskType.isSuccess_canInvite;
                }else{
                    if(isRunning(taskFile.getTaskName())){
                        return PlayerTaskType.Running;
                    }
                    if(isSuccess(taskFile)){
                        return PlayerTaskType.Success;
                    }
                    if(isSuccessed(taskFile.getTaskName()) && !canInvite(taskFile.getTaskName())){
                        return PlayerTaskType.isSuccess_noInvite;
                    }
                }
            }
            return PlayerTaskType.No_Invite;
        }else{
            if(canInvite(taskFile.getTaskName())) {
                String last = taskFile.getLastTask();
                if (last != null && !"null".equals(last)) {
                    if (issetTask(last) && isSuccessed(last)) {
                        return PlayerTaskType.can_Invite;
                    } else {
                        return PlayerTaskType.No_Invite;
                    }
                } else {
                    return PlayerTaskType.can_Invite;
                }
            }else{
                return PlayerTaskType.No_Invite;
            }
        }
    }

    /** 获取完成过可领取任务 */
    public LinkedList<PlayerTask> getisSuccessedAndCanInvite(){
        return getTasksByType(PlayerTaskType.isSuccess_canInvite);
    }

    /** 获取完成过不可领取任务 */
    public LinkedList<PlayerTask> getisSuccessedAndNotInvite(){
        return getTasksByType(PlayerTaskType.isSuccess_noInvite);
    }

    /** 获取可以领取任务 */
    public LinkedList<PlayerTask> getCanInviteTasks(int star){
        LinkedList<PlayerTask> tasks = new LinkedList<>();
        LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(star);
        for(TaskFile file:taskFiles){
            if(canInvite(file.getTaskName())){
                tasks.add(new PlayerTask(file));
            }
        }
        return tasks;
    }


    /** 获取不能领取任务 */
    public LinkedList<PlayerTask> getNoInviteTasks(int star){
        LinkedList<PlayerTask> tasks = new LinkedList<>();
        LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(star);
        for(TaskFile file:taskFiles){
            if(file.getSuccessCount() <= getSuccessedCount(file.getTaskName())){
                tasks.add(new PlayerTask(file));
            }else if(isSuccess(file.getTaskName()) && inDay(file.getTaskName())){
                tasks.add(new PlayerTask(file));
            }
        }

        return tasks;
    }


    /** 获取进行任务（不包括完成与不可领取的） */
    public LinkedList<PlayerTask> getInviteTasks(){
        return getTasksByType(PlayerTaskType.Running);
    }

    public LinkedList<PlayerTask> getInviteTasks(int level){
        return getTasksByType(PlayerTaskType.Running,level);
    }

    /** 获取已完成(达到要求)任务 */
    public LinkedList<PlayerTask> getSuccessTasks(){
        return getTasksByType(PlayerTaskType.Success);
    }

    public LinkedList<PlayerTask> getSuccessTasks(int level){
        return getTasksByType(PlayerTaskType.Success,level);
    }




    public LinkedList<PlayerTask> getTasksByType(PlayerTaskType taskType, int level){
        LinkedList<PlayerTask> tasks = new LinkedList<>();
        for(PlayerTask task:playerTasks){
            if(task.getTaskFile().getGroup() == level){
                if(getTaskType(task.getTaskFile()) == taskType){
                    tasks.add(task);
                }
            }
        }
        return tasks;
    }

    public enum PlayerTaskType{
        /** 是否可以被领取*/
        can_Invite,
        /** 完成*/
        Success,
        /** 不能被领取*/
        No_Invite,
        /** 进行中*/
        Running,
        /** 完成后可以领取*/
        isSuccess_canInvite,
        /** 完成后不能领取*/
        isSuccess_noInvite,
    }

    private LinkedList<PlayerTask> getTasksByType(PlayerTaskType taskType){
        LinkedList<PlayerTask> tasks = new LinkedList<>();
        for(PlayerTask task:playerTasks) {
            if(getTaskType(task.getTaskFile())==taskType){
                tasks.add(task);
            }
        }
        return tasks;
    }

    /** 判断此难度是否解锁(需开启积分验证)
     1级难度默认解锁 */
    public boolean canLock(int star) {
        return RsTask.countChecking && (getCount() >= DataTool.starNeed(star));
    }


    /** 是否第一次领取*/
    public boolean isFirst(TaskFile file){
        if(!issetTask(file)){
            return true;
        }else{
            return this.getTaskByName(file.getTaskName()).getTaskClass().getCount() == 0;
        }
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
        if(issetTask(taskName.getTaskName())){
            PlayerTaskClass use = getTaskByName(taskName.getTaskName()).getTaskClass();
            if(use.getOpen()){
                int i = 0;
                for(TaskItem item:use.getValue()){
                    if(item != null) {
                        if(TaskItemSuccess(item,taskName)){
                            i++;
                        }
                    }
                }
                return i == use.getValue().length;
            }
        }
        return false;
    }

    /** 输入玩家 */
    private boolean TaskItemSuccess(TaskItem item,TaskFile file){
        for(TaskItem item1:file.getTaskItem()){
            if(item.equals(item1)){
                if(item.getEndCount() >= item1.getEndCount()) {
                    return true;
                }
            }
        }
        return false;
    }



    /** 获取玩家所有任务 */
    public LinkedList<PlayerTask> getPlayerTasks() {
        return playerTasks;
    }



    /** 减少任务分支 */
    public boolean delTaskItemByTask(String taskName,TaskItem item){
        PlayerTask task = getTaskByName(taskName);
        if(canInArrayTaskItem(task,item)){
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
        PlayerTask task = getTaskByName(taskName);
        if(task != null){
            if(!canInArrayTaskItem(task,item)){
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
        PlayerTask task =  getTaskByName(taskName);
        PlayerTaskClass playerTaskClass = task.getTaskClass();
        playerTaskClass.setLoad(item);
        task.setTaskClass(playerTaskClass);

//
    }

    /** 获取玩家积分 */
    public int getCount(){
        return count;
    }

    /** 设置玩家积分 */
    public void setCount(int value){
        this.count = value;
//        toSaveConfig(defaultConfig(playerTasks,value));
    }

    /** 玩家完成任务 */
    public static void givePlayerSuccessItems(Player player, String taskName,boolean pass){
        if(!pass){
            PlayerFile file = PlayerFile.getPlayerFile(player.getName());
            if(!file.isSuccess(taskName) ){
                return;
            }
        }
        if(TaskFile.isFileTask(taskName)){
            if(player != null) {
                SuccessTaskEvent event = new SuccessTaskEvent(player, taskName);
                Server.getInstance().getPluginManager().callEvent(event);
                if(!event.isCancelled()){
                    playerSuccessTask(player, taskName,event);
                }
            }
        }
    }

    /** 玩家完成任务 */
    public static void givePlayerSuccessItems(Player player, String taskName){
        givePlayerSuccessItems(player, taskName,false);
    }

    private synchronized static void playerSuccessTask(Player player,String taskName,SuccessTaskEvent event){
        TaskFile file = TaskFile.getTask(taskName);
        int success = 0;
        if(file != null && player.isOnline()){
            if(file.getType() == TaskFile.TaskType.CollectItem){
                TaskItem[] items1 = file.getTaskItem();
                for(TaskItem item:items1){
                    ItemClass itemClass = ItemClass.toItem(item);
                    if(itemClass != null){
                        itemClass.getItem().setCount(item.getEndCount());
                        if(DataTool.getCount(player,itemClass) >= itemClass.getItem().getCount()){
                            player.getInventory().removeItem(itemClass.getItem());
                        }else{
                            event.setCancelled();
                            player.sendMessage(RsTask.getTask().getLag("error-task","§d§l[任务系统]§c出现异常!!!"));
                            return;
                        }
                    }
                }
            }

            SuccessItem item;
            if(PlayerFile.getPlayerFile(player.getName()).isFirst(file)){
                item = file.getFirstSuccessItem();
            }else{
                item = file.getSuccessItem();
            }
            if(item.getItem() != null && item.getItem().length > 0){
                for(ItemClass itemClass:item.getItem()){
                    if(itemClass != null){
                        player.getInventory().addItem(itemClass.getItem().clone());
                        player.sendMessage(RsTask.getTask().getLag("add-item-message")
                                .replace("%s", ItemIDSunName.getIDByName(itemClass.getItem())).replace("%c",itemClass.getItem().getCount()+""));
                    }

                }
            }
            if(item.getCmd() != null && item.getCmd().length > 0){
                for(CommandClass commandClass:item.getCmd()){
                    if(commandClass != null){
                        Server.getInstance().getCommandMap().dispatch(new ConsoleCommandSender(),commandClass.getCmd().replace("%p",player.getName()));
                        player.sendMessage(RsTask.getTask().getLag("add-Cmd-message").replace("%s",commandClass.getSendMessage()));
                    }
                }
            }
            if(RsTask.loadEconomy){
                if(item.getMoney() > 0){
                    RsTask.getTask().getLoadMoney().addMoney(player,item.getMoney());
                    player.sendMessage(RsTask.getTask().getLag("add-money-message")
                            .replace("%c",item.getMoney()+"").
                                    replace("%m", RsTask.getTask().getCoinName()));
                }
            }
            if(RsTask.canOpen()) {
                if(item.getCount() > 0) {
                    success += item.getCount();
                }
            }
        }

        PlayerFile playerFiles = PlayerFile.getPlayerFile(player.getName());
        PlayerTask task = playerFiles.getTaskByName(taskName);
        if(task == null){
            playerFiles.addTask(taskName);
            task = playerFiles.getTaskByName(taskName);
        }
        initSuccessTask(task,playerFiles,success,player,taskName);

    }
    private static void initSuccessTask(PlayerTask task,PlayerFile playerFiles,int success,Player player,String taskName){
        PlayerTaskClass playerTaskClass = task.getTaskClass();
        TaskFile file;
        TaskItem[] items = playerTaskClass.getValue();
        for(TaskItem item:items){
            item.setEndCount(0);
        }
        playerTaskClass.setOpen(false);
        playerTaskClass.setValue(items);
        playerTaskClass.setCount(playerTaskClass.getCount()+1);
        playerTaskClass.setTime(new Date());
        task.setTaskClass(playerTaskClass);
        if(RsTask.canOpen()){
            playerFiles.setCount(playerFiles.getCount() + success);
        }

        playerFiles.setPlayerTask(task);
        Level level = player.getLevel();
        level.addSound(player.getPosition(), Sound.RANDOM_LEVELUP);
        RsTask.taskNames.remove(taskName);
        DataTool.spawnFirework(player);
        file = TaskFile.getTask(taskName);
        if(file != null){
            String send = file.getBroadcastMessage().
                    replace("%p",player.getName()).replace("%s",file.getName());
            if(file.getMessageType() == 0){
                Server.getInstance().broadcastMessage(send);
            }else{
                player.sendMessage(send);
            }
        }

    }



    private void toSaveConfig(LinkedHashMap<String,Object> maps){
        Config config = RsTask.getTask().getPlayerConfig(playerName);
        config.setAll(maps);
        config.save();
    }

    public boolean toSave(){
        Config config = RsTask.getTask().getPlayerConfig(playerName);
        if(defaultConfig().isEmpty()){
            return false;
        }
        config.setAll(defaultConfig());
        config.save();
        return true;
    }


    private LinkedHashMap<String,Object> defaultConfig(){
        return defaultConfig(playerTasks,count);
    }


    private LinkedHashMap<String,Object> defaultConfig(LinkedList<PlayerTask> tasks){
        return defaultConfig(tasks, count);
    }


    private LinkedHashMap<String,Object> defaultConfig(LinkedList<PlayerTask> tasks, int count){
        LinkedHashMap<String,Object> playerTasks = new LinkedHashMap<>();
        for(PlayerTask task:tasks){
            playerTasks.putAll(task.toSaveConfig());
        }
        return new LinkedHashMap<String,Object>(){{
            put("Task",playerTasks);
            put("Count",count);
        }};
    }


    public int getTimeOutDay(String taskName){
        PlayerTask task = getTaskByName(taskName);

        if(task != null){
            TaskFile file = task.getTaskFile();
            int time = file.getLoadDay();
            int del = time - DataTool.getTime(task.getTaskClass().getTime());
            if(del <= 0){
                del = -1;
            }
            return del;
        }
        return -1;
    }

    @Override
    public String toString() {
        return defaultConfig().toString()+"; tasks: "+playerTasks.toString();
    }
}
