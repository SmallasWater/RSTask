package com.smallaswater.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import com.smallaswater.task.commands.base.BaseSubCommand;
import com.smallaswater.task.utils.tasks.PlayerFile;

/**
 * 设置玩家积分子指令
 * @author SmallasWater
 */
public class CountSubCommand extends BaseSubCommand {
    public CountSubCommand(String name) {
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
        if(args.length > 2){
            String playerName = args[1];
            Player player = Server.getInstance().getPlayer(playerName);
            if(player != null){
                String list = args[2];
                try {
                    int count = Integer.parseInt(list);
                    PlayerFile file = PlayerFile.getPlayerFile(player.getName());
                    file.setCount(count);
                }catch (Exception e){
                    sender.sendMessage(TextFormat.RED+"请输入正确的积分数值 （整数）");
                    return true;
                }
                sender.sendMessage(TextFormat.GREEN+"成功设置玩家 "+player.getName()+"积分为 "+list);
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
                new CommandParameter("count",CommandParamType.INT,false)
        };
    }
}
