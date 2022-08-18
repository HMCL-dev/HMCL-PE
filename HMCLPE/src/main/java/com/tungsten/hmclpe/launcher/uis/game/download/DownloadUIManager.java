package com.tungsten.hmclpe.launcher.uis.game.download;

import android.content.Context;
import android.content.Intent;

import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.uis.game.download.right.DownloadMinecraftUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.DownloadModUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.DownloadPackageUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.DownloadResourcePackUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.DownloadWorldUI;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;

public class DownloadUIManager {

    public DownloadModUI downloadModUI;
    public DownloadPackageUI downloadPackageUI;
    public DownloadResourcePackUI downloadResourcePackUI;
    public DownloadWorldUI downloadWorldUI;
    public DownloadMinecraftUI downloadMinecraftUI;

    public BaseUI[] downloadUIs;

    public DownloadUIManager (Context context, MainActivity activity){
        downloadMinecraftUI = new DownloadMinecraftUI(context,activity);
        downloadModUI = new DownloadModUI(context,activity);
        downloadPackageUI = new DownloadPackageUI(context,activity);
        downloadResourcePackUI = new DownloadResourcePackUI(context,activity);
        downloadWorldUI = new DownloadWorldUI(context,activity);

        downloadMinecraftUI.onCreate();
        downloadModUI.onCreate();
        downloadPackageUI.onCreate();
        downloadResourcePackUI.onCreate();
        downloadWorldUI.onCreate();

        downloadUIs = new BaseUI[]{downloadMinecraftUI,downloadModUI,downloadPackageUI,downloadResourcePackUI,downloadWorldUI};
        switchDownloadUI(downloadMinecraftUI);
    }

    public void switchDownloadUI(BaseUI ui){
        for (int i = 0;i < downloadUIs.length;i++){
            if (downloadUIs[i] == ui){
                downloadUIs[i].onStart();
            }
            else {
                downloadUIs[i].onStop();
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        for (BaseUI ui : downloadUIs){
            ui.onActivityResult(requestCode,resultCode,data);
        }
    }

    public void onPause(){
        for (BaseUI ui : downloadUIs){
            ui.onPause();
        }
    }

    public void onResume(){
        for (BaseUI ui : downloadUIs){
            ui.onResume();
        }
    }
}
