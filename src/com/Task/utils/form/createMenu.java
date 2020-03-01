package com.Task.utils.form;


import cn.nukkit.Player;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import com.Task.RSTask;
import com.Task.utils.ItemIDSunName;
import com.Task.utils.Task.CollectItemTask;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.TaskItems.*;
import com.Task.utils.Tasks.playerFile;

import java.util.LinkedList;
import java.util.Map;

public class createMenu {

    static final int Menu = 0xcc1001;
    public static final int Tasks = 0xcc1002;
    static final int Tasks_Menu = 0xcc1003;
    static final int Create = 0xcc1004;
    static final int again = 0xcc1005;

    public static RSTask task = RSTask.getTask();


    public static void sendMenu(Player player){
        playerFile playerFiles = playerFile.getPlayerFile(player.getName());
        FormWindowSimple simple =
                new FormWindowSimple
                        (task.getLag("title"),(RSTask.canOpen())?
                                (task.getLag("player-task-integral").replace("%c",playerFiles.getCount()+"").replace("%f",RSTask.getTask().getFName()))
                                : "");
        int i = 0;
        Map map = ((Map)task.getConfig().get("自定义图片路径"));
        for (Object o:map.keySet()){
            Map map1 = (Map) map.get(o);
            String s = " ";
            if(RSTask.canOpen()){
                if(playerFiles.canLock(i)){
                    if(RSTask.getTask().canShowLodding()){
                        if(playerFiles.getCanInviteTasks(i).size() == 0 && playerFiles.getInviteTasks(i).size() == 0 && playerFiles.getSuccessTasks(i).size() == 0){
                            s = (RSTask.getTask().getLag("success-all"));
                        }else if(playerFiles.getSuccessTasks(i).size() > 0){
                            s = (RSTask.getTask().getLag("task-message-success").replace("%c",playerFiles.getSuccessTasks(i).size()+""));
                        }else if(playerFiles.getInviteTasks(i).size() > 0){
                            s = (RSTask.getTask().getLag("task-message-lodding").replace("%c",playerFiles.getInviteTasks(i).size()+""));
                        }else if(playerFiles.getCanInviteTasks(i).size() > 0){
                            s = (RSTask.getTask().getLag("task-message-can-receive").replace("%c",playerFiles.getCanInviteTasks(i).size()+""));
                        }
                    }
                }else{
                    s = (RSTask.getTask().getLag("Lock").replace("%c",RSTask.starNeed(i)+"").replace("%f",RSTask.getTask().getFName()));
                }
            }
            ElementButton button = new ElementButton(map1.get("名称")+s);
            ElementButtonImageData imageData = new ElementButtonImageData(map1.get("图片类型").toString().equals("网络")?"url":"path",(String) map1.get("图片路径"));
            button.addImage(imageData);
            simple.addButton(button);

            i ++ ;
        }
        send(player,simple,Menu);
    }


    static void sendTaskList(Player player,int star){
        LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(star);

        playerFile playerFiles = playerFile.getPlayerFile(player.getName());
        FormWindowSimple simple = new FormWindowSimple(task.getLag("title"),
                RSTask.getTask().getLag("sendMenu-content"));

        for(TaskFile file:taskFiles) {
            if(file != null){
                String s = "";
                switch (playerFiles.getTaskType(file)) {
                    case Running:
                        s = (RSTask.getTask().getLag("using"));
                        break;
                    case No_Invite:
                        s = (RSTask.getTask().getLag("not-receive").replace("%s",file.getLastTask().trim()));
                        break;
                    case Success:
                        s = (RSTask.getTask().getLag("success"));
                        break;
                    case can_Invite:
                        s = (RSTask.getTask().getLag("can-receive"));
                        break;
                    case isSuccess_canInvite:
                        s = (RSTask.getTask().getLag("enable-receive"));
                        break;
                    case isSuccess_noInvite:
                        s = (RSTask.getTask().getLag("cannot-receive"));
                        break;
                        default:
                            break;
                }
                ElementButton button = file.getButton().toButton();
                button.setText(file.getTaskName() + s);
                simple.addButton(button);
                if(simple.getButtons().size() == 0){
                    simple.setContent((taskFiles.size() == 0)?"\n\n\n\n\n\n    "+RSTask.getTask().getLag("no-task"):"");
                }
            }
        }
        send(player,simple,Tasks);
    }

    public static void sendAgain(Player player){
        if(RSTask.getTask().getClickTask.containsKey(player)){
            TaskFile file = RSTask.getTask().getClickTask.get(player);
            if(file != null){
                FormWindowModal simple = new FormWindowModal(task.getLag("title"),
                        RSTask.getTask().getLag("giveUpChose","§d§l您确定要放弃了 %s 任务吗?\n§c放弃后会丢失当前进度")
                                .replace("%s",file.getTaskName()),"确定","取消");

                send(player,simple,again);
            }
        }else{
            player.sendMessage("§c请再尝试一次");
        }
    }

    public static LinkedList<String> toTaskItemString(TaskItem[] items,Player player){
        LinkedList<String> builder = new LinkedList<>();
        for(TaskItem item:items){
            playerFile file2 = playerFile.getPlayerFile(player.getName());
            playerTask task = file2.getTaskByName(item.getTaskName());
            if(task != null){
                PlayerTaskClass taskClass = task.getTaskClass();
                int playerItem = taskClass.getLoad(item);
                int taskCount = item.getEndCount();
                if(item.getTaskTag() != TaskItem.TaskItemTag.diyName){
                    builder.add(ItemIDSunName.getIDByName(item.getItemClass().getItem())+"> "+playerItem+" / "+taskCount+"\n");
                }else{
                    builder.add(item.getTask()+"> "+playerItem+" / "+taskCount+"\n");
                }
            }
        }
        return builder;
    }

    public static StringBuilder getTitles(TaskFile file){
        StringBuilder builder = new StringBuilder();
        builder.append(RSTask.getTask().getLag("tast-title")).append("§r ").append(file.getTaskName()).append("\n");
        builder.append(RSTask.getTask().getLag("task-difficulty")).append("§r ").append(RSTask.getStar(file.getStar())).append("\n\n");
        builder.append(RSTask.getTask().getLag("task-introduce")).append("§r \n").append(file.getTaskMessage()).append("\n\n");
        return builder;
    }

    public static void sendTaskMenu(Player player, TaskFile file){

        FormWindowSimple simple = new FormWindowSimple(task.getLag("title"),"");
        StringBuilder builder = new StringBuilder();
        builder.append(getTitles(file));
        TaskFile file1 = TaskFile.getTask(file.getTaskName());
        builder.append(RSTask.getTask().getLag("task-speed")).append("§r\n");
        TaskItem[] items = new TaskItem[]{};
        if(file1 != null){
            items = file1.getTaskItem();
        }
        LinkedList<String> linkedList = new LinkedList<>();
        if(items.length > 0){
            CollectItemTask.onRun(player);
            linkedList = toTaskItemString(items,player);
            for(String s:linkedList){
                builder.append(s);
            }
        }else{
            builder.append(RSTask.getTask().getLag("notTasks")).append("§r\n");
        }

        builder.append("\n\n");


        builder.append(RSTask.getTask().getLag("success-item")).append("§r\n");
        successItem successItem = file.getFristSuccessItem();
        if(!playerFile.getPlayerFile(player.getName()).isFrist(file)){
            successItem = file.getSuccessItem();
        }
        LinkedList<StringBuilder> builders = new LinkedList<>();
        if(successItem != null){
            builders = successItem.toList();
        }
        if(builders.size() > 0){
            for(StringBuilder builder1:builders){
                builder.append(builder1).append("\n");
            }
        }else{
            builder.append(RSTask.getTask().getLag("nothave-SuccessItem"));
        }
        builder.append("\n");
        simple.setContent(builder.toString());

        if(playerFile.getPlayerFile(player.getName()).isSuccess(file) && linkedList.size() == items.length){
            simple.addButton(getSuccessButton());
        }else{
            simple.addButton(getCancelButton());
        }

        ElementButton giveUp = new ElementButton(RSTask.getTask().getLag("giveUpTask","§c放弃任务"));
        giveUp.addImage(new ElementButtonImageData("path","textures/ui/book_trash_default"));
        simple.addButton(giveUp);
        if(RSTask.canBack()){
            ElementButton button2 = new ElementButton(RSTask.getTask().getLag("back","返回"));
            ElementButtonImageData imageData2 = new ElementButtonImageData("path","textures/ui/refresh_light");
            button2.addImage(imageData2);
            simple.addButton(button2);
        }

        send(player,simple,Tasks_Menu);
    }


    public static void sendCreateTaskMenu(Player player){
        FormWindowCustom custom = new FormWindowCustom("创建任务");
        custom.addElement(new ElementLabel("任务创建UI 请根据提示填写(此UI仅提供简易的任务创建，若进一步更改请修改配置)"));
        custom.addElement(new ElementInput("请输入任务名称","例如: 任务①--破坏收集橡木","任务①--破坏收集橡木"));
        LinkedList<String> list = new LinkedList<>();
        for(TaskFile.TaskType type: TaskFile.TaskType.values()){
            list.add(type.getTaskType());
        }
        custom.addElement(new ElementDropdown("请选择任务类型",list,3));
        LinkedList<String> list1 = new LinkedList<>();
        Map map = ((Map)task.getConfig().get("自定义图片路径"));
        for(int i = 0;i<map.size();i++){
            list1.add((i+1)+"");
        }
        custom.addElement(new ElementDropdown("请选择任务难度",list1));
        custom.addElement(new ElementInput("请输入任务介绍","例如: 收集10个橡木","收集10个橡木"));
        custom.addElement(new ElementInput("请输入任务完成条件(&区分多个元素)(自定义任务请输 内容:数量)","例如: 17:0:10@item 或 id:10@tag(收集任务)","17:0:10@item"));
        custom.addElement(new ElementInput("请输入任务奖励(&区分多个元素)@item 为奖励物品 @tag奖励TagItem.json里的物品@money奖励金钱 @Cmd奖励指令(%p代表玩家)","例如: 366:0:1@item 或 id:1@tag","366:0:1@item&100@money"));
        send(player,custom,Create);
    }




    private static void send(Player player,FormWindow window,int id){

        player.showFormWindow(window,id);

    }

    private static ElementButton getCancelButton(){
        ElementButton button = new ElementButton(RSTask.getTask().getLag("unsubmission-task"));
        ElementButtonImageData imageData = new ElementButtonImageData("path","textures/ui/cancel");
        button.addImage(imageData);
        return button;
    }

    private static ElementButton getSuccessButton(){
        ElementButton button = new ElementButton(RSTask.getTask().getLag("submission-task"));
        ElementButtonImageData imageData = new ElementButtonImageData("path","textures/ui/confirm");
        button.addImage(imageData);
        return button;
    }





}
