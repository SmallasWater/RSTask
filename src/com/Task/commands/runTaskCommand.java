package com.Task.commands;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import com.Task.utils.API;

public class runTaskCommand extends Command {
    public runTaskCommand(String name) {
        super(name,"增加任务进度","/rtc help");
        this.setPermission("RSTask.command.sh");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(args.length > 4){
            String player = args[1];
            String taskName = args[2];
            String load = args[3];
            String value = args[4];
            int v;
            try {
                v = Integer.parseInt(value);
            }catch (Exception e){
                return false;
            }
            if ("add".equals(args[0])) {
                API.playerAddRunTask(player, taskName, load, v);
            } else {
                API.playerSetRunTask(player, taskName, load, v);
            }
        }else{
            sender.sendMessage("/rtc add <玩家> <任务名> <进度> <数量>");
            sender.sendMessage("/rtc set <玩家> <任务名> <进度> <数量>");
            return true;
        }
        return false;
    }
}
