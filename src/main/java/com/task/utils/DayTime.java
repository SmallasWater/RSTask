package com.task.utils;

/**
 * @author SmallasWater
 */
public class DayTime {

    public static final String[] STRINGS = new String[]{"天","分钟","小时"};

    public static final int DAY = 0;
    public static final int MIN = 1;
    public static final int HOUR = 2;

    private int type;

    private int time;

    public DayTime(int type,int time){
        this.type = type;
        this.time = time;
    }

    public int getTime() {
        return time;
    }

    public int getType() {
        return type;
    }
}
