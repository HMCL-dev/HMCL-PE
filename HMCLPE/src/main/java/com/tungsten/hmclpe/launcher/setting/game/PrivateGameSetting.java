package com.tungsten.hmclpe.launcher.setting.game;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.launcher.setting.game.child.BoatLauncherSetting;
import com.tungsten.hmclpe.launcher.setting.game.child.GameDirSetting;
import com.tungsten.hmclpe.launcher.setting.game.child.JavaSetting;
import com.tungsten.hmclpe.launcher.setting.game.child.PojavLauncherSetting;
import com.tungsten.hmclpe.launcher.setting.game.child.RamSetting;

import java.util.ArrayList;

public class PrivateGameSetting implements Cloneable{

    public boolean forceEnable;
    public boolean enable;
    public boolean log;
    public boolean notCheckJvm;
    public boolean notCheckMinecraft;
    public boolean notCheckForge;
    public boolean touchInjector;
    public JavaSetting javaSetting;
    public String extraJavaFlags;
    public String extraMinecraftFlags;
    public String server;
    public GameDirSetting gameDirSetting;
    public BoatLauncherSetting boatLauncherSetting;
    public PojavLauncherSetting pojavLauncherSetting;
    public RamSetting ramSetting;
    public String controlLayout;
    public float scaleFactor;

    public PrivateGameSetting (boolean forceEnable,boolean enable,boolean log,boolean notCheckJvm,boolean notCheckMinecraft,boolean notCheckForge,boolean touchInjector,JavaSetting javaSetting,String extraJavaFlags,String extraMinecraftFlags,String server,GameDirSetting gameDirSetting,BoatLauncherSetting boatLauncherSetting,PojavLauncherSetting pojavLauncherSetting,RamSetting ramSetting,String controlLayout,float scaleFactor){
        this.forceEnable = forceEnable;
        this.enable = enable;
        this.log = log;
        this.notCheckJvm = notCheckJvm;
        this.notCheckMinecraft = notCheckMinecraft;
        this.notCheckForge = notCheckForge;
        this.touchInjector = touchInjector;
        this.javaSetting = javaSetting;
        this.extraJavaFlags = extraJavaFlags;
        this.extraMinecraftFlags = extraMinecraftFlags;
        this.server = server;
        this.gameDirSetting = gameDirSetting;
        this.boatLauncherSetting = boatLauncherSetting;
        this.pojavLauncherSetting = pojavLauncherSetting;
        this.ramSetting = ramSetting;
        this.controlLayout = controlLayout;
        this.scaleFactor = scaleFactor;
    }

    public static String getGameDir(String gameFileDir,String currentVersion,GameDirSetting gameDirSetting) {
        if (gameDirSetting.type == 0) {
            return gameFileDir;
        }
        else if (gameDirSetting.type == 1) {
            return currentVersion;
        }
        else {
            return gameDirSetting.path;
        }
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        PrivateGameSetting privateGameSetting = (PrivateGameSetting) super.clone();
        privateGameSetting.javaSetting = (JavaSetting) javaSetting.clone();
        privateGameSetting.gameDirSetting = (GameDirSetting) gameDirSetting.clone();
        privateGameSetting.boatLauncherSetting = (BoatLauncherSetting) boatLauncherSetting.clone();
        privateGameSetting.pojavLauncherSetting = (PojavLauncherSetting) pojavLauncherSetting.clone();
        privateGameSetting.ramSetting = (RamSetting) ramSetting.clone();
        return privateGameSetting;
    }
}
