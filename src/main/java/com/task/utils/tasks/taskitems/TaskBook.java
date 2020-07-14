package com.task.utils.tasks.taskitems;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.StringTag;
import com.task.RsTask;
import com.task.utils.tasks.TaskFile;
import com.task.utils.tasks.PlayerFile;
import com.task.utils.form.CreateMenu;

import java.util.*;

/**
 * @author SmallasWater
 */
public class TaskBook {

    public String title = "";


    public ItemBookWritten book;


    public List<String> texts = new ArrayList<>();

    public TaskBook(ItemBookWritten book){
        this.title = book.getCustomName();
        this.book = book;
    }
    public TaskBook(ItemBookWritten book,LinkedList<String> texts){
        this.book = book;
        if(book.hasCompoundTag()){
            this.title = book.getNamedTag().getString("bookTaskName");
        }
        if(title.equalsIgnoreCase("")){
            title = book.getCustomName();
        }
        if(book.getNamedTag() != null){
            if(!book.getNamedTag().getString("bookTaskName").equalsIgnoreCase("")){
                this.title = book.getNamedTag().getString("bookTaskName");
            }
        }
        this.texts = texts;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCustomName(String customName){
        this.book.setCustomName(customName);
    }


    public TaskBook writeIn(String text){
        texts.add(text);
        return this;
    }

    public TaskBook setText(String[] strings){
        this.texts = Arrays.asList(strings);
        return this;
    }

    public TaskBook write(String[] texts){
        this.texts.addAll(Arrays.asList(texts));
        return this;
    }

    public TaskBook setText(int index,String text){
        texts.set(index,text);
        return this;
    }

    public TaskBook removeIndex(int index){
        texts.remove(index);
        return this;
    }

    public TaskBook delText(String text){
        texts.remove(text);
        return this;
    }


    public void cleanAll(){
        this.texts = new LinkedList<>();
    }

    public void upData(TaskFile file, Player player){
        cleanAll();
        if(file != null){
            StringBuilder two = new StringBuilder();
            StringBuilder one = new StringBuilder();
            one.append(CreateMenu.getTitles(player, file)).append("§r\n");
            one.append(RsTask.getTask().getLag("task-speed")).append("§r \n");
            TaskItem[] items = file.getTaskItem();
            if(items.length > 0){
                for(String s: CreateMenu.toTaskItemString(items,player)){
                    one.append(s);
                }
            }else{
                one.append(RsTask.getTask().getLag("notTasks")).append("§r\n");
            }
            SuccessItem item = file.getSuccessItem();
            if(PlayerFile.getPlayerFile(player.getName()).isFirst(file)){
                item = file.getFirstSuccessItem();
            }
            two.append("\n").append(RsTask.getTask().getLag("success-item")).append("\n");
            for(StringBuilder s:item.toList()){
                two.append("§r").append(s.toString()).append("\n");
            }
            two.append("\n\n\n§c(如果内容不符请重新打开或执行/cbook up)");
            writeIn((one.toString()).replace("§e","§r")).writeIn(two.toString().replace("§e","§r"));
        }
    }

    public static TaskBook getTaskBookByItem(ItemBookWritten book){
        LinkedList<String> strings = new LinkedList<>();
        Object books = book.getPages();
        List l = null;
        //双核心版本兼容
        if(books != null) {
            if(books instanceof List) {
               l = (List) books;
            }else{
                l = new LinkedList<>(Arrays.asList((String[]) books));
            }
        }
        if(l != null) {
            for (Object o : l) {
                if (o instanceof StringTag) {
                    strings.add(((StringTag) o).parseValue());
                } else {
                    strings.add(o.toString());
                }
            }
        }
        return new TaskBook(book,strings);
    }

    public static boolean canInventory(Player player,String taskName){
        for(Item item:player.getInventory().getContents().values()) {
            if (item instanceof ItemBookWritten) {
                if (item.getNamedTag() != null) {
                    if(!item.getNamedTag().getString("bookTaskName").equalsIgnoreCase("")){
                        if(item.getNamedTag().getString("bookTaskName").equalsIgnoreCase(taskName)){
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    public static boolean isBook(ItemBookWritten itemBookWritten){
        if(itemBookWritten.getNamedTag() != null){
            String title = itemBookWritten.getNamedTag().getString("bookTaskName");
            return TaskFile.isFileTask(title);
        }
       return false;
    }


    public ItemBookWritten toBook(){
        ItemBookWritten bookWritten = new ItemBookWritten();
        bookWritten.setCustomName(book.getCustomName());
        String[] strings = new String[]{"§r§b-------------------","§r§b|§e右键/点地 打开查看§b|","§r§b-------------------"};
        bookWritten.setLore(strings);
        bookWritten.writeBook(RsTask.getTask().getLag("title"),title,(texts.size() <= 50 ? texts.toArray(new String[0]) : Arrays.copyOfRange(texts.toArray(new String[0]), 0, 50)));
        CompoundTag tag = bookWritten.getNamedTag();
        tag.putString("bookTaskName",title);
        bookWritten.setNamedTag(tag);
        return bookWritten;
    }
}
