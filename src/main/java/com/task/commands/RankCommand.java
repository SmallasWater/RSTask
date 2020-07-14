package com.task.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import com.task.utils.form.CreateMenu;
import com.task.commands.base.BaseCommand;

/**
 * 任务排行榜主命令
 * @author SmallasWater
 */
public class RankCommand extends BaseCommand {
    public RankCommand(String name) {
        super(name,"任务积分排行榜");
        this.setPermission("RSTask.command.rank");
        this.usageMessage = "/c-rank";
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.isPlayer() && sender.hasPermission(getPermission());
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        if (commandSender.isPlayer()) {
            CreateMenu.sendRankMenu((Player) commandSender);
        }else{
            commandSender.sendMessage("请不要用控制台执行");
        }
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {}
}
