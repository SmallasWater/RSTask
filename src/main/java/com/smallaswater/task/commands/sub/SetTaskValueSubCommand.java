package com.smallaswater.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import com.smallaswater.task.commands.base.BaseSubCommand;
import com.smallaswater.task.utils.API;
import com.smallaswater.task.utils.DataTool;
import com.smallaswater.task.utils.RunValue;

/**
 * @author SmallasWater
 */
public class SetTaskValueSubCommand extends BaseSubCommand {
    public SetTaskValueSubCommand(String name) {
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
                if(API.setPlayerRunTask(p.getName(), v.getTaskName(), v.getLoad(), v.getValue())){
                    if(args.length > 5 && args[5].equalsIgnoreCase("true")) {
                        sender.sendMessage("§6[§7任务系统§6] §2成功给 §7" + p.getName() + "设置 §5" + v.getValue() + "§2点§r" + v.getTaskName() + "§2的§2" + v.getLoad() + "进度");
                    }
                }else{
                    if(args.length > 5 && args[5].equalsIgnoreCase("true")) {
                        sender.sendMessage("§6[§7任务系统§6] §7" + p.getName() + "§c设置" + v.getTaskName() + "的" + v.getLoad() + "进度失败");
                    }
                }
            }else{
                return false;
            }
        }else{
            if(args.length > 5 && args[5].equalsIgnoreCase("true")) {
                sender.sendMessage("玩家" + player + "不在线");
            }
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
