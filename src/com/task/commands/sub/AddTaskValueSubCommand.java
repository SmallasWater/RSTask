package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import com.task.commands.base.BaseSubCommand;
import com.task.utils.API;
import com.task.utils.DataTool;
import com.task.utils.RunValue;

/**
 * @author SmallasWater
 */
public class AddTaskValueSubCommand extends BaseSubCommand {
    public AddTaskValueSubCommand(String name) {
        super(name);
    }



    @Override
    protected boolean canUse(CommandSender sender) {
        return sender.isOp();
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        String player = args[1];
        Player p = Server.getInstance().getPlayer(player);
        if (p != null) {
            if (args.length > 4) {
                RunValue v = RunValue.getInstance(args);
                if(v == null){
                    return false;
                }
                if(API.addPlayerRunTask(p.getName(), v.getTaskName(), v.getLoad(), v.getValue())){
                    sender.sendMessage("成功给 "+p.getName()+"增加 "+v+"点"+v.getTaskName()+"的"+v.getLoad()+"进度");
                }else{
                    sender.sendMessage( p.getName()+"增加"+v.getTaskName()+"的"+v.getLoad()+"进度失败");
                }
            }else{
                return false;
            }
        }else{
            sender.sendMessage("玩家"+player+"不在线");
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{
                new CommandParameter("playerName", CommandParamType.TARGET,false),
                new CommandParameter("taskName", DataTool.getTaskAllNames()),
                new CommandParameter("load", CommandParamType.TEXT,false),
                new CommandParameter("value", CommandParamType.INT,false),
        };
    }
}
