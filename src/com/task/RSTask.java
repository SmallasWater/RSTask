package com.task;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;

import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.task.commands.*;
import com.task.utils.DataTool;
import com.task.utils.LoadMoney;
import com.task.utils.task.ChunkPlayerInventoryBookTask;
import com.task.utils.task.ChunkTaskTask;
import com.task.utils.task.CollectItemTask;
import com.task.utils.task.ListerEvents;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.ItemClass;
import com.task.utils.tasks.taskitems.TaskBook;
import com.task.utils.tasks.taskitems.TaskItem;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.events.CreateTaskEvent;
import com.task.utils.events.DelTaskEvent;
import com.task.utils.events.PlayerClickTaskEvent;
import com.task.utils.events.PlayerOpenBookEvent;
import com.task.utils.form.ListenerMenu;
import com.task.utils.form.CreateMenu;
import de.theamychan.scoreboard.network.Scoreboard;
import org.jline.utils.Log;

import java.io.File;

import java.util.*;


/**
 * @author SmallasWater
 */
public class RSTask extends PluginBase{

    public static final String CONFIG_VERSION = "1.5.0";
    private static RSTask task;

    public static LinkedList<String> taskNames = new LinkedList<>();

    public static LinkedHashMap<Player,Integer> getClickStar = new LinkedHashMap<>();

    public LinkedHashMap<Player,TaskFile> getClickTask = new LinkedHashMap<>();

    public LinkedHashMap<String,TaskFile> tasks = new LinkedHashMap<>();

    public LinkedHashMap<String, PlayerFile> playerFiles = new LinkedHashMap<>();

    public static boolean loadSocket = false;

    public static boolean loadEconomy = false;

    public LoadMoney loadMoney;

    public static boolean countChecking = true;

    public static boolean showCount = true;

    public static boolean showLoading = true;

    public static boolean showBack = true;

    public static boolean runC = true;

    public static boolean canGiveBook = true;

    public static boolean canSuccess = false;

    public int count = 10;

//    public LinkedList<Player> bookOpen = new LinkedList<>();

    public LinkedHashMap<String,Config> playerConfig = new LinkedHashMap<>();

    public LinkedHashMap<String,Config> taskConfig = new LinkedHashMap<>();

    private Config lag;

    public LinkedHashMap<Player, Scoreboard> scores = new LinkedHashMap<>();


    private String[] defaultFirstName = new String[]{
            "a","b","c","d","e","f","g","h",
            "i","j","k", "l","m","n","o","p","q",
            "r","s","t","u","v","w","x","y","z","0","1","2",
            "3","4","5","6","7","8","9","#"
    };

    @Override
    public void onEnable() {
        task = this;
        this.getLogger().info("[RSTask] 启动任务系统插件");
        this.getServer().getPluginManager().registerEvents(new ListenerMenu(),this);
        this.getServer().getPluginManager().registerEvents(new ListerEvents(),this);
        loadTask();
        registerCommand();
        if(countChecking) {
            this.getServer().getCommandMap().register("superTask", new RankCommand("c-rank"));
        }
        this.getServer().getScheduler().scheduleRepeatingTask(new ChunkTaskTask(),20);
        this.getServer().getScheduler().scheduleRepeatingTask(new ChunkPlayerInventoryBookTask(),20);


    }



    private void init(){

        count = getConfig().getInt("排行榜显示玩家数量",10);

        countChecking = getConfig().getBoolean("是否开启积分验证");

        showCount = getConfig().getBoolean("主页面是否显示数量",true);

        showLoading = getConfig().getBoolean("是否在底部显示任务进度");

        showBack = getConfig().getBoolean("是否增加任务界面返回按钮");

        runC = getConfig().getBoolean("是否允许玩家执行c指令",true);

        canGiveBook = getConfig().getBoolean("领取任务是否给予任务书",true);

        canSuccess = getConfig().getBoolean("完成任务是否直接领取奖励",true);

        playerFiles = new LinkedHashMap<>();
        for(String playerName:getPlayerNames()){
            playerFiles.put(playerName, PlayerFile.getPlayerFile(playerName));
        }
    }

    private void registerCommand(){
        this.getServer().getCommandMap().register("cbook",new BookCommand("cbook"));
        if(canRunC()) {
            this.getServer().getCommandMap().register("c", new OpenTaskCommand("c"));
        }
        if(countChecking) {
            this.getServer().getCommandMap().register("c-rank", new RankCommand("c-rank"));
        }
        this.getServer().getCommandMap().register("rtc", new RunTaskCommand("rtc"));
        this.getServer().getCommandMap().register("sh", new SaveItemCommand("sh"));
        this.getServer().getCommandMap().register("t-task", new TaskCommand("task"));
    }


    public static RSTask getTask() {
        return task;
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
            ItemClass itemClass1 = ItemClass.toItem(c);
            if(itemClass1 != null){
                return itemClass1.equals(itemClass);
            }
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
            if(c.equals(itemClass.toString())) {
                return string;
            }
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

    public int getCount() {
        return count;
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
    public void saveTagItemsConfig(ItemClass itemClass, String tag){
        Config config = new Config(this.getDataFolder()+"/TagItem.json",Config.JSON);
        config.set(tag,itemClass.toString());
        config.save();


    }

    public String getLag(String value){
        return lag.getString(value);
    }


    public String getLag(String value,String defaultString){
        return lag.getString(value,defaultString);
    }

    /** 获取金币名称*/
    public String getCoinName(){
        return getConfig().getString("金币名称","§e金币§r");
    }

    /** 获取积分名称*/
    public String getFName(){
        return getConfig().getString("积分名称","§b积分§r");
    }

    /** 获取积分名称*/
    public boolean canShowLodding(){
        return showCount;
    }



    /** 判断是否开启世界独立任务
     * @deprecated 无
     * */

    public boolean isWorldAloneTask(){
        return getConfig().getBoolean("是否开启世界独立任务",false);
    }

    /** 如果开启，则初始化文件夹
     * @deprecated */
    public void initWorlds(){
        for(Level level:Server.getInstance().getLevels().values()){
            if(!new File(this.getDataFolder()+"/Worlds").exists()){
                if(new File(this.getDataFolder()+"/Worlds").mkdir()){
                    this.getLogger().info("Worlds文件夹创建成功");
                }else{
                    this.getLogger().info("Worlds文件夹创建失败");
                }
            }
            if(!new File(this.getDataFolder()+"/Worlds/"+level.getFolderName()).exists()){
                if(new File(this.getDataFolder()+"/Worlds/"+level.getFolderName()).mkdir()){
                    this.getLogger().info("Worlds/"+level.getFolderName()+"文件夹创建成功");
                }else{
                    this.getLogger().info("Worlds/"+level.getFolderName()+"文件夹创建失败");
                }
            }
            for(String i: defaultFirstName){
                File file = new File(this.getDataFolder()+"/Worlds/"+level.getFolderName()+"/Players/"+i);
                if(!file.exists()){
                    if(!file.mkdirs()) {
                        Log.error("玩家文件初始化失败");
                    }
                }
            }
            this.getLogger().info("Worlds/"+level.getFolderName()+"/Players/文件夹创建成功");
        }
    }


    public Config getTaskConfig(String taskName){
        if(TaskFile.isFileTask(taskName)){
            if(taskConfig.containsKey(taskName)) {
                return taskConfig.get(taskName);
            }
            return new Config(this.getDataFolder()+"/Tasks/"+taskName+".yml",Config.YAML);
        }
        return null;
    }


    public Config getPlayerConfig(String playerName){
        if(!getPlayerFile(playerName).exists()){
            saveResource("player",getPlayerFileName(playerName),false);
        }
        if(!playerConfig.containsKey(playerName)){
            playerConfig.put(playerName,new Config(this.getDataFolder()+getPlayerFileName(playerName)));
        }
        return playerConfig.get(playerName);
    }

    public File getPlayerFile(String playerName){
        return new File(this.getDataFolder()+getPlayerFileName(playerName));
    }


    public String getPlayerFileName(String player){
        for(String i: defaultFirstName)
        {
            if(i.equals(player.substring(0,1).toLowerCase())){
                return "/Players/"+i+"/"+player+".yml";
            }
        }
        return "/Players/#/"+player+".yml";
    }

    public boolean canUseScore() {
        return getConfig().getBoolean("是否使用计分板",true);
    }


    /**
     * 获取任务小数进度 百分比
     */
    public double getTaskLoading(String taskName,String player){
        PlayerFile playerFiles = PlayerFile.getPlayerFile(player);
        TaskFile file = TaskFile.getTask(taskName);
        if(file != null){
            if(playerFiles.issetTask(file.getTaskName())){
                double count = playerFiles.getTaskItems(file.getTaskName()).length;
                double math = 0.0D;
                for(TaskItem item:playerFiles.getTaskItems(file.getTaskName())){
                    if(item != null){
                        double fileCount = file.getCountByTaskItem(item);
                        double playerCount = item.getEndCount();
                        if(playerCount > 0){
                            if(playerCount > fileCount){
                                playerCount = fileCount;
                            }
                            double con =  playerCount / fileCount;
                            if(con != 0) {
                                math += ((con /  count) * 100);
                            }
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
        return countChecking;
    }

    private static boolean canUseEconomyAPI(){
        return RSTask.getTask().getConfig().getBoolean("enable-Economy");
    }


    /** 是否显示返回按钮 */
    public static boolean canBack(){
        return showBack;
    }

    /**获取难度需要积分 */
    public static int starNeed(int star){
        Map map = (Map) RSTask.getTask().getConfig().get("自定义图片路径");
        if(map.containsKey(star+"")){
            Map map1 = (Map) map.get(star+"");
            return Integer.parseInt(map1.get("解锁积分").toString());
        }else{
            return 0;
        }
//        int add = RSTask.getTask().getConfig().getInt("任务等级增幅");
//        return ((star * add));
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

    /** 获取分组名称*/
    public String getGroupName(int group){
        Map map = (Map) RSTask.getTask().getConfig().get("自定义图片路径");
        if(map.containsKey(group+"")){
            return RSTask.getTask().getConfig().get("自定义图片路径."+group+"."+"名称").toString();
        }
        return RSTask.getTask().lag.getString("title");
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
            CreateTaskEvent event1 = new CreateTaskEvent(file);
            Server.getInstance().getPluginManager().callEvent(event1);
        }
    }

    public boolean existsGroup(int group){
        Map map = (Map) RSTask.getTask().getConfig().get("自定义图片路径");
        return map.containsKey(group + "");
    }


    public boolean canRunC(){
        return runC;
    }



    public void loadTask() {
        taskConfig = new LinkedHashMap<>();
        File taskFiles = new File(this.getDataFolder()+"/Tasks");
        if(!taskFiles.exists()) {
            if(!taskFiles.mkdirs()) {
                Log.error("创建Tasks文件夹失败");
            }
        }
        File fileE = new File(RSTask.getTask().getDataFolder()+"/Tasks");
        File[] files = fileE.listFiles();
        if(files != null){
            Arrays.sort(files);
            for(File file1:files){
                if(file1.isFile()){
                    String names = file1.getName().substring(0,file1.getName().lastIndexOf("."));
                    taskConfig.put(names,new Config(this.getDataFolder()+"/Tasks/"+names+".yml",Config.YAML));
                }
            }
        }
        tasks = TaskFile.getTasks();
        for(String i: defaultFirstName){
            File file = new File(this.getDataFolder()+"/Players/"+i);
            if(!file.exists()){
                if(!file.mkdirs()) {
                    Log.error("玩家文件初始化失败");
                }
            }
        }
        playerConfig = new LinkedHashMap<>();
        for(String playerName:getPlayerNames()){
            playerConfig.put(playerName,new Config(getDataFolder()+getPlayerFileName(playerName)));

        }
        chunkConfigVersion();
        if(!new File(this.getDataFolder()+"/language.properties").exists()){
            this.saveResource("language.properties",false);
        }
        lag = new Config(this.getDataFolder()+"/language.properties",Config.PROPERTIES);
        if(canUseEconomyAPI()){
            getLogger().info("正在检查经济系统....");
            loadEconomy();
            if(loadMoney.getMoney() == -1){
                getLogger().info("未检测到经济核心");
            }else{
                loadEconomy = true;
            }
        }
        if(canUseScore()){
            try{
                Class.forName("de.theamychan.scoreboard.ScoreboardPlugin");
                loadSocket = true;

            }catch (ClassNotFoundException e) {
                Server.getInstance().getLogger().warning("未检测到ScoreboardAPI 前置");
                loadSocket = false;
            }
        }
        init();
    }

    private void chunkConfigVersion(){
        if(!new File(this.getDataFolder()+"/config.yml").exists()){
            this.saveDefaultConfig();
            this.reloadConfig();
        }else{
            String v1 = getConfig().get("version","1.0.0");
            int ver = DataTool.compareVersion(CONFIG_VERSION,v1);
            if(ver == 1 || ver == -1){
                this.getLogger().info("检测到新版本 配置文件 正在进行更新...");
                File file = new File(this.getDataFolder()+"/config.yml");
                if(file.delete()){
                    this.saveDefaultConfig();
                    this.reloadConfig();
                    this.getLogger().info("配置文件更新完毕 当前配置版本: "+CONFIG_VERSION);
                }else{
                    this.getLogger().warning("配置文件删除失败 请手动删除");
                }
                File file1 = new File(this.getDataFolder()+"/language.properties");
                if(!file1.delete()){
                    this.getLogger().warning("language.properties 删除失败 请手动删除");
                }
            }

        }
    }

    public int getGroupSize(){
        Map map = (Map) RSTask.getTask().getConfig().get("自定义图片路径");
        return map.size();
    }

    public LinkedHashMap<String, TaskFile> getTasks() {
        return tasks;
    }

    public LoadMoney getLoadMoney() {
        return loadMoney;
    }

    private void loadEconomy(){
        loadMoney = new LoadMoney();
        String economy = getConfig().getString("使用经济核心","default");
        if(loadMoney.getMoney() != -1) {
            if ("default".equalsIgnoreCase(economy)) {
                getLogger().info("任务系统 经济核心已启用:" + TextFormat.GREEN + " 自动");
            }
            if ("money".equalsIgnoreCase(economy)) {
                loadMoney.setMoney(LoadMoney.MONEY);
                getLogger().info("任务系统 经济核心已启用:" + TextFormat.GREEN + " Money");
            }
            if ("playerpoint".equalsIgnoreCase(economy)) {
                loadMoney.setMoney(LoadMoney.PLAYER_POINT);
                getLogger().info("任务系统 经济核心已启用:" + TextFormat.GREEN + " PlayerPoints");
            } else {
                loadMoney.setMoney(LoadMoney.ECONOMY_API);
                getLogger().info("任务系统 经济核心已启用:" + TextFormat.GREEN + " EconomyAPI");
            }
        }

    }

    @Override
    public void onDisable() {
        for(TaskFile file:tasks.values()){
            file.toSaveConfig();
        }
        for(PlayerFile player:playerFiles.values()){
            player.toSave();
        }

    }
}
