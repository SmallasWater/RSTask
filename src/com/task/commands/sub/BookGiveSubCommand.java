package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.ItemBookWritten;
import com.task.commands.base.BaseSubCommand;
import com.task.utils.DataTool;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.TaskBook;

/**
 * 给予玩家书本子指令
 * @author SmallasWater
 */
public class BookGiveSubCommand extends BaseSubCommand {
    public BookGiveSubCommand(String name) {
        super(name);
    }

    @Override
    public String[] getAliases() {
        return new String[0];
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        String taskName = args[0];
        TaskFile file = TaskFile.getTask(taskName);
        if(file == null){
            sender.sendMessage("§c不存在"+args[0]+"任务");
            return true;
        }
        PlayerFile file1 = PlayerFile.getPlayerFile(sender.getName());
        if(file1.canInvite(file.getTaskName())){
            ItemBookWritten written = new ItemBookWritten();
            TaskBook book = new TaskBook(written);

            book.setTitle(file.getTaskName());
            book.writeIn("\n\n\n\n加载中...请再次打开");
            ((Player) sender).getInventory().setItemInHand(book.toBook().clone());
        }else{
            sender.sendMessage("§c"+args[0]+"任务不可领取");
            return true;
        }
        return true;
    }

    @Override
    protected boolean canUse(CommandSender sender){
        return sender.isOp();
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[]{
                new CommandParameter("taskName", DataTool.getTaskAllNames())

        };
    }
}
