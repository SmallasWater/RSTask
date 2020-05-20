package com.task.utils.tasks.taskitems;



import cn.nukkit.form.element.ElementButton;
import cn.nukkit.form.element.ElementButtonImageData;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author SmallasWater
 */
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

    private ButtonImageType type;


    private String text;

    private String data;

    public TaskButton(String text){
        this(text,ButtonImageType.Path,"textures/items/book_enchanted");
    }

    public TaskButton(String buttonText,ButtonImageType buttonType,String buttonData){
       text = buttonText;
       type = buttonType;
       data = buttonData;
    }




    public ElementButton toButton(){
        ElementButton elementButton = new ElementButton(text);
        ElementButtonImageData imageData = new ElementButtonImageData(type == ButtonImageType.Path?"path":"url",data);
        elementButton.addImage(imageData);
        return elementButton;
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setType(ButtonImageType type) {
        this.type = type;
    }


    public LinkedHashMap<String,Object> toSaveConfig(){
        LinkedHashMap<String,Object> linkedHashMap = new LinkedHashMap<>();
        linkedHashMap.put("位置",type.getName());
        linkedHashMap.put("路径",data);
        return linkedHashMap;
    }

    public static TaskButton toTaskButton(Map map){
        if(map == null) {
            return null;
        }
        ButtonImageType type;
        String data;
        if(map.containsKey("位置")){
            type = map.get("位置").equals("本地")?ButtonImageType.Path:ButtonImageType.Url;
        }else{
            return null;
        }
        if(map.containsKey("路径")){
            data = (String) map.get("路径");
        }else{
            return null;
        }
        return new TaskButton("",type,data);

    }
}
