package com.task.utils.task;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.level.Level;
import cn.nukkit.level.Sound;
import com.task.RsTask;
import com.task.utils.API;
import com.task.utils.ItemIDSunName;
import com.smallaswater.task.utils.events.*;
import com.task.utils.events.*;
import com.task.utils.form.CreateMenu;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.tasks.TaskFile;
import com.task.utils.DataTool;

import com.smallaswater.task.utils.tasks.taskitems.*;
import com.task.utils.tasks.taskitems.*;

import java.util.Date;
import java.util.LinkedList;

/**
 * @author SmallasWater
 */
public class ListerEvents implements Listener{


    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        String s = block.getId()+":"+block.getDamage()+"@item";
        defaultUseTask(player.getName(),s, TaskFile.TaskType.BlockBreak,false);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Block block = event.getBlock();
        String s = block.getId()+":"+block.getDamage()+"@item";
        defaultUseTask(player.getName(),s, TaskFile.TaskType.BlockPlayer,false);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onUseItem(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Item item = event.getItem();
        API.addItem(player,item, TaskFile.TaskType.EatItem);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Item item = event.getItem();
        API.addItem(player,item, TaskFile.TaskType.DropItem);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void craft(CraftItemEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Item item = event.getRecipe().getResult();
        API.addItem(player,item, TaskFile.TaskType.CraftItem);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onRightBook(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Item item = event.getItem();
        if(item instanceof ItemBookWritten){
            if(TaskBook.isBook((ItemBookWritten) item)){
                TaskBook book = TaskBook.getTaskBookByItem((ItemBookWritten) item);
                String taskName = book.title;
                PlayerFile file = PlayerFile.getPlayerFile(player.getName());
                if(file.isSuccess(taskName)){
                    TaskFile file1 = TaskFile.getTask(taskName);
                    if(file1 != null) {
                        event.setCancelled();

                        PlayerClickTaskEvent event1 = new PlayerClickTaskEvent(file1, player);
                        Server.getInstance().getPluginManager().callEvent(event1);
                    }
                }

            }
        }



    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onInt(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Item item = event.getItem();
        API.addItem(player,item, TaskFile.TaskType.Click);

    }



    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!RsTask.getTask().getPlayerFile(player.getName()).exists()){
            Server.getInstance().broadcastMessage(RsTask.getTask().getLag("join-achievement").replace("%p",player.getName()));
            RsTask.getTask().getPlayerConfig(player.getName());
        }
    }

    @EventHandler
    public void use(UseTaskEvent event){
        Player player = event.getPlayer();
        PlayerTask task = event.getTaskItem();
        PlayerFile file = PlayerFile.getPlayerFile(player.getName());
        Level level = player.getLevel();
        level.addSound(player.getPosition(),Sound.TILE_PISTON_OUT);
        PlayerTask newTask = file.getTaskByName(task.getTaskName());
        if(RsTask.showLoading){
            if(!file.isSuccess(newTask.getTaskName())){
                String send = RsTask.getTask().getLag("run-task").
                        replace("%s",task.getTaskFile().getShowName()).replace("%c", RsTask.getTask().
                        getTaskLoading(task.getTaskName(),player.getName())+"").replace("\\n","\n");
                DataTool.sendMessage(player,send);
            }else{
                if(!RsTask.taskNames.contains(task.getTaskName())){
                    level.addSound(player.getPosition(),Sound.BLOCK_COMPOSTER_READY);
                    String send = RsTask.getTask().getLag("success-message")
                            .replace("%d",task.getTaskFile()
                                    .getStar()+"").replace("%s",task.getTaskFile().getShowName()).replace("\\n","\n");
                    DataTool.sendMessage(player,send);
                }
                if(RsTask.canSuccess){
                    PlayerFile.givePlayerSuccessItems(player,task.getTaskName());
                }
            }
        }


    }

    @EventHandler
    public void onTimeOut(TaskTimeOutEvent event){
        String playerName = event.getPlayer().getPlayerName();
        Player player = Server.getInstance().getPlayer(playerName);
        if(player != null){
            player.sendMessage(RsTask.getTask().getLag("task-time-out","§d§l[任务系统]§c 你的任务 %s 超时啦").
                    replace("%s",event.getFile().getShowName()));

        }

    }

    @EventHandler
    public void onGiveUp(PlayerGiveUpTaskEvent event){
        if(event.isCancelled()){
            return;
        }
        Player player = event.getPlayer();
        TaskFile file = event.getFile();
        PlayerFile file2 = PlayerFile.getPlayerFile(player.getName());
        if(!file2.closeTask(file.getTaskName())){
            Server.getInstance().getLogger().warning("玩家"+player.getName()+"取消"+file.getTaskName()+"任务异常");
            return;
        }
        player.sendMessage(RsTask.getTask().getLag("giveUpTaskMessage","§d§l[任务系统]§b 您放弃了 %s 任务")
                .replace("%s",file.getShowName()));
        RsTask.getTask().getClickTask.remove(player);

    }


    @EventHandler
    public void onReceive(PlayerClickTaskEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()){
            return;
        }
        if(player.getGamemode() == 1) {
            player.sendMessage(RsTask.getTask().getLag("CreateUI","§d§l[任务系统]§c创造模式无法唤醒UI"));
            return;
        }
        TaskFile file = event.getFile();

        if(TaskFile.runTaskFile(player,file)) {
            addBook(player, file);
            RsTask.getTask().getClickTask.put(player, file);
            RsTask.getClickStar.put(player,file.getGroup());
            if(event.isShow()){
                CreateMenu.sendTaskMenu(player, file);
            }
        }
//
    }

    private void addBook(Player player, TaskFile file) {
        if(RsTask.canGiveBook){
            if(!TaskBook.canInventory(player,file.getTaskName())){
                ItemBookWritten written = new ItemBookWritten();
                TaskBook book = new TaskBook(written);
                book.setTitle(file.getTaskName());
                book.setCustomName(file.getShowName());
                book.writeIn("\n\n\n\n加载中...请再次打开");
                ItemBookWritten written1 =  book.toBook();
                player.getInventory().addItem(written1.clone());
            }
        }
    }

    @EventHandler
    public void useItem(PlayerOpenBookEvent event){
        Player player = event.getPlayer();
        TaskBook book = event.getBookWritten();
        TaskFile file = TaskFile.getTask(book.title);
        if(file != null){
            if(!PlayerFile.getPlayerFile(player.getName()).issetTask(file)){
                PlayerClickTaskEvent events = new PlayerClickTaskEvent(file,player);
                Server.getInstance().getPluginManager().callEvent(events);
            }
        }
        book.upData(file,player);
        ItemBookWritten written = book.toBook();
        player.getInventory().removeItem(player.getInventory().getItemInHand());
        player.getInventory().setItemInHand(written.clone());


    }

    @EventHandler
    public void onAddTask(PlayerAddTaskEvent event){
        Player player = event.getPlayer();
        TaskFile file = event.getFile();
        if(player.getGamemode() == 1) {
            player.sendMessage(RsTask.getTask().getLag("CreateUI","§d§l[任务系统]§c创造模式无法唤醒UI"));
            return;
        }

        if(file != null){
            PlayerFile file1 = PlayerFile.getPlayerFile(player.getName());
            file1.addTask(file);
            addBook(player, file);
        }

    }
    @EventHandler
    public void onSuccessTask(SuccessTaskEvent event){
        if(event.isCancelled()){
            return;
        }
        Player player = event.getPlayer();
        String taskName = event.getTaskName();
        TaskFile file = TaskFile.getTask(taskName);
        int success = 0;
        if(file != null && player.isOnline()){
            if(file.getType() == TaskFile.TaskType.CollectItem){
                TaskItem[] items1 = file.getTaskItem();
                for(TaskItem item:items1){
                    ItemClass itemClass = ItemClass.toItem(item);
                    if(itemClass != null){
                        itemClass.getItem().setCount(item.getEndCount());
                        if(DataTool.getCount(player,itemClass) >= itemClass.getItem().getCount()){
                            player.getInventory().removeItem(itemClass.getItem());
                        }else{
                            event.setCancelled();
                            player.sendMessage(RsTask.getTask().getLag("error-task","§d§l[任务系统]§c出现异常!!!"));
                            return;
                        }
                    }
                }
            }

            SuccessItem item;
            if(PlayerFile.getPlayerFile(player.getName()).isFirst(file)){
                item = file.getFirstSuccessItem();
            }else{
                item = file.getSuccessItem();
            }
            if(item.getItem() != null && item.getItem().length > 0){
                for(ItemClass itemClass:item.getItem()){
                    if(itemClass != null){
                        player.getInventory().addItem(itemClass.getItem().clone());
                        player.sendMessage(RsTask.getTask().getLag("add-item-message")
                                .replace("%s", ItemIDSunName.getIDByName(itemClass.getItem())).replace("%c",itemClass.getItem().getCount()+""));
                    }

                }
            }
            if(item.getCmd() != null && item.getCmd().length > 0){
                for(CommandClass commandClass:item.getCmd()){
                    if(commandClass != null){
                        Server.getInstance().getCommandMap().dispatch(new ConsoleCommandSender(),commandClass.getCmd().replace("%p",player.getName()));
                        player.sendMessage(RsTask.getTask().getLag("add-Cmd-message").replace("%s",commandClass.getSendMessage()));
                    }
                }
            }
            if(RsTask.loadEconomy){
                if(item.getMoney() > 0){
                    RsTask.getTask().getLoadMoney().addMoney(player,item.getMoney());
                    player.sendMessage(RsTask.getTask().getLag("add-money-message")
                            .replace("%c",item.getMoney()+"").
                                    replace("%m", RsTask.getTask().getCoinName()));
                }
            }
            if(RsTask.canOpen()) {
                if(item.getCount() > 0) {
                    success += item.getCount();
                }
            }
        }

        PlayerFile playerFiles = PlayerFile.getPlayerFile(player.getName());
        PlayerTask task = playerFiles.getTaskByName(taskName);
        if(task == null){
            playerFiles.addTask(taskName);
            task = playerFiles.getTaskByName(taskName);
        }
        initSuccessTask(task,playerFiles,success,player,event);

    }


    private void initSuccessTask(PlayerTask task,PlayerFile playerFiles,int success,Player player,SuccessTaskEvent event){
        PlayerTaskClass playerTaskClass = task.getTaskClass();
        TaskFile file;
        TaskItem[] items = playerTaskClass.getValue();
        for(TaskItem item:items){
            item.setEndCount(0);
        }
        playerTaskClass.setOpen(false);
        playerTaskClass.setValue(items);
        playerTaskClass.setCount(playerTaskClass.getCount()+1);
        playerTaskClass.setTime(new Date());
        task.setTaskClass(playerTaskClass);
        if(RsTask.canOpen()){
            playerFiles.setCount(playerFiles.getCount() + success);
        }

        playerFiles.setPlayerTask(task);
        Level level = player.getLevel();
        level.addSound(player.getPosition(), Sound.RANDOM_LEVELUP);
        RsTask.taskNames.remove(event.getTaskName());
        DataTool.spawnFirework(event.getPlayer());
        file = TaskFile.getTask(event.getTaskName());
        if(file != null){
            String send = file.getBroadcastMessage().
                    replace("%p",player.getName()).replace("%s",file.getShowName());
            if(file.getMessageType() == 0){
                Server.getInstance().broadcastMessage(send);
            }else{
                player.sendMessage(send);
            }
        }

    }





    @EventHandler
    public void onDelTask(DelTaskEvent event){
        TaskFile file = event.getTask();

        RsTask.getTask().tasks.remove(file.getTaskName());
        Server.getInstance().getLogger().info("[任务系统] 准备删除"+file.getTaskName()+"任务");
        RsTask.getTask().taskConfig.remove(event.getTask().getTaskName());
        Server.getInstance().getLogger().info("[任务系统] 开始查找玩家");
        LinkedList<String> players = RsTask.getTask().getPlayerNames();
        Server.getInstance().getLogger().info("[任务系统] 已查找到"+players.size()+"位玩家");
        int i = 0;
        for(String playerName:players){
            Player player = Server.getInstance().getPlayer(playerName);
            if(player != null){
                RsTask.getTask().getClickTask.remove(player);
            }

            PlayerFile file1 = PlayerFile.getPlayerFile(playerName);
            if(file1.issetTask(file)){
                i++;
                if(!file1.delTask(file.getTaskName())){
                    file1.toSave();
                    Server.getInstance().getLogger().info("[任务系统] 玩家"+playerName+"移除"+file.getTaskName()+"任务失败");
                }else{
                    file1.toSave();
                    Server.getInstance().getLogger().info("[任务系统] 玩家"+playerName+"移除"+file.getTaskName()+"任务成功");
                }
            }

        }
        Server.getInstance().getLogger().info("[任务系统] 已将"+file.getTaskName()+"任务从"+i+"位玩家移除");
    }



    @EventHandler(priority = EventPriority.LOWEST,ignoreCancelled = true)
    public void onInventoryChange(EntityInventoryChangeEvent event){
        Entity player = event.getEntity();
        if(player instanceof Player){
            try{
                PlayerFile file = PlayerFile.getPlayerFile(player.getName());
                if(file != null) {
                    LinkedList<PlayerTask> getTasks = file.getInviteTasks();
                    if (getTasks == null || getTasks.size() == 0) {
                        return;
                    }
                   Server.getInstance().getScheduler().scheduleAsyncTask(RsTask.getTask(),
                            new CheckInventoryTask((Player) player, getTasks, file, event.getOldItem(), event.getNewItem(), event.isCancelled()));
                }
            }catch(Exception ignored){ }

        }
    }
    @EventHandler
    public void pickBlock(PlayerBlockPickEvent event){
        Item item = event.getItem();
        if(event.isCancelled()) {
            return;
        }
        API.addItem(event.getPlayer(),item,TaskFile.TaskType.CollectItem);
    }






    public static void defaultUseTask(String player, String item, TaskFile.TaskType type, boolean echo){
        defaultUseTask(player,item,type,1,echo,true);
    }



    /** DIY 任务哦 ~~~*/

    public static void defaultUseTask(String player, String item, TaskFile.TaskType type,int add, boolean echo,boolean canAdd){

        PlayerFile file = PlayerFile.getPlayerFile(player);
        LinkedList<PlayerTask> getTasks = file.getInviteTasks();
        for(PlayerTask task:getTasks){
            if(!task.getTaskClass().issetTaskItem(item)){
                continue;
            }
            if(task.getTaskClass().getOpen()){
                if(task.getTaskFile().getType() == type){
                    if(canAdd){
                        if(!file.addTaskValue(task.getTaskName(),item,add)){
                            if(echo){
                                Server.getInstance().getLogger().warning(player+"完成"+task.getTaskName()+"任务中支线"+item+"出现异常");
                                return;
                            }
                        }else{
                            Player player1 = Server.getInstance().getPlayer(player);
                            if(player1 != null){
                                useTask(player1,task);
                            }
                        }
                    }else{
                        if(file.setTaskValue(task.getTaskName(),item,add)){
                            Player player1 = Server.getInstance().getPlayer(player);
                            if(player1 != null){
                                useTask(player1,task);
                            }
                        }
                    }
                }
            }
        }

    }

    private static void useTask(Player player, PlayerTask task){
        if(player.getGamemode() == 1){
            player.sendMessage(RsTask.getTask().getLag("CreateTask","§d§l[任务系统]§c创造模式无法增加任务"));
            return;
        }
        UseTaskEvent event = new UseTaskEvent(player,task);
        Server.getInstance().getPluginManager().callEvent(event);
    }


}
