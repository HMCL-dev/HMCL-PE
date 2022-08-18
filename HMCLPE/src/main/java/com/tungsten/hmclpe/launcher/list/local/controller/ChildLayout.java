package com.tungsten.hmclpe.launcher.list.local.controller;

import com.google.gson.Gson;
import com.tungsten.hmclpe.control.bean.BaseButtonInfo;
import com.tungsten.hmclpe.control.bean.BaseRockerViewInfo;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.file.FileStringUtils;

import java.util.ArrayList;

public class ChildLayout {

    public String name;
    public int visibility;
    public ArrayList<BaseButtonInfo> baseButtonList;
    public ArrayList<BaseRockerViewInfo> baseRockerViewList;

    public ChildLayout (String name,int visibility,ArrayList<BaseButtonInfo> baseButtonList,ArrayList<BaseRockerViewInfo> baseRockerViewList){
        this.name = name;
        this.visibility = visibility;
        this.baseButtonList = baseButtonList;
        this.baseRockerViewList = baseRockerViewList;
    }

    public static void saveChildLayout(String pattern,ChildLayout childLayout){
        Gson gson = new Gson();
        String string = gson.toJson(childLayout);
        FileStringUtils.writeFile(AppManifest.CONTROLLER_DIR + "/" + pattern + "/" + childLayout.name + ".json",string);
    }

}
