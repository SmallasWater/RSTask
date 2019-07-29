package com.Task.utils.Tasks.TaskItems;



import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;

import java.util.LinkedHashMap;
import java.util.Map;

public class TaskButton {

    public enum ButtonImageType{
        Path("本地"),
        Url("网络");
        protected String name;
        ButtonImageType(String name){
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static ButtonImageType type;


    public static String text;

    public static String data;

    public TaskButton(String text){
        this(text,ButtonImageType.Path,"textures/items/book_enchanted");
    }

    public TaskButton(String ButtonText,ButtonImageType ButtonType,String ButtonData){
       text = ButtonText;

       type = ButtonType;
       data = ButtonData;
    }

    public static LinkedHashMap<String,Object> toButton(String text){

        LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap<>(),
                                     image = new LinkedHashMap<>();
        image.put("type",type == ButtonImageType.Path?"path":"url");
        image.put("data",data);
        linkedHashMap.put("text",text);
        linkedHashMap.put("image",image);
        return linkedHashMap;
    }


    public ElementButton toButton(){
        ElementButton elementButton = new ElementButton(text);
        ElementButtonImageData imageData = new ElementButtonImageData(type == ButtonImageType.Path?"path":"url",data);
        elementButton.addImage(imageData);
        return elementButton;
    }

    public static void setText(String text) {
        TaskButton.text = text;
    }

    public static void setData(String data) {
        TaskButton.data = data;
    }

    public static void setType(ButtonImageType type) {
        TaskButton.type = type;
    }


    public LinkedHashMap<String,Object> toSaveConfig(){
        LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("位置",type.getName());
        linkedHashMap.put("路径",data);
        return linkedHashMap;
    }

    public static TaskButton toTaskButton(Map map){
        if(map == null) return null;
        if(map.containsKey("位置")){
            TaskButton.type = map.get("位置").equals("本地")?ButtonImageType.Path:ButtonImageType.Url;
        }else{
            return null;
        }
        if(map.containsKey("路径")){
            TaskButton.data = (String) map.get("路径");
        }else{
            return null;
        }
        return new TaskButton("",type,data);

    }
}
