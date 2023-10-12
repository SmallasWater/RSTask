package com.task.utils;

/**
 * @author SmallasWater
 */
public class RunValue {
    private String taskName;
    private int value;
    private String load;
    private RunValue(String taskName,String load,int value){
        this.taskName = taskName;
        this.value = value;
        this.load = load;
    }

    public String getTaskName() {
        return taskName;
    }

    public int getValue() {
        return value;
    }

    public String getLoad() {
        return load;
    }

    public static RunValue getInstance(String[] args){
        return getInstance(args,true);
    }
    public static RunValue getInstance(String[] args,boolean hasTaskName){
        String taskName = null;
        String load;
        String value;
        if(!hasTaskName){
            load = args[2];
            value = args[3];
        }else{
            taskName = args[2];
            load = args[3];
            value = args[4];

        }

        int v;
        try {
            v = Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
        return new RunValue(taskName,load,v);
    }
}
