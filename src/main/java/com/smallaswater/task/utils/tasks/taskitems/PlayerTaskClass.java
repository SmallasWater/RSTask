package com.smallaswater.task.utils.tasks.taskitems;


import com.smallaswater.task.utils.DataTool;
import com.smallaswater.task.utils.tasks.TaskFile;

import java.util.Date;
import java.util.LinkedHashMap;

import java.util.Map;

/**
 * @author SmallasWater
 */
public class PlayerTaskClass {

    /** 玩家配置存储任务 */
    private  TaskItem[] value;

    /** 日期 */
    private Date time;

    /** 开关 */
    private boolean open = false;

    /** 任务名称 */
    private String taskName;

    /** 完成次数 */
    private int count;

    public PlayerTaskClass(String taskName, TaskItem[] taskItems, Date time, int count){
        this.time = time;
        this.count = count;
        this.value = taskItems;
        this.taskName = taskName;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }


    public LinkedHashMap<String,Object> toSaveConfig(){
        LinkedHashMap<String,Object> TaskItem = new LinkedHashMap<>();
        for(TaskItem item:value){
            TaskItem.putAll(item.toSaveConfig());
        }
        LinkedHashMap<String,Object> t = new LinkedHashMap<>();
        t.put("load",TaskItem);
        t.put("open",open);
        t.put("time", DataTool.toDateString(time));
        t.put("count",count);
        return t;
    }



    public String getTaskName() {
        return taskName;
    }

    public int getLoad(TaskItem taskItem){
        for(TaskItem item:value){
            if(item != null) {
                if(taskItem.equals(item)){
                    return item.getEndCount();
                }
            }
        }
        return 0;
    }

    public boolean getOpen(){
        return open;
    }

    public void setLoad(TaskItem taskItem){
        int i = 0;
        for(TaskItem item:value){
            if(taskItem.equals(item)){
                value[i] = taskItem;
                break;
            }
            i++;
        }
    }

    public int getCount() {
        return count;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public TaskItem[] getValue() {
        return value;
    }

    public void setValue(TaskItem[] value) {
        this.value = value;
    }



    public boolean issetTaskItem(String item){
        for(TaskItem item1:value){
            if(item1.getTask().equals(item)) {
                return true;
            }
        }
        return false;
    }

    public static PlayerTaskClass toPlayerTaskClass(String taskName, Map map){
        if(map == null) {
            return null;
        }
        Map maps = (Map) map.get("load");
        TaskItem[] values = new TaskItem[maps.size()];
        int i = 0;
        TaskFile file = TaskFile.getTask(taskName);
        if(file != null) {
            //对主体任务进行核对
            if (file.getTaskItem().length == values.length) {
                for (Object so : maps.keySet()) {
                    values[i] = TaskItem.toTaskItem(taskName, new LinkedHashMap<String, Integer>() {{
                        put(String.valueOf(so), (int) maps.get(so));
                    }});
                    i++;
                }
            } else {
                values = new TaskItem[file.getTaskItem().length];
                for (TaskItem item : file.getTaskItem()) {
                    if (maps.containsKey(item.getTask())) {
                        values[i] = TaskItem.toTaskItem(taskName, new LinkedHashMap<String, Integer>() {{
                            put(String.valueOf(item.getTask()), (int) maps.get(item.getTask()));
                        }});

                    } else {
                        values[i] = new TaskItem(taskName, item.getTask(), 0);
                    }
                    i++;
                }
            }

            Date time = DataTool.getDate((String) map.get("time"));
            int count = (int) map.get("count");
            PlayerTaskClass playerTaskClass = new PlayerTaskClass(taskName, values, time, count);
            playerTaskClass.setOpen((boolean) map.get("open"));
            return playerTaskClass;
        }
        return null;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return toSaveConfig().toString();
    }
}
