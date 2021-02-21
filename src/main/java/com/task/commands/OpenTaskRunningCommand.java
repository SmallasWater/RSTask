package com.task.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import com.task.commands.base.BaseCommand;
import com.task.events.PlayerClickTaskEvent;
import com.task.form.CreateMenu;
import com.task.utils.tasks.TaskFile;

/**
 * @author SmallasWater
 * Create on 2021/2/21 20:44
 * Package com.task.commands
 */
public class OpenTaskRunningCommand extends BaseCommand {
    public OpenTaskRunningCommand(String name) {
        super(name, "唤醒正在进行任务的UI");
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.isPlayer();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(sender instanceof Player){
            CreateMenu.sendMenuRunningTaskList((Player) sender);
        }
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {

    }
}
