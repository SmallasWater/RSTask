package com.Task.utils.Tasks.TaskItems;




import com.Task.RSTask;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


/**
 * 任务奖励
 * */
public class successItem {
    private ItemClass[] item = new ItemClass[]{};
    private int money = 0;
    private CommandClass[] cmd = new CommandClass[]{};
    private int count;

    public successItem(){}

    public successItem(int money){
        this(money, new ItemClass[]{},10);
    }

    public successItem(ItemClass[] item){
        this(0,item,10);
    }

    public successItem(CommandClass[] cmd){
       this(0,cmd,10);
    }



    public successItem(int money,ItemClass[] item,int count){
        this(money,item,new CommandClass[]{},count);
    }

    public successItem(int money,CommandClass[] cmd,int count){
        this(money,new ItemClass[]{},cmd,count);
    }


    public successItem(ItemClass[] item,CommandClass[] cmd,int count){
        this(0,item,cmd,count);
    }

    public successItem(int money,ItemClass[] item,CommandClass[] cmd,int count){
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
            CommandClass[] commandClasses = new CommandClass[cmd.length+1];
            commandClasses[cmd.length] = commandClass;
            cmd = commandClasses;
        }
    }


    public void addItemClass(ItemClass itemClass){
        if(itemClass != null){
            ItemClass[] itemClasses = new ItemClass[item.length+1];
            itemClasses[cmd.length] = itemClass;
            item = itemClasses;
        }

    }

    /**奖励积分*/
    public int getCount() {
        return count;
    }

    public LinkedHashMap<String,Object> toSaveConfig(){
        LinkedHashMap<String,Object> conf = new LinkedHashMap<>();
        LinkedList<String> ItemList = new LinkedList<>();
        LinkedList<String> CmdList = new LinkedList<>();

        for(ItemClass item:this.item){
            ItemList.add(item.toSaveConfig());
        }
        for(CommandClass cmd:this.cmd){
            CmdList.add(cmd.toString());
        }
        conf.put("Items",ItemList);
        conf.put("Cmd",CmdList);
        conf.put("Money",this.money);
        conf.put("Count",this.count);
        return conf;
    }


    public static successItem toSuccessItem(Map map){
        if(map == null) return null;
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
        return new successItem((int)map.get("Money"),itemClasses,commandClasses,(int)map.get("Count"));
    }

    public void add(String value){
        if(value.split("@").length < 1) return;
        switch (value.split("@")[1]) {
            case "item":
                String sItem = value.split("@")[0];
                String[] lists = sItem.split(":");
                addItemClass(new ItemClass(Integer.parseInt(lists[0]),
                        Integer.parseInt(lists[1]),Integer.parseInt(lists[2])));
                break;
            case "tag":
                sItem = value.split("@")[0];
                ItemClass itemClass = RSTask.getTask().getTagItemsConfig(sItem);
                if(itemClass != null)
                    addItemClass(itemClass);
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
        }
    }

    /** id:damage:count@item 或 id@tag 或 count@money 或 String@Cmd*/
    public static successItem toSuccessItem(String string){
        if(string.split("@").length < 1) return null;
        switch (string.split("@")[1]) {
            case "item":
                String sItem = string.split("@")[0];
                String[] lists = sItem.split(":");
                return new successItem(new ItemClass[]{new ItemClass(Integer.parseInt(lists[0]),
                        Integer.parseInt(lists[1]),Integer.parseInt(lists[2]))});
            case "tag":
                sItem = string.split("@")[0];
                ItemClass itemClass = RSTask.getTask().getTagItemsConfig(sItem);
                if(itemClass != null)
                    return new successItem(new ItemClass[]{itemClass});
                else
                    return null;
            case "money":
                int money;
                try {
                    money = Integer.parseInt(string.split("@")[0]);
                }catch (Exception e){
                    money = 0;
                }
                return new successItem(money);
            case "Cmd":
                sItem = string.split("@")[0];
                return new successItem(new CommandClass[]{CommandClass.toCommandClass(sItem)});
        }
        return null;
    }




}
