package com.tungsten.hmclpe.launcher.download.quilt;

import android.os.AsyncTask;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.launcher.setting.game.PublicGameSetting;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.task.DownloadTask;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.io.DownloadUtil;

import java.io.IOException;

public class QuiltAPIInstallTask extends AsyncTask<RemoteMod.Version,Integer,Exception> {

    private MainActivity activity;
    private String name;
    private DownloadTaskListAdapter adapter;
    private InstallQuiltAPICallback callback;

    private DownloadTaskListBean bean;

    public QuiltAPIInstallTask(MainActivity activity, String name, DownloadTaskListAdapter adapter, InstallQuiltAPICallback callback) {
        this.activity = activity;
        this.name = name;
        this.adapter = adapter;
        this.callback = callback;

        this.bean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_install_quilt_api),"","","");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
        if (!isCancelled()) adapter.addDownloadTask(bean);
    }

    @Override
    protected Exception doInBackground(RemoteMod.Version... versions) {
        RemoteMod.Version quiltAPIVersion = versions[0];
        String path;
        if (PublicGameSetting.isUsingIsolateSetting(activity.launcherSetting.gameFileDirectory + "/versions/" + name)) {
            path = PrivateGameSetting.getGameDir(activity.launcherSetting.gameFileDirectory,activity.launcherSetting.gameFileDirectory + "/versions/" + name, GsonUtils.getPrivateGameSettingFromFile(activity.launcherSetting.gameFileDirectory + "/versions/" + name + "/hmclpe.cfg").gameDirSetting);
        }
        else {
            path = PrivateGameSetting.getGameDir(activity.launcherSetting.gameFileDirectory,activity.launcherSetting.gameFileDirectory + "/versions/" + name,GsonUtils.getPrivateGameSettingFromFile(AppManifest.SETTING_DIR + "/private_game_setting.json").gameDirSetting);
        }
        String fileName = "quilt-api-" + quiltAPIVersion.getVersion() + ".jar";
        String modPath = path + "/mods/" + fileName;
        String url = quiltAPIVersion.getFile().getUrl();
        DownloadTaskListBean bean = new DownloadTaskListBean(fileName, url, modPath, quiltAPIVersion.getFile().getHashes().get("sha1"));
        DownloadTask.DownloadFeedback feedback = new DownloadTask.DownloadFeedback() {
            @Override
            public void updateProgress(long curr, long max) {
                long progress = 100 * curr / max;
                bean.progress = (int) progress;
                activity.runOnUiThread(() -> {
                    if (!isCancelled()) adapter.onProgress(bean);
                });
            }

            @Override
            public void updateSpeed(String speed) {

            }
        };
        for (int i = 0;i < 5;i++) {
            try {
                activity.runOnUiThread(() -> {
                    if (!isCancelled()) adapter.addDownloadTask(bean);
                });
                if (DownloadUtil.downloadFile(url,modPath,null,feedback)) {
                    activity.runOnUiThread(() -> {
                        if (!isCancelled()) adapter.onComplete(bean);
                    });
                    if (!isCancelled()) return null;
                }
                else {
                    activity.runOnUiThread(() -> {
                        if (!isCancelled()) adapter.onComplete(bean);
                    });
                    if (i == 4) {
                        if (!isCancelled()) return new Exception("Failed to download " + fileName);
                    }
                }
            }
            catch (IOException e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> {
                    if (!isCancelled()) adapter.onComplete(bean);
                });
                if (i == 4) {
                    if (!isCancelled()) return e;
                }
            }
        }
        if (!isCancelled()) return new Exception("Unknown error");
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        adapter.onComplete(bean);
        callback.onFinish(e);
    }

    public interface InstallQuiltAPICallback{
        void onStart();
        void onFinish(Exception e);
    }
}
