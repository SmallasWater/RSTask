package com.Task.utils.Tasks;



import cn.nukkit.Player;
import cn.nukkit.Server;

import cn.nukkit.utils.Config;
import com.Task.RSTask;
import com.Task.utils.DataTool;
import com.Task.utils.Tasks.TaskItems.*;
import com.Task.utils.events.playerAddTaskEvent;

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
    private String TaskName = null;

    /** 任务难度 */
    private int star = 1;

    /** 任务分组 */
    private int group;

    /** 任务介绍 */
    private String TaskMessage = null;

    /** 奖励物品 */
    private successItem successItem = null;

    /** 首次完成奖励*/
    private successItem fristSuccessItem = null;

    /** 任务内容 */
    private TaskItem[] taskItem = null;

    /** 任务类型 */
    private TaskType type;

    /** 上一个任务 */
    private String task;

    /** 刷新时间 */
    private int day = 0;

    /** 完成公告类型(0/1) */
    private int MessageType = 0;

    /** 公告内容 */
    private String broadcastMessage = "§l§c[§b任务系统§c]§e恭喜 §a%p §e完成了§d[ %s ]§e任务";

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
        protected String TaskType;
        TaskType(String TaskType){

            this.TaskType = TaskType;
        }

        public String getTaskType() {
            return TaskType;
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

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, successItem item){
        this(taskName,type,taskItem,taskMessage,star,item,null);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, successItem item, String task){
        this(taskName,type,taskItem,taskMessage,star,item,task,0);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, successItem item, String task, int day){
        this(taskName,type,taskItem,taskMessage,star,item,null,task,day,0);
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, successItem item,successItem firstItem, String task, int day,int MessageType){
        this(taskName,type,taskItem,taskMessage,star,item,firstItem,task,day,MessageType,"§l§c[§b任务系统§c]§e恭喜 §a%p §e完成了§d[ %s ]§e任务");
    }
    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, successItem item,successItem firstItem, String task, int day,int MessageType,String broadcastMessage){
        this(taskName,type,taskItem,taskMessage,star,item,firstItem,task,day,MessageType,broadcastMessage,new TaskButton(""));
    }

    public TaskFile(String taskName, TaskType type, TaskItem[] taskItem, String taskMessage, int star, successItem item,successItem firstItem, String task, int day,int MessageType,String broadcastMessage,TaskButton button){
        this.type = type;
        this.taskItem = taskItem;
        this.TaskName = taskName;
        this.TaskMessage = taskMessage;
        this.star = star;
        this.group = star-1;
        this.successItem = item;
        this.task = task;
        this.day = day;
        this.fristSuccessItem = firstItem;
        this.MessageType = MessageType;
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
        return TaskMessage;
    }

    public String getTaskName() {
        return TaskName;
    }

    public successItem getSuccessItem() {
        return successItem;
    }

    public successItem getFristSuccessItem() {
        return fristSuccessItem;
    }

    public TaskItem[] getTaskItem() {
        return taskItem;
    }

    public String getLastTask() {
        return task;
    }

    public void toSaveConfig(){
        if(TaskName == null) {
            return;
        }
        if(!isFileTask(TaskName)){
            RSTask.getTask().saveResource("Task.yml","/Tasks/"+TaskName+".yml",false);
        }
        LinkedHashMap<String,Object> taskitems = new LinkedHashMap<>();
        if(taskItem != null){
            for(TaskItem taskItem:taskItem){
                taskitems.putAll(taskItem.toSaveConfig());
            }
        }
        Config config = RSTask.getTask().getTaskConfig(TaskName);

        config.set("任务难度",star);
        config.set("任务分组",group);
        config.set("任务介绍",TaskMessage == null ? "无":TaskMessage);
        config.set("刷新时间(天)",day);
        config.set("任务类型",type.getTaskType());
        if(task != null && !task.equals("null")) {
            config.set("完成此任务前需完成",task);
        }
        config.set("任务内容",taskitems);
        if(fristSuccessItem != null){
            config.set("首次完成奖励",fristSuccessItem.toSaveConfig());
        } else {
            config.set("首次完成奖励",successItem.toSaveConfig());
        }
        config.set("奖励",successItem.toSaveConfig());
        config.set("完成公告类型(0/1)",MessageType);
        config.set("公告内容",broadcastMessage);
        config.set("自定义按键图片",button.toSaveConfig());
        config.save();
        RSTask.getTask().taskConfig.put(TaskName,config);
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
        MessageType = messageType;
    }

    public void setFristSuccessItem(com.Task.utils.Tasks.TaskItems.successItem fristSuccessItem) {
        this.fristSuccessItem = fristSuccessItem;
    }

    public void setSuccessItem(com.Task.utils.Tasks.TaskItems.successItem successItem) {
        this.successItem = successItem;
    }

    public void setTaskItem(TaskItem[] taskItem) {
        this.taskItem = taskItem;
    }

    public void setLastTask(String task) {
        this.task = task;
    }

    public void setTaskMessage(String taskMessage) {
        TaskMessage = taskMessage;
    }

    public void setTaskName(String taskName) {
        TaskName = taskName;
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
        return MessageType;
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
                successItem first =
                        com.Task.utils.Tasks.TaskItems.successItem.toSuccessItem(firstSuccess);
                Map success = (Map) config.get("奖励");
                successItem second =
                        com.Task.utils.Tasks.TaskItems.successItem.toSuccessItem(success);
                if(type == null) {
                    return null;
                }
                TaskFile file = new TaskFile(taskName,type,taskItems,config.getString("任务介绍")
                        ,config.getInt("任务难度"),second,first,config.getString("完成此任务前需完成"),
                        +config.getInt("刷新时间(天)"),config.getInt("完成公告类型(0/1)"),config.getString("公告内容"),TaskButton.toTaskButton((Map) config.get("自定义按键图片")));
                file.setGroup(config.getInt("任务分组",file.getStar() - 1));
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

    public boolean close(){
        if(isFileTask(this.TaskName)){
            File file = new File(RSTask.getTask().getDataFolder()+"/Tasks/"+TaskName+".yml");
            return file.delete();
        }
        return true;
    }


    /** 根据TaskItem 获取count */
    public int getCountByTaskItem(TaskItem item){
        for(TaskItem item1:taskItem){
            if(item1.equals(item)) {
                return item1.getEndCount();
            }
        }
        return 0;
    }

    /** 获取所有任务文件 */

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

    /** 获取同一级别难度的任务 */
    public static LinkedList<TaskFile> getDifficultyTasks(int star){
        LinkedList<TaskFile> files = new LinkedList<>();
        for (TaskFile file:RSTask.getTask().tasks.values()){
            if(file.getGroup() == star) {
                files.add(file);
            }
        }
        return files;
    }

    public static boolean runTaskFile(Player player,TaskFile file){
        playerFile file1 = playerFile.getPlayerFile(player.getName());
        playerFile.PlayerTaskType type = file1.getTaskType(file);
        if(type == playerFile.PlayerTaskType.can_Invite || type == playerFile.PlayerTaskType.isSuccess_canInvite){
            int starCount = RSTask.starNeed(file.getGroup());
            if(RSTask.canOpen() && file1.getCount() < starCount){
                player.sendMessage(RSTask.getTask().getLag("not-add-task","§c[任务系统] 抱歉，此任务不能领取"));
                return false;
            }else{
                playerAddTaskEvent event1= new playerAddTaskEvent(player,file);
                Server.getInstance().getPluginManager().callEvent(event1);
                return true;
            }
        }
        if(type == playerFile.PlayerTaskType.Running){
            return true;
        }
        if((file.getLastTask() != null && !file.getLastTask().equals("null") && !file.getLastTask().equals(""))){
            if(!file1.isSuccessed(file.getLastTask())){
                player.sendMessage(RSTask.getTask().getLag("useLastTask").replace("%s",file.getLastTask()));
                return false;
            }
        }
        if(file1.isSuccessed(file.getTaskName()) && file.getSuccessItem().getCount() == 0){
            player.sendMessage(RSTask.getTask().getLag("repeat-collection"));
            return false;
        }
        if(!file1.inDay(file.getTaskName())){
            int day = file.getDay();
            int out = DataTool.getTime(file1.getTaskByName(file.getTaskName()).getTaskClass().getTime());
            player.sendMessage(RSTask.getTask().getLag("repeat-inDay").
                    replace("%c",((day > out)?(day - out):0)+""));
            return false;
        }



        return true;
    }




}
