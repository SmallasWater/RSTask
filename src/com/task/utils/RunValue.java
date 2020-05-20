package com.task.utils;

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
        String taskName = args[2];
        String load = args[3];
        String value = args[4];
        int v;
        try {
            v = Integer.parseInt(value);
        } catch (Exception e) {
            return null;
        }
        return new RunValue(taskName,load,v);
    }
}
