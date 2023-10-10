package com.task.commands.sub;

import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import com.task.events.DelTaskEvent;
import com.task.commands.base.BaseSubCommand;
import com.task.utils.DataTool;
import com.task.utils.tasks.TaskFile;

/**
 * 删除玩家任务指令
 * @author SmallasWater
 */
public class DelSubCommand extends BaseSubCommand {
    public DelSubCommand(String name) {
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
        if(args.length < 1){
            sender.sendMessage("用法:/task del <任务名>");
            return false;
        }
        String taskName = args[1];
        if(TaskFile.isFileTask(taskName)){
            TaskFile file = TaskFile.getTask(taskName);
            if(file != null){
                DelTaskEvent event = new DelTaskEvent(file);
                Server.getInstance().getPluginManager().callEvent(event);
                if(!file.close()){
                    sender.sendMessage("任务删除失败");
                }else{
                    sender.sendMessage("任务删除成功");
                }
            }
        }else{
            sender.sendMessage("不存在"+taskName+"任务");
            return false;
        }
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{
                new CommandParameter("taskName", DataTool.getTaskAllNames())};
    }
}
