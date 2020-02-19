package com.Task.utils.Tasks.TaskItems;

import com.Task.utils.Tasks.TaskFile;

import java.util.*;

public class playerTask {

    /** 任务名称 */
    private String TaskName;


    /** 任务文件 */
    private TaskFile taskFile;



    /** 任务分支 */
    private PlayerTaskClass taskClass = null;


    /** 读取任务 */
    public playerTask(String taskName){
        this.TaskName = taskName;
        this.taskFile = TaskFile.getTask(taskName);
        if(taskFile != null){
            TaskItem[] items = new TaskItem[taskFile.getTaskItem().length];
            for(int i=0;i < items.length;i++){
                TaskItem itemF = taskFile.getTaskItem()[i];
                TaskItem item = new TaskItem(itemF.getTaskName(),itemF.getTask(),0);
                items[i] = item;
            }
            taskClass = new PlayerTaskClass(taskName,items,new Date(),0);
        }
    }

    /** 同步*/
    public playerTask(TaskFile taskName){
        this.TaskName = taskName.getTaskName();
        this.taskFile = taskName;
        TaskItem[] items = new TaskItem[taskFile.getTaskItem().length];
        for(int i=0;i < items.length;i++){
            TaskItem itemF = taskFile.getTaskItem()[i];
            TaskItem item = new TaskItem(itemF.getTaskName(),itemF.getTask(),0);
            items[i] = item;
        }
        taskClass = new PlayerTaskClass(taskName.getTaskName(),items,new Date(),0);
    }

    /** 读取配置 */
    public playerTask(PlayerTaskClass task){
        this.TaskName = task.getTaskName();
        taskClass = task;
        this.taskFile = TaskFile.getTask(taskClass.getTaskName());
    }


    public LinkedHashMap<String,Object> toSaveConfig(){
        LinkedHashMap<String,Object> objectLinkedHashMap = new LinkedHashMap<>();
        objectLinkedHashMap.put(TaskName,taskClass.toSaveConfig());
        return objectLinkedHashMap;
    }


    public TaskFile getTaskFile(){
        return taskFile;
    }

    public void setTaskClass(PlayerTaskClass taskClass) {
        this.taskClass = taskClass;
    }

    public PlayerTaskClass getTaskClass() {
        return taskClass;
    }

    public String getTaskName() {
        return TaskName;
    }

    public final boolean equals(playerTask task){
        return task.getTaskName().equals(TaskName);
    }




    public static playerTask toPlayerTask(Map map){
        if(map == null) return null;
        for(Object name:map.keySet()){
            if(name instanceof String){
                return new playerTask(PlayerTaskClass.toPlayerTaskClass((String)name,(Map)map.get(name)));
            }
        }
        return null;
    }

    public void sync(playerTask task){
        this.TaskName = task.getTaskName();
        this.taskFile = task.getTaskFile();
        this.taskClass = task.getTaskClass();
    }

    @Override
    public String toString() {
        return "{TaskName:"+TaskName+"TaskFile:"+taskFile.toString()+"taskClass"+taskClass.toString()+"}";
    }
}
