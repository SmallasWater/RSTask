package com.task.commands;

import cn.nukkit.command.CommandSender;
import com.task.commands.base.BaseCommand;
import com.task.commands.sub.*;


/**
 * 任务主命令 （管理）
 * @author SmallasWater
 */
public class TaskCommand extends BaseCommand {
    public TaskCommand(String name) {
        super(name, "任务系统主命令");
        this.setPermission("RSTask.command.ic");
        this.addSubCommand(new IcSubCommand("ic"));
        this.addSubCommand(new DelSubCommand("del"));
        this.addSubCommand(new ReloadSubCommand("reload"));
        this.addSubCommand(new OpenTaskSubCommand("c"));
        this.addSubCommand(new OpenListSubCommand("clist"));
        this.addSubCommand(new CountSubCommand("count"));
        this.loadCommandBase();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(args.length > 0) {
            return super.execute(sender, s, args);
        }
        sendHelp(sender);
        return true;
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission(getPermission());
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§6[§7任务系统§6] §e->§f /task ic §7创建任务指令");
        sender.sendMessage("§6[§7任务系统§6] §e->§f /task del <名称> §7删除指定任务");
        sender.sendMessage("§6[§7任务系统§6] §e->§f /task reload §7重新加载配置");
        sender.sendMessage("§6[§7任务系统§6] §e->§f /task c <玩家> <任务名> §7让玩家打开任务");
        sender.sendMessage("§6[§7任务系统§6] §e->§f /task clist <玩家> <分组> §7打开指定分组GUI");
        sender.sendMessage("§6[§7任务系统§6] §e->§f /task count <玩家> <积分> §7设置玩家任务积分");
    }
}
