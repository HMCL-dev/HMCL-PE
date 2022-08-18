package com.tungsten.hmclpe.launcher.uis.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.launch.check.LaunchTools;
import com.tungsten.hmclpe.launcher.list.local.game.GameListBean;
import com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher.ExteriorSettingUI;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.InitializeSetting;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.launcher.view.spinner.VersionSpinnerAdapter;
import com.tungsten.hmclpe.skin.utils.Avatar;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.DrawableUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.io.FileUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainUI extends BaseUI implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    public LinearLayout mainUI;

    private LinearLayout startAccountUI;
    private LinearLayout startGameManagerUI;
    private LinearLayout startVersionListUI;
    private LinearLayout startDownloadUI;
    private LinearLayout startMultiPlayerUI;
    private LinearLayout startSettingUI;

    private LinearLayout startGame;
    private TextView launchVersionText;

    public ImageView accountSkinFace;
    public ImageView accountSkinHat;
    public TextView accountName;
    public TextView accountType;

    private ImageView versionIcon;
    private LinearLayout noVersionAlert;
    private TextView currentVersionText;

    private VersionSpinnerAdapter versionSpinnerAdapter;

    private ImageView versionListIcon;
    private ImageView downloadIcon;
    private ImageView multiplayerIcon;
    private ImageView settingIcon;

    public MainUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mainUI = activity.findViewById(R.id.ui_main);

        startAccountUI = activity.findViewById(R.id.start_ui_account);
        startGameManagerUI = activity.findViewById(R.id.start_ui_game_manager);
        startVersionListUI = activity.findViewById(R.id.start_ui_version_list);
        startDownloadUI = activity.findViewById(R.id.start_ui_download);
        startMultiPlayerUI = activity.findViewById(R.id.start_ui_multi_player);
        startSettingUI = activity.findViewById(R.id.start_ui_setting);

        startGame = activity.findViewById(R.id.launcher_play_button);
        launchVersionText = activity.findViewById(R.id.launch_version_text);

        accountSkinFace = activity.findViewById(R.id.account_skin_face);
        accountSkinHat = activity.findViewById(R.id.account_skin_hat);
        accountName = activity.findViewById(R.id.account_name_text);
        accountType = activity.findViewById(R.id.account_state_text);

        versionIcon = activity.findViewById(R.id.current_version_icon);
        noVersionAlert = activity.findViewById(R.id.no_version_alert_text);
        currentVersionText = activity.findViewById(R.id.current_version_name_text);

        //icon
        versionListIcon = activity.findViewById(R.id.version_list_icon);
        downloadIcon = activity.findViewById(R.id.download_icon);
        multiplayerIcon = activity.findViewById(R.id.multiplayer_icon);
        settingIcon = activity.findViewById(R.id.setting_icon);

        startAccountUI.setOnClickListener(this);
        startGameManagerUI.setOnClickListener(this);
        startVersionListUI.setOnClickListener(this);
        startDownloadUI.setOnClickListener(this);
        startMultiPlayerUI.setOnClickListener(this);
        startSettingUI.setOnClickListener(this);

        startGame.setOnClickListener(this);
    }

    private AuthlibInjectorServer getServerFromUrl(String url){
        ArrayList<AuthlibInjectorServer> list = InitializeSetting.initializeAuthlibInjectorServer(context);
        for (int i = 0;i < list.size();i++){
            if (list.get(i).getUrl().equals(url)){
                return list.get(i);
            }
        }
        return null;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(mainUI,activity,context,true);
        activity.hideBarTitle();

        new Thread(() -> {
            ArrayList<GameListBean> gameList = SettingUtils.getLocalVersionInfo(activity.launcherSetting.gameFileDirectory,activity.publicGameSetting.currentVersion);
            activity.runOnUiThread(() -> {
                GameListBean currentVersion = new GameListBean("","","",true);
                if (!activity.publicGameSetting.currentVersion.equals("")){
                    for (int i = 0;i < gameList.size();i++) {
                        if (gameList.get(i).name.equals(activity.publicGameSetting.currentVersion.substring(activity.publicGameSetting.currentVersion.lastIndexOf("/") + 1))) {
                            currentVersion = gameList.get(i);
                        }
                    }
                }
                if (gameList.size() > 0 && currentVersion.name.equals("")) {
                    currentVersion = gameList.get(0);
                    activity.publicGameSetting.currentVersion = activity.launcherSetting.gameFileDirectory + "/versions/" + currentVersion.name;
                    GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                }
                versionSpinnerAdapter = new VersionSpinnerAdapter(context,gameList);
                Spinner gameVersionSpinner = activity.findViewById(R.id.launcher_spinner_version);
                gameVersionSpinner.setAdapter(versionSpinnerAdapter);
                gameVersionSpinner.setSelection(versionSpinnerAdapter.getPosition(currentVersion));
                gameVersionSpinner.setOnItemSelectedListener(this);
                if (!currentVersion.name.equals("")){
                    noVersionAlert.setVisibility(View.GONE);
                    currentVersionText.setVisibility(View.VISIBLE);
                    currentVersionText.setText(currentVersion.name);
                    launchVersionText.setText(currentVersion.name);
                    if (!currentVersion.iconPath.equals("") && new File(currentVersion.iconPath).exists()) {
                        versionIcon.setBackground(DrawableUtils.getDrawableFromFile(currentVersion.iconPath));
                    }
                    else {
                        if (!currentVersion.version.contains(",")) {
                            versionIcon.setBackground(context.getDrawable(R.drawable.ic_grass));
                        }
                        else {
                            versionIcon.setBackground(context.getDrawable(R.drawable.ic_furnace));
                        }
                    }
                }
                else {
                    noVersionAlert.setVisibility(View.VISIBLE);
                    currentVersionText.setVisibility(View.GONE);
                    launchVersionText.setText(context.getString(R.string.launcher_button_current_version));
                    versionIcon.setBackground(context.getDrawable(R.drawable.ic_grass));
                }
            });
        }).start();

        refreshAccount();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void refreshAccount() {
        switch (activity.publicGameSetting.account.loginType){
            case 1:
                accountName.setText(activity.publicGameSetting.account.auth_player_name);
                accountType.setText(context.getString(R.string.item_account_type_offline));
                Avatar.setAvatar(activity.publicGameSetting.account.texture, accountSkinFace, accountSkinHat);
                break;
            case 2:
                accountName.setText(activity.publicGameSetting.account.auth_player_name);
                accountType.setText(context.getString(R.string.item_account_type_mojang));
                Avatar.setAvatar(activity.publicGameSetting.account.texture, accountSkinFace, accountSkinHat);
                break;
            case 3:
                accountName.setText(activity.publicGameSetting.account.auth_player_name);
                accountType.setText(context.getString(R.string.item_account_type_microsoft));
                Avatar.setAvatar(activity.publicGameSetting.account.texture, accountSkinFace, accountSkinHat);
                break;
            case 4:
            case 5:
                accountName.setText(activity.publicGameSetting.account.auth_player_name);
                accountType.setText(getServerFromUrl(activity.publicGameSetting.account.loginServer).getName());
                Avatar.setAvatar(activity.publicGameSetting.account.texture, accountSkinFace, accountSkinHat);
                break;
            default:
                accountName.setText(context.getString(R.string.launcher_scroll_account_name));
                accountType.setText(context.getString(R.string.launcher_scroll_account_state));
                accountSkinFace.setImageBitmap(((BitmapDrawable) context.getDrawable(R.drawable.ic_steve)).getBitmap());
                accountSkinHat.setImageBitmap(null);
                break;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(mainUI,activity,context,true);
    }

    @Override
    public void onClick(View v) {
        if (v == startAccountUI){
            activity.uiManager.switchMainUI(activity.uiManager.accountUI);
        }
        if (v == startGameManagerUI){
            if (noVersionAlert.getVisibility() == View.VISIBLE){
                activity.uiManager.switchMainUI(activity.uiManager.versionListUI);
            }
            else {
                activity.uiManager.gameManagerUI.versionName = activity.publicGameSetting.currentVersion.substring(activity.publicGameSetting.currentVersion.lastIndexOf("/") + 1);
                activity.uiManager.switchMainUI(activity.uiManager.gameManagerUI);
            }
        }
        if (v == startVersionListUI){
            activity.uiManager.switchMainUI(activity.uiManager.versionListUI);
        }
        if (v == startDownloadUI){
            activity.uiManager.switchMainUI(activity.uiManager.downloadUI);
        }
        if (v == startMultiPlayerUI){
            activity.uiManager.switchMainUI(activity.uiManager.multiPlayerUI);
        }
        if (v == startSettingUI){
            activity.uiManager.switchMainUI(activity.uiManager.settingUI);
        }
        if (v == startGame){
            String settingPath = activity.publicGameSetting.currentVersion + "/hmclpe.cfg";
            String finalPath;
            if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null && (GsonUtils.getPrivateGameSettingFromFile(settingPath).forceEnable || GsonUtils.getPrivateGameSettingFromFile(settingPath).enable)) {
                finalPath = settingPath;
            }
            else {
                finalPath = AppManifest.SETTING_DIR + "/private_game_setting.json";
            }
            Bundle bundle = new Bundle();
            bundle.putString("setting_path",finalPath);
            bundle.putBoolean("test",false);
            LaunchTools.launch(context,activity,activity.publicGameSetting.currentVersion,bundle);
        }
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        activity.publicGameSetting.currentVersion = activity.launcherSetting.gameFileDirectory + "/versions/" + ((GameListBean) versionSpinnerAdapter.getItem(position)).name;
        if (activity.privateGameSetting.gameDirSetting.type == 1){
            activity.uiManager.settingUI.settingUIManager.universalGameSettingUI.gameDirText.setText(activity.launcherSetting.gameFileDirectory + "/versions/" + ((GameListBean) versionSpinnerAdapter.getItem(position)).name);
        }
        GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
        currentVersionText.setText(((GameListBean) versionSpinnerAdapter.getItem(position)).name);
        launchVersionText.setText(((GameListBean) versionSpinnerAdapter.getItem(position)).name);
        if (!((GameListBean) versionSpinnerAdapter.getItem(position)).iconPath.equals("") && new File(((GameListBean) versionSpinnerAdapter.getItem(position)).iconPath).exists()) {
            versionIcon.setBackground(DrawableUtils.getDrawableFromFile(((GameListBean) versionSpinnerAdapter.getItem(position)).iconPath));
        }
        else {
            if (!((GameListBean) versionSpinnerAdapter.getItem(position)).version.contains(",")) {
                versionIcon.setBackground(context.getDrawable(R.drawable.ic_grass));
            }
            else {
                versionIcon.setBackground(context.getDrawable(R.drawable.ic_furnace));
            }
        }
        changeIcon(versionIcon,themePath,"versionIcon");
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
    File themePath;
    public void customTheme(){
        themePath = activity.getExternalFilesDir("Theme");

        if (!themePath.exists()){
            return;
        }
        changeIcon(versionListIcon, themePath, "versionListIcon");
        changeIcon(downloadIcon, themePath, "downloadIcon");
        changeIcon(multiplayerIcon, themePath, "multiplayerIcon");
        changeIcon(settingIcon, themePath, "settingIcon");
        changeIcon(activity.launcherLayout, themePath, "background");
        changeIcon(versionIcon, themePath, "versionIcon");
        if (new File(themePath,"color.json").exists()) {
            try {
                JSONObject jsonObject = new JSONObject(FileUtils.readText(new File(themePath,"color.json")));
                activity.exteriorConfig.primaryColor(Color.parseColor(jsonObject.getString("primaryColor")));
                activity.exteriorConfig.accentColor(Color.parseColor(jsonObject.getString("accentColor")));
                activity.exteriorConfig.apply(activity);
                activity.appBar.setBackgroundColor(Color.parseColor(jsonObject.getString("primaryColor")));
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(activity, e.toString(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void changeIcon(View view, File themePath, String iconName) {
        File path = new File(themePath, iconName + ".png");
        if (path.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(path.getAbsolutePath());
            view.setBackground(new BitmapDrawable(activity.getResources(), bitmap));
        }
    }
}
