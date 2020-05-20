package com.task.utils.tasks;



import cn.nukkit.Player;
import cn.nukkit.Server;

import cn.nukkit.utils.Config;
import com.task.RSTask;
import com.task.utils.DataTool;
import com.task.utils.tasks.taskitems.*;
import com.task.utils.events.PlayerAddTaskEvent;

import java.io.File;
import java.util.*;

/**
 *   ____  ____ _____         _
 |  _ \/ ___|_   _|_ _ ___| | __
 | |_) \___ \ | |/ _` / __| |/ /
 |  _ < ___) || | (_| \__ \   <
 |_| \_\____/ |_|\__,_|___/_|\_\

 @author 若水
 */
public class TaskFile {


    /** 任务名称 */
    private String taskName;

    /** 任务难度 */
    private int star;

    /** 任务分组 */
    private int group;

    /** 任务介绍 */
    private String taskMessage;

    /** 完成次数限制*/
    private int successCount = 1;

    /** 奖励物品 */
    private SuccessItem successItem;

    /** 首次完成奖励*/
    private SuccessItem firstSuccessItem;

    /** 任务内容 */
    private TaskItem[] taskItem;

    /** 任务类型 */
    private TaskType type;

    /** 上一个任务 */
    private String task;

    private LinkedList<String> notInviteTasks;

    private LinkedList<String> notToInviteTasks;
    /** 刷新时间 */
    private int day;

    /**持续时间*/
    private int loadDay = -1;

    /** 完成公告类型(0/1) */
    private int messageType;

    /** 公告内容 */
    private String broadcastMessage;

    /** 按键图片*/
    private TaskButton button;


    public enum TaskType{
        BlockBreak("破坏"),
        BlockPlayer("放置"),
        DropItem("丢弃"),
        CollectItem("收集"),
        CraftItem("合成"),
        GetItem("获得"),
        EatItem("吃"),
        GetWater("打水"),
        Click("点击"),
//        RPGLevel("RPG等级(需API)"),
//        RPGAtt("RPG属性(需API)"),
//        RPGPF("RPG评级(需API)"),
        DIY("自定义");
        protected String taskType;
        TaskType(String taskType){

            this.taskType = taskType;
        }

        public String getTaskType() {
            return taskType;
        }
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem){
        this(taskName,type,taskItem,"无");
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage){
        this(taskName,type,taskItem,taskMessage,1);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star){
        this(taskName,type,taskItem,taskMessage,star,null);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, SuccessItem item){
        this(taskName,type,taskItem,taskMessage,star,item,null);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, SuccessItem item, String task){
        this(taskName,type,taskItem,taskMessage,star,item,task,0);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, SuccessItem item, String task, int day){
        this(taskName,type,taskItem,taskMessage,star,item,null,task,day,0);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, SuccessItem item, SuccessItem firstItem, String task, int day, int messageType){
        this(taskName,type,taskItem,taskMessage,star,item,firstItem,task,day,messageType,"§l§c[§b任务系统§c]§e恭喜 §a%p §e完成了§d[ %s ]§e任务");
    }
    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, SuccessItem item, SuccessItem firstItem, String task, int day, int messageType, String broadcastMessage){
        this(taskName,type,taskItem,taskMessage,star,item,firstItem,task,day,messageType,broadcastMessage,new TaskButton(""));
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, SuccessItem item, SuccessItem firstItem, String task, int day, int messageType, String broadcastMessage, TaskButton button){
        this.type = type;
        this.taskItem = taskItem;
        this.taskName = taskName;
        this.taskMessage = taskMessage;
        this.star = star;
        this.group = star-1;
        this.successItem = item;
        this.task = task;
        this.day = day;
        this.firstSuccessItem = firstItem;
        this.messageType = messageType;
        this.button = button;
        this.broadcastMessage = broadcastMessage;
    }

    public TaskType getType() {
        return type;
    }

    public int getStar() {
        return star;
    }

    public String getTaskMessage() {
        return taskMessage;
    }

    public String getTaskName() {
        return taskName;
    }

    public SuccessItem getSuccessItem() {
        return successItem;
    }

    public SuccessItem getFirstSuccessItem() {
        return firstSuccessItem;
    }

    public TaskItem[] getTaskItem() {
        return taskItem;
    }

    public String getLastTask() {
        return task;
    }

    public void toSaveConfig(){
        if(taskName == null) {
            return;
        }
        if(!isFileTask(taskName)){
            RSTask.getTask().saveResource("Task.yml","/Tasks/"+ taskName +".yml",false);
        }
        LinkedHashMap<String,Object> taskitems = new LinkedHashMap<>();
        if(taskItem != null){
            for(TaskItem taskItem:taskItem){
                taskitems.putAll(taskItem.toSaveConfig());
            }
        }
        Config config = RSTask.getTask().getTaskConfig(taskName);

        config.set("任务难度",star);
        config.set("任务分组",group);
        config.set("任务介绍",taskMessage == null ? "无":taskMessage);
        config.set("刷新时间(分钟)",day);
        config.set("持续时间(分钟)",loadDay);
        config.set("任务类型",type.getTaskType());
        config.set("完成次数限制",successCount);
        if(task != null && !task.equals("null")) {
            config.set("完成此任务前需完成",task);
        }
        config.set("完成以下任务不能领取此任务",notInviteTasks);
        config.set("领取以下任务不能领取此任务",notToInviteTasks);
        config.set("任务内容",taskitems);
        if(firstSuccessItem != null){
            config.set("首次完成奖励",firstSuccessItem.toSaveConfig());
        } else {
            config.set("首次完成奖励",successItem.toSaveConfig());
        }
        config.set("奖励",successItem.toSaveConfig());
        config.set("完成公告类型(0/1)",messageType);
        config.set("公告内容",broadcastMessage);
        config.set("自定义按键图片",button.toSaveConfig());
        config.save();
        RSTask.getTask().taskConfig.put(taskName,config);
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public int getGroup() {
        return group;
    }

    public void setDay(int day) {
        this.day = day;
    }



    public void setStar(int star) {
        this.star = star;
    }


    public void setButton(TaskButton button) {
        this.button = button;
    }

    public TaskButton getButton() {
        return button;
    }

    public void setBroadcastMessage(String broadcastMessage) {
        this.broadcastMessage = broadcastMessage;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public void setFristSuccessItem(SuccessItem fristSuccessItem) {
        this.firstSuccessItem = fristSuccessItem;
    }

    public void setSuccessItem(SuccessItem successItem) {
        this.successItem = successItem;
    }

    public void setTaskItem(TaskItem[] taskItem) {
        this.taskItem = taskItem;
    }

    public void setLastTask(String task) {
        this.task = task;
    }

    public void setTaskMessage(String taskMessage) {
        taskMessage = taskMessage;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void setType(TaskType type) {
        this.type = type;
    }

    public int getDay() {
        return day;
    }


    public void addTaskItem(TaskItem item){
        TaskItem[] items = getTaskItem();
        TaskItem[] newItem = new TaskItem[items.length+1];
        System.arraycopy(items,0,newItem,0,newItem.length);
        newItem[items.length] = item;
        this.setTaskItem(newItem);
    }


    public void removeTaskItem(TaskItem item){
        if(canInArrayTaskItem(item)){
            TaskItem[] taskItems = taskItem;
            TaskItem[] newItem = new TaskItem[taskItems.length-1];
            int i = 1;
            for(TaskItem item1:taskItems){
                if(!item1.equals(item)){
                    newItem[i] = item1;
                }
                i++;
            }
            taskItem = newItem;
        }
    }

    public boolean canInArrayTaskItem(TaskItem item){
        for(TaskItem item1:taskItem){
            if(item1.equals(item)) {
                return true;
            }
        }
        return false;
    }


    public int getMessageType() {
        return messageType;
    }

    public String getBroadcastMessage() {
        return broadcastMessage;
    }

    public static boolean isFileTask(String taskName){
        File file = new File(RSTask.getTask().getDataFolder()+"/Tasks/"+taskName+".yml");
        return file.exists();
    }

    public static TaskFile getTask(String taskName){
        return RSTask.getTask().tasks.get(taskName);
    }

    private static TaskFile toTask(String taskName){
        try{
            if(isFileTask(taskName)){
                Config config = RSTask.getTask().getTaskConfig(taskName);
                String sType = config.getString("任务类型");
                int succount = config.getInt("完成次数限制",1);
                TaskType type = null;
                for(TaskType taskType:TaskType.values()){
                    if(taskType.getTaskType().equals(sType)){
                        type = taskType;
                        break;
                    }
                }
                Map map = (Map) config.get("任务内容");
                TaskItem[] taskItems;
                if(map != null){
                    taskItems = new TaskItem[map.size()];
                    int i = 0;
                    for(Object os :map.keySet()){
                        if(os  instanceof String){
                            taskItems[i] = TaskItem.toTaskItem(taskName,new LinkedHashMap<String,Integer>() {{
                                put((String) os ,Integer.parseInt(String.valueOf( map.get(os ))));}});
                            i++;
                        }
                    }
                }else{
                    return null;
                }
                Map firstSuccess = (Map) config.get("首次完成奖励");
                SuccessItem first =
                        SuccessItem.toSuccessItem(firstSuccess);
                Map success = (Map) config.get("奖励");
                SuccessItem second =
                        SuccessItem.toSuccessItem(success);
                if(type == null) {
                    return null;
                }
                TaskFile file = new TaskFile(taskName,type,taskItems,config.getString("任务介绍")
                        ,config.getInt("任务难度"),second,first,config.getString("完成此任务前需完成"),
                        +config.getInt("刷新时间(分钟)",0),config.getInt("完成公告类型(0/1)"),config.getString("公告内容"),TaskButton.toTaskButton((Map) config.get("自定义按键图片")));
                file.setGroup(config.getInt("任务分组",file.getStar() - 1));
                file.setSuccessCount(succount);
                file.setNotInviteTasks(new LinkedList<>(config.getStringList("完成以下任务不能领取此任务")));
                file.setNotToInviteTasks(new LinkedList<>(config.getStringList("领取以下任务不能领取此任务")));
                file.setLoadDay(config.getInt("持续时间(分钟)",1440));
                return file;
            }

        }catch (Exception e){
            e.printStackTrace();
            Server.getInstance().getLogger().error("读取"+taskName+"任务文件出现错误 可能是因为已经不存在或者 配置出现问题");
            File file = new File(RSTask.getTask().getDataFolder()+"/Tasks/"+taskName+".yml");
            if(file.exists()){
                Server.getInstance().getLogger().error("更新报错: 检测到"+taskName+"存在，已删除"+taskName+".yml文件");
                if(!file.delete()){
                    Server.getInstance().getLogger().error("删除"+taskName+".yml失败");
                }
            }
            return null;
        }
        return null;
    }

    public void setLoadDay(int loadDay) {
        this.loadDay = loadDay;
    }

    public int getLoadDay() {
        return loadDay;
    }

    public void setNotToInviteTasks(LinkedList<String> notToInviteTasks) {
        this.notToInviteTasks = notToInviteTasks;
    }

    public LinkedList<String> getNotToInviteTasks() {
        return notToInviteTasks;
    }

    public LinkedList<String> getNotInviteTasks() {
        return notInviteTasks;
    }

    public void setNotInviteTasks(LinkedList<String> notInviteTasks) {
        this.notInviteTasks = notInviteTasks;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public boolean close(){
        if(isFileTask(this.taskName)){
            File file = new File(RSTask.getTask().getDataFolder()+"/Tasks/"+ taskName +".yml");
            return file.delete();
        }
        return true;
    }


    /** 根据TaskItem 获取count
     *
     * @param item 任务进度类
     *
     * @return 获取进度*/
    public int getCountByTaskItem(TaskItem item){
        for(TaskItem item1:taskItem){
            if(item1.equals(item)) {
                return item1.getEndCount();
            }
        }
        return 0;
    }

    /** 获取所有任务文件
     * @return 任务类*/
    public static LinkedHashMap<String,TaskFile> getTasks(){
        File file = new File(RSTask.getTask().getDataFolder()+"/Tasks");
        LinkedHashMap<String,TaskFile> names = new LinkedHashMap<>();
        File[] files = file.listFiles();
        if(files != null){
            Arrays.sort(files);
            for(File file1:files){
                if(file1.isFile()){
                    String name = file1.getName().substring(0,file1.getName().lastIndexOf("."));
                    TaskFile file2 = TaskFile.toTask(name);
                    if(file2 != null) {
                        names.put(name,file2);
                    }
                }
            }
        }
        return names;
    }

    /** 获取任务分组中的全部任务
     * @param group 分组
     *
     * @return 任务文件列表
     * */
    public static LinkedList<TaskFile> getDifficultyTasks(int group){
        LinkedList<TaskFile> files = new LinkedList<>();
        for (TaskFile file:RSTask.getTask().tasks.values()){
            if(file.getGroup() == group) {
                files.add(file);
            }
        }
        return files;
    }

    /**
     * 玩家点击任务后进行的一系列判断
     * @param player 玩家
     * @param file 任务文件
     *
     * @return 是否成功*/
    public static boolean runTaskFile(Player player,TaskFile file){
        PlayerFile file1 = PlayerFile.getPlayerFile(player.getName());
        PlayerFile.PlayerTaskType type = file1.getTaskType(file);

        if(type == PlayerFile.PlayerTaskType.can_Invite || type == PlayerFile.PlayerTaskType.isSuccess_canInvite){
            int starCount = RSTask.starNeed(file.getGroup());
            if(RSTask.canOpen() && file1.getCount() < starCount){
                player.sendMessage(RSTask.getTask().getLag("not-add-task","§c[任务系统] 抱歉，此任务不能领取"));
                return false;
            }else{
                PlayerAddTaskEvent event1= new PlayerAddTaskEvent(player,file);
                Server.getInstance().getPluginManager().callEvent(event1);
                return true;
            }
        }
        if(type == PlayerFile.PlayerTaskType.Running || type == PlayerFile.PlayerTaskType.Success){
            return true;
        }
        if((file.getLastTask() != null && !file.getLastTask().equals("null") && !file.getLastTask().equals(""))){
            if(!file1.isSuccessed(file.getLastTask())){
                player.sendMessage(RSTask.getTask().getLag("useLastTask").replace("%s",file.getLastTask()));
                return false;
            }
        }
        if(file1.isSuccessed(file.getTaskName())){
            if(file.getSuccessCount() != -1) {
                if(file.getSuccessCount() <= file1.getSuccessedCount(file.getTaskName())) {
                    player.sendMessage(RSTask.getTask().getLag("repeat-collection"));
                    return false;
                }
            }
        }


        if(!file1.inDay(file.getTaskName()) ){
            int day = file.getDay();
            int out = DataTool.getTime(file1.getTaskByName(file.getTaskName()).getTaskClass().getTime());
            player.sendMessage(RSTask.getTask().getLag("repeat-inDay").
                    replace("%c",((day > out)?(day - out):0)+""));
            return false;
        }
        if(type == PlayerFile.PlayerTaskType.No_Invite){
            player.sendMessage(RSTask.getTask().getLag("not-add-task","§c[任务系统] 抱歉，此任务不能领取"));
            return false;
        }

        return true;
    }




}
