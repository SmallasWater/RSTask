package com.task.commands.sub;

import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import com.task.RSTask;
import com.task.commands.base.BaseSubCommand;

/**
 * 重新读取配置文件子指令
 * @author SmallasWater
 */
public class ReloadSubCommand extends BaseSubCommand {
    public ReloadSubCommand(String name) {
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
        RSTask.getTask().loadTask();
        sender.sendMessage("§e任务重新读取");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
