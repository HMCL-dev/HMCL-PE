package com.tungsten.hmclpe.launcher.launch.check;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.io.File;

public class CheckJavaTask extends AsyncTask<Object,Integer,Exception> {

    private final MainActivity activity;
    private final String launchVersion;
    private final CheckJavaCallback callback;

    public CheckJavaTask (MainActivity activity,String launchVersion,CheckJavaCallback callback) {
        this.activity = activity;
        this.launchVersion = launchVersion;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
    }

    @Override
    protected Exception doInBackground(Object... objects) {
        try {
            PrivateGameSetting privateGameSetting;
            String settingPath = launchVersion + "/hmclpe.cfg";
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
                gameDir = launchVersion;
            }
            else {
                gameDir = privateGameSetting.gameDirSetting.path;
            }
            if (!privateGameSetting.notCheckForge) {
                FileStringUtils.writeFile(gameDir + "/config/splash.properties","enabled=false");
            }
            if (privateGameSetting.notCheckJvm) {
                return null;
            }
            int expectedJava;
            int java;
            String versionJson = FileStringUtils.getStringFromFile(launchVersion + "/" + new File(launchVersion).getName() + ".json");
            Gson gson = JsonUtils.defaultGsonBuilder()
                    .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                    .registerTypeAdapter(Bits.class, new Bits.Serializer())
                    .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                    .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                    .create();
            Version version = gson.fromJson(versionJson, Version.class);
            expectedJava = version.getMinimumLauncherVersion() < 9 ? 8 : (version.getJavaVersion() == null ? 8 : version.getJavaVersion().getMajorVersion());
            if (privateGameSetting.javaSetting.autoSelect) {
                java = expectedJava;
                if (expectedJava == 16) {
                    java = 17;
                }
            }
            else {
                java = privateGameSetting.javaSetting.name.equals("default") ? 8 : 17;
            }
            if (java == expectedJava || (java == 17 && expectedJava == 16)) {
                return null;
            }
            else {
                return new Exception(activity.getString(R.string.launch_check_dialog_exception_error_java) + " -- java" + java);
            }
        }
        catch (Exception e) {
            return e;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        callback.onFinish(e);
    }

    public interface CheckJavaCallback{
        void onStart();
        void onFinish(Exception e);
    }
}
