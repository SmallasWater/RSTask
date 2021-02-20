package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.utils.TextFormat;
import com.task.RsTask;
import com.task.utils.DataTool;
import com.task.form.CreateMenu;


/**
 * 打开分组界面子指令
 * @author SmallasWater
 */
public class OpenListSubCommand extends OpenTaskSubCommand{

    public OpenListSubCommand(String name) {
        super(name);
    }

    @Override
    protected boolean canUse(CommandSender sender){
        return sender.isOp();
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        if(args.length > 2){
            String playerName = args[1];
            Player player = Server.getInstance().getPlayer(playerName);
            if(player != null){
                String list = args[2];
                try {
                    int group = Integer.parseInt(list);
                    if(DataTool.existsGroup(group)) {
                        if (LaTestSubCommand.canOpenGroup(sender, playerName, player, group)) {
                            return true;
                        }
                        CreateMenu.sendTaskList(player, RsTask.getClickStar.get(player));
                    }else{
                        sender.sendMessage(TextFormat.RED+"不存在分组: "+group);
                        return true;
                    }

                }catch (Exception e){
                    sender.sendMessage(TextFormat.RED+"请输入正确的分组 （整数）");
                    return true;
                }

                sender.sendMessage(TextFormat.GREEN+"成功让玩家 "+player.getName()+"触发点击分组 "+list);
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
                new CommandParameter("group", DataTool.getGropAllName())
        };
    }
}
