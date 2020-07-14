package com.task.utils.tasks.taskitems;




import com.task.RsTask;
import com.task.utils.ItemIDSunName;

import java.util.*;


/**
 * 任务奖励
 *
 * @author SmallasWater*/
public class SuccessItem {
    private ItemClass[] item = new ItemClass[]{};
    private int money = 0;
    private CommandClass[] cmd = new CommandClass[]{};
    private int count = 0;

    public SuccessItem(){}

    public SuccessItem(int money){
        this(money, new ItemClass[]{},10);
    }

    public SuccessItem(ItemClass[] item){
        this(0,item,10);
    }

    public SuccessItem(CommandClass[] cmd){
       this(0,cmd,10);
    }

    public SuccessItem(int money, ItemClass[] item, int count){
        this(money,item,new CommandClass[]{},count);
    }

    public SuccessItem(int money, CommandClass[] cmd, int count){
        this(money,new ItemClass[]{},cmd,count);
    }


    public SuccessItem(ItemClass[] item, CommandClass[] cmd, int count){
        this(0,item,cmd,count);
    }

    public SuccessItem(int money, ItemClass[] item, CommandClass[] cmd, int count){
        this.money = money;
        this.cmd = cmd;
        this.item = item;
        this.count = count;
    }

    public int getMoney() {
        return money;
    }

    public ItemClass[] getItem() {
        return item;
    }

    public CommandClass[] getCmd() {
        return cmd;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setMoney(int money) {
        this.money = money;
    }


    public void addCmdClass(CommandClass commandClass){
        if(commandClass != null){
            LinkedList<CommandClass> classes = new LinkedList<>(Arrays.asList(cmd));
            classes.add(commandClass);
            cmd = classes.toArray(new CommandClass[0]);
        }
    }


    public void addItemClass(ItemClass itemClass){
        if(itemClass != null){
            LinkedList<ItemClass> classes = new LinkedList<>(Arrays.asList(item));
            classes.add(itemClass);
            item = classes.toArray(new ItemClass[0]);
        }

    }

    /**奖励积分*/
    public int getCount() {
        return count;
    }

    public LinkedHashMap<String,Object> toSaveConfig(){
        LinkedHashMap<String,Object> conf = new LinkedHashMap<>();
        LinkedList<String> itemList = new LinkedList<>();
        LinkedList<String> cmdList = new LinkedList<>();

        for(ItemClass item:this.item){
            if(item != null){
                itemList.add(item.toSaveConfig());
            }
        }
        for(CommandClass cmd:this.cmd){
            if(cmd != null){
                cmdList.add(cmd.toString());
            }
        }
        conf.put("Items",itemList);
        conf.put("Cmd",cmdList);
        conf.put("Money",this.money);
        conf.put("Count",this.count);
        return conf;
    }


    public static SuccessItem toSuccessItem(Map map){
        if(map == null) {
            return null;
        }
        ItemClass[] itemClasses = new ItemClass[]{};
        CommandClass[] commandClasses = new CommandClass[]{};
        for(Object tag:map.keySet()){
            if(tag instanceof String ){
                if(tag.equals("Items")){
                    Object o = map.get(tag);
                    if(o instanceof List){
                        itemClasses = new ItemClass[((List) o).size()];
                        int i = 0;
                        for(Object o1:(List)o){
                            if(o1 instanceof String){
                                itemClasses[i] = ItemClass.toItem((String) o1);
                                i++;
                            }
                        }
                    }
                }
                if(tag.equals("Cmd")){
                    Object o = map.get(tag);
                    if(o instanceof List) {
                        commandClasses = new CommandClass[((List) o).size()];
                        int i = 0;
                        for (Object o1 : (List) o) {
                            if (o1 instanceof String) {
                                commandClasses[i] = CommandClass.toCommandClass((String) o1);
                                i++;
                            }
                        }
                    }
                }
            }
        }
        return new SuccessItem((int)map.get("Money"),itemClasses,commandClasses,(int)map.get("Count"));
    }

    public void add(String value){
        if(value.split("@").length < 1) {
            return;
        }
        switch (value.split("@")[1]) {
            case "item":
                String sItem = value.split("@")[0];
                String[] lists = sItem.split(":");
                addItemClass(new ItemClass(Integer.parseInt(lists[0]),
                        Integer.parseInt(lists[1]),Integer.parseInt(lists[2])));
                break;
            case "tag":
                sItem = value.split("@")[0];
                int c = 0;
                String s = sItem;
                if(sItem.split(":").length > 1){
                    s = sItem.split(":")[0];
                    c = Integer.parseInt(sItem.split(":")[1]);
                }
                ItemClass itemClass = RsTask.getTask().getTagItemsConfig(s);
                if(c > 0) {
                    itemClass.getItem().setCount(c);
                }
                if(itemClass != null) {
                    addItemClass(itemClass);
                }
                break;
            case "money":
                int money;
                try {
                    money = Integer.parseInt(value.split("@")[0]);
                }catch (Exception e){
                    money = 0;
                }
                setMoney(this.money+money);
                break;
            case "Cmd":
                sItem = value.split("@")[0];
                addCmdClass(CommandClass.toCommandClass(sItem));
                default:
                    break;
        }
    }

    /** id:damage:count@item 或 id:count@tag 或 count@money 或 String@Cmd*/
    public static SuccessItem toSuccessItem(String string){
        if(string.split("@").length < 2) {
            return null;
        }
        switch (string.split("@")[1]) {
            case "item":
                String sItem = string.split("@")[0];
                String[] lists = sItem.split(":");
                return new SuccessItem(new ItemClass[]{new ItemClass(Integer.parseInt(lists[0]),
                        Integer.parseInt(lists[1]),Integer.parseInt(lists[2]))});
            case "tag":
                sItem = string.split("@")[0];
                int c = 0;
                String s = sItem;
                if(sItem.split(":").length > 1){
                    s = sItem.split(":")[0];
                    c = Integer.parseInt(sItem.split(":")[1]);
                }
                ItemClass itemClass = RsTask.getTask().getTagItemsConfig(s);
                if(itemClass != null){
                    if(c > 0){
                        itemClass.getItem().setCount(c);
                    }
                    return new SuccessItem(new ItemClass[]{itemClass});
                }

                else {
                    return null;
                }
            case "money":
                int money;
                try {
                    money = Integer.parseInt(string.split("@")[0]);
                }catch (Exception e){
                    money = 0;
                }
                return new SuccessItem(money);
            case "Cmd":
                sItem = string.split("@")[0];
                return new SuccessItem(new CommandClass[]{CommandClass.toCommandClass(sItem)});
                default:
                    return null;
        }
    }

    public LinkedList<StringBuilder> toList() {
        LinkedList<StringBuilder> builders = new LinkedList<>();
        ItemClass[] classes = getItem();
        CommandClass[] commandClasses = getCmd();
        if(classes != null && classes.length > 0){
            for(ItemClass itemClass:classes){
                if(itemClass != null){
                    StringBuilder builder1 = new StringBuilder();
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
        if(getMoney() > 0){
            builders.add(new StringBuilder(RsTask.getTask().getCoinName()).append(">").append(getMoney()));
        }
        if(RsTask.canOpen()){
            if(getCount() > 0){
                builders.add(new StringBuilder(RsTask.getTask().getFName()).append(">").append(getCount()));
            }
        }
        return builders;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (StringBuilder builder1:toList()){
            builder.append(builder1).append("\n");
        }
        return builder.toString();
    }
}
