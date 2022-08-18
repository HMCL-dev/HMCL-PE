package com.tungsten.hmclpe.launcher.setting.launcher;

import com.tungsten.hmclpe.launcher.setting.launcher.child.BackgroundSetting;
import com.tungsten.hmclpe.launcher.setting.launcher.child.SourceSetting;

public class LauncherSetting {

    public String gameFileDirectory;
    public SourceSetting downloadUrlSource;
    public int language;
    public int maxDownloadTask;
    public boolean autoDownloadTaskQuantity;
    public boolean autoCheckUpdate;
    public boolean getBetaVersion;
    public boolean fullscreen;
    public boolean transBar;
    public String launcherTheme;
    public BackgroundSetting launcherBackground;
    public String cachePath;

    public LauncherSetting(String gameFileDirectory,SourceSetting downloadUrlSource,int language,int maxDownloadTask,boolean autoDownloadTaskQuantity,boolean autoCheckUpdate,boolean getBetaVersion,boolean fullscreen,boolean transBar,String launcherTheme,BackgroundSetting launcherBackground,String cachePath){
        this.gameFileDirectory = gameFileDirectory;
        this.downloadUrlSource = downloadUrlSource;
        this.language = language;
        this.maxDownloadTask = maxDownloadTask;
        this.autoDownloadTaskQuantity = autoDownloadTaskQuantity;
        this.autoCheckUpdate = autoCheckUpdate;
        this.getBetaVersion = getBetaVersion;
        this.fullscreen = fullscreen;
        this.transBar = transBar;
        this.launcherTheme = launcherTheme;
        this.launcherBackground = launcherBackground;
        this.cachePath = cachePath;
    }

}
