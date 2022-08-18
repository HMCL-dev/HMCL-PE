package com.tungsten.hmclpe.launcher.download.optifine;

import android.os.AsyncTask;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.task.DownloadTask;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.io.DownloadUtil;

import java.io.IOException;

public class OptifineDownloadTask extends AsyncTask<OptifineVersion,Integer,Exception> {

    private MainActivity activity;
    private DownloadTaskListAdapter adapter;
    private DownloadOptifineCallback callback;

    private DownloadTaskListBean bean;

    public OptifineDownloadTask (MainActivity activity,DownloadTaskListAdapter adapter, DownloadOptifineCallback callback) {
        this.activity = activity;
        this.adapter = adapter;
        this.callback = callback;

        this.bean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_download_optifine),"","","");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
        if (!isCancelled()) adapter.addDownloadTask(bean);
    }

    @Override
    protected Exception doInBackground(OptifineVersion... optifineVersions) {
        OptifineVersion optifineVersion = optifineVersions[0];
        String start = "https://bmclapi2.bangbang93.com";
        String mirror = start + "/optifine/" + optifineVersion.mcVersion + "/" + optifineVersion.type + "/" + optifineVersion.patch;
        String path = AppManifest.INSTALL_DIR + "/optifine/" + optifineVersion.fileName;
        FileUtils.deleteDirectory(AppManifest.INSTALL_DIR);
        DownloadTaskListBean bean = new DownloadTaskListBean(optifineVersion.fileName, mirror,path,null);
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
                if (DownloadUtil.downloadFile(mirror,path,null,feedback)) {
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
                        if (!isCancelled()) return new Exception("Failed to download " + optifineVersion.fileName);
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

    public interface DownloadOptifineCallback{
        void onStart();
        void onFinish(Exception e);
    }
}
