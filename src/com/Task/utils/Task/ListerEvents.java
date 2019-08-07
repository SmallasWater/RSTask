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
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class ListerEvents implements Listener{
    private LinkedList<Player> giveUp = new LinkedList<>();

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) return;
        Block block = event.getBlock();
        String s = block.getId()+":"+block.getDamage()+"@item";
        defaultUseTask(player.getName(),s, TaskFile.TaskType.BlockBreak,false);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) return;
        Block block = event.getBlock();
        String s = block.getId()+":"+block.getDamage()+"@item";
        defaultUseTask(player.getName(),s, TaskFile.TaskType.BlockPlayer,false);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onUseItem(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) return;
        Item item = event.getItem();
        addItem(player,item, TaskFile.TaskType.EatItem);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) return;
        Item item = event.getItem();
        addItem(player,item, TaskFile.TaskType.DropItem);

    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void Craft(CraftItemEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) return;
        Item item = event.getRecipe().getResult();
        addItem(player,item, TaskFile.TaskType.CraftItem);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onInt(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if(event.isCancelled()) return;
        Item item = event.getItem();
        addItem(player,item, TaskFile.TaskType.Click);

    }


    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!RSTask.getTask().getPlayerFile(player.getName()).exists()){
            Server.getInstance().broadcastMessage(RSTask.getTask().getLag("join-achievement").replace("%p",player.getName()));
            RSTask.getTask().getPlayerConfig(player.getName());
        }
        if(RSTask.loadSocket){
            new ScoreTask(player).init();
        }
    }

    @EventHandler
    public void use(useTaskEvent event){
        Player player = event.getPlayer();
        playerTask task = event.getTaskItem();
        playerFile file = new playerFile(player.getName());
        Level level = player.getLevel();
        level.addSound(player.getPosition(),Sound.TILE_PISTON_OUT);
        playerTask newTask = file.getTaskByName(task.getTaskName());
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
//        giveUp.remove(player);
        player.sendMessage(RSTask.getTask().getLag("giveUpTaskMessage","§d§l[任务系统]§b 您放弃了 %s 任务")
                .replace("%s",file.getTaskName()));
        RSTask.getClickTask.remove(player);
    }


    @EventHandler
    public void onReceive(playerClickTaskEvent event){
        Player player = event.getPlayer();
        if(player.getGamemode() == 1) {
            player.sendMessage(RSTask.getTask().getLag("CreateUI","§d§l[任务系统]§c创造模式无法唤醒UI"));
            return;
        }
        TaskFile file = event.getFile();
        if(file != null){
            if(TaskFile.runTaskFile(player,file)){
                RSTask.getClickTask.put(player,file);
                createMenu.sendTaskMenu(player,file);
            }
        }
    }

    @EventHandler
    public void onAddTask(playerAddTaskEvent event){
        Player player = event.getPlayer();

        TaskFile file = event.getFile();
        playerFile file1 = playerFile.getPlayerFile(player.getName());
        file1.addTask(file);

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
            if(RSTask.canOpen())
                if(item.getCount() > 0)
                    success += item.getCount();
        }

        playerFile playerFile = new playerFile(player.getName());
        playerTask task = playerFile.getTaskByName(taskName);
        if(task == null){
            playerFile.addTask(taskName);
            task = playerFile.getTaskByName(taskName);
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
            playerFile.setCount(playerFile.getCount() + success);
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
        playerFile.setPlayerTask(task);
    }





    @EventHandler
    public void onSuccess(successTaskEvent event){
        Player player = event.getPlayer();
        Level level = player.getLevel();
        level.addSound(player.getPosition(), Sound.RANDOM_LEVELUP);
        if(RSTask.taskNames.contains(event.getTaskName())){
            RSTask.taskNames.remove(event.getTaskName());
        }
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


    @EventHandler
    public void onDelTask(delTaskEvent event){
        TaskFile file = event.getTask();
        Server.getInstance().getLogger().info("[任务系统] 准备删除"+file.getTaskName()+"任务");
        Server.getInstance().getLogger().info("[任务系统] 开始查找玩家");
        LinkedList<String> players = RSTask.getTask().getPlayerNames();
        Server.getInstance().getLogger().info("[任务系统] 已查找到"+players.size()+"位玩家");
        int i = 0;
        for(String playerName:players){
            Player player = Server.getInstance().getPlayer(playerName);
            if(player != null){
                if(RSTask.getClickTask.containsKey(player)){
                    RSTask.getClickTask.remove(player);
                }
            }
            playerFile file1 = playerFile.getPlayerFile(playerName);
            if(file1.issetTask(file)){
                i++;
                if(!file1.delTask(file.getTaskName())){
                    Server.getInstance().getLogger().info("[任务系统] 玩家"+playerName+"移除"+file.getTaskName()+"任务失败");
                }else{
                    Server.getInstance().getLogger().info("[任务系统] 玩家"+playerName+"移除"+file.getTaskName()+"任务成功");
                }
            }

        }
        Server.getInstance().getLogger().info("[任务系统] 已将"+file.getTaskName()+"任务从"+i+"位玩家移除");
    }



    @EventHandler(priority = EventPriority.MONITOR,ignoreCancelled = true)
    public void onInventoryChange(EntityInventoryChangeEvent event){
        Entity player = event.getEntity();
        if(player instanceof Player){
            if(event.isCancelled()) return;
            playerFile file = new playerFile(player.getName());
            LinkedList<playerTask> getTasks = file.getInviteTasks();
            if(getTasks == null) return;
            for(playerTask task:getTasks){
                if(task.getTaskFile().getType() == TaskFile.TaskType.CollectItem ) {
                    if(file.isRunning(task.getTaskName()) && task.getTaskClass().getOpen()){
                        TaskItem[] items = task.getTaskClass().getValue();
                        for(TaskItem item:items){
                            ItemClass itemClass = ItemClass.toItem(item);
                            if(itemClass != null ){
                                if(event.getNewItem().equals(itemClass.getItem()) || event.getOldItem().equals(itemClass.getItem())){
                                    int c = CollectItemTask.getCount((Player) player,itemClass);
                                    if(event.getOldItem().getCount() > event.getNewItem().getCount()){
                                        c -= (event.getOldItem().getCount()-event.getNewItem().getCount());
                                    }else{
                                        c += (event.getNewItem().getCount() - event.getOldItem().getCount());
                                    }
                                    if(file.setTaskValue(task.getTaskName(),item.getTask(),c)){
                                        useTaskEvent event1 = new useTaskEvent((Player) player,task);
                                        Server.getInstance().getPluginManager().callEvent(event1);
                                    }
//                                    defaultUseTask(player.getName(),item.getTask(), TaskFile.TaskType.CollectItem,c,false,false);
                                }else if(CollectItemTask.getCount((Player) player,itemClass) != item.getEndCount()){
                                    itemClass = ItemClass.toItem(item);
                                    int c = CollectItemTask.getCount((Player) player,itemClass);
                                    if(file.setTaskValue(task.getTaskName(),item.getTask(),c)){
                                        useTaskEvent event1 = new useTaskEvent((Player) player,task);
                                        Server.getInstance().getPluginManager().callEvent(event1);
                                    }
//                                    defaultUseTask(player.getName(),item.getTask(), TaskFile.TaskType.CollectItem,c,false,false);
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
        }
    }






    public static void defaultUseTask(String player, String item, TaskFile.TaskType type, boolean echo){
        defaultUseTask(player,item,type,1,echo,true);
    }



    /** DIY 任务哦 ~~~*/

    public static void defaultUseTask(String player, String item, TaskFile.TaskType type,int add, boolean echo,boolean canAdd){

        playerFile file = new playerFile(player);
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
