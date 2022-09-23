package com.tungsten.hmclpe.launcher.uis.tools;

import android.content.Context;
import android.content.Intent;

import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.uis.account.AccountUI;
import com.tungsten.hmclpe.launcher.uis.game.download.DownloadUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.game.DownloadFabricAPIUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.game.DownloadFabricUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.game.DownloadForgeUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.game.DownloadLiteLoaderUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.game.DownloadOptifineUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.game.DownloadQuiltAPIUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.game.DownloadQuiltUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.game.InstallGameUI;
import com.tungsten.hmclpe.launcher.uis.game.download.right.resource.BaseDownloadUI;
import com.tungsten.hmclpe.launcher.uis.game.manager.GameManagerUI;
import com.tungsten.hmclpe.launcher.uis.game.manager.universal.ExportWorldUI;
import com.tungsten.hmclpe.launcher.uis.game.manager.universal.ModUpdateUI;
import com.tungsten.hmclpe.launcher.uis.game.manager.universal.PackMcManagerUI;
import com.tungsten.hmclpe.launcher.uis.game.version.VersionListUI;
import com.tungsten.hmclpe.launcher.uis.game.version.universal.AddGameDirectoryUI;
import com.tungsten.hmclpe.launcher.uis.game.version.universal.ExportPackageFileUI;
import com.tungsten.hmclpe.launcher.uis.game.version.universal.ExportPackageInfoUI;
import com.tungsten.hmclpe.launcher.uis.game.version.universal.ExportPackageTypeUI;
import com.tungsten.hmclpe.launcher.uis.game.version.universal.InstallPackageUI;
import com.tungsten.hmclpe.launcher.uis.main.MainUI;
import com.tungsten.hmclpe.launcher.uis.universal.multiplayer.MultiPlayerUI;
import com.tungsten.hmclpe.launcher.uis.universal.setting.SettingUI;

import java.util.ArrayList;

public class UIManager {

    public MainUI mainUI;
    public AccountUI accountUI;
    public GameManagerUI gameManagerUI;
    public VersionListUI versionListUI;
    public DownloadUI downloadUI;
    public MultiPlayerUI multiPlayerUI;
    public SettingUI settingUI;

    public ModUpdateUI modUpdateUI;
    public PackMcManagerUI packMcManagerUI;
    public ExportWorldUI exportWorldUI;
    public AddGameDirectoryUI addGameDirectoryUI;
    public InstallPackageUI installPackageUI;
    public ExportPackageTypeUI exportPackageTypeUI;
    public ExportPackageInfoUI exportPackageInfoUI;
    public ExportPackageFileUI exportPackageFileUI;

    public InstallGameUI installGameUI;
    public DownloadForgeUI downloadForgeUI;
    public DownloadFabricUI downloadFabricUI;
    public DownloadFabricAPIUI downloadFabricAPIUI;
    public DownloadLiteLoaderUI downloadLiteLoaderUI;
    public DownloadOptifineUI downloadOptifineUI;
    public DownloadQuiltUI downloadQuiltUI;
    public DownloadQuiltAPIUI downloadQuiltAPIUI;

    public BaseUI[] mainUIs;
    public ArrayList<BaseUI> uis;
    public BaseUI currentUI;

    public UIManager (Context context, MainActivity activity){
        mainUI = new MainUI(context, activity);
        accountUI = new AccountUI(context, activity);
        gameManagerUI = new GameManagerUI(context, activity);
        versionListUI = new VersionListUI(context, activity);
        downloadUI = new DownloadUI(context, activity);
        multiPlayerUI = new MultiPlayerUI(context, activity);
        settingUI = new SettingUI(context, activity);

        modUpdateUI = new ModUpdateUI(context, activity);
        packMcManagerUI = new PackMcManagerUI(context, activity);
        exportWorldUI = new ExportWorldUI(context, activity);
        addGameDirectoryUI = new AddGameDirectoryUI(context, activity);
        installPackageUI = new InstallPackageUI(context, activity);
        exportPackageTypeUI = new ExportPackageTypeUI(context, activity);
        exportPackageInfoUI = new ExportPackageInfoUI(context, activity);
        exportPackageFileUI = new ExportPackageFileUI(context, activity);

        installGameUI = new InstallGameUI(context, activity);
        downloadForgeUI = new DownloadForgeUI(context, activity);
        downloadFabricUI = new DownloadFabricUI(context, activity);
        downloadFabricAPIUI = new DownloadFabricAPIUI(context, activity);
        downloadLiteLoaderUI = new DownloadLiteLoaderUI(context, activity);
        downloadOptifineUI = new DownloadOptifineUI(context, activity);
        downloadQuiltUI = new DownloadQuiltUI(context, activity);
        downloadQuiltAPIUI = new DownloadQuiltAPIUI(context, activity);

        mainUI.onCreate();
        accountUI.onCreate();
        gameManagerUI.onCreate();
        versionListUI.onCreate();
        downloadUI.onCreate();
        multiPlayerUI.onCreate();
        settingUI.onCreate();

        modUpdateUI.onCreate();
        packMcManagerUI.onCreate();
        exportWorldUI.onCreate();
        addGameDirectoryUI.onCreate();
        installPackageUI.onCreate();
        exportPackageTypeUI.onCreate();
        exportPackageInfoUI.onCreate();
        exportPackageFileUI.onCreate();

        installGameUI.onCreate();
        downloadForgeUI.onCreate();
        downloadFabricUI.onCreate();
        downloadFabricAPIUI.onCreate();
        downloadLiteLoaderUI.onCreate();
        downloadOptifineUI.onCreate();
        downloadQuiltUI.onCreate();
        downloadQuiltAPIUI.onCreate();

        mainUIs = new BaseUI[] {
                mainUI,
                modUpdateUI,
                packMcManagerUI,
                exportWorldUI,
                addGameDirectoryUI,
                installPackageUI,
                exportPackageTypeUI,
                exportPackageInfoUI,
                exportPackageFileUI,
                accountUI,
                gameManagerUI,
                versionListUI,
                downloadUI,
                multiPlayerUI,
                settingUI,
                installGameUI,
                downloadForgeUI,
                downloadFabricUI,
                downloadLiteLoaderUI,
                downloadOptifineUI,
                downloadFabricAPIUI,
                downloadQuiltUI,
                downloadQuiltAPIUI
        };
        uis = new ArrayList<>();
        switchMainUI(mainUI);
    }

    public void switchMainUI(BaseUI ui) {
        currentUI = ui;
        uis.add(ui);
        ui.onStart();
        if (uis.size() > 1){
            uis.get(uis.size() - 2).onStop();
        }
        System.out.println("-----------------------------------------------------------------------------------------------------------------------------switch to new ui");
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (BaseUI ui : mainUIs) {
            ui.onActivityResult(requestCode,resultCode,data);
        }
        for (BaseUI ui : uis) {
            if (ui instanceof BaseDownloadUI) {
                ui.onActivityResult(requestCode,resultCode,data);
            }
        }
    }

    public void onPause() {
        for (BaseUI ui : mainUIs) {
            ui.onPause();
        }
        for (BaseUI ui : uis) {
            if (ui instanceof BaseDownloadUI) {
                ui.onPause();
            }
        }
    }

    public void onResume() {
        for (BaseUI ui : mainUIs) {
            ui.onResume();
        }
        for (BaseUI ui : uis) {
            if (ui instanceof BaseDownloadUI) {
                ui.onResume();
            }
        }
    }

    public void removeUIIfExist(BaseUI ui) {
        for (int i = 0;i < uis.size();i++){
            if (uis.get(i) == ui){
                uis.remove(i);
            }
        }
    }
}
