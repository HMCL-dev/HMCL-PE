package com.tungsten.hmclpe.launcher.list.local.game;

public class GameListBean {

    public String name;
    public String version;
    public String iconPath;
    public boolean isSelected;

    public GameListBean (String name,String version,String iconPath,boolean isSelected){
        this.name = name;
        this.version = version;
        this.iconPath = iconPath;
        this.isSelected = isSelected;
    }
}
