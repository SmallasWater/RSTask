package com.task.form;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerFormRespondedEvent;
import cn.nukkit.event.server.DataPacketReceiveEvent;
import cn.nukkit.form.response.FormResponseCustom;
import cn.nukkit.form.response.FormResponseModal;
import cn.nukkit.form.response.FormResponseSimple;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.network.protocol.InventoryTransactionPacket;
import com.task.RsTask;
import com.task.events.PlayerClickTaskEvent;
import com.task.events.PlayerGiveUpTaskEvent;
import com.task.events.PlayerOpenBookEvent;
import com.task.utils.DataTool;
import com.task.utils.task.CollectItemTask;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.SuccessItem;
import com.task.utils.tasks.taskitems.TaskBook;
import com.task.utils.tasks.taskitems.TaskItem;

import java.util.LinkedList;

/**
 * @author SmallasWater
 */
public class ListenerMenu implements Listener {

    public LinkedList<Player> onRunning = new LinkedList<>();

    @EventHandler
    public void onPlayerFormResponded(PlayerFormRespondedEvent event) {
        Player player = event.getPlayer();
        int fromId = event.getFormID();
        switch (fromId) {
            case CreateMenu.MENU:
                if (event.wasClosed()) {
                    return;
                }
                FormResponseSimple response = (FormResponseSimple) event.getResponse();
                int starCount = DataTool.starNeed(response.getClickedButtonId());
                if (RsTask.canOpen()) {
                    PlayerFile pf = PlayerFile.getPlayerFile(player.getName());
                    if (pf.getCount() < starCount) {
                        player.sendMessage(RsTask.getTask().getLag("unlocked").replace("%f", RsTask.getTask().getFName()));
                        return;
                    }
                }
                int c = CreateMenu.clickGroup.get(player).get(response.getClickedButtonId());
                RsTask.getClickStar.put(player, c);
                CreateMenu.sendTaskList(player, RsTask.getClickStar.get(player));
                break;
            case CreateMenu.INVITE:
                if (event.wasClosed()) {
                    return;
                }
                response = (FormResponseSimple) event.getResponse();
                if (CreateMenu.runTaskFiles.containsKey(player)) {
                    LinkedList<TaskFile> fileList = CreateMenu.runTaskFiles.get(player);
                    onRunning.add(player);
                    TaskFile taskFile = fileList.get(response.getClickedButtonId());
                    PlayerClickTaskEvent clickTaskEvent = new PlayerClickTaskEvent(taskFile, player);
                    Server.getInstance().getPluginManager().callEvent(clickTaskEvent);
                    CreateMenu.runTaskFiles.remove(player);
                }
                break;
            case CreateMenu.TASKS:
                if (event.wasClosed()) {
                    return;
                }
                response = (FormResponseSimple) event.getResponse();
                LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(RsTask.getClickStar.get(player));
                TaskFile file = taskFiles.get(response.getClickedButtonId());
                PlayerClickTaskEvent event2 = new PlayerClickTaskEvent(file, player);
                Server.getInstance().getPluginManager().callEvent(event2);
                break;
            case CreateMenu.TASKS_MENU:
                if (event.wasClosed()) {
                    return;
                }
                decodeClickTask(player, (FormResponseSimple) event.getResponse());
                break;
            case CreateMenu.AGAIN:
                if (event.wasClosed()) {
                    if (RsTask.getTask().getClickTask.containsKey(player)) {
                        CreateMenu.sendTaskMenu(player, RsTask.getTask().getClickTask.get(player));
                    }
                    return;
                }
                FormResponseModal responseModal = (FormResponseModal) event.getResponse();
                if (responseModal.getClickedButtonId() == 0) {
                    TaskFile file1 = RsTask.getTask().getClickTask.get(player);
                    PlayerGiveUpTaskEvent event1 = new PlayerGiveUpTaskEvent(player, file1);
                    Server.getInstance().getPluginManager().callEvent(event1);
                    return;
                } else {
                    CreateMenu.sendTaskMenu(player, RsTask.getTask().getClickTask.get(player));
                }
                break;
            case CreateMenu.CREATE:
                if (event.wasClosed()) {
                    return;
                }
                decodeTask(player, (FormResponseCustom) event.getResponse());
                break;
            default:
                break;
        }
    }

    @EventHandler
    public void onDataPacketReceive(DataPacketReceiveEvent event) {
        if (event.getPacket() instanceof InventoryTransactionPacket) {
            Player player = event.getPlayer();
            Item item = player.getInventory().getItemInHand();
            if (item instanceof ItemBookWritten) {
                if (TaskBook.isBook((ItemBookWritten) item)) {
                    PlayerOpenBookEvent event1 = new PlayerOpenBookEvent(player, TaskBook.getTaskBookByItem((ItemBookWritten) item));
                    Server.getInstance().getPluginManager().callEvent(event1);
                }
            }
        }
    }

    private void decodeClickTask(Player player, FormResponseSimple data) {
        int clickedButtonId = data.getClickedButtonId();
        if (clickedButtonId == 0) {
            if (RsTask.getTask().getClickTask.containsKey(player)) {
                TaskFile file1 = RsTask.getTask().getClickTask.get(player);
                PlayerFile file2 = PlayerFile.getPlayerFile(player.getName());
                RsTask.executor.submit(new CollectItemTask(RsTask.getTask(), player));
                if (file2.isSuccess(file1.getTaskName())) {
                    PlayerFile.givePlayerSuccessItems(player, file1.getTaskName());
                } else {
                    player.sendMessage(RsTask.getTask().getLag("unable-complete"));
                }
            } else {
                Server.getInstance().getLogger().warning("无法获取玩家" + player.getName() + "点击的任务");
            }
        } else if (clickedButtonId == 1) {
            CreateMenu.sendAgain(player);
        } else {
            if (onRunning.contains(player)) {
                onRunning.remove(player);
                CreateMenu.sendMenuRunningTaskList(player);
                return;
            }
            CreateMenu.sendTaskList(player, RsTask.getClickStar.get(player));
        }
    }

    private void decodeTask(Player player, FormResponseCustom data) {
        String name = data.getInputResponse(1);
        if (TaskFile.isFileTask(name)) {
            player.sendMessage("§c抱歉，名为" + name + "的任务已经存在");
            return;
        }
        int l = data.getDropdownResponse(2).getElementID();
        TaskFile.TaskType type = TaskFile.TaskType.values()[l];
        int size = 1;
        try {
            size = Integer.parseInt(data.getInputResponse(4));
        } catch (Exception ignore) {
        }
        int group = data.getDropdownResponse(3).getElementID();
        String message = data.getInputResponse(5);
        String dStringItem = data.getInputResponse(6);
        TaskItem[] items = new TaskItem[dStringItem.split("&").length];
        if (dStringItem.split("&").length < 2) {
            TaskItem item = TaskItem.toTaskItem(name, dStringItem.split("&")[0]);
            if (item == null) {
                player.sendMessage("§c抱歉,任务条件出现问题，请检查后重新创建");
                return;
            }
            items[0] = item;
        } else {
            int i = 0;
            for (String stings : dStringItem.split("&")) {
                TaskItem item = TaskItem.toTaskItem(name, stings);
                if (item == null) {
                    player.sendMessage("§c抱歉,任务条件出现问题，请检查后重新创建");
                    return;
                }
                items[i++] = item;
            }
        }
        SuccessItem successItem1 = new SuccessItem();
        String successItems = data.getInputResponse(7);
        if (successItems.split("&").length > 1) {
            for (String add : successItems.split("&")) {
                successItem1.add(add);
            }
        } else {
            successItem1 = SuccessItem.toSuccessItem(successItems);
        }
        TaskFile file1 = new TaskFile(name, type, items, message, size, group, successItem1);
        DataTool.createTask(file1);
        player.sendMessage("§a任务 " + name + " 创建成功 请输入/task reload 同步任务");
    }

}
