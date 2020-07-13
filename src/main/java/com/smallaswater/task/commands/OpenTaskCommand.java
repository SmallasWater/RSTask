package com.smallaswater.task.commands;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import com.smallaswater.task.commands.base.BaseCommand;
import com.smallaswater.task.utils.events.PlayerClickTaskEvent;
import com.smallaswater.task.utils.form.CreateMenu;
import com.smallaswater.task.utils.tasks.TaskFile;

/**
 * 玩家打开任务主指令
 * @author SmallasWater
 */
public class OpenTaskCommand extends BaseCommand {
    public OpenTaskCommand(String name) {
        super(name, "唤醒任务界面");
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.isPlayer();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(sender instanceof Player){
            if(args.length < 1){
                CreateMenu.sendMenu((Player) sender);
            }else{
                String taskName = args[0];
                TaskFile file = TaskFile.getTask(taskName);

                PlayerClickTaskEvent event = new PlayerClickTaskEvent(file,(Player) sender);
                Server.getInstance().getPluginManager().callEvent(event);
            }
        }
        return true;
    }

    @Override
    public void sendHelp(CommandSender sender) {}
}
