package com.tungsten.hmclpe.launcher.setting.game.child;

import androidx.annotation.NonNull;

public class GameDirSetting implements Cloneable{

    public int type;
    public String path;

    public GameDirSetting(int type,String path){
        this.type = type;
        this.path = path;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
