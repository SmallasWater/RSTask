package com.Task.utils.form;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import com.Task.RSTask;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.TaskItems.TaskBook;
import com.Task.utils.Tasks.TaskItems.TaskItem;
import com.Task.utils.Tasks.TaskItems.successItem;
import com.Task.utils.Tasks.playerFile;
import com.Task.utils.events.playerGiveUpTaskEvent;
import com.Task.utils.events.playerClickTaskEvent;
import com.Task.utils.events.playerOpenBookEvent;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.LinkedList;

public class ListenerMenu implements Listener{

    @EventHandler
    public void getUI(DataPacketReceiveEvent event){
        String data;
        ModalFormResponsePacket ui;
        Player player = event.getPlayer();
        if((event.getPacket() instanceof ModalFormResponsePacket)){
            ui = (ModalFormResponsePacket)event.getPacket();
            data = ui.data.trim();
            int fromId = ui.formId;
            switch (fromId){
                case createMenu.Menu:
                    // 测试难度1
                    if(data.equals("null")) {
                        return;
                    }
                    int starCount = RSTask.starNeed(Integer.parseInt(data));
                    if(RSTask.canOpen()){
                        playerFile pf = playerFile.getPlayerFile(player.getName());
                        if(pf.getCount() < starCount){
                            player.sendMessage(RSTask.getTask().getLag("unlocked").replace("%f",RSTask.getTask().getFName()));
                            return;
                        }
                    }
                    RSTask.getClickStar.put(player,Integer.parseInt(data));
                    createMenu.sendTaskList(player,RSTask.getClickStar.get(player));
                    break;
                case createMenu.Tasks:
                    if(data.equals("null")) {
                        return;
                    }
                    LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(RSTask.getClickStar.get(player));
                    TaskFile file = taskFiles.get(Integer.parseInt(data));
                    playerClickTaskEvent event2 = new playerClickTaskEvent(file,player);
                    Server.getInstance().getPluginManager().callEvent(event2);
                    break;
                case createMenu.Tasks_Menu:
                    if(data.equals("null")) {
                        return;
                    }
                    if(Integer.parseInt(data) == 0){
                        if(RSTask.getTask().getClickTask.containsKey(player)){
                            TaskFile file1 = RSTask.getTask().getClickTask.get(player);
                            playerFile file2 = playerFile.getPlayerFile(player.getName());
                            if(file2.isSuccess(file1.getTaskName())){
                                playerFile.givePlayerSuccessItems(player,file1.getTaskName());
                            }else{
                                player.sendMessage(RSTask.getTask().getLag("unable-complete"));
                            }
                        }else{
                            Server.getInstance().getLogger().warning("无法获取玩家"+player.getName()+"点击的任务");
                        }
                    }else if(Integer.parseInt(data) == 1){
                        createMenu.sendAgain(player);
                    }else{
                        createMenu.sendTaskList(player,RSTask.getClickStar.get(player));
                    }
                    break;
                case createMenu.again:
                    if(data.equals("null")){
                        if(RSTask.getTask().getClickTask.containsKey(player)){
                            createMenu.sendTaskMenu(player,RSTask.getTask().getClickTask.get(player));
                        }
                        return;
                    }
                    if(data.equals("true")){
                        TaskFile file1 = RSTask.getTask().getClickTask.get(player);
                        playerGiveUpTaskEvent event1 = new playerGiveUpTaskEvent(file1,player);
                        Server.getInstance().getPluginManager().callEvent(event1);
                        return;
                    }else{
                        createMenu.sendTaskMenu(player,RSTask.getTask().getClickTask.get(player));
                    }
                    break;
                case createMenu.Create:
                    if(data.equals("null")) {
                        return;
                    }
                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();
                    @SuppressWarnings("serial")
                    Object[] UIData = gson.fromJson(data,new TypeToken<Object[]>(){}.getType());
                    if(UIData == null) {
                        return;
                    }
                    if(UIData.length < 1) {
                        return;
                    }
                    String name = (String) UIData[1];
                    if(TaskFile.isFileTask(name)){
                        player.sendMessage("§c抱歉，名为"+name+"的任务已经存在");
                        return;
                    }
                    double l = (double)UIData[2];
                    TaskFile.TaskType type = TaskFile.TaskType.values()[(int)l];
                    int size = (int) ((double)(UIData[3]) + 1);
                    String message = (String) UIData[4];
                    String dStringItem = (String) UIData[5];
                    TaskItem[] items = new TaskItem[dStringItem.split("&").length];
                    if(dStringItem.split("&").length < 2){
                        TaskItem item = TaskItem.toTaskItem(name,dStringItem.split("&")[0]);
                        if(item == null){
                            player.sendMessage("§c抱歉,任务条件出现问题，请检查后重新创建");
                            return;
                        }
                        items[0] = item;
                    }else{
                        int i = 0;
                        for(String stings:dStringItem.split("&")){
                            TaskItem item = TaskItem.toTaskItem(name,stings);
                            if(item == null){
                                player.sendMessage("§c抱歉,任务条件出现问题，请检查后重新创建");
                                return;
                            }
                            items[i++] = item;
                        }
                    }
                    successItem successItem1 = new successItem();
                    String successItems = (String) UIData[6];
                    if(successItems.split("&").length > 1){
                        for(String add:successItems.split("&")){
                            successItem1.add(add);
                        }
                    }else{
                        successItem1 = successItem.toSuccessItem(successItems);
                    }
                    TaskFile file1 = new TaskFile(name,type,items,message,size,successItem1);
                    RSTask.getTask().createTask(file1);
                    player.sendMessage("§a任务 "+name+" 创建成功 请输入/reload-task 同步任务");
                    break;
                default:
                    break;
            }
        }
        if(event.getPacket() instanceof InventoryTransactionPacket){
            Item item = player.getInventory().getItemInHand();
            if(item instanceof ItemBookWritten){
                if(TaskBook.isBook((ItemBookWritten) item)){
                    playerOpenBookEvent event1 = new playerOpenBookEvent(player,TaskBook.getTaskBookByItem((ItemBookWritten) item));
                    Server.getInstance().getPluginManager().callEvent(event1);
                }
            }
        }

    }


}
