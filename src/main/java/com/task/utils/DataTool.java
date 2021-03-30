package com.task.utils;


import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.item.EntityFirework;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemFirework;
import cn.nukkit.level.Level;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.DyeColor;
import com.google.gson.reflect.TypeToken;
import com.task.RsTask;
import com.task.events.CreateTaskEvent;
import com.task.items.ItemLib;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.taskitems.ItemClass;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author SmallasWater
 */
public class DataTool {

    /** 将 2019/6/9/24/11 格式的string转换为 Date
     * @param format 时间格式
     *
     * @return 时间类*/
    public static Date getDate(String format){
        SimpleDateFormat lsdStrFormat = new SimpleDateFormat("yyyy/MM/dd/HH/ss");
        // 兼容旧版本...
        try {
            return lsdStrFormat.parse(format);
        }catch (ParseException e){
            lsdStrFormat = new SimpleDateFormat("yyyy/MM/dd");
            try{
                return lsdStrFormat.parse(format);
            } catch (ParseException ex) {
                return null;
            }
        }
    }

    /** 获取所有分组字符串
     *
     * @return 分组字符串*/
    public static String[] getGropAllName(){
        LinkedList<String> taskNames = new LinkedList<>();
        for(int i = 0; i < RsTask.getTask().getGroupSize(); i++){
            taskNames.add(i+"");
        }
        return taskNames.toArray(new String[0]);
    }

    /** 获取所有任务任务名
     *
     * @return 任务名数组
     * */
    public static String[] getTaskAllNames(){
        LinkedList<String> taskNames = new LinkedList<>(RsTask.getTask().getTasks().keySet());
        return taskNames.toArray(new String[0]);
    }


    /** 获取相差分钟
     * @param oldData 时间1
     *
     * @return 分钟数*/

    public static int getTime(Date oldData) {
        long temp = System.currentTimeMillis() - oldData.getTime();
        return (int) temp / 1000 / 60;
    }

    public static DayTime getTimeByDay(int min){
        if(min > 0){
            int hour = min / 60;
            int day;
            if(hour >= 24){
                day = hour / 24;
                return new DayTime(DayTime.DAY,day);
            }else if(hour > 0){
                return new DayTime(DayTime.HOUR,hour);
            }else{
                return new DayTime(DayTime.MIN,min);
            }
        }
        return new DayTime(DayTime.MIN,min);
    }




    /** 将Date 转换 String*/
    public static String toDateString(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd/HH/ss");
        return sdf.format(date);
    }


    /** Data 转为 Object*/
    public static Object[] toArrayByData(String data){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        return gson.fromJson(data,(new TypeToken<Object[]>() {
        }).getType());
    }

    /** 放烟花*/
    public static void spawnFirework(Player player) {

        Level level = player.getLevel();
        ItemFirework item = new ItemFirework();
        CompoundTag tag = new CompoundTag();
        Random random = new Random();
        CompoundTag ex = new CompoundTag();
        ex.putByteArray("FireworkColor",new byte[]{
                (byte) DyeColor.values()[random.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].getDyeData()
        });
        ex.putByteArray("FireworkFade",new byte[0]);
        ex.putBoolean("FireworkFlicker",random.nextBoolean());
        ex.putBoolean("FireworkTrail",random.nextBoolean());
        ex.putByte("FireworkType",ItemFirework.FireworkExplosion.ExplosionType.values()
                [random.nextInt(ItemFirework.FireworkExplosion.ExplosionType.values().length)].ordinal());
        tag.putCompound("Fireworks",(new CompoundTag("Fireworks")).putList(new ListTag<CompoundTag>("Explosions").add(ex)).putByte("Flight",1));
        item.setNamedTag(tag);
        CompoundTag nbt = new CompoundTag();
        nbt.putList(new ListTag<DoubleTag>("Pos")
                .add(new DoubleTag("",player.x+0.5D))
                .add(new DoubleTag("",player.y+0.5D))
                .add(new DoubleTag("",player.z+0.5D))
        );
        nbt.putList(new ListTag<DoubleTag>("Motion")
                .add(new DoubleTag("",0.0D))
                .add(new DoubleTag("",0.0D))
                .add(new DoubleTag("",0.0D))
        );
        nbt.putList(new ListTag<FloatTag>("Rotation")
                .add(new FloatTag("",0.0F))
                .add(new FloatTag("",0.0F))

        );
        nbt.putCompound("FireworkItem", NBTIO.putItemHelper(item));
        EntityFirework entity = new EntityFirework(level.getChunk((int)player.x >> 4, (int)player.z >> 4), nbt);
        entity.spawnToAll();
    }


    public static int compareVersion(String v1, String v2) {
        if (v1.equals(v2)) {
            return 0;
        }
        String[] version1Array = v1.split("[._]");
        String[] version2Array = v2.split("[._]");
        int index = 0;
        int minLen = Math.min(version1Array.length, version2Array.length);
        long diff = 0;

        while (index < minLen
                && (diff = Long.parseLong(version1Array[index])
                - Long.parseLong(version2Array[index])) == 0) {
            index++;
        }
        if (diff == 0) {
            for (int i = index; i < version1Array.length; i++) {
                if (Long.parseLong(version1Array[i]) > 0) {
                    return 1;
                }
            }

            for (int i = index; i < version2Array.length; i++) {
                if (Long.parseLong(version2Array[i]) > 0) {
                    return -1;
                }
            }
            return 0;
        } else {
            return diff > 0 ? 1 : -1;
        }
    }

    /**
     * String 为 UUID
     * 排行榜
     * */
    public static HashMap<String,Integer> getPlayerRankingList(Map<String,Integer> map){
        HashMap<String,Integer> map1 = new LinkedHashMap<>();
        Comparator<Map.Entry<String, Integer>> valCmp = (o1, o2) -> {
            // TODO Auto-generated method stub
            return o2.getValue()-o1.getValue();
        };
        List<Map.Entry<String, Integer>> list = new ArrayList<>(map.entrySet());
        list.sort(valCmp);
        for(Map.Entry<String,Integer> ma:list){
            map1.put(ma.getKey(),ma.getValue());
        }
        return map1;
    }

    /** 获取物品数量
     * @param player 玩家
     * @param item 物品
     *
     * @return 物品数量*/
    public static int getCount(Player player, ItemClass item){
        if(item instanceof ItemLib){
            return ((ItemLib) item).getPlayerAllItemCount(player);
        }
        int i = 0;
        for(Item playerItem:player.getInventory().getContents().values()){
            ItemClass itemClass = new ItemClass(playerItem);
            if(item != null){
                if(itemClass.equals(item)){
                    i += playerItem.getCount();
                }
            }
        }
        return i;
    }
    /**获取难度需要积分 */
    public static int starNeed(int star){
        Map map = (Map) RsTask.getTask().getConfig().get("自定义图片路径");
        if(map.containsKey(star+"")){
            Map map1 = (Map) map.get(star+"");
            return Integer.parseInt(map1.get("解锁积分").toString());
        }else{
            return 0;
        }
    }

    public static void sendMessage(Player player,String message){
        switch (RsTask.getTask().getConfig().getString("底部显示类型")){
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
    public static String getGroupName(int group){
        Map map = (Map) RsTask.getTask().getConfig().get("自定义图片路径");
        if(map.containsKey(group+"")){
            return RsTask.getTask().getConfig().get("自定义图片路径."+group+"."+"名称").toString();
        }
        return RsTask.getTask().getLag("title");
    }

    /** 获取计分板标题*/
    public static String getScoreTitle(){
        Map map = (Map) RsTask.getTask().getConfig().get("计分板");
        return (String) map.get("标题");
    }

    /** 创建任务 */
    public static void createTask(TaskFile file){
        if(!TaskFile.isFileTask(file.getTaskName())){
            file.toSaveConfig();
            CreateTaskEvent event1 = new CreateTaskEvent(file);
            Server.getInstance().getPluginManager().callEvent(event1);
        }
    }

    public static boolean existsGroup(int group){
        Map map = (Map) RsTask.getTask().getConfig().get("自定义图片路径");
        return map.containsKey(group + "");
    }

    public static ArrayList<ItemLib> loadItemLib(Config config){
        ArrayList<ItemLib> itemLibs = new ArrayList<>();
        ItemLib lib;
        ArrayList<ItemClass> items;
        for(Map.Entry<String, Object> objectEntry : config.getAll().entrySet()){
            if(objectEntry.getValue() instanceof List){
                items = new ArrayList<>();
                for(Object msg: (List)objectEntry.getValue()){
                    ItemClass customItem = ItemClass.toItem(msg.toString());
                    if(customItem != null){
                        items.add(customItem);
                    }
                }
                lib = new ItemLib(objectEntry.getKey(),items);
                itemLibs.add(lib);
            }
        }
        return itemLibs;
    }




}
