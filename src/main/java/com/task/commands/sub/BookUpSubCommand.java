package com.task.commands.sub;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import com.task.RsTask;
import com.task.commands.base.BaseSubCommand;
import com.task.events.PlayerOpenBookEvent;
import com.task.utils.task.CollectItemTask;
import com.task.utils.tasks.taskitems.TaskBook;


/**
 * 书本更新子指令
 * @author SmallasWater
 */
public class BookUpSubCommand extends BaseSubCommand {

    public BookUpSubCommand(String name) {
        super(name);
    }

    @Override
    public String[] getAliases() {
        return new String[]{"更新"};
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        Item item = ((Player) sender).getInventory().getItemInHand();
        if(item instanceof ItemBookWritten){
            if(TaskBook.isBook((ItemBookWritten) item)){
                Server.getInstance().getScheduler().scheduleDelayedTask(new CollectItemTask(RsTask.getTask(),(Player) sender),1);
                PlayerOpenBookEvent event = new PlayerOpenBookEvent(
                        (Player) sender,TaskBook.getTaskBookByItem(((ItemBookWritten) item)));
                Server.getInstance().getPluginManager().callEvent(event);
                sender.sendMessage("§a==============");
                sender.sendMessage("§e任务书更新成功");
                sender.sendMessage("§a==============");
                return true;
            }
        }
        sender.sendMessage("§c请手持任务书");
        return true;
    }

    @Override
    public CommandParameter[] getParameters() {
        return new CommandParameter[0];
    }
}
