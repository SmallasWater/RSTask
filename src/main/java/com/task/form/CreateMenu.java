package com.task.form;


import cn.nukkit.Player;
import cn.nukkit.form.element.*;
import cn.nukkit.form.window.FormWindow;
import cn.nukkit.form.window.FormWindowCustom;
import cn.nukkit.form.window.FormWindowModal;
import cn.nukkit.form.window.FormWindowSimple;
import com.task.RsTask;
import com.task.utils.ItemIDSunName;
import com.task.utils.task.CollectItemTask;
import com.task.utils.DayTime;
import com.task.tasks.TaskFile;
import com.task.tasks.taskitems.PlayerTask;
import com.task.tasks.taskitems.PlayerTaskClass;
import com.task.tasks.taskitems.SuccessItem;
import com.task.tasks.taskitems.TaskItem;
import com.task.utils.DataTool;
import com.task.tasks.PlayerFile;

import java.util.*;

/**
 * @author SmallasWater
 */
public class CreateMenu {

    static final int MENU = 0xcc1001;
    static final int TASKS = 0xcc1002;
    static final int TASKS_MENU = 0xcc1003;
    static final int CREATE = 0xcc1004;
    static final int AGAIN = 0xcc1005;
    static final int INVITE = 0xcc1006;

    public static RsTask task = RsTask.getTask();


    /**
     * 给玩家发送任务主界面GUI
     * @param player 玩家
     * */
    public static void sendMenu(Player player){
        PlayerFile playerFiles = PlayerFile.getPlayerFile(player.getName());
        FormWindowSimple simple =
                new FormWindowSimple
                        (task.getLag("title"),(RsTask.canOpen())?
                                (task.getLag("player-task-integral").replace("%c",playerFiles.getCount()+"").replace("%f", RsTask.getTask().getFName()))
                                : "");
        int i = 0;
        Map map = ((Map)task.getConfig().get("自定义图片路径"));
        for (Object o:map.keySet()){
            Map map1 = (Map) map.get(o);
            String s = " ";
            if(RsTask.canOpen()){
                if(playerFiles.canLock(i)){
                    if(RsTask.getTask().canShowLodding()){
                        if(playerFiles.getCanInviteTasks(i).size() == 0 && playerFiles.getInviteTasks(i).size() == 0 && playerFiles.getSuccessTasks(i).size() == 0){
                            s = (RsTask.getTask().getLag("success-all"));
                        }else if(playerFiles.getSuccessTasks(i).size() > 0){
                            s = (RsTask.getTask().getLag("task-message-success").replace("%c",playerFiles.getSuccessTasks(i).size()+""));
                        }else if(playerFiles.getInviteTasks(i).size() > 0){
                            s = (RsTask.getTask().getLag("task-message-lodding").replace("%c",playerFiles.getInviteTasks(i).size()+""));
                        }else if(playerFiles.getCanInviteTasks(i).size() > 0){
                            s = (RsTask.getTask().getLag("task-message-can-receive").replace("%c",playerFiles.getCanInviteTasks(i).size()+""));
                        }
                    }
                }else{
                    s = (RsTask.getTask().getLag("Lock").replace("%c", DataTool.starNeed(i)+"").replace("%f", RsTask.getTask().getFName()));
                }
            }
            ElementButton button = new ElementButton(map1.get("名称")+s);
            ElementButtonImageData imageData = new ElementButtonImageData(map1.get("图片类型").toString().equals("网络")?"url":"path",(String) map1.get("图片路径"));
            button.addImage(imageData);
            simple.addButton(button);

            i ++ ;
        }
        send(player,simple, MENU);
    }


    /**
     * 给玩家发送任务列表GUI
     * @param player 玩家
     * @param group 分组
     * */
    public static void sendTaskList(Player player, int group){
        LinkedList<TaskFile> taskFiles = TaskFile.getDifficultyTasks(group);
        PlayerFile playerFiles = PlayerFile.getPlayerFile(player.getName());
        FormWindowSimple simple = new FormWindowSimple(DataTool.getGroupName(group),
                RsTask.getTask().getLag("sendMenu-content"));

        for(TaskFile file:taskFiles) {
            if(file != null){
                String s = "";
                switch (playerFiles.getTaskType(file)) {
                    case Running:
                        s = (RsTask.getTask().getLag("using"));
                        break;
                    case No_Invite:
                        if(!"null".equals(file.getLastTask())) {
                            if (!playerFiles.isSuccessed(file.getLastTask())) {
                                String last = file.getLastTask();
                                TaskFile file1 = file.getLastTaskFile();
                                if(file1 != null){
                                    last = file1.getName();
                                }
                                s = (RsTask.getTask().getLag("not-receive").replace("%s", last));
                                break;
                            }
                        }
                        s = RsTask.getTask().getLag("not-invite","§c[不可领取]");
                        break;
                    case Success:
                        s = (RsTask.getTask().getLag("success"));
                        break;
                    case can_Invite:
                        s = (RsTask.getTask().getLag("can-receive"));
                        break;
                    case isSuccess_canInvite:
                        s = (RsTask.getTask().getLag("enable-receive"));
                        break;
                    case isSuccess_noInvite:
                        s = (RsTask.getTask().getLag("cannot-receive"));
                        break;
                        default:
                            break;
                }
                ElementButton button = file.getButton().toButton();
                button.setText(file.getName()+ s);
                simple.addButton(button);
                if(simple.getButtons().size() == 0){
                    simple.setContent((taskFiles.size() == 0)?"\n\n\n\n\n\n    "+ RsTask.getTask().getLag("no-task"):"");
                }
            }
        }
        send(player,simple, TASKS);
    }

    static void sendAgain(Player player){
        if(RsTask.getTask().getClickTask.containsKey(player)){
            TaskFile file = RsTask.getTask().getClickTask.get(player);
            if(file != null){
                FormWindowModal simple = new FormWindowModal(task.getLag("title"),
                        RsTask.getTask().getLag("giveUpChose","§d§l您确定要放弃了 %s 任务吗?{换行}§c放弃后会丢失当前进度")
                                .replace("%s",file.getName()).replace("{换行}","\n"),"确定","取消");

                send(player,simple, AGAIN);
            }
        }else{
            player.sendMessage("§c请再尝试一次");
        }
    }

    /**
     * 将任务进度转换为显示在GUI的内容
     * @param player 玩家
     * @param items 任务进度
     *
     * @return 显示内容
     * */
    public static LinkedList<String> toTaskItemString(TaskItem[] items, Player player){
        LinkedList<String> builder = new LinkedList<>();
        for(TaskItem item:items){
            PlayerFile file2 = PlayerFile.getPlayerFile(player.getName());
            PlayerTask task = file2.getTaskByName(item.getTaskName());
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

    /**
     * 将任务进度转换为显示在GUI的内容
     * @param player 玩家
     * @param file 任务文件 {@link TaskFile}
     *
     * @return 显示内容
     * */
    public static StringBuilder getTitles(Player player,TaskFile file){
        StringBuilder builder = new StringBuilder();
        builder.append(RsTask.getTask().getLag("tast-title")).append("§r ").append(file.getName()).append("\n");
        builder.append(RsTask.getTask().getLag("task-difficulty")).append("§r ").append(DataTool.getStar(file.getStar())).append("\n\n");
        builder.append(RsTask.getTask().getLag("task-introduce")).append("§r \n").append(file.getTaskMessage()).append("\n\n");
        PlayerFile file1 = PlayerFile.getPlayerFile(player.getName());
        int timeOut = file1.getTimeOutDay(file.getTaskName());
        int time = file.getLoadDay();
        DayTime dayTime1 = DataTool.getTimeByDay(timeOut);
        builder.append(RsTask.getTask().getLag("time-out","§e§l到期时间:")).append("§r ").append(time <= 0?"§2无限制":dayTime1.getTime() > 0? "§a"+dayTime1.getTime()+DayTime.STRINGS[dayTime1.getType()]+" §c后失效":"§c已失效").append("\n\n");
        return builder;
    }

    /**
     * 给玩家发送排行榜GUI
     * @param player 玩家
     * */
    public static void sendRankMenu(Player player){
        FormWindowSimple simple = new FormWindowSimple("任务系统--排行榜","");
        StringBuilder builder = new StringBuilder();
        LinkedHashMap<String,Integer> map = new LinkedHashMap<>();
        PlayerFile file;
        for(String name: RsTask.getTask().getPlayerNames()){
            file = PlayerFile.getPlayerFile(name);
            map.put(name,file.getCount());
        }
        HashMap<String,Integer> list = DataTool.getPlayerRankingList(map);
        int in = 1;
        int i = 1;
        for(String uuid: list.keySet()){
            if (player.getName().equals(uuid)) {
                in = i;
            }
            if(i <= RsTask.getTask().getCount()){
                builder.append("§7No.§a").append(i).append(" §e>>§6").append(uuid).append("§e: ").append( list.get(uuid)).append("\n");
            }
            i++;
        }
        simple.setContent("任务积分排行榜: \n§b当前您的排名: §7No.§a"+in+"\n\n-------------------\n"+builder.toString());
        int lead = 0xcc1006;
        player.showFormWindow(simple, lead);

    }



    /**
     * 给玩家发送任务界面GUI
     * @param player 玩家
     * @param file 任务文件
     * */
    public static void sendTaskMenu(Player player, TaskFile file){

        FormWindowSimple simple = new FormWindowSimple(task.getLag("title"),"");
        StringBuilder builder = new StringBuilder();
        builder.append(getTitles(player,file));
        TaskFile file1 = TaskFile.getTask(file.getTaskName());
        builder.append(RsTask.getTask().getLag("task-speed")).append("§r\n");
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
            builder.append(RsTask.getTask().getLag("notTasks")).append("§r\n");
        }

        builder.append("\n\n");


        builder.append(RsTask.getTask().getLag("success-item")).append("§r\n");
        SuccessItem successItem = file.getFirstSuccessItem();
        if(!PlayerFile.getPlayerFile(player.getName()).isFirst(file)){
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
            builder.append(RsTask.getTask().getLag("nothave-SuccessItem"));
        }
        builder.append("\n");
        simple.setContent(builder.toString());

        if(PlayerFile.getPlayerFile(player.getName()).isSuccess(file) && linkedList.size() == items.length){
            simple.addButton(getSuccessButton());
        }else{
            simple.addButton(getCancelButton());
        }

        ElementButton giveUp = new ElementButton(RsTask.getTask().getLag("giveUpTask","§c放弃任务"));
        giveUp.addImage(new ElementButtonImageData("path","textures/ui/book_trash_default"));
        simple.addButton(giveUp);
        if(RsTask.canBack()){
            ElementButton button2 = new ElementButton(RsTask.getTask().getLag("back","返回"));
            ElementButtonImageData imageData2 = new ElementButtonImageData("path","textures/ui/refresh_light");
            button2.addImage(imageData2);
            simple.addButton(button2);
        }

        send(player,simple, TASKS_MENU);
    }

    /**
     * 给玩家发送创建任务GUI
     * @param player 玩家
     * */
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
        Map map1;
        for(int i = 0;i<map.size();i++){
             map1 = (Map) map.get(i+"");
             list1.add(map1.get("名称").toString());
        }
        custom.addElement(new ElementDropdown("请选择任务分组",list1));
        custom.addElement(new ElementInput("请输入任务难度(整数)","例如: 1","1"));
        custom.addElement(new ElementInput("请输入任务介绍","例如: 收集10个橡木","收集10个橡木"));
        custom.addElement(new ElementInput("请输入任务完成条件(&区分多个元素)(自定义任务请输 内容:数量)","例如: 17:0:10@item 或 id:10@tag(收集任务)","17:0:10@item"));
        custom.addElement(new ElementInput("请输入任务奖励(&区分多个元素)@item 为奖励物品 @tag奖励TagItem.json里的物品@money奖励金钱 @Cmd奖励指令(%p代表玩家)","例如: 366:0:1@item 或 id:1@tag","366:0:1@item&100@money"));
        send(player,custom, CREATE);
    }


    private static void send(Player player,FormWindow window,int id){
        player.showFormWindow(window,id);
    }

    private static ElementButton getCancelButton(){
        ElementButton button = new ElementButton(RsTask.getTask().getLag("unsubmission-task"));
        ElementButtonImageData imageData = new ElementButtonImageData("path","textures/ui/cancel");
        button.addImage(imageData);
        return button;
    }

    private static ElementButton getSuccessButton(){
        ElementButton button = new ElementButton(RsTask.getTask().getLag("submission-task"));
        ElementButtonImageData imageData = new ElementButtonImageData("path","textures/ui/confirm");
        button.addImage(imageData);
        return button;
    }





}
