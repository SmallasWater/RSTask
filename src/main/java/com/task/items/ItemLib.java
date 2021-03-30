package com.task.items;

import cn.nukkit.Player;
import cn.nukkit.inventory.Inventory;
import cn.nukkit.item.Item;
import com.task.RsTask;
import com.task.utils.tasks.taskitems.ItemClass;


import java.util.ArrayList;

/**
 * @author SmallasWater
 * Create on 2021/3/28 18:35
 * Package com.smallaswater.inborn.data
 */
public class ItemLib extends ItemClass implements Cloneable{

    private String libName;

    private int count = 1;

    private ArrayList<ItemClass> item;

    public static ArrayList<ItemLib> ItemLibs = new ArrayList<>();

    public ItemLib(String name, ArrayList<ItemClass> item){
        this.libName = name;
        this.item = item;

    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public static ItemLib getItem(String libName){
        for(ItemLib itemLibs: ItemLibs){
            if(itemLibs.libName.equalsIgnoreCase(libName)){
                return itemLibs.clone();
            }
        }
        return null;
    }

    public ItemClass getItemClass(){
        return this.item.get(0);
    }

    @Override
    public String toTaskItem(boolean defaultType){
        return libName+"@lib";
    }

    /**
     * 获取名称
     * */
    @Override
    public String toSaveConfig(boolean defaultType){
        return libName+"@lib";
    }

    /**
     * 判断是否可以减少物品
     * */
    public boolean hasReduceItem(Player player){
        return getPlayerAllItemCount(player) >= getCount();
    }

    public void reduceItem(Player player){
        ArrayList<Item> items = getPlayerExistsItem(player);
        if(getPlayerAllItemCount(items) >= getCount()){
            int max = getCount();
            for(Item item: items){
                if(item.getCount() >= max){
                    item.setCount(max);
                    player.getInventory().removeItem(item);
                    break;
                }else{
                    max -= item.getCount();
                    player.getInventory().removeItem(item);
                }
            }
        }

    }

    public int getPlayerAllItemCount(Player player){
        return getPlayerAllItemCount(getPlayerExistsItem(player));

    }
    public int getPlayerAllItemCount(ArrayList<Item> items){
        int count = 0;
        for(Item i: items){
            count += i.getCount();
        }
        return count;

    }

    public ArrayList<Item> getPlayerExistsItem(Player player){
        ArrayList<Item> items = new ArrayList<>();
        Item ic;
        for(ItemClass item: this.item){
            int count = getItemCount(item.getItem(), player.getInventory());
            ic = item.getItem().clone();
            ic.setCount(count);
            items.add(ic);

        }
        return items;
    }


    public int getItemCount(Item item, Inventory inventory){
        int count = 0;
        for(Item i: inventory.getContents().values()){
            if(i.equals(item,true,true)){
                count += i.getCount();
            }
        }
        return count;
    }

    public boolean hasItem(Item item){
        ItemClass itemClass = new ItemClass(item);
        return this.item.contains(itemClass);
    }

    public String getLibName() {
        return libName;
    }

    public static void registerItemLib(String itemName, ArrayList<ItemClass> item){
        ItemLib lib = new ItemLib(itemName,item);
        if(ItemLibs.contains(lib)){
            lib = ItemLib.ItemLibs.get(ItemLib.ItemLibs.indexOf(lib));
            lib.addItem(item.toArray(new ItemClass[0]));
        }else{
            ItemLibs.add(lib);
        }
    }

    public void addItem(ItemClass... item){
        for(ItemClass item1: item){
            if(this.item.contains(item1)){
                continue;
            }
            this.item.add(item1);
        }

    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof ItemLib){
            return ((ItemLib) obj).libName.equalsIgnoreCase(libName);
        }
        return false;
    }

    @Override
    public ItemLib clone() {
        ItemLib lib;
        try {
            lib = (ItemLib) super.clone();
        }catch (Exception e){
            return null;
        }
        return lib;
    }
}
