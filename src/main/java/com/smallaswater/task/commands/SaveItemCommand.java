package com.smallaswater.task.commands;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import com.smallaswater.task.RsTask;
import com.smallaswater.task.utils.tasks.taskitems.ItemClass;
import com.smallaswater.task.commands.base.BaseCommand;

/**
 * 手持物品命令
 * @author SmallasWater
 */
public class SaveItemCommand extends BaseCommand {

    public SaveItemCommand(String name) {
        super(name, "§d保存手上的物品到ItemTag");
        this.setPermission("RSTask.command.sh");
    }

    @Override
    public boolean hasPermission(CommandSender sender) {
        return sender.isPlayer() && sender.hasPermission(getPermission());
    }

    @Override
    public void sendHelp(CommandSender sender) {
        sender.sendMessage("§c=======================");
        sender.sendMessage("§e/sh <编号(可不填)> <数量(可不填)>");
        sender.sendMessage("§c=======================");
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {
        if(sender instanceof Player){
            Item item = ((Player) sender).getInventory().getItemInHand();
            if(item.getId() == 0){
                sender.sendMessage("§c无法添加空气");
                return true;
            }
            ItemClass itemClass = new ItemClass(item);
            if(args.length < 1){
                String n = RsTask.getTask().saveTagItemsConfig(itemClass);
                sender.sendMessage("§e成功添加"+n+"至TagItem.json");
            }else if(args.length < 2){
                RsTask.getTask().saveTagItemsConfig(itemClass,args[0]);
                sender.sendMessage("§e成功添加"+args[0]+"至TagItem.json");
            }else{
                int count = Integer.parseInt(args[1]);
                itemClass.getItem().setCount(count);
                RsTask.getTask().saveTagItemsConfig(itemClass,args[0]);
                sender.sendMessage("§e成功添加"+args[0]+"至 数量设置为 "+count+" TagItem.json");
            }
        }
        return true;
    }
}
