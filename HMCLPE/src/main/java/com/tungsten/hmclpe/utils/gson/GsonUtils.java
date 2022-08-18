package com.tungsten.hmclpe.utils.gson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;
import com.tungsten.hmclpe.launcher.list.info.contents.ContentListBean;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.launcher.setting.game.PublicGameSetting;
import com.tungsten.hmclpe.launcher.setting.launcher.LauncherSetting;
import com.tungsten.hmclpe.utils.file.FileStringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class GsonUtils {

    public static LauncherSetting getLauncherSettingFromFile(String path){
        String string = FileStringUtils.getStringFromFile(path);
        Gson gson = new Gson();
        return gson.fromJson(string,LauncherSetting.class);
    }

    public static PrivateGameSetting getPrivateGameSettingFromFile(String path){
        String string = FileStringUtils.getStringFromFile(path);
        Gson gson = new Gson();
        return gson.fromJson(string,PrivateGameSetting.class);
    }

    public static PublicGameSetting getPublicGameSettingFromFile(String path){
        String string = FileStringUtils.getStringFromFile(path);
        Gson gson = new Gson();
        return gson.fromJson(string,PublicGameSetting.class);
    }

    public static ArrayList<ContentListBean> getContentListFromFile(String path){
        String string = FileStringUtils.getStringFromFile(path);
        Gson gson = new Gson();
        Type contentListType =new TypeToken<ArrayList<ContentListBean>>(){}.getType();
        return gson.fromJson(string,contentListType);
    }

    public static ArrayList<Account> getAccountListFromFile(String path){
        String string = FileStringUtils.getStringFromFile(path);
        Gson gson = new Gson();
        Type accountListType =new TypeToken<ArrayList<Account>>(){}.getType();
        return gson.fromJson(string,accountListType);
    }

    public static ArrayList<AuthlibInjectorServer> getServerListFromFile(String path){
        String string = FileStringUtils.getStringFromFile(path);
        Gson gson = JsonUtils.defaultGsonBuilder().registerTypeAdapter(AuthlibInjectorServer.class, new AuthlibInjectorServer.Deserializer()).create();
        Type serverListType =new TypeToken<ArrayList<AuthlibInjectorServer>>(){}.getType();
        return gson.fromJson(string,serverListType);
    }

    public static void saveLauncherSetting(LauncherSetting launcherSetting,String path){
        Gson gson = new Gson();
        String string = gson.toJson(launcherSetting);
        FileStringUtils.writeFile(path,string);
    }

    public static void savePrivateGameSetting(PrivateGameSetting privateGameSetting,String path){
        Gson gson = new Gson();
        String string = gson.toJson(privateGameSetting);
        FileStringUtils.writeFile(path,string);
    }

    public static void savePublicGameSetting(PublicGameSetting publicGameSetting,String path){
        Gson gson = new Gson();
        String string = gson.toJson(publicGameSetting);
        FileStringUtils.writeFile(path,string);
    }

    public static void saveContents(ArrayList<ContentListBean> list,String path){
        Gson gson = new Gson();
        String string = gson.toJson(list);
        FileStringUtils.writeFile(path,string);
    }

    public static void saveAccounts(ArrayList<Account> list,String path){
        Gson gson = new Gson();
        String string = gson.toJson(list);
        FileStringUtils.writeFile(path,string);
    }

    public static void saveServer(ArrayList<AuthlibInjectorServer> list, String path){
        Gson gson = JsonUtils.defaultGsonBuilder().registerTypeAdapter(AuthlibInjectorServer.class, new AuthlibInjectorServer.Deserializer()).create();
        String string = gson.toJson(list);
        FileStringUtils.writeFile(path,string);
    }
}
