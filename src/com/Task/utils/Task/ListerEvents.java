package com.Task.utils.Task;


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
import com.Task.RSTask;
import com.Task.utils.DataTool;
import com.Task.utils.ItemIDSunName;
import com.Task.utils.Scorebroad.ScoreTask;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.TaskItems.*;
import com.Task.utils.Tasks.playerFile;
import com.Task.utils.events.*;
import com.Task.utils.form.createMenu;

import java.util.Date;
import java.util.LinkedList;

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
        addItem(player,item, TaskFile.TaskType.EatItem);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Item item = event.getItem();
        addItem(player,item, TaskFile.TaskType.DropItem);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void craft(CraftItemEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Item item = event.getRecipe().getResult();
        addItem(player,item, TaskFile.TaskType.CraftItem);
    }



    @EventHandler(priority = EventPriority.MONITOR)
    public void onInt(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) {
            return;
        }
        Item item = event.getItem();
        addItem(player,item, TaskFile.TaskType.Click);

    }



    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!RSTask.getTask().getPlayerFile(player.getName()).exists()){
            Server.getInstance().broadcastMessage(RSTask.getTask().getLag("join-achievement").replace("%p",player.getName()));
            RSTask.getTask().playerConfig.put(player.getName(),RSTask.getTask().getPlayerConfig(player.getName()));
        }
        if(RSTask.loadSocket){
            Server.getInstance().getScheduler().scheduleRepeatingTask(new ScoreTask(player),20);
        }
    }

    @EventHandler
    public void use(useTaskEvent event){
        Player player = event.getPlayer();
        playerTask task = event.getTaskItem();
        playerFile file = playerFile.getPlayerFile(player.getName());
        Level level = player.getLevel();
        level.addSound(player.getPosition(),Sound.TILE_PISTON_OUT);
        playerTask newTask = file.getTaskByName(task.getTaskName());
        if(RSTask.showLoading){
            if(!file.isSuccess(newTask.getTaskName())){
                String send = RSTask.getTask().getLag("run-task").
                        replace("%s",task.getTaskName()).replace("%c",RSTask.getTask().
                        getTaskLoading(task.getTaskName(),player.getName())+"").replace("\\n","\n");
                RSTask.sendMessage(player,send);
            }else{
                if(!RSTask.taskNames.contains(task.getTaskName())){
                    level.addSound(player.getPosition(),Sound.BLOCK_COMPOSTER_READY);
                    String send = RSTask.getTask().getLag("success-message")
                            .replace("%d",task.getTaskFile()
                                    .getStar()+"").replace("%s",task.getTaskName()).replace("\\n","\n");
                    RSTask.sendMessage(player,send);
                }
                if(RSTask.canSuccess){
                    playerFile.givePlayerSuccessItems(player,task.getTaskName());
                }
            }
        }

    }

    @EventHandler
    public void onGiveUp(playerGiveUpTaskEvent event){
        Player player = event.getPlayer();
        TaskFile file = event.getFile();
        playerFile file2 = playerFile.getPlayerFile(player.getName());
        if(!file2.closeTask(file.getTaskName())){
            Server.getInstance().getLogger().warning("玩家"+player.getName()+"取消"+file.getTaskName()+"任务异常");
            return;
        }
        player.sendMessage(RSTask.getTask().getLag("giveUpTaskMessage","§d§l[任务系统]§b 您放弃了 %s 任务")
                .replace("%s",file.getTaskName()));
        RSTask.getTask().getClickTask.remove(player);

    }


    @EventHandler(ignoreCancelled = true)
    public void onReceive(playerClickTaskEvent event){
        Player player = event.getPlayer();
        if(player.getGamemode() == 1) {
            player.sendMessage(RSTask.getTask().getLag("CreateUI","§d§l[任务系统]§c创造模式无法唤醒UI"));
            return;
        }
        TaskFile file = event.getFile();
        if(TaskFile.runTaskFile(player,file)) {
            RSTask.getTask().getClickTask.put(player, file);
            if(event.isShow()){
                createMenu.sendTaskMenu(player, file);
            }
        }
//
    }

    @EventHandler
    public void useItem(playerOpenBookEvent event){
        Player player = event.getPlayer();
        TaskBook book = event.getBookWritten();
        TaskFile file = TaskFile.getTask(book.title);
        if(file != null){
            if(!playerFile.getPlayerFile(player.getName()).issetTask(file)){
                playerClickTaskEvent events = new playerClickTaskEvent(file,player);
                Server.getInstance().getPluginManager().callEvent(events);
            }
        }
        book.upData(file,player);
        ItemBookWritten written = book.toBook();
        player.getInventory().removeItem(player.getInventory().getItemInHand());
        player.getInventory().setItemInHand(written.clone());
    }

    @EventHandler
    public void onAddTask(playerAddTaskEvent event){
        Player player = event.getPlayer();
        TaskFile file = event.getFile();
        if(player.getGamemode() == 1) {
            player.sendMessage(RSTask.getTask().getLag("CreateUI","§d§l[任务系统]§c创造模式无法唤醒UI"));
            return;
        }

        if(file != null){
            playerFile file1 = playerFile.getPlayerFile(player.getName());
            file1.addTask(file);
            file1.toSave();
            if(RSTask.canGiveBook){
                if(!TaskBook.canInventory(player,file.getTaskName())){
                    ItemBookWritten written = new ItemBookWritten();
                    TaskBook book = new TaskBook(written);
                    book.setTitle(file.getTaskName());
                    book.writeIn("\n\n\n\n加载中...请再次打开");
                    ItemBookWritten written1 =  book.toBook();
                    player.getInventory().addItem(written1.clone());
                }
            }
        }

    }
    @EventHandler
    public void onSuccessTask(successTaskEvent event){
        Player player = event.getPlayer();
        String taskName = event.getTaskName();
        TaskFile file = TaskFile.getTask(taskName);
        int success = 0;
        if(file != null && player.isOnline()){
            com.Task.utils.Tasks.TaskItems.successItem item;
            if(playerFile.getPlayerFile(player.getName()).isFrist(file)){
                item = file.getFristSuccessItem();
            }else{
                item = file.getSuccessItem();
            }

            if(item.getItem() != null && item.getItem().length > 0){
                for(ItemClass itemClass:item.getItem()){
                    if(itemClass != null){
                        player.getInventory().addItem(itemClass.getItem().clone());
                        player.sendMessage(RSTask.getTask().getLag("add-item-message")
                                .replace("%s", ItemIDSunName.getIDByName(itemClass.getItem())).replace("%c",itemClass.getItem().getCount()+""));
                    }

                }
            }
            if(item.getCmd() != null && item.getCmd().length > 0){
                for(CommandClass commandClass:item.getCmd()){
                    if(commandClass != null){
                        Server.getInstance().getCommandMap().dispatch(new ConsoleCommandSender(),commandClass.getCmd().replace("%p",player.getName()));
                        player.sendMessage(RSTask.getTask().getLag("add-Cmd-message").replace("%s",commandClass.getSendMessage()));
                    }
                }
            }
            if(RSTask.loadEconomyAPI){
                if(item.getMoney() > 0){
                    me.onebone.economyapi.EconomyAPI.getInstance().addMoney(player,item.getMoney());
                    player.sendMessage(RSTask.getTask().getLag("add-money-message")
                            .replace("%c",item.getMoney()+"").
                                    replace("%m",RSTask.getTask().getCoinName()));
                }
            }
            if(RSTask.canOpen()) {
                if(item.getCount() > 0) {
                    success += item.getCount();
                }
            }
        }

        playerFile playerFiles = playerFile.getPlayerFile(player.getName());
        playerTask task = playerFiles.getTaskByName(taskName);
        if(task == null){
            playerFiles.addTask(taskName);
            task = playerFiles.getTaskByName(taskName);
        }
        PlayerTaskClass playerTaskClass = task.getTaskClass();
        TaskItem[] items = playerTaskClass.getValue();
        for(TaskItem item:items){
            item.setEndCount(0);
        }
        playerTaskClass.setOpen(false);
        playerTaskClass.setValue(items);
        playerTaskClass.setCount(playerTaskClass.getCount()+1);
        playerTaskClass.setTime(new Date());
        task.setTaskClass(playerTaskClass);
        if(RSTask.canOpen()){
            playerFiles.setCount(playerFiles.getCount() + success);
        }

        if(file != null){
            if(file.getType() == TaskFile.TaskType.CollectItem){
                TaskItem[] items1 = file.getTaskItem();
                for(TaskItem item:items1){
                    ItemClass itemClass = ItemClass.toItem(item);
                    if(itemClass != null){
                        itemClass.getItem().setCount(item.getEndCount());
                        player.getInventory().removeItem(itemClass.getItem());
                    }
                }
            }
        }
        playerFiles.setPlayerTask(task);
        playerFiles.toSave();
    }





    @EventHandler
    public void onSuccess(successTaskEvent event){
        Player player = event.getPlayer();
        Level level = player.getLevel();
        level.addSound(player.getPosition(), Sound.RANDOM_LEVELUP);
        RSTask.taskNames.remove(event.getTaskName());
        DataTool.spawnFirework(event.getPlayer());
        TaskFile file = TaskFile.getTask(event.getTaskName());
        if(file != null){
            String send = file.getBroadcastMessage().
                    replace("%p",player.getName()).replace("%s",event.getTaskName());
            if(file.getMessageType() == 0){
                Server.getInstance().broadcastMessage(send);
            }else{
                player.sendMessage(send);
            }
        }

    }

//    @EventHandler
//    public void createTask(createTaskEvent event){
//
//        new Thread(() -> RSTask.getTask().Tasks = TaskFile.getTasks(true)).start();
//    }

    @EventHandler
    public void onDelTask(delTaskEvent event){
        TaskFile file = event.getTask();

        RSTask.getTask().tasks.remove(file.getTaskName());
        Server.getInstance().getLogger().info("[任务系统] 准备删除"+file.getTaskName()+"任务");
        RSTask.getTask().taskConfig.remove(event.getTask().getTaskName());
        Server.getInstance().getLogger().info("[任务系统] 开始查找玩家");
        LinkedList<String> players = RSTask.getTask().getPlayerNames();
        Server.getInstance().getLogger().info("[任务系统] 已查找到"+players.size()+"位玩家");
        int i = 0;
        for(String playerName:players){
            Player player = Server.getInstance().getPlayer(playerName);
            if(player != null){
                RSTask.getTask().getClickTask.remove(player);
            }

            playerFile file1 = playerFile.getPlayerFile(playerName);
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
                playerFile file = playerFile.getPlayerFile(player.getName());
                LinkedList<playerTask> getTasks = file.getInviteTasks();
                if(getTasks == null) {
                    return;
                }
                Thread thread = new Thread(()->{
                    for(playerTask task:getTasks){
                        if(task.getTaskFile().getType() == TaskFile.TaskType.CollectItem) {
                            if(file.isRunning(task.getTaskName()) && task.getTaskClass().getOpen()){
                                TaskItem[] items = task.getTaskClass().getValue();
                                for(TaskItem item:items){
                                    ItemClass itemClass = ItemClass.toItem(item);
                                    if(itemClass != null ){
                                        if(event.getNewItem().equals(itemClass.getItem()) || event.getOldItem().equals(itemClass.getItem())){
                                            int c = CollectItemTask.getCount((Player) player,itemClass);
                                            if(c < 0) {
                                                c = 0;
                                            }
                                            if(c != item.getEndCount()){
                                                if(file.setTaskValue(task.getTaskName(),item.getTask(),c)){
                                                    file.toSave();
                                                    useTaskEvent event1 = new useTaskEvent((Player) player,task);
                                                    Server.getInstance().getPluginManager().callEvent(event1);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if(!event.isCancelled()){
                            if(task.getTaskFile().getType() == TaskFile.TaskType.GetItem){
                                Item item = event.getNewItem();
                                addItem((Player) player,item, TaskFile.TaskType.GetItem);
                            }
                        }

                    }
                });
                thread.start();
            }catch(NullPointerException ignore){}

        }
    }
    @EventHandler
    public void pickBlock(PlayerBlockPickEvent event){
        Item item = event.getItem();
        if(event.isCancelled()) {
            return;
        }
        addItem(event.getPlayer(),item,TaskFile.TaskType.CollectItem);
    }






    public static void defaultUseTask(String player, String item, TaskFile.TaskType type, boolean echo){
        defaultUseTask(player,item,type,1,echo,true);
    }



    /** DIY 任务哦 ~~~*/

    public static void defaultUseTask(String player, String item, TaskFile.TaskType type,int add, boolean echo,boolean canAdd){

        playerFile file = playerFile.getPlayerFile(player);
        LinkedList<playerTask> getTasks = file.getInviteTasks();
        for(playerTask task:getTasks){
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
                            file.toSave();
                            Player player1 = Server.getInstance().getPlayer(player);
                            if(player1 != null){
                                useTask(player1,task);
                            }
                        }
                    }else{
                        if(file.setTaskValue(task.getTaskName(),item,add)){
                            file.toSave();
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

    private static void useTask(Player player,playerTask task){
        if(player.getGamemode() == 1){
            player.sendMessage(RSTask.getTask().getLag("CreateTask","§d§l[任务系统]§c创造模式无法增加任务"));
            return;
        }
        useTaskEvent event = new useTaskEvent(player,task);
        Server.getInstance().getPluginManager().callEvent(event);
    }

    private void addItem(Player player,Item item,TaskFile.TaskType type){
        if(item != null){
            ItemClass itemClass = new ItemClass(item);
            if(!RSTask.getTask().canExisteItemClass(itemClass)){
                defaultUseTask(player.getName(),itemClass.toTaskItem(false), type,false);
            }else{
                defaultUseTask(player.getName(),itemClass.toTaskItem(true),type,false);

            }
        }
    }
}
