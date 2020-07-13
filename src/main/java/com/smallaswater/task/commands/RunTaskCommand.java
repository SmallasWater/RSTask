package com.smallaswater.task.commands;


import cn.nukkit.command.CommandSender;
import com.smallaswater.task.commands.base.BaseCommand;
import com.smallaswater.task.commands.sub.AddTaskValueSubCommand;
import com.smallaswater.task.commands.sub.SetTaskValueSubCommand;


/**
 * 增加玩家任务进度主指令
 * @author 若水
 */
public class RunTaskCommand extends BaseCommand {
    public RunTaskCommand(String name) {
        super(name,"增加任务进度");
        this.setPermission("RSTask.command.sh");
        this.usageMessage = "/rtc help";
        this.addSubCommand(new AddTaskValueSubCommand("add"));
        this.addSubCommand(new SetTaskValueSubCommand("set"));
        loadCommandBase();
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(getPermission());
    }




    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("/rtc add <玩家> <任务名> <进度> <数量> <提示(false) 可不填>");
        sender.sendMessage("/rtc set <玩家> <任务名> <进度> <数量> <提示(false) 可不填>");
    }
}
