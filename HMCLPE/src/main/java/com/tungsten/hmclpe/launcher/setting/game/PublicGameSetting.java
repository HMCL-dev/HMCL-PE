package com.tungsten.hmclpe.launcher.setting.game;

import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.io.File;

public class PublicGameSetting {

    public Account account;
    public String home;
    public String currentVersion;

    public PublicGameSetting (Account account,String home,String currentVersion){
        this.account = account;
        this.home = home;
        this.currentVersion = currentVersion;
    }

    public static boolean isUsingIsolateSetting(String currentVersion){
        if (new File(currentVersion + "/hmclpe.cfg").exists() && GsonUtils.getPrivateGameSettingFromFile(currentVersion + "/hmclpe.cfg") != null) {
            return GsonUtils.getPrivateGameSettingFromFile(currentVersion + "/hmclpe.cfg").forceEnable || GsonUtils.getPrivateGameSettingFromFile(currentVersion + "/hmclpe.cfg").enable;
        }
        else {
            return false;
        }
    }
}
