package com.task.tasks.taskitems;


/**
 * @author SmallasWater
 */
public class CommandClass {

    private String cmd;
    private String sendMessage;

    public CommandClass(String cmd){
        this(cmd,"奖励指令");
    }

    public CommandClass(String cmd, String sendMessage){
        this.cmd = cmd;
        this.sendMessage = sendMessage;
    }

    /**
     * 获取指令
     * @return 指令*/
    public String getCmd() {
        return cmd;
    }

    /**
     * 获取显示信息
     * @return 指令*/
    public String getSendMessage() {
        return sendMessage;
    }

    /**
     * 将命令字符串转换为 命令奖励类
     *
    * @param defaultCmd GUI传入的字符串
     * @return command奖励{@link CommandClass}
    * */
    public static CommandClass toCommandClass(String defaultCmd){
        if(defaultCmd == null) {
            return null;
        }
        if(defaultCmd.split(":").length < 2) {
            return null;
        }
        return new CommandClass(defaultCmd.split(":")[0],defaultCmd.split(":")[1]);
    }

    @Override
    public String toString(){
        if(cmd.split(":").length > 1) {
            return cmd;
        }
        return cmd+":"+sendMessage;
    }
}
