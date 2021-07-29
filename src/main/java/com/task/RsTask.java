package com.task;


import cn.nukkit.Player;
import cn.nukkit.Server;

import cn.nukkit.level.Level;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import com.task.commands.*;
import com.task.items.ItemLib;
import com.task.utils.task.AutoSaveFileTask;
import com.task.utils.task.ChunkPlayerInventoryBookTask;
import com.task.utils.task.ChunkTaskTask;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.TaskItem;
import com.task.utils.DataTool;
import com.task.utils.LoadMoney;
import com.task.utils.task.ListerEvents;
import com.task.utils.tasks.taskitems.ItemClass;
import com.task.utils.tasks.PlayerFile;
import com.task.form.ListenerMenu;
import org.jline.utils.Log;
import updata.AutoData;

import java.io.File;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author SmallasWater
 */
public class RsTask extends PluginBase{

    private static final String CONFIG_VERSION = "1.5.3";
    private static RsTask task;

    public static LinkedList<String> taskNames = new LinkedList<>();

    public static LinkedHashMap<Player,Integer> getClickStar = new LinkedHashMap<>();

    public LinkedHashMap<Player, TaskFile> getClickTask = new LinkedHashMap<>();

    public LinkedHashMap<String,TaskFile> tasks = new LinkedHashMap<>();



    public LinkedHashMap<String, PlayerFile> playerFiles = new LinkedHashMap<>();

    public static boolean loadEconomy = false;

    private LoadMoney loadMoney;

    public static boolean countChecking = true;

    private static boolean showCount = true;

    public static boolean showLoading = true;

    private static boolean showBack = true;

    private static boolean runC = true;

    public static boolean canGiveBook = true;

    public static boolean canSuccess = false;

    public int count = 10;

    private LinkedHashMap<String, Config> playerConfig = new LinkedHashMap<>();

    public LinkedHashMap<String, Config> taskConfig = new LinkedHashMap<>();

    private Config lag;

    private Config tagItem;

    public static ExecutorService executor = Executors.newCachedThreadPool();



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

        if(Server.getInstance().getPluginManager().getPlugin("AutoUpData") != null){
            if(AutoData.defaultUpData(this,getFile(),"SmallasWater","RSTask")){
                return;
            }
        }

        this.getServer().getPluginManager().registerEvents(new ListenerMenu(),this);
        this.getServer().getPluginManager().registerEvents(new ListerEvents(),this);

        loadItem();
        loadTask();
        registerCommand();
        if(countChecking) {
            this.getServer().getCommandMap().register("superTask", new RankCommand("c-rank"));
        }
        executor.execute(new ChunkTaskTask(this));
        executor.execute(new ChunkPlayerInventoryBookTask(this));
        if(getConfig().getBoolean("auto-save-task.open")){
            executor.execute(new AutoSaveFileTask(this));
        }
        Server.getInstance().getScheduler().scheduleDelayedTask(this, () -> {
            RsTask.getTask().getLogger().info("本插件为免费开源插件");
            RsTask.getTask().getLogger().info("GitHub: https://github.com/SmallasWater/RSTask");
        },20);

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

    public void loadItem(){
        this.saveResource("ItemLib.yml",false);
        ItemLib.ItemLibs = DataTool.loadItemLib(new Config(this.getDataFolder()+"/ItemLib.yml",Config.YAML));


    }

    private void registerCommand(){
        this.getServer().getCommandMap().register("cbook",new BookCommand("cbook"));
        if(canRunC()) {
            this.getServer().getCommandMap().register("c", new OpenTaskCommand("c"));
        }
        if(canRunCList()) {
            this.getServer().getCommandMap().register("c-list", new OpenTaskRunningCommand("c-list"));
        }
        if(countChecking) {
            this.getServer().getCommandMap().register("c-rank", new RankCommand("c-rank"));
        }
        this.getServer().getCommandMap().register("rtc", new RunTaskCommand("rtc"));
        this.getServer().getCommandMap().register("sh", new SaveItemCommand("sh"));
        this.getServer().getCommandMap().register("t-task", new TaskCommand("task"));
    }


    public static RsTask getTask() {
        return task;
    }

    private boolean canRunCList(){
        return getConfig().getBoolean("是否允许玩家执行c-list指令",true);
    }

    /** 判断编号是否存在*/
    public boolean canExistsNumber(String number){
        Config config = getTagItem();
        return (config.get(number) != null);
    }

    private Config getTagItem() {
        if(tagItem == null){
            tagItem = new Config(this.getDataFolder()+"/TagItem.json",Config.JSON);
        }
        return tagItem;
    }

    /** 根据编号获取ItemClass */
    public ItemClass getTagItemsConfig(String number){
        if(canExistsNumber(number)){
            Config config = getTagItem();
            return ItemClass.toItem(config.getString(number));
        }
        return null;

    }




    /** 判断是否存在*/
    public boolean canExisteItemClass(ItemClass itemClass){
        Config config = getTagItem();
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
        Config config = getTagItem();
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
        Config config = getTagItem();
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


    private String getPlayerFileName(String player){
        for(String i: defaultFirstName)
        {
            if(i.equals(player.substring(0,1).toLowerCase())){
                return "/Players/"+i+"/"+player+".yml";
            }
        }
        return "/Players/#/"+player+".yml";
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
        return RsTask.getTask().getConfig().getBoolean("enable-Economy");
    }


    /** 是否显示返回按钮 */
    public static boolean canBack(){
        return showBack;
    }




    private boolean canRunC(){
        return runC;
    }



    public void loadTask() {
        taskConfig = new LinkedHashMap<>();
        File taskFiles = new File(this.getDataFolder() + "/Tasks");
        if (!taskFiles.exists()) {
            if (!taskFiles.mkdirs()) {
                Log.error("创建Tasks文件夹失败");
            }
        }
        File fileE = new File(RsTask.getTask().getDataFolder() + "/Tasks");
        File[] files = fileE.listFiles();
        if (files != null) {
            Arrays.sort(files);
            for (File file1 : files) {
                if (file1.isFile()) {
                    String names = file1.getName().substring(0, file1.getName().lastIndexOf("."));
                    taskConfig.put(names, new Config(this.getDataFolder() + "/Tasks/" + names + ".yml", Config.YAML));
                }
            }
        }
        tasks = TaskFile.getTasks();
        for (String i : defaultFirstName) {
            File file = new File(this.getDataFolder() + "/Players/" + i);
            if (!file.exists()) {
                if (!file.mkdirs()) {
                    Log.error("玩家文件初始化失败");
                }
            }
        }
        playerConfig = new LinkedHashMap<>();
        for (String playerName : getPlayerNames()) {
            playerConfig.put(playerName, new Config(getDataFolder() + getPlayerFileName(playerName)));

        }
        chunkConfigVersion();
        if(!new File(this.getDataFolder()+"/language.properties").exists()){
            this.saveResource("language.properties",false);
        }
        lag = new Config(this.getDataFolder()+"/language.properties",Config.PROPERTIES);
        chunkLanguageVersion();
        if(canUseEconomyAPI()){
            getLogger().info("正在检查经济系统....");
            loadEconomy();
            if(loadMoney.getMoney() == -1){
                getLogger().info("未检测到经济核心");
            }else{
                loadEconomy = true;
            }
        }

        init();
    }
    private void chunkLanguageVersion(){
        String v1 = lag.get("version","1.0.0");
        int ver = DataTool.compareVersion(CONFIG_VERSION,v1);
        if(ver == 1 || ver == -1) {
            this.getLogger().info("检测到新版本 语言文件 正在进行更新...");
            File file = new File(this.getDataFolder() + "/language.properties");
            if (file.delete()) {
                this.saveResource("language.properties",false);
                lag = new Config(this.getDataFolder()+"/language.properties",Config.PROPERTIES);
                this.getLogger().info("配置文件更新完毕 当前语言文件版本: " + CONFIG_VERSION);
            } else {
                this.getLogger().warning("配置文件删除失败 请手动删除");
            }
        }

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
            }

        }

    }

    public int getGroupSize(){
        Map map = (Map) RsTask.getTask().getConfig().get("自定义图片路径");
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
