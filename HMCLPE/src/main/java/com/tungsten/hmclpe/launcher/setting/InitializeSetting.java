package com.tungsten.hmclpe.launcher.setting;

import android.app.Activity;
import android.content.Context;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.info.contents.ContentListBean;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.game.child.BoatLauncherSetting;
import com.tungsten.hmclpe.launcher.setting.game.child.GameDirSetting;
import com.tungsten.hmclpe.launcher.setting.game.child.JavaSetting;
import com.tungsten.hmclpe.launcher.setting.game.child.PojavLauncherSetting;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.launcher.setting.game.PublicGameSetting;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.launcher.setting.game.child.RamSetting;
import com.tungsten.hmclpe.launcher.setting.launcher.LauncherSetting;
import com.tungsten.hmclpe.launcher.setting.launcher.child.BackgroundSetting;
import com.tungsten.hmclpe.launcher.setting.launcher.child.SourceSetting;
import com.tungsten.hmclpe.utils.file.AssetsUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.platform.MemoryUtils;

import java.io.File;
import java.util.ArrayList;

public class InitializeSetting {

    public static void initializeControlPattern (Activity activity, AssetsUtils.FileOperateCallback callback) {
        String[] string = new File(AppManifest.CONTROLLER_DIR + "/").list();
        if (new File(AppManifest.CONTROLLER_DIR + "/").exists()){
            assert string != null;
            if (string.length == 0) {
                AssetsUtils.getInstance(activity.getApplicationContext()).copyAssetsToSD("control", AppManifest.CONTROLLER_DIR).setFileOperateCallback(callback);
            }
        }
        else {
            AssetsUtils.getInstance(activity.getApplicationContext()).copyAssetsToSD("control", AppManifest.CONTROLLER_DIR).setFileOperateCallback(callback);
        }
    }

    public static ArrayList<Account> initializeAccounts(Context context){
        ArrayList<Account> accountList = new ArrayList<>();
        if (new File(AppManifest.ACCOUNT_DIR + "/accounts.json").exists() && GsonUtils.getContentListFromFile(AppManifest.ACCOUNT_DIR + "/accounts.json").size() != 0){
            accountList = GsonUtils.getAccountListFromFile(AppManifest.ACCOUNT_DIR + "/accounts.json");
        }
        else {
            GsonUtils.saveAccounts(accountList,AppManifest.ACCOUNT_DIR + "/accounts.json");
        }
        return accountList;
    }

    public static ArrayList<AuthlibInjectorServer> initializeAuthlibInjectorServer(Context context){
        ArrayList<AuthlibInjectorServer> serverListBeans = new ArrayList<>();
        if (new File(AppManifest.ACCOUNT_DIR + "/authlib_injector_server.json").exists() && GsonUtils.getContentListFromFile(AppManifest.ACCOUNT_DIR + "/authlib_injector_server.json").size() != 0){
            serverListBeans = GsonUtils.getServerListFromFile(AppManifest.ACCOUNT_DIR + "/authlib_injector_server.json");
        }
        else {
            GsonUtils.saveServer(serverListBeans,AppManifest.ACCOUNT_DIR + "/authlib_injector_server.json");
        }
        return serverListBeans;
    }

    public static ArrayList<ContentListBean> initializeContents(Context context){
        ArrayList<ContentListBean> contentList = new ArrayList<>();
        if (new File(AppManifest.GAME_FILE_DIRECTORY_DIR + "/game_file_directories.json").exists() && GsonUtils.getContentListFromFile(AppManifest.GAME_FILE_DIRECTORY_DIR + "/game_file_directories.json").size() != 0){
            contentList = GsonUtils.getContentListFromFile(AppManifest.GAME_FILE_DIRECTORY_DIR + "/game_file_directories.json");
        }
        else {
            contentList.add(new ContentListBean(context.getString(R.string.default_game_file_directory_list_pri),AppManifest.DEFAULT_GAME_DIR,true));
            contentList.add(new ContentListBean(context.getString(R.string.default_game_file_directory_list_sec),AppManifest.INNER_GAME_DIR,false));
            GsonUtils.saveContents(contentList,AppManifest.GAME_FILE_DIRECTORY_DIR + "/game_file_directories.json");
        }
        return contentList;
    }

    public static LauncherSetting initializeLauncherSetting(){
        LauncherSetting launcherSetting;
        if (new File(AppManifest.SETTING_DIR + "/launcher_setting.json").exists()){
            launcherSetting = GsonUtils.getLauncherSettingFromFile(AppManifest.SETTING_DIR + "/launcher_setting.json");
        }
        else {
            launcherSetting = new LauncherSetting(AppManifest.DEFAULT_GAME_DIR,new SourceSetting(true,1,0),0,64,false,true,false,false,false,"DEFAULT",new BackgroundSetting(0,"",""),AppManifest.DEFAULT_CACHE_DIR);
            GsonUtils.saveLauncherSetting(launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
        }
        return launcherSetting;
    }

    public static PublicGameSetting initializePublicGameSetting(Context context, MainActivity activity){
        PublicGameSetting publicGameSetting;
        if (new File(AppManifest.SETTING_DIR + "/public_game_setting.json").exists()){
            publicGameSetting = GsonUtils.getPublicGameSettingFromFile(AppManifest.SETTING_DIR + "/public_game_setting.json");
        }
        else {
            String currentVersion;
            if (SettingUtils.getLocalVersionNames(activity.launcherSetting.gameFileDirectory).size() != 0){
                currentVersion = activity.launcherSetting.gameFileDirectory + "/versions/" + SettingUtils.getLocalVersionNames(activity.launcherSetting.gameFileDirectory).get(0);
            }
            else {
                currentVersion = "";
            }
            Account account = new Account(0,"","","","","","","","","","","");
            publicGameSetting = new PublicGameSetting(account,AppManifest.DEBUG_DIR,currentVersion);
            GsonUtils.savePublicGameSetting(publicGameSetting,AppManifest.SETTING_DIR + "/public_game_setting.json");
        }
        return publicGameSetting;
    }

    public static PrivateGameSetting initializePrivateGameSetting(Context context){
        PrivateGameSetting privateGameSetting;
        if (new File(AppManifest.SETTING_DIR + "/private_game_setting.json").exists()){
            privateGameSetting = GsonUtils.getPrivateGameSettingFromFile(AppManifest.SETTING_DIR + "/private_game_setting.json");
        }
        else {
            int ram = MemoryUtils.findBestRAMAllocation(context);
            privateGameSetting = new PrivateGameSetting(false,false,false,false,false,false,false,new JavaSetting(true,AppManifest.JAVA_DIR + "/default"),"","","",new GameDirSetting(0,AppManifest.DEFAULT_GAME_DIR),new BoatLauncherSetting(true,"GL4ES115","default"),new PojavLauncherSetting(false,"opengles2","default"),new RamSetting(ram,ram,true),"Default",1.0F);
            GsonUtils.savePrivateGameSetting(privateGameSetting,AppManifest.SETTING_DIR + "/private_game_setting.json");
        }
        return privateGameSetting;
    }
}
