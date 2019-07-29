package com.Task;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import cn.nukkit.item.Item;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import com.Task.utils.Scorebroad.ScoreTask;
import com.Task.utils.Task.ListerEvents;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.TaskItems.ItemClass;
import com.Task.utils.Tasks.TaskItems.TaskItem;
import com.Task.utils.Tasks.playerFile;
import com.Task.utils.events.createTaskEvent;
import com.Task.utils.events.delTaskEvent;
import com.Task.utils.events.playerClickTaskEvent;
import com.Task.utils.form.ListenerMenu;
import com.Task.utils.form.createMenu;
import org.jline.utils.Log;

import java.io.File;

import java.util.*;


public class RSTask extends PluginBase{
    private static RSTask task;
    public static LinkedList<String> taskNames = new LinkedList<>();
    public static LinkedHashMap<Player,Integer> getClickStar = new LinkedHashMap<>();
    public static LinkedHashMap<Player,TaskFile> getClickTask = new LinkedHashMap<>();
    public static boolean loadSocket = false;
    public static boolean loadEconomyAPI = false;


    private String[] Default_First_Name = new String[]{
            "a","b","c","d","e","f","g","h",
            "i","j","k", "l","m","n","o","p","q",
            "r","s","t","u","v","w","x","y","z","0","1","2",
            "3","4","5","6","7","8","9","#"
    };
    @Override
    public void onEnable() {
        task = this;
        this.getLogger().info("[RSTask] 启动任务系统插件");
        File taskFiles = new File(this.getDataFolder()+"/Tasks");
        if(!taskFiles.exists())
            if(!taskFiles.mkdirs())
                Log.error("创建Tasks文件夹失败");
        for(String i: Default_First_Name){
            File file = new File(this.getDataFolder()+"/Players/"+i);
            if(!file.exists()){
                if(!file.mkdirs())
                    Log.error("玩家文件初始化失败");
            }
        }
        if(!new File(this.getDataFolder()+"/config").exists()){
            this.saveDefaultConfig();
            this.reloadConfig();
        }
        if(!new File(this.getDataFolder()+"/language.properties").exists()){
            this.saveResource("language.properties",false);
        }
        this.getServer().getPluginManager().registerEvents(new ListenerMenu(),this);
        this.getServer().getPluginManager().registerEvents(new ListerEvents(),this);
        if(canUseEconomyAPI()){
            if(Server.getInstance().getPluginManager().getPlugin("EconomyAPI") != null){
                loadEconomyAPI = true;
            }else{
                Server.getInstance().getLogger().warning("未检测到EconomyAPI");
            }
        }
        if(canUseScore()){
            for(int i = 1;i <= 5;i++){
                if(Server.getInstance().getPluginManager().getPlugin("ScoreboardAPI") != null){
                    loadSocket = true;
                    break;
                }else{
                    Server.getInstance().getLogger().warning("未检测到ScoreboardAPI");
                    Server.getInstance().getLogger().warning("尝试"+i+"次重载");
                    Server.getInstance().getPluginManager().loadPlugin(this.getServer().getFilePath()+"/plugins/ScoreboardAPI.jar");
                }
            }
            if(Server.getInstance().getPluginManager().getPlugin("ScoreboardAPI") == null){
                Server.getInstance().getLogger().warning("ScoreboardAPI加载失败");
            }

        }
    }

    public static RSTask getTask() {
        return task;
    }

    @Override
    public Config getConfig() {
        this.reloadConfig();
        return super.getConfig();
    }

    /** 判断编号是否存在*/
    public boolean canExistsNumber(String number){
        Config config = new Config(this.getDataFolder()+"/TagItem.json",Config.JSON);
        return (config.get(number) != null);
    }


    /** 根据编号获取ItemClass */
    public ItemClass getTagItemsConfig(String number){
        if(canExistsNumber(number)){
            Config config = new Config(this.getDataFolder()+"/TagItem.json",Config.JSON);
            return ItemClass.toItem(config.getString(number));
        }
        return null;

    }


    /** 判断是否存在*/
    public boolean canExisteItemClass(ItemClass itemClass){
        Config config = new Config(this.getDataFolder()+"/TagItem.json",Config.JSON);
        LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>) config.getAll();
        for(String string:map.keySet()){
            String c = (String) map.get(string);
            if(ItemClass.toItem(c).equals(itemClass))
                return true;
        }
        return false;
    }

    public String saveTagItemsConfig(ItemClass itemClass){
        int id = new Random().nextInt(100000)+100;
        int s;
        Config config = new Config(this.getDataFolder()+"/TagItem.json",Config.JSON);
        LinkedHashMap<String,Object> map = (LinkedHashMap<String, Object>) config.getAll();
        for(String string:map.keySet()){
            String c = (String) map.get(string);
            if(c.equals(itemClass.toString()))
                return string;
        }
        if(config.get(id+"") == null){
            s = id;
            config.set(s+"",itemClass.toString());
        }else{
            for(;;){
                id = new Random().nextInt(100000)+100;
                if(config.get(id+"") == null){
                    s = id;
                    config.set(s+"",itemClass.toString());
                    break;
                }
            }
        }
        config.save();
        return s+"";
    }


    /** 获取全部玩家*/
    public LinkedList<String> getPlayerNames(){
        LinkedList<String> linkedList = new LinkedList<>();
        LinkedList<String> playerNames = new LinkedList<>();
        File dir = new File(this.getDataFolder()+"/Players");
        if(dir.exists()){
            List<String> list = getAllFiles(dir,linkedList);
            for(String names:list){
                File file = new File(names);
                int dot = file.getName().lastIndexOf('.');
                if ((dot >-1) && (dot < (file.getName().length()))) {
                    playerNames.add(file.getName().substring(0, dot));
                }

            }
        }
        return playerNames;
    }

    private static List<String> getAllFiles(File dir, List<String> filelist){
        File[] fs = dir.listFiles();
        if(fs != null){
            for (File f : fs) {
                if (f.getAbsolutePath().matches(".*\\.yml$")) {
                    filelist.add(f.getAbsolutePath());
                }
                if (f.isDirectory()) {
                    try {
                        getAllFiles(f, filelist);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return filelist;
    }


    /** 获取保存NbtItem的编号 */
    private void saveTagItemsConfig(ItemClass itemClass, int tag){
        Config config = new Config(this.getDataFolder()+"/TagItem.json",Config.JSON);
        config.set(tag+"",itemClass.toString());
        config.save();


    }

    public String getLag(String value){
        Config config = new Config(this.getDataFolder()+"/language.properties",Config.PROPERTIES);
        return config.getString(value);
    }


    public String getLag(String value,String defaultString){
        Config config = new Config(this.getDataFolder()+"/language.properties",Config.PROPERTIES);
        return config.getString(value,defaultString);
    }

    /** 获取金币名称*/
    public String getCoinName(){
        return getConfig().getString("金币名称","§e金币§r");
    }

    /** 获取积分名称*/
    public String getFName(){
        return getConfig().getString("积分名称","§b积分§r");
    }



    public Config getTaskConfig(String taskName){
        if(TaskFile.isFileTask(taskName)){
            return new Config(this.getDataFolder()+"/Tasks/"+taskName+".yml",Config.YAML);
        }
        return null;
    }


    public Config getPlayerConfig(String playerName){
        if(!getPlayerFile(playerName).exists()){
            saveResource("player",getPlayerFileName(playerName),false);
        }
        return new Config(this.getDataFolder()+getPlayerFileName(playerName));
    }

    public File getPlayerFile(String playerName){
        return new File(this.getDataFolder()+getPlayerFileName(playerName));
    }


    private String getPlayerFileName(String player){
        for(String i: Default_First_Name)
        {
            if(i.equals(player.substring(0,1).toLowerCase())){
                return "/Players/"+i+"/"+player+".yml";
            }
        }
        return "/Players/#/"+player+".yml";
    }

    public boolean canUseScore() {
        return getConfig().getBoolean("是否使用计分板",false);
    }


    /**
     * 获取任务小数进度 百分比
     */
    public double getTaskLoading(String taskName,String player){
        playerFile playerFile = new playerFile(player);
        TaskFile file = TaskFile.getTask(taskName);
        if(file != null){
            if(playerFile.issetTask(file.getTaskName())){
                double count = playerFile.getTaskItems(file.getTaskName()).length;
                double math = 0.0D;
                for(TaskItem item:playerFile.getTaskItems(file.getTaskName())){
                    if(item != null){
                        double fileCount = file.getCountByTaskItem(item);
                        double playerCount = item.getEndCount();
                        if(playerCount > 0){
                            if(playerCount > fileCount){
                                playerCount = fileCount;
                            }
                            double con =  playerCount / fileCount;
                            if(con != 0)
                                math += ((con /  count) * 100);
                        }
                    }
                }
                return math;
            }
        }

        return 0.0D;
    }
    /** 是否开启积分限制 */
    public static boolean canOpen(){
        return RSTask.getTask().getConfig().getBoolean("是否开启积分验证");
    }

    public static boolean canUseEconomyAPI(){
            return RSTask.getTask().getConfig().getBoolean("enable-EconomyAPi");
    }


    /** 是否显示返回按钮 */
    public static boolean canBack(){
        return RSTask.getTask().getConfig().getBoolean("是否增加任务界面返回按钮");
    }

    /**获取难度需要积分 */
    public static int starNeed(int star){
        int add = RSTask.getTask().getConfig().getInt("任务等级增幅");
        return ((star * add) - 100);
    }

    public static void sendMessage(Player player,String message){
        switch (RSTask.getTask().getConfig().getString("底部显示类型")){
            case "tip":
                player.sendTip(message);
                break;
            case "popup":
                player.sendPopup(message);
                break;
            default:
                player.sendActionBar(message);
                break;
        }
    }


    /** 将难度转换为星星*/
    public static String getStar(int star){
        StringBuilder builder = new StringBuilder("");
        for(int i = 0;i < star;i++){
            builder.append(" ★ ");
        }
        return builder.toString();
    }

    /** 获取计分板标题*/
    public String getScoreTitle(){
        Map map = (Map) getConfig().get("计分板");
        return (String) map.get("标题");
    }

    /** 创建任务 */
    public void createTask(TaskFile file){
        if(!TaskFile.isFileTask(file.getTaskName())){
            file.toSaveConfig();
            createTaskEvent event1 = new createTaskEvent(file);
            Server.getInstance().getPluginManager().callEvent(event1);
        }
    }


    public boolean canRunC(){
        return getConfig().getBoolean("是否允许玩家执行c指令",true);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(command.getName().equals("c")) {
            if(sender instanceof Player){
                if(!canRunC() && !sender.isOp()){
                    return false;
                }
                if(args.length < 1){
                    createMenu.sendMenu((Player) sender);
                }else{
                    String taskName = args[0];
                    TaskFile file = TaskFile.getTask(taskName);
                    playerClickTaskEvent event = new playerClickTaskEvent(file,(Player) sender);
                    Server.getInstance().getPluginManager().callEvent(event);
                }

            }
        }
        if(command.getName().equals("sh")){
            if(args.length > 0){
                if(args[0].equals("help")){
                    sender.sendMessage("§c=======================");
                    sender.sendMessage("§e/sh <编号(可不填)> <数量(可不填)>");
                    sender.sendMessage("§c=======================");
                    return true;
                }
            }
            if(sender instanceof Player){
                Item item = ((Player) sender).getInventory().getItemInHand();
                if(item.getId() == 0){
                    sender.sendMessage("§c无法添加空气");
                    return true;
                }
                ItemClass itemClass = new ItemClass(item);
                if(args.length < 1){
                    String n =  saveTagItemsConfig(itemClass);
                    sender.sendMessage("§e成功添加"+n+"至TagItem.json");
                }else if(args.length < 2){
                    saveTagItemsConfig(itemClass,Integer.parseInt(args[0]));
                    sender.sendMessage("§e成功添加"+args[0]+"至TagItem.json");
                }else{
                    int count = Integer.parseInt(args[1]);
                    itemClass.getItem().setCount(count);
                    saveTagItemsConfig(itemClass,Integer.parseInt(args[0]));
                    sender.sendMessage("§e成功添加"+args[0]+"至 数量设置为 "+count+" TagItem.json");
                }
            }
        }
        if(command.getName().equals("ic")){
            if(sender instanceof Player){
                createMenu.sendCreateTaskMenu((Player) sender);
            }else{
                sender.sendMessage("控制台无法执行此指令");
            }
        }
        if(command.getName().equals("del-task")){
            if(args.length < 1){
                sender.sendMessage("用法:/del-task <任务名>");
                return false;
            }
            String taskName = args[0];

            if(TaskFile.isFileTask(taskName)){
                TaskFile file = TaskFile.getTask(taskName);
                if(file != null){
                    delTaskEvent event = new delTaskEvent(file);
                    Server.getInstance().getPluginManager().callEvent(event);
                    if(!file.close()){
                        sender.sendMessage("任务删除失败");
                    }else{
                        sender.sendMessage("任务删除成功");
                    }
                }
            }else{
                sender.sendMessage("不存在"+taskName+"任务");
                return false;
            }
        }


        return true;
    }
}
