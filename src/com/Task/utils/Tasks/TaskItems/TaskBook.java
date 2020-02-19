package com.Task.utils.Tasks.TaskItems;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.ItemBookWritten;
import cn.nukkit.nbt.tag.CompoundTag;
import com.Task.RSTask;
import com.Task.utils.Tasks.TaskFile;
import com.Task.utils.Tasks.playerFile;
import com.Task.utils.events.playerClickTaskEvent;
import com.Task.utils.form.createMenu;

import java.util.*;

public class TaskBook {

    public String title;

    public ItemBookWritten book;


    public List<String> texts = new ArrayList<>();

    public TaskBook(ItemBookWritten book){
        this.title = book.getCustomName();
        this.book = book;
    }
    public TaskBook(ItemBookWritten book,LinkedList<String> texts){
        this.book = book;
        this.title = book.getCustomName();
        this.texts = texts;
    }

    public void setTitle(String title) {
        this.title = title;
        this.book.setCustomName(title);
    }



    public TaskBook writeIn(String text){
        texts.add(text);
        return this;
    }

    public TaskBook setText(String[] strings){
        this.texts = (LinkedList<String>) Arrays.asList(strings);
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

    public void upData(TaskFile file,Player player){
        cleanAll();
        if(file != null){
            StringBuilder two = new StringBuilder();
            two.append(RSTask.getTask().getLag("task-speed")).append("§r \n");
            TaskItem[] items = file.getTaskItem();
            if(items.length > 0){
                for(String s: createMenu.toTaskItemString(items,player)){
                    two.append(s);
                }
            }else{
                two.append(RSTask.getTask().getLag("notTasks")).append("§r\n");
            }
            two.append("\n\n\n§c(如果内容不符请重新打开)");
            StringBuilder one = new StringBuilder();
            one.append(createMenu.getTitles(file)).append("§r\n");
            successItem item = file.getSuccessItem();
            if(playerFile.getPlayerFile(player.getName()).isFrist(file)){
                item = file.getFristSuccessItem();
            }
            one.append(RSTask.getTask().getLag("success-item")).append("\n");
            for(StringBuilder s:item.toList()){
                one.append("§r").append(s.toString()).append("\n");
            }
            one.append("\n\n\n§c(如果内容不符请重新打开或执行/cbook up)");
            writeIn(one.toString()).writeIn(two.toString());
        }
    }

    public static TaskBook getTaskBookByItem(ItemBookWritten book){
        LinkedList<String> strings = new LinkedList<>(Arrays.asList(book.getPages()));
        return new TaskBook(book,strings);
    }

    public static boolean canInventory(Player player,String taskName){
        for(Item item:player.getInventory().getContents().values()){
            if(taskName.equals(item.getCustomName())) {
                return true;
            }
        }
        return false;
    }

    public static boolean isBook(ItemBookWritten itemBookWritten){
        String title = itemBookWritten.getCustomName();
        return TaskFile.isFileTask(title);
    }


    public ItemBookWritten toBook(){
        ItemBookWritten bookWritten = new ItemBookWritten();
        bookWritten.setCustomName(book.getCustomName());
        String[] strings = new String[]{"§r§b-------","§r§b|§e打开查看§b|","§r§b-------"};
        bookWritten.setLore(strings);
        bookWritten.writeBook(RSTask.getTask().getLag("title"),title,(texts.size() <= 50 ? texts.toArray(new String[0]) : Arrays.copyOfRange(texts.toArray(new String[0]), 0, 50)));
        return bookWritten;
    }
}
