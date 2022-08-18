package com.tungsten.hmclpe.launcher.uis.universal.setting;

import android.content.Context;
import android.content.Intent;

import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.launcher.uis.universal.setting.right.UniversalGameSettingUI;
import com.tungsten.hmclpe.launcher.uis.universal.setting.right.help.AboutUsUI;
import com.tungsten.hmclpe.launcher.uis.universal.setting.right.help.DonateUI;
import com.tungsten.hmclpe.launcher.uis.universal.setting.right.help.FeedbackUI;
import com.tungsten.hmclpe.launcher.uis.universal.setting.right.help.HelpUI;
import com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher.DownloadSettingUI;
import com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher.ExteriorSettingUI;
import com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher.UniversalSettingUI;

public class SettingUIManager {

    public UniversalGameSettingUI universalGameSettingUI;
    public DownloadSettingUI downloadSettingUI;
    public ExteriorSettingUI exteriorSettingUI;
    public UniversalSettingUI universalSettingUI;
    public HelpUI helpUI;
    public FeedbackUI feedbackUI;
    public DonateUI donateUI;
    public AboutUsUI aboutUsUI;

    public BaseUI[] settingUIs;

    public SettingUIManager (Context context, MainActivity activity){
        universalGameSettingUI = new UniversalGameSettingUI(context,activity);
        downloadSettingUI = new DownloadSettingUI(context,activity);
        exteriorSettingUI = new ExteriorSettingUI(context,activity);
        universalSettingUI = new UniversalSettingUI(context,activity);
        helpUI = new HelpUI(context,activity);
        feedbackUI = new FeedbackUI(context,activity);
        donateUI = new DonateUI(context,activity);
        aboutUsUI = new AboutUsUI(context,activity);

        universalGameSettingUI.onCreate();
        downloadSettingUI.onCreate();
        exteriorSettingUI.onCreate();
        universalSettingUI.onCreate();
        helpUI.onCreate();
        feedbackUI.onCreate();
        donateUI.onCreate();
        aboutUsUI.onCreate();

        settingUIs = new BaseUI[]{universalGameSettingUI,universalSettingUI,downloadSettingUI,exteriorSettingUI,helpUI,feedbackUI,donateUI,aboutUsUI};
        switchSettingUIs(universalGameSettingUI);
    }

    public void switchSettingUIs(BaseUI ui){
        for (int i = 0;i < settingUIs.length;i++){
            if (settingUIs[i] == ui){
                settingUIs[i].onStart();
            }
            else {
                settingUIs[i].onStop();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        for (BaseUI ui : settingUIs){
            ui.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void onPause(){
        for (BaseUI ui : settingUIs){
            ui.onPause();
        }
    }

    public void onResume(){
        for (BaseUI ui : settingUIs){
            ui.onResume();
        }
    }
}
