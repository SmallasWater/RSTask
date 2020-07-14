package com.task.utils.tasks.taskitems;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 任务目标
 *
 * @author SmallasWater*/
public class TaskItem {



    private String taskName;

    private int endCount;

    //任务分支名称
    private String task;

    public TaskItem(String taskName,String task,int endCount){
        this.task = task;
        this.endCount = endCount;
        this.taskName = taskName;
    }

    public int getEndCount() {
        return endCount;
    }


    /** 获取任务分支名称 */
    public String getTask() {
        return task;
    }


    @Override
    public String toString(){
        return task+":"+endCount;
    }


    public LinkedHashMap<String,Integer> toSaveConfig(){
        return new LinkedHashMap<String,Integer>(){
            {
                put(task,endCount);
            }
        };
    }


    /** 检测Task标签 */
    public TaskItemTag getTaskTag(){
        if(task.split("@").length > 1){
            switch (task.split("@")[1]){
                case "tag":
                    return TaskItemTag.NbtItem;
                case "item":
                    return TaskItemTag.defaultItem;
                    default:
                        break;
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
        return item != null && item.getTask().equals(task);

    }


    public enum TaskItemTag{
        NbtItem,defaultItem,diyName
    }



    public static TaskItem toTaskItem(String taskName,Map<? extends String,? extends Integer> map){
        if(map == null) {
            return null;
        }
        for (String tag : map.keySet()) {
            int ints = map.get(tag);
            if(tag != null){
                return new TaskItem(taskName,tag,ints);
            }
        }
        return null;
    }

    public void setEndCount(int endCount) {
        this.endCount = endCount;
    }

    public void addEndCount(int value){
        this.endCount += value;
    }


    /** defaultString: id:damage:count@item 或 id:count@tag 或 内容:id*/
    public static TaskItem toTaskItem(String taskName,String defaultString){
        if(defaultString.split("@").length < 1) {
            return null;
        }
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
                    default:
                        break;
            }
        }
        String[] lists = defaultString.split(":");
        return new TaskItem(taskName,lists[0],Integer.parseInt(lists[1]));
    }

}
