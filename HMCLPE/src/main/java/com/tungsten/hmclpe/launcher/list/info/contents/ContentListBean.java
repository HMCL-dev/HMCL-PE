package com.tungsten.hmclpe.launcher.list.info.contents;

public class ContentListBean {

    public String name;
    public String path;
    public boolean selected;

    public ContentListBean (String name,String path,boolean selected){
        this.name = name;
        this.path = path;
        this.selected = selected;
    }
}
