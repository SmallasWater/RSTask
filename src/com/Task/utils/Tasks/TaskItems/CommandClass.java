package com.Task.utils.Tasks.TaskItems;


public class CommandClass {

    private String cmd;
    private String sendMessage;

    public CommandClass(String cmd){
        this(cmd,"奖励指令");
    }

    public CommandClass(String cmd,String sendMessage){
        this.cmd = cmd;
        this.sendMessage = sendMessage;
    }

    public String getCmd() {
        return cmd;
    }

    public String getSendMessage() {
        return sendMessage;
    }

    /*
    *     cmd:string
    * */
    public static CommandClass toCommandClass(String defaultCmd){
        if(defaultCmd == null) return null;
        if(defaultCmd.split(":").length < 2) return null;
        return new CommandClass(defaultCmd.split(":")[0],defaultCmd.split(":")[1]);
    }

    public String toString(){
        if(cmd.split(":").length > 1)
            return cmd;
        return cmd+":"+sendMessage;
    }
}
