package com.Task.utils.Scorebroad;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.Task;
import com.Task.RSTask;
import com.Task.utils.ItemIDSunName;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.TaskItems.TaskItem;
import com.Task.utils.Tasks.playerFile;
import gt.creeperface.nukkit.scoreboardapi.scoreboard.*;

import java.util.LinkedList;


public class ScoreTask{
    private Player player;


    public ScoreTask(Player player){
        this.player = player;
    }

    public void init() {
        if(!player.isOnline()) return;
        if(!RSTask.getTask().canUseScore()) return;
        FakeScoreboard fakeScoreboard = new FakeScoreboard();
        fakeScoreboard.objective = ScoreMessage.getMessage(player);
        Server.getInstance().getScheduler().scheduleDelayedTask(new Task() {
            @Override
            public void onRun(int i) {
                fakeScoreboard.addPlayer(player);
            }
        },20);

        Server.getInstance().getScheduler().scheduleRepeatingTask(new Task() {
            @Override
            public void onRun(int i) {
                fakeScoreboard.objective = ScoreMessage.getMessage(player);
                fakeScoreboard.update();
            }
        },20);
    }
}
class ScoreMessage{
    private static String[] color = new String[]{"§c","§6","§e","§a","§b","§9","§d"};
    private static int a = 0;

    static DisplayObjective getMessage(Player player){
        Objective objective = new Objective("task",new ObjectiveCriteria("dummy",true));
        DisplayObjective displayObjective = new DisplayObjective(objective,ObjectiveSortOrder.DESCENDING,ObjectiveDisplaySlot.SIDEBAR);
        objective.setDisplayName(color[a]+RSTask.getTask().getScoreTitle());
        a++;
        if(a >= color.length){
            a = 0;
        }
        TaskFile file = null;
        if(RSTask.getClickTask.containsKey(player) &&
                !playerFile.getPlayerFile(player.getName()).isSuccess(RSTask.getClickTask.get(player))
                &&playerFile.getPlayerFile(player.getName()).isRunning(RSTask.getClickTask.get(player).getTaskName())){
            file = RSTask.getClickTask.get(player);
        }else{
            playerFile file1 = new playerFile(player.getName());
            if(file1.getInviteTasks().size() > 0){
                file = file1.getInviteTasks().get(0).getTaskFile();
            }
        }
        TaskItem[] items = new TaskItem[]{};
        if(file != null){
            items = file.getTaskItem();
        }
        LinkedList<String> builder1 = new LinkedList<>();
        if(items.length != 0){
            for(TaskItem item:items){
                if(item.getTaskTag() == TaskItem.TaskItemTag.diyName){
                    int playerItem = playerFile.getPlayerFile(player.getName()).getTaskByName(item.getTaskName()).getTaskClass().getLoad(item);
                    builder1.add(item.getTask()+">"+playerItem+" / "+item.getEndCount());
                }else{
                    int playerItem = playerFile.getPlayerFile(player.getName()).getTaskByName(item.getTaskName()).getTaskClass().getLoad(item);
                    builder1.add(ItemIDSunName.getIDByName(item.getItemClass().getItem())+">"+playerItem+" / "+item.getEndCount());
                }

            }
        }
        int line = builder1.size() + 7;
        int l = 1;
        objective.setScore(l+1,"§d†§b任务: §r"+(file!=null?file.getTaskName():" §c请先领取任务"), line-1);
        objective.setScore(l+2,"§d†§b介绍: §r"+(file!=null?file.getTaskMessage():"     §e暂无"), line-2);
        objective.setScore(l+3,"§d†§b进度: §r"+(file!=null?"   §e↓↓↓↓":"     §e暂无"), line-3);
        int i = 4;
        if(builder1.size() > 0){
            for(String s:builder1){
                objective.setScore(l+i,"§d†§r      "+s, line-i);
                i++;
            }
        }
        return displayObjective;
    }
}