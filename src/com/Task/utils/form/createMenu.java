package com.Task.utils.form;


import cn.nukkit.Player;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import com.Task.RSTask;
import com.Task.utils.ItemIDSunName;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.TaskItems.CommandClass;
import com.Task.utils.Tasks.TaskItems.ItemClass;
import com.Task.utils.Tasks.TaskItems.TaskItem;
import com.Task.utils.Tasks.TaskItems.successItem;
import com.Task.utils.Tasks.playerFile;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class createMenu {

    static final int Menu = 0xcc1001;
    public static final int Tasks = 0xcc1002;
    static final int Tasks_Menu = 0xcc1003;
    static final int Create = 0xcc1004;
    static final int again = 0xcc1005;

    public static RSTask task = RSTask.getTask();


    public static void sendMenu(Player player){
        playerFile playerFile = new playerFile(player.getName());
        FormWindowSimple simple =
                new FormWindowSimple
                        (task.getLag("title"),(RSTask.canOpen())?
                                (task.getLag("player-task-integral").replace("%c",playerFile.getCount()+"").replace("%f",RSTask.getTask().getFName()))
                                : "");
        int i = 1;
        Map map = ((Map)task.getConfig().get("自定义图片路径"));
        for (Object o:map.keySet()){
            if(o instanceof String){
                String s = " ";
                if(RSTask.canOpen()){
                    if(playerFile.canLock(i)){
                        if(playerFile.getCanInviteTasks(i).size() > 0){
                            s = (RSTask.getTask().getLag("task-message-can-receive").replace("%c",playerFile.getCanInviteTasks(i).size()+""));
                        }
                        if(playerFile.getInviteTasks().size() > 0){
                            s = (RSTask.getTask().getLag("task-message-lodding").replace("%c",playerFile.getInviteTasks().size()+""));
                        }
                        if(playerFile.getSuccessTasks().size() > 0){
                            s = (RSTask.getTask().getLag("task-message-success").replace("%c",playerFile.getSuccessTasks().size()+""));
                        }
                        if(playerFile.getCanInviteTasks(i).size() == 0 && playerFile.getInviteTasks().size() == 0 && playerFile.getSuccessTasks().size() == 0){
                            s = (RSTask.getTask().getLag("success-all"));
                        }
                    }else{
                        s = (RSTask.getTask().getLag("Lock").replace("%c",RSTask.starNeed(i)+"").replace("%f",RSTask.getTask().getFName()));
                    }
                }
                ElementButton button = new ElementButton(o+s);
                ElementButtonImageData imageData = new ElementButtonImageData("path",(String) map.get(o));
                button.addImage(imageData);
                simple.addButton(button);
            }
            i ++ ;
        }
        send(player,simple,Menu);
    }


    static void sendTaskList(Player player,int star){
        LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(star);

        playerFile playerFile = new playerFile(player.getName());
        FormWindowSimple simple = new FormWindowSimple(task.getLag("title"),
                RSTask.getTask().getLag("sendMenu-content"));

        for(TaskFile file:taskFiles) {
            if(file != null){
                String s = "";
//            if(playerFile.getTaskType(file) == )

                switch (playerFile.getTaskType(file)) {
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
        if(RSTask.getClickTask.containsKey(player)){
            TaskFile file = RSTask.getClickTask.get(player);
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

    public static void sendTaskMenu(Player player, TaskFile file){

        FormWindowSimple simple = new FormWindowSimple(task.getLag("title"),"");
        StringBuilder builder = new StringBuilder("");
        builder.append(RSTask.getTask().getLag("tast-title")).append("§r ").append(file.getTaskName()).append("\n");
        builder.append(RSTask.getTask().getLag("task-difficulty")).append("§r ").append(RSTask.getStar(file.getStar())).append("\n\n");
        builder.append(RSTask.getTask().getLag("task-introduce")).append("§r \n").append(file.getTaskMessage()).append("\n\n");
        builder.append(RSTask.getTask().getLag("task-speed")).append("§r\n");
        TaskFile file1 = TaskFile.getTask(file.getTaskName());
        TaskItem[] items = new TaskItem[]{};
        if(file1 != null){
            items = file1.getTaskItem();
        }
        int i = 0;
        if(items.length > 0){
            for(TaskItem item:items){
                int playerItem = playerFile.getPlayerFile(player.getName()).getTaskByName(item.getTaskName()).getTaskClass().getLoad(item);
                int taskCount = item.getEndCount();
                if(playerItem >= taskCount){
                    i++;
                }
                if(item.getTaskTag() != TaskItem.TaskItemTag.diyName){
                    builder.append(ItemIDSunName.getIDByName(item.getItemClass().getItem())).
                            append(">").append(" ").append(playerItem).append(" / ").append(taskCount).append("\n");

                }else{
                    builder.append(item.getTask()).
                            append(">").append(" ").append(playerItem).append(" / ").append(taskCount).append("\n");
                }
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
            ItemClass[] classes = successItem.getItem();
            CommandClass[] commandClasses = successItem.getCmd();
            if(classes != null && classes.length > 0){
                for(ItemClass itemClass:classes){
                    if(itemClass != null){
                        StringBuilder builder1 = new StringBuilder("");
                        builder1.append(ItemIDSunName.getIDByName(itemClass.getItem())).append("*").append(itemClass.getItem().getCount());
                        builders.add(builder1);
                    }
                }
            }
            if(commandClasses != null && commandClasses.length > 0){
                for(CommandClass commandClass:commandClasses){
                    if(commandClass != null){
                        StringBuilder builder1 = new StringBuilder("");
                        builder1.append(commandClass.getSendMessage());
                        builders.add(builder1);
                    }
                }
            }
            if(successItem.getMoney() > 0){
                builders.add(new StringBuilder(RSTask.getTask().getCoinName()).append(">").append(successItem.getMoney()));
            }
            if(RSTask.canOpen()){
                if(successItem.getCount() > 0){
                    builders.add(new StringBuilder(RSTask.getTask().getFName()).append(">").append(successItem.getCount()));
                }
            }
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

        if(playerFile.getPlayerFile(player.getName()).isSuccess(file) && i == items.length){
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
        custom.addElement(new ElementInput("请输入任务奖励(&区分多个元素)@item 为奖励物品 @tag奖励TagItem.json里的物品@money奖励金钱 @Cmd奖励指令","例如: 366:0:1@item 或 id@tag","366:0:1@item&100@money"));
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
