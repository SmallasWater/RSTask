package com.task.utils.tasks.taskitems;

import com.task.utils.tasks.TaskFile;

import java.util.*;

/**
 * @author SmallasWater
 */
public class PlayerTask {

    /** 任务名称 */
    private String taskName;


    /** 任务文件 */
    private TaskFile taskFile;



    /** 任务分支 */
    private PlayerTaskClass taskClass = null;


    /**
     * 读取任务
     * */
    public PlayerTask(String taskName){
        this.taskName = taskName;
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
    public PlayerTask(TaskFile taskName){
        this.taskName = taskName.getTaskName();
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
    public PlayerTask(PlayerTaskClass task){
        this.taskName = task.getTaskName();
        taskClass = task;
        this.taskFile = TaskFile.getTask(taskClass.getTaskName());
    }


    public LinkedHashMap<String,Object> toSaveConfig(){
        LinkedHashMap<String,Object> objectLinkedHashMap = new LinkedHashMap<>();
        objectLinkedHashMap.put(taskName,taskClass.toSaveConfig());
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
        return taskName;
    }

    public final boolean equals(PlayerTask task){
        return task.getTaskName().equals(taskName);
    }




    public static PlayerTask toPlayerTask(Map map){
        if(map == null) {
            return null;
        }
        for(Object name:map.keySet()){
            if(name instanceof String){
                PlayerTaskClass taskClass = PlayerTaskClass.toPlayerTaskClass((String)name,(Map)map.get(name));
                if(taskClass != null) {
                    return new PlayerTask(taskClass);
                }
            }
        }
        return null;
    }

    public void sync(PlayerTask task){
        this.taskName = task.getTaskName();
        this.taskFile = task.getTaskFile();
        this.taskClass = task.getTaskClass();
    }

    @Override
    public String toString() {
        return "{TaskName:"+taskName+"TaskFile:"+taskFile.toString()+"taskClass"+taskClass.toString()+"}";
    }
}
