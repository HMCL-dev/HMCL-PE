package com.tungsten.hmclpe.auth.offline;

import android.content.Context;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.yggdrasil.TextureModel;
import com.tungsten.hmclpe.skin.utils.Avatar;

public class OfflineSkinSetting implements Cloneable{

    public int type;
    public TextureModel model;
    public String skinPath;
    public String capePath;
    public String server;

    public OfflineSkinSetting(Context context){
        this(0,TextureModel.ALEX,"","","");
    }

    public OfflineSkinSetting(int type,TextureModel model,String skinPath,String capePath,String server) {
        this.type = type;
        this.model = model;
        this.skinPath = skinPath;
        this.capePath = capePath;
        this.server = server;
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
