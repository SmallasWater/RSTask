package com.task.utils.form;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import cn.nukkit.network.protocol.ModalFormResponsePacket;
import com.google.gson.reflect.TypeToken;
import com.task.RSTask;
import com.task.utils.DataTool;
import com.task.utils.events.PlayerGiveUpTaskEvent;
import com.task.utils.task.CollectItemTask;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.TaskBook;
import com.task.utils.tasks.taskitems.TaskItem;
import com.task.utils.tasks.taskitems.SuccessItem;
import com.task.utils.tasks.PlayerFile;

import com.task.utils.events.PlayerClickTaskEvent;
import com.task.utils.events.PlayerOpenBookEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;


import java.util.LinkedList;

/**
 * @author SmallasWater
 */
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
                case CreateMenu.MENU:
                    // 测试难度1
                    if(data.equals("null")) {
                        return;
                    }
                    int starCount = RSTask.starNeed(Integer.parseInt(data));
                    if(RSTask.canOpen()){
                        PlayerFile pf = PlayerFile.getPlayerFile(player.getName());
                        if(pf.getCount() < starCount){
                            player.sendMessage(RSTask.getTask().getLag("unlocked").replace("%f",RSTask.getTask().getFName()));
                            return;
                        }
                    }
                    RSTask.getClickStar.put(player,Integer.parseInt(data));
                    CreateMenu.sendTaskList(player,RSTask.getClickStar.get(player));
                    break;
                case CreateMenu.TASKS:
                    if(data.equals("null")) {
                        return;
                    }
                    LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(RSTask.getClickStar.get(player));
                    TaskFile file = taskFiles.get(Integer.parseInt(data));
                    PlayerClickTaskEvent event2 = new PlayerClickTaskEvent(file,player);
                    Server.getInstance().getPluginManager().callEvent(event2);
                    break;
                case CreateMenu.TASKS_MENU:
                    decodeClickTask(player,data);
                    break;
                case CreateMenu.AGAIN:
                    if(data.equals("null")){
                        if(RSTask.getTask().getClickTask.containsKey(player)){
                            CreateMenu.sendTaskMenu(player,RSTask.getTask().getClickTask.get(player));
                        }
                        return;
                    }
                    if(data.equals("true")){
                        TaskFile file1 = RSTask.getTask().getClickTask.get(player);
                        PlayerGiveUpTaskEvent event1 = new PlayerGiveUpTaskEvent(file1,player);
                        Server.getInstance().getPluginManager().callEvent(event1);
                        return;
                    }else{
                        CreateMenu.sendTaskMenu(player,RSTask.getTask().getClickTask.get(player));
                    }
                    break;
                case CreateMenu.CREATE:
                    decodeTask(player,data);
                    break;
                default:
                    break;
            }
        }
        if(event.getPacket() instanceof InventoryTransactionPacket){
            Item item = player.getInventory().getItemInHand();
            if(item instanceof ItemBookWritten){
                if(TaskBook.isBook((ItemBookWritten) item)){
                    PlayerOpenBookEvent event1 = new PlayerOpenBookEvent(player,TaskBook.getTaskBookByItem((ItemBookWritten) item));
                    Server.getInstance().getPluginManager().callEvent(event1);
                }
            }
        }

    }

    private void decodeClickTask(Player player,String data){
        if(data.equals("null")) {
            return;
        }
        if(Integer.parseInt(data) == 0){
            if(RSTask.getTask().getClickTask.containsKey(player)){
                TaskFile file1 = RSTask.getTask().getClickTask.get(player);
                PlayerFile file2 = PlayerFile.getPlayerFile(player.getName());
                Server.getInstance().getScheduler().scheduleDelayedTask(new CollectItemTask(player),1);
                if(file2.isSuccess(file1.getTaskName())){
                    PlayerFile.givePlayerSuccessItems(player,file1.getTaskName());
                }else{
                    player.sendMessage(RSTask.getTask().getLag("unable-complete"));
                }
            }else{
                Server.getInstance().getLogger().warning("无法获取玩家"+player.getName()+"点击的任务");
            }
        }else if(Integer.parseInt(data) == 1){
            CreateMenu.sendAgain(player);
        }else{
            CreateMenu.sendTaskList(player,RSTask.getClickStar.get(player));
        }
    }

    private void decodeTask(Player player,String data){
        if(data.equals("null")) {
            return;
        }
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        @SuppressWarnings("serial")
        Object[] uiData = DataTool.toArrayByData(data);
        if(uiData == null) {
            return;
        }
        if(uiData.length < 1) {
            return;
        }
        String name = (String) uiData[1];
        if(TaskFile.isFileTask(name)){
            player.sendMessage("§c抱歉，名为"+name+"的任务已经存在");
            return;
        }
        double l = (double)uiData[2];
        TaskFile.TaskType type = TaskFile.TaskType.values()[(int)l];
        int size = (int) ((double)(uiData[3]) + 1);
        String message = (String) uiData[4];
        String dStringItem = (String) uiData[5];
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
        SuccessItem successItem1 = new SuccessItem();
        String successItems = (String) uiData[6];
        if(successItems.split("&").length > 1){
            for(String add:successItems.split("&")){
                successItem1.add(add);
            }
        }else{
            successItem1 = SuccessItem.toSuccessItem(successItems);
        }
        TaskFile file1 = new TaskFile(name,type,items,message,size,successItem1);
        RSTask.getTask().createTask(file1);
        player.sendMessage("§a任务 "+name+" 创建成功 请输入/task reload 同步任务");
    }


}
