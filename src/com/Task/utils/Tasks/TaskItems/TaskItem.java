package com.Task.utils.Tasks.TaskItems;

import cn.nukkit.Server;
import com.Task.utils.Tasks.TaskFile;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 任务目标
 * */
public class TaskItem {



    private String taskName;

    private int EndCount;

    //任务分支名称
    private String Task;

    public TaskItem(String taskName,String Task,int endCount){
        this.Task = Task;
        this.EndCount = endCount;
        this.taskName = taskName;
    }

    public int getEndCount() {
        return EndCount;
    }


    /** 获取任务分支名称 */
    public String getTask() {
        return Task;
    }


    public String toString(){
        return Task+":"+EndCount;
    }


    public LinkedHashMap<String,Integer> toSaveConfig(){
        return new LinkedHashMap<String,Integer>(){
            {
                put(Task,EndCount);
            }
        };
    }


    /** 检测Task标签 */
    public TaskItemTag getTaskTag(){
        if(Task.split("@").length > 1){
            switch (Task.split("@")[1]){
                case "tag":
                    return TaskItemTag.NbtItem;
                case "item":
                    return TaskItemTag.defaultItem;
            }
        }
        return TaskItemTag.diyName;
    }



    /** 如果为item | tag 则返回 ItemClass*/
    public ItemClass getItemClass(){
        return ItemClass.toItem(this);
    }


    public String getTaskName() {
        return taskName;
    }


    public boolean equals(TaskItem item) {
        return item != null && item.getTask().equals(Task);

    }


    public enum TaskItemTag{
        NbtItem,defaultItem,diyName
    }



    public static TaskItem toTaskItem(String taskName,Map<? extends String,? extends Integer> map){
        if(map == null) return null;
        for (String tag : map.keySet()) {
            int ints = map.get(tag);
            if(tag != null){
                return new TaskItem(taskName,tag,ints);
            }
        }
        return null;
    }

    public void setEndCount(int endCount) {
        EndCount = endCount;
    }

    public void addEndCount(int value){
        EndCount += value;
    }


    /** defaultString: id:damage:count@item 或 id:count@tag 或 内容:id*/
    public static TaskItem toTaskItem(String taskName,String defaultString){
        if(defaultString.split("@").length < 1) return null;
        if(defaultString.split("@").length > 1){
            switch (defaultString.split("@")[1]){
                case "item":
                    String sItem = defaultString.split("@")[0];
                    String[] lists = sItem.split(":");
                    return new TaskItem(taskName,lists[0]+":"+lists[1]+"@item",Integer.parseInt(lists[2]));
                case "tag":
                    sItem = defaultString.split("@")[0];
                    lists = sItem.split(":");
                    return new TaskItem(taskName,lists[0]+"@tag",Integer.parseInt(lists[1]));
            }
        }
        String[] lists = defaultString.split(":");
        return new TaskItem(taskName,lists[0],Integer.parseInt(lists[1]));
    }
}
