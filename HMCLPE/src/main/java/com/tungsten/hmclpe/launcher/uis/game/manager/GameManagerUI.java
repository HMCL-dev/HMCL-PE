package com.tungsten.hmclpe.launcher.uis.game.manager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileBrowser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.CopyVersionDialog;
import com.tungsten.hmclpe.launcher.dialogs.RenameVersionDialog;
import com.tungsten.hmclpe.launcher.download.AssetsUpdateDialog;
import com.tungsten.hmclpe.launcher.launch.check.LaunchTools;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.io.File;
import java.util.ArrayList;

public class GameManagerUI extends BaseUI implements View.OnClickListener {

    public LinearLayout gameManagerUI;
    public GameManagerUIManager gameManagerUIManager;
    public String versionPath;
    public String versionName;

    public LinearLayout startGameSetting;
    public LinearLayout startModManager;
    public LinearLayout startAutoInstall;
    public LinearLayout startWorldManager;

    private LinearLayout testGame;
    private LinearLayout browse;
    private LinearLayout manage;

    public GameManagerUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        gameManagerUI = activity.findViewById(R.id.ui_game_manager);

        startGameSetting = activity.findViewById(R.id.game_manager_game_setting);
        startModManager = activity.findViewById(R.id.game_manager_manage_mod);
        startAutoInstall = activity.findViewById(R.id.game_manager_auto_install);
        startWorldManager = activity.findViewById(R.id.game_manager_world);

        testGame = activity.findViewById(R.id.game_manager_test_game);
        browse = activity.findViewById(R.id.game_manager_browse);
        manage = activity.findViewById(R.id.game_manager_manage);

        startGameSetting.setOnClickListener(this);
        startModManager.setOnClickListener(this);
        startAutoInstall.setOnClickListener(this);
        startWorldManager.setOnClickListener(this);

        testGame.setOnClickListener(this);
        browse.setOnClickListener(this);
        manage.setOnClickListener(this);

        gameManagerUIManager = new GameManagerUIManager(context,activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.game_manager_ui_title) + " - " + versionName,activity.uiManager.uis.get(activity.uiManager.uis.size() - 2) != activity.uiManager.mainUI,false);
        CustomAnimationUtils.showViewFromLeft(gameManagerUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(gameManagerUI,activity,context,true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        gameManagerUIManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onPause() {
        super.onPause();
        gameManagerUIManager.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        gameManagerUIManager.onResume();
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        if (v == startGameSetting){
            gameManagerUIManager.switchGameManagerUIs(gameManagerUIManager.versionSettingUI);
        }
        if (v == startModManager){
            gameManagerUIManager.switchGameManagerUIs(gameManagerUIManager.modManagerUI);
        }
        if (v == startAutoInstall){
            gameManagerUIManager.switchGameManagerUIs(gameManagerUIManager.autoInstallUI);
        }
        if (v == startWorldManager){
            gameManagerUIManager.switchGameManagerUIs(gameManagerUIManager.worldManagerUI);
        }
        if (v == testGame){
            testGame(versionName);
        }
        if (v == browse){
            Context wrapper = new ContextThemeWrapper(context, R.style.MenuStyle);
            @SuppressLint("RtlHardcoded") PopupMenu menu = new PopupMenu(wrapper, browse, Gravity.RIGHT);
            menu.inflate(R.menu.browse_menu);
            menu.setForceShowIcon(true);
            menu.setOnMenuItemClickListener(item -> {
                PrivateGameSetting privateGameSetting;
                String settingPath = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg";
                if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null && (GsonUtils.getPrivateGameSettingFromFile(settingPath).forceEnable || GsonUtils.getPrivateGameSettingFromFile(settingPath).enable)) {
                    privateGameSetting = GsonUtils.getPrivateGameSettingFromFile(settingPath);
                }
                else {
                    privateGameSetting = activity.privateGameSetting;
                }
                String gameDir;
                if (privateGameSetting.gameDirSetting.type == 0){
                    gameDir = activity.launcherSetting.gameFileDirectory;
                }
                else if (privateGameSetting.gameDirSetting.type == 1){
                    gameDir = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName;
                }
                else {
                    gameDir = privateGameSetting.gameDirSetting.path;
                }
                Intent intent = new Intent(context, FileBrowser.class);
                switch (item.getItemId()){
                    case R.id.browse_game_dir:
                        intent.putExtra(Constants.INITIAL_DIRECTORY, gameDir);
                        context.startActivity(intent);
                        return true;
                    case R.id.browse_mod_dir:
                        FileUtils.createDirectory(gameDir + "/mods");
                        intent.putExtra(Constants.INITIAL_DIRECTORY, gameDir + "/mods");
                        context.startActivity(intent);
                        return true;
                    case R.id.browse_setting_dir:
                        FileUtils.createDirectory(gameDir + "/config");
                        intent.putExtra(Constants.INITIAL_DIRECTORY, gameDir + "/config");
                        context.startActivity(intent);
                        return true;
                    case R.id.browse_resource_dir:
                        FileUtils.createDirectory(gameDir + "/resourcepacks");
                        intent.putExtra(Constants.INITIAL_DIRECTORY, gameDir + "/resourcepacks");
                        context.startActivity(intent);
                        return true;
                    case R.id.browse_screenshots_dir:
                        FileUtils.createDirectory(gameDir + "/screenshots");
                        intent.putExtra(Constants.INITIAL_DIRECTORY, gameDir + "/screenshots");
                        context.startActivity(intent);
                        return true;
                    case R.id.browse_save_dir:
                        FileUtils.createDirectory(gameDir + "/saves");
                        intent.putExtra(Constants.INITIAL_DIRECTORY, gameDir + "/saves");
                        context.startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            });
            menu.show();
        }
        if (v == manage){
            Context wrapper = new ContextThemeWrapper(context, R.style.MenuStyle);
            @SuppressLint("RtlHardcoded") PopupMenu menu = new PopupMenu(wrapper, manage, Gravity.RIGHT);
            menu.inflate(R.menu.manage_menu);
            menu.setForceShowIcon(true);
            menu.setOnMenuItemClickListener(item -> {
                PrivateGameSetting privateGameSetting;
                String settingPath = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg";
                if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null && (GsonUtils.getPrivateGameSettingFromFile(settingPath).forceEnable || GsonUtils.getPrivateGameSettingFromFile(settingPath).enable)) {
                    privateGameSetting = GsonUtils.getPrivateGameSettingFromFile(settingPath);
                }
                else {
                    privateGameSetting = activity.privateGameSetting;
                }
                String gameDir;
                if (privateGameSetting.gameDirSetting.type == 0){
                    gameDir = activity.launcherSetting.gameFileDirectory;
                }
                else if (privateGameSetting.gameDirSetting.type == 1){
                    gameDir = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName;
                }
                else {
                    gameDir = privateGameSetting.gameDirSetting.path;
                }
                switch (item.getItemId()){
                    case R.id.manage_test_game:
                        testGame(versionName);
                        return true;
                    case R.id.manage_rename:
                        @SuppressLint("SetTextI18n") RenameVersionDialog dialog = new RenameVersionDialog(context, versionName, name -> new Thread(() -> {
                            FileUtils.rename(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/" + versionName + ".jar",name + ".jar");
                            FileUtils.rename(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/" + versionName + ".json",name + ".json");
                            FileUtils.rename(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName,name);
                            if (versionName.equals(activity.publicGameSetting.currentVersion.substring(activity.publicGameSetting.currentVersion.lastIndexOf("/") + 1))) {
                                activity.publicGameSetting.currentVersion = activity.launcherSetting.gameFileDirectory + "/versions/" + name;
                                GsonUtils.savePublicGameSetting(activity.publicGameSetting,AppManifest.SETTING_DIR + "/public_game_setting.json");
                            }
                            versionName = name;
                            activity.runOnUiThread(() -> {
                                activity.currentUIText.setText(context.getResources().getString(R.string.game_manager_ui_title) + " - " + name);
                                init();
                            });
                            activity.uiManager.versionListUI.refreshVersionList();
                        }).start());
                        dialog.show();
                        return true;
                    case R.id.manage_copy:
                        CopyVersionDialog copyVersionDialog = new CopyVersionDialog(context, activity.uiManager.versionListUI.gameList,privateGameSetting,gameDir, activity.launcherSetting.gameFileDirectory + "/versions/", versionName, () -> {
                            new Thread(() -> {
                                activity.uiManager.versionListUI.refreshVersionList();
                            }).start();
                        });
                        copyVersionDialog.show();
                        return true;
                    case R.id.manage_delete_version:
                        AlertDialog.Builder deleteAlertBuilder = new AlertDialog.Builder(context);
                        deleteAlertBuilder.setTitle(context.getString(R.string.dialog_delete_version_title));
                        deleteAlertBuilder.setPositiveButton(context.getString(R.string.dialog_delete_version_positive), (dialogInterface, i) -> {
                            new Thread(() -> {
                                FileUtils.deleteDirectory(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName);
                                activity.uiManager.versionListUI.refreshVersionList();
                                if (activity.publicGameSetting.currentVersion.equals(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName) && activity.uiManager.versionListUI.gameList.size() > 0) {
                                    activity.publicGameSetting.currentVersion = activity.launcherSetting.gameFileDirectory + "/versions/" + activity.uiManager.versionListUI.gameList.get(0).name;
                                    GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                                }
                                activity.runOnUiThread(() -> {
                                    activity.uiManager.uis = new ArrayList<>();
                                    activity.uiManager.uis.add(activity.uiManager.mainUI);
                                    activity.uiManager.uis.add(activity.uiManager.versionListUI);
                                    activity.uiManager.gameManagerUI.onStop();
                                    activity.uiManager.versionListUI.onStart();
                                });
                            }).start();
                        });
                        deleteAlertBuilder.setNegativeButton(context.getString(R.string.dialog_delete_version_negative), (dialogInterface, i) -> {});
                        if (privateGameSetting.gameDirSetting.type == 1){
                            deleteAlertBuilder.setMessage(context.getString(R.string.dialog_delete_version_isolate_msg).replace("%s", versionName));
                        }
                        else {
                            deleteAlertBuilder.setMessage(context.getString(R.string.dialog_delete_version_msg).replace("%s", versionName));
                        }
                        deleteAlertBuilder.create().show();
                        return true;
                    case R.id.manage_export_package:
                        activity.uiManager.switchMainUI(activity.uiManager.exportPackageTypeUI);
                        return true;
                    case R.id.manage_update_assets:
                        AssetsUpdateDialog assetsUpdateDialog = new AssetsUpdateDialog(context,activity,versionName);
                        assetsUpdateDialog.show();
                        return true;
                    case R.id.manage_delete_libs:
                        new Thread(() -> {
                            FileUtils.deleteDirectory(activity.launcherSetting.gameFileDirectory + "/libraries");
                        }).start();
                        return true;
                    case R.id.manage_clear_logs:
                        new Thread(() -> {
                            FileUtils.deleteDirectory(gameDir + "/logs");
                            FileUtils.deleteDirectory(gameDir + "/crash-reports");
                        }).start();
                        return true;
                    default:
                        return false;
                }
            });
            menu.show();
        }
    }

    private void testGame(String name) {
        String settingPath = activity.launcherSetting.gameFileDirectory + "/versions/" + name + "/hmclpe.cfg";
        String finalPath;
        if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null && (GsonUtils.getPrivateGameSettingFromFile(settingPath).forceEnable || GsonUtils.getPrivateGameSettingFromFile(settingPath).enable)) {
            finalPath = settingPath;
        }
        else {
            finalPath = AppManifest.SETTING_DIR + "/private_game_setting.json";
        }
        Bundle bundle = new Bundle();
        bundle.putString("setting_path",finalPath);
        bundle.putBoolean("test",true);
        bundle.putString("version",activity.launcherSetting.gameFileDirectory + "/versions/" + name);
        LaunchTools.launch(context,activity,activity.launcherSetting.gameFileDirectory + "/versions/" + name,bundle);
    }

    private void init(){
        String newVersionPath = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName;
        gameManagerUIManager.modManagerUI.refresh(versionName);
        gameManagerUIManager.worldManagerUI.refresh(versionName);
        if (!newVersionPath.equals(versionPath)) {
            gameManagerUIManager.versionSettingUI.refresh(versionName);
            gameManagerUIManager.autoInstallUI.refresh(versionName);
            versionPath = newVersionPath;
            Log.e("gameManager","refresh!");
        }
    }
}
