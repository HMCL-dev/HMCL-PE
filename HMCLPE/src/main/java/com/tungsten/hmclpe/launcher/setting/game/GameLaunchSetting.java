package com.tungsten.hmclpe.launcher.setting.game;

import com.google.gson.Gson;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.launch.LaunchVersion;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.launcher.LauncherSetting;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.io.File;

public class GameLaunchSetting {

    public Account account;
    public String home;
    public String currentVersion;

    public String javaPath;
    public String extraJavaFlags;
    public String extraMinecraftFlags;
    public String game_directory;
    public String boatRenderer;
    public String pojavRenderer;
    public boolean touchInjector;
    public float scaleFactor;
    public int minRam;
    public int maxRam;
    public String server;
    public String controlLayout;
    public boolean fullscreen;
    public boolean log;

    public String gameFileDirectory;

    public GameLaunchSetting(Account account,String home,String currentVersion,String javaPath,String extraJavaFlags,String extraMinecraftFlags,String game_directory,String boatRenderer,String pojavRenderer,boolean touchInjector,float scaleFactor,String gameFileDirectory,int minRam,int maxRam,String controlLayout,String server,boolean fullscreen,boolean log){
        this.account = account;
        this.home = home;
        this.currentVersion = currentVersion;

        this.javaPath = javaPath;
        this.extraJavaFlags = extraJavaFlags;
        this.extraMinecraftFlags = extraMinecraftFlags;
        this.game_directory = game_directory;
        this.boatRenderer = boatRenderer;
        this.pojavRenderer = pojavRenderer;
        this.touchInjector = touchInjector;
        this.scaleFactor = scaleFactor;
        this.minRam = minRam;
        this.maxRam = maxRam;
        this.server = server;
        this.controlLayout = controlLayout;
        this.fullscreen = fullscreen;
        this.log = log;

        this.gameFileDirectory = gameFileDirectory;
    }

    public static boolean isHighVersion(GameLaunchSetting gameLaunchSetting){
        LaunchVersion version = LaunchVersion.fromDirectory(new File(gameLaunchSetting.currentVersion));
        return version.minimumLauncherVersion >= 21;
    }

    public static GameLaunchSetting getGameLaunchSetting(String privatePath,String v){
        LauncherSetting launcherSetting = GsonUtils.getLauncherSettingFromFile(AppManifest.SETTING_DIR + "/launcher_setting.json");
        PublicGameSetting publicGameSetting = GsonUtils.getPublicGameSettingFromFile(AppManifest.SETTING_DIR + "/public_game_setting.json");
        PrivateGameSetting privateGameSetting = GsonUtils.getPrivateGameSettingFromFile(privatePath);

        String gameDir;
        if (privateGameSetting.gameDirSetting.type == 0){
            gameDir = launcherSetting.gameFileDirectory;
        }
        else if (privateGameSetting.gameDirSetting.type == 1){
            gameDir = (v == null || v.equals("")) ? publicGameSetting.currentVersion : v;
        }
        else {
            gameDir = privateGameSetting.gameDirSetting.path;
        }

        String javaPath;
        if (privateGameSetting.javaSetting.autoSelect){
            String versionJson = FileStringUtils.getStringFromFile(((v == null || v.equals("")) ? publicGameSetting.currentVersion : v) + "/" + (new File(((v == null || v.equals("")) ? publicGameSetting.currentVersion : v))).getName() + ".json");
            Gson gson = JsonUtils.defaultGsonBuilder()
                    .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                    .registerTypeAdapter(Bits.class, new Bits.Serializer())
                    .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                    .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                    .create();
            Version version = gson.fromJson(versionJson, Version.class);
            if (version.getJavaVersion() == null || version.getJavaVersion().getMajorVersion() == 8){
                javaPath = AppManifest.JAVA_DIR + "/default";
            }
            else {
                javaPath = AppManifest.JAVA_DIR + "/JRE17";
            }
        }
        else {
            javaPath = AppManifest.JAVA_DIR + "/" + privateGameSetting.javaSetting.name;
        }

        return new GameLaunchSetting(publicGameSetting.account,
                publicGameSetting.home,
                (v == null || v.equals("")) ? publicGameSetting.currentVersion : v,
                javaPath,
                privateGameSetting.extraJavaFlags,
                privateGameSetting.extraMinecraftFlags,
                gameDir,
                privateGameSetting.boatLauncherSetting.renderer,
                privateGameSetting.pojavLauncherSetting.renderer,
                privateGameSetting.touchInjector,
                privateGameSetting.scaleFactor,
                launcherSetting.gameFileDirectory,
                privateGameSetting.ramSetting.minRam,
                privateGameSetting.ramSetting.maxRam,
                privateGameSetting.controlLayout,
                privateGameSetting.server,
                launcherSetting.fullscreen,
                privateGameSetting.log);
    }

}
