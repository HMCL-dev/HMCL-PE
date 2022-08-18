package com.tungsten.hmclpe.launcher.uis.universal.setting;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

public class SettingUI extends BaseUI implements View.OnClickListener {

    public LinearLayout settingUI;

    public SettingUIManager settingUIManager;

    public LinearLayout startGlobalGameSettingUI;
    public LinearLayout startUniversalSettingUI;
    public LinearLayout startExteriorSettingUI;
    public LinearLayout startDownloadSettingUI;
    public LinearLayout startHelpUI;
    public LinearLayout startFeedbackUI;
    public LinearLayout startDonateUI;
    public LinearLayout startAboutUsUI;

    public SettingUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        settingUI = activity.findViewById(R.id.ui_setting);

        startGlobalGameSettingUI = activity.findViewById(R.id.start_global_game_setting_ui);
        startUniversalSettingUI = activity.findViewById(R.id.start_universal_setting_ui);
        startExteriorSettingUI = activity.findViewById(R.id.start_exterior_setting_ui);
        startDownloadSettingUI = activity.findViewById(R.id.start_download_setting_ui);
        startHelpUI = activity.findViewById(R.id.start_help_ui);
        startFeedbackUI = activity.findViewById(R.id.start_feedback_ui);
        startDonateUI = activity.findViewById(R.id.start_donate_ui);
        startAboutUsUI = activity.findViewById(R.id.start_about_ui);

        startGlobalGameSettingUI.setOnClickListener(this);
        startUniversalSettingUI.setOnClickListener(this);
        startExteriorSettingUI.setOnClickListener(this);
        startDownloadSettingUI.setOnClickListener(this);
        startHelpUI.setOnClickListener(this);
        startFeedbackUI.setOnClickListener(this);
        startDonateUI.setOnClickListener(this);
        startAboutUsUI.setOnClickListener(this);

        settingUIManager = new SettingUIManager(context,activity);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.setting_ui_title),activity.uiManager.uis.get(activity.uiManager.uis.size() - 2) != activity.uiManager.mainUI,false);
        CustomAnimationUtils.showViewFromLeft(settingUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(settingUI,activity,context,true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        settingUIManager.onActivityResult(requestCode,resultCode,data);
    }

    @Override
    public void onPause() {
        super.onPause();
        settingUIManager.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        settingUIManager.onResume();
    }

    @Override
    public void onClick(View v) {
        if (v == startGlobalGameSettingUI){
            settingUIManager.switchSettingUIs(settingUIManager.universalGameSettingUI);
        }
        if (v == startUniversalSettingUI){
            settingUIManager.switchSettingUIs(settingUIManager.universalSettingUI);
        }
        if (v == startExteriorSettingUI){
            settingUIManager.switchSettingUIs(settingUIManager.exteriorSettingUI);
        }
        if (v == startDownloadSettingUI){
            settingUIManager.switchSettingUIs(settingUIManager.downloadSettingUI);
        }
        if (v == startHelpUI){
            settingUIManager.switchSettingUIs(settingUIManager.helpUI);
        }
        if (v == startFeedbackUI){
            settingUIManager.switchSettingUIs(settingUIManager.feedbackUI);
        }
        if (v == startDonateUI){
            settingUIManager.switchSettingUIs(settingUIManager.donateUI);
        }
        if (v == startAboutUsUI){
            settingUIManager.switchSettingUIs(settingUIManager.aboutUsUI);
        }
    }

    private void init(){
        settingUIManager.universalGameSettingUI.refresh();
    }
}
