package com.Task.utils.Task;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.block.Block;
import cn.nukkit.entity.Entity;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.block.BlockPlaceEvent;
import cn.nukkit.event.entity.EntityInventoryChangeEvent;
import cn.nukkit.event.inventory.CraftItemEvent;
import cn.nukkit.event.player.*;
import cn.nukkit.item.Item;
import com.Task.RSTask;
import com.Task.utils.DataTool;
import com.Task.utils.Scorebroad.ScoreTask;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.TaskItems.ItemClass;
import com.Task.utils.Tasks.TaskItems.TaskItem;
import com.Task.utils.Tasks.TaskItems.playerTask;
import com.Task.utils.Tasks.playerFile;
import com.Task.utils.events.*;
import com.Task.utils.form.createMenu;

import java.util.LinkedList;

public class ListerEvents implements Listener{

    @EventHandler
    public void onBreak(BlockBreakEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String s = block.getId()+":"+block.getDamage()+"@item";
        defaultUseTask(player.getName(),s, TaskFile.TaskType.BlockBreak,false);
    }


    @EventHandler
    public void onPlace(BlockPlaceEvent event){
        Player player = event.getPlayer();
        Block block = event.getBlock();
        String s = block.getId()+":"+block.getDamage()+"@item";
        defaultUseTask(player.getName(),s, TaskFile.TaskType.BlockPlayer,false);
    }


    @EventHandler
    public void onUseItem(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        Item item = event.getItem();
        addItem(player,item, TaskFile.TaskType.EatItem);
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event){
        Player player = event.getPlayer();
        Item item = event.getItem();
        addItem(player,item, TaskFile.TaskType.DropItem);

    }

    @EventHandler
    public void Craft(CraftItemEvent event){
        Player player = event.getPlayer();
        Item item = event.getRecipe().getResult();
        addItem(player,item, TaskFile.TaskType.CraftItem);
    }


    @EventHandler
    public void onInt(PlayerInteractEvent event){
        Player player = event.getPlayer();
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
        playerTask newTask = file.getTaskByName(task.getTaskName());
        if(!file.isSuccess(newTask.getTaskName())){
            String send = RSTask.getTask().getLag("run-task").
                    replace("%s",task.getTaskName()).replace("%c",RSTask.getTask().
                    getTaskLoading(task.getTaskName(),player.getName())+"").replace("\\n","\n");
            RSTask.sendMessage(player,send);
        }else{
            if(!RSTask.taskNames.contains(task.getTaskName())){

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
        player.sendMessage(RSTask.getTask().getLag("giveUpTaskMessage","§d§l[任务系统]§b 您放弃了 %s 任务")
                .replace("%s",file.getTaskName()));
        RSTask.getClickTask.remove(player);
    }


    @EventHandler
    public void onReceive(playerClickTaskEvent event){
        Player player = event.getPlayer();
        TaskFile file = event.getFile();
        if(file != null){
            TaskFile.runTaskFile(player,file);
        }
        RSTask.getClickTask.put(player,file);

        createMenu.sendTaskMenu(player,file);
    }

    @EventHandler
    public void onAddTask(playerAddTaskEvent event){
        Player player = event.getPlayer();
        TaskFile file = event.getFile();
        playerFile file1 = playerFile.getPlayerFile(player.getName());
        file1.addTask(file);

    }




    @EventHandler
    public void onSuccess(successTaskEvent event){
        Player player = event.getPlayer();
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



    @EventHandler
    public void onInventoryChange(EntityInventoryChangeEvent event){
        Entity player = event.getEntity();
        if(player instanceof Player){
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
                if(task.getTaskFile().getType() == TaskFile.TaskType.GetItem){
                    Item item = event.getNewItem();
                    addItem((Player) player,item, TaskFile.TaskType.GetItem);
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
                                useTaskEvent event = new useTaskEvent(player1,task);
                                Server.getInstance().getPluginManager().callEvent(event);
                            }
                        }
                    }else{
                        if(file.setTaskValue(task.getTaskName(),item,add)){
                            Player player1 = Server.getInstance().getPlayer(player);
                            if(player1 != null){
                                useTaskEvent event = new useTaskEvent(player1,task);
                                Server.getInstance().getPluginManager().callEvent(event);
                            }
                        }
                    }
                }
            }
        }
    }

    private void addItem(Player player,Item item,TaskFile.TaskType type){
        if(item != null){
            ItemClass itemClass = new ItemClass(item);
            if(RSTask.getTask().canExisteItemClass(itemClass)){
                defaultUseTask(player.getName(),itemClass.toSaveConfig(), type,false);
            }else{
                defaultUseTask(player.getName(),itemClass.toSaveConfig(true),type,false);
            }
        }
    }
}
