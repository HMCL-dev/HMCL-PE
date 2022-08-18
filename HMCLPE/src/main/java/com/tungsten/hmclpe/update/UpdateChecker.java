package com.tungsten.hmclpe.update;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;

import com.google.gson.Gson;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.utils.io.NetworkUtils;

import java.io.IOException;

public class UpdateChecker {

    public static final String UPDATE_URL = "http://101.43.66.4/launcherInfo.json";

    private Context context;
    private MainActivity activity;

    private boolean isChecking;

    private Handler handler;

    public UpdateChecker (Context context, MainActivity activity) {
        this.context = context;
        this.activity = activity;
        handler = new Handler();
    }

    public void check (boolean getBetaVersion,UpdateCallback callback) {
        if (!isChecking) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (callback != null) {
                            handler.post(callback::onCheck);
                        }
                        isChecking = true;
                        String updateJson = NetworkUtils.doGet(NetworkUtils.toURL(UPDATE_URL));
                        UpdateJSON json = new Gson().fromJson(updateJson,UpdateJSON.class);
                        LauncherVersion latest;
                        boolean isBeta;
                        if (getBetaVersion) {
                            LauncherVersion betaVersion = json.latestPrerelease;
                            LauncherVersion releaseVersion = json.latestRelease;
                            latest = betaVersion.versionCode > releaseVersion.versionCode ? betaVersion : releaseVersion;
                            isBeta = betaVersion.versionCode > releaseVersion.versionCode;
                        }
                        else {
                            latest = json.latestRelease;
                            isBeta = false;
                        }
                        if (latest.versionCode > getPackageVersionCode()) {
                            showUpdateDialog(latest,isBeta);
                            if (callback != null) {
                                handler.post(() -> {
                                    callback.onFinish(false);
                                });
                            }
                        }
                        else {
                            if (callback != null) {
                                handler.post(() -> {
                                    callback.onFinish(true);
                                });
                            }
                        }
                        isChecking = false;
                    } catch (IOException e) {
                        e.printStackTrace();
                        if (callback != null) {
                            handler.post(() -> {
                                callback.onFinish(true);
                            });
                        }
                        isChecking = false;
                    }
                }
            }).start();
        }
    }

    private int getPackageVersionCode() {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            int versionCode = packageInfo.versionCode;
            return versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public void showUpdateDialog (LauncherVersion version,boolean isBeta) {
        handler.post(() -> {
            UpdateDialog dialog = new UpdateDialog(context,activity,version,isBeta);
            dialog.show();
        });
    }

    public interface UpdateCallback{
        void onCheck();
        void onFinish(boolean latest);
    }

}
