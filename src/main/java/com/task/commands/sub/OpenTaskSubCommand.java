package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import com.task.commands.base.BaseSubCommand;
import com.task.utils.tasks.TaskFile;
import com.task.utils.DataTool;
import com.task.utils.events.PlayerClickTaskEvent;

/**
 * 打开任务界面子指令
 * @author SmallasWater
 */
public class OpenTaskSubCommand extends BaseSubCommand {
    public OpenTaskSubCommand(String name) {
        super(name);
    }

    @Override
    protected boolean canUse(CommandSender sender){
        return sender.isOp();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length > 2){
            String playerName = args[1];
            Player player = Server.getInstance().getPlayer(playerName);
            if(player != null){
                String taskName = args[2];
                TaskFile file = TaskFile.getTask(taskName);
                PlayerClickTaskEvent event = new PlayerClickTaskEvent(file,player);
                Server.getInstance().getPluginManager().callEvent(event);
                sender.sendMessage(TextFormat.GREEN+"成功让玩家 "+player.getName()+"触发点击任务 "+taskName);
            }else{
                sender.sendMessage(TextFormat.RED+"玩家 "+playerName+"不在线");
            }
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{
                new CommandParameter("playerName", CommandParamType.TARGET,false),
                new CommandParameter("taskName", DataTool.getTaskAllNames())
        };
    }
}
