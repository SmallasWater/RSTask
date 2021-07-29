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
import com.task.utils.tasks.taskitems.PlayerTask;

/**
 * @author SmallasWater
 * Create on 2021/7/29 23:07
 * Package com.task.commands.sub
 */
public class SetAllTaskValueSubCommand extends BaseSubCommand {

    public SetAllTaskValueSubCommand(String name) {
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
            if (args.length > 3) {
                RunValue v = RunValue.getInstance(args, false);
                if (v == null) {
                    return false;
                }
                int sizeSuccess = 0;
                int sizeError = 0;
                for (PlayerTask file : API.getAllRunTasks(p)) {
                    if (API.setPlayerRunTask(p.getName(), file.getTaskName(), v.getLoad(), v.getValue())) {
                        sizeSuccess++;
                    }else{
                        sizeError++;
                    }
                }
                if(args.length > 4 && "true".equalsIgnoreCase(args[4])) {
                    sender.sendMessage("§6[§7任务系统§6] §7"+ "设置玩家: §a"+p.getName()+"§5" + v.getValue() + "§2点§r" + "§2的§2" + v.getLoad() + "进度§a 成功:§e"+sizeSuccess+" §a个 §c失败:§e "+sizeError+" §c个");
                }
            }
        } else {
            if (args.length > 5 && "true".equalsIgnoreCase(args[5])) {
                sender.sendMessage("玩家" + player + "不在线");
            }
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{
                new CommandParameter("playerName", CommandParamType.TARGET, false),
                new CommandParameter("load", CommandParamType.TEXT, false),
                new CommandParameter("value", CommandParamType.INT, false),
        };
    }
}