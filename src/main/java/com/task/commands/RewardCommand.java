package com.task.commands;

import cn.nukkit.command.CommandSender;
import com.task.commands.base.BaseCommand;

public class RewardCommand extends BaseCommand {
    public RewardCommand(String name, String description) {
        super(name, description);
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.hasPermission("RSTask.command.reward");
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§6[§7任务系统§6] §e->§f /rd c §7创建悬赏任务");
        sender.sendMessage("§6[§7任务系统§6] §e->§f /rd del <名称> §7删除指定任务");
        sender.sendMessage("§6[§7任务系统§6] §e->§f /rd reload §7重新加载配置");

    }
}
