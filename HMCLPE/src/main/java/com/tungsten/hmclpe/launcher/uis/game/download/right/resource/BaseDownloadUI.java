package com.tungsten.hmclpe.launcher.uis.game.download.right.resource;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.mod.RemoteModRepository;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.string.ModTranslations;

public class BaseDownloadUI extends BaseUI {

    public RemoteModRepository repository;
    public RemoteMod bean;
    public ModTranslations.Mod modTranslation;
    public int resourceType;
    public boolean isFirst = true;

    public LinearLayout baseDownloadUI;

    public BaseDownloadUI(Context context, MainActivity activity, RemoteModRepository repository, RemoteMod bean, int resourceType) {
        super(context, activity);
        this.repository = repository;
        this.bean = bean;
        ModTranslations modTranslations;
        if (resourceType == 0) {
            modTranslations = ModTranslations.MOD;
        }
        else if (resourceType == 1) {
            modTranslations = ModTranslations.MODPACK;
        }
        else {
            modTranslations = ModTranslations.EMPTY;
        }
        this.modTranslation = modTranslations.getModByCurseForgeId(bean.getSlug());
        this.resourceType = resourceType;
        onCreate();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        baseDownloadUI = (LinearLayout) LayoutInflater.from(context).inflate(R.layout.ui_download_resource,null);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.uiContainer.addView(baseDownloadUI);
        ViewGroup.LayoutParams layoutParams = baseDownloadUI.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        baseDownloadUI.setLayoutParams(layoutParams);
        activity.showBarTitle(bean.getTitle(),activity.uiManager.uis.get(activity.uiManager.uis.size() - 2) != activity.uiManager.mainUI,false);
        CustomAnimationUtils.showViewFromLeft(baseDownloadUI,activity,context,true);
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(baseDownloadUI,activity,context,true);
        activity.uiContainer.removeView(baseDownloadUI);
    }

    public <T> T findViewById(int id) {
        return (T) baseDownloadUI.findViewById(id);
    }

}
