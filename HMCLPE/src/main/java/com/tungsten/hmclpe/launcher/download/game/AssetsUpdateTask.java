package com.tungsten.hmclpe.launcher.download.game;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.AssetIndex;
import com.tungsten.hmclpe.launcher.game.AssetObject;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.launcher.uis.game.download.DownloadUrlSource;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.DownloadUtil;
import com.tungsten.hmclpe.utils.io.NetworkUtils;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class AssetsUpdateTask extends AsyncTask<String,Integer, Exception> {

    private MainActivity activity;
    private DownloadTaskListAdapter adapter;
    private AssetsUpdateCallback callback;

    private DownloadTaskListBean bean;

    public AssetsUpdateTask (MainActivity activity,DownloadTaskListAdapter adapter, AssetsUpdateCallback callback) {
        this.activity = activity;
        this.adapter = adapter;
        this.callback = callback;

        this.bean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_assets_check),"","","");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
        adapter.addDownloadTask(bean);
    }

    @Override
    protected Exception doInBackground(String... strings) {
        String name = strings[0];
        String versionJson = FileStringUtils.getStringFromFile(activity.launcherSetting.gameFileDirectory + "/versions/" + name + "/" + name + ".json");
        Gson gson = JsonUtils.defaultGsonBuilder()
                .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                .registerTypeAdapter(Bits.class, new Bits.Serializer())
                .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                .create();
        Version version = gson.fromJson(versionJson, Version.class);
        String assetIndexString;
        ArrayList<DownloadTaskListBean> list = new ArrayList<>();
        if (isRightFile(activity.launcherSetting.gameFileDirectory + "/assets/indexes/" + version.getAssetIndex().id + ".json",version.getAssetIndex().getSha1())) {
            assetIndexString = FileStringUtils.getStringFromFile(activity.launcherSetting.gameFileDirectory + "/assets/indexes/" + version.getAssetIndex().id + ".json");
        }
        else {
            String assetIndexUrl = DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.ASSETS_INDEX_JSON) + version.getAssetIndex().getUrl().replace("https://launchermeta.mojang.com","").replace("https://piston-meta.mojang.com", "");
            try {
                assetIndexString = NetworkUtils.doGet(NetworkUtils.toURL(assetIndexUrl));
                list.add(new DownloadTaskListBean(version.getAssetIndex().id + ".json",
                        assetIndexUrl,
                        activity.launcherSetting.gameFileDirectory + "/assets/indexes/" + version.getAssetIndex().id + ".json",
                        version.getAssetIndex().getSha1()));
            } catch (IOException e) {
                e.printStackTrace();
                return new Exception("Failed to download asset_index.json");
            }
        }
        AssetIndex assetIndex = gson.fromJson(assetIndexString,AssetIndex.class);
        int current = 0;
        int total = assetIndex.getObjects().values().size();
        for (AssetObject object : assetIndex.getObjects().values()) {
            if (!isRightFile(activity.launcherSetting.gameFileDirectory + "/assets/objects/" + object.getLocation(),object.getHash())) {
                list.add(new DownloadTaskListBean(object.getHash(),
                        DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.ASSETS_OBJ) + "/" + object.getLocation(),
                        activity.launcherSetting.gameFileDirectory + "/assets/objects/" + object.getLocation(),
                        object.getHash()));
            }
            current++;
            bean.progress = (100 * current) / total;
            activity.runOnUiThread(() -> {
                adapter.onProgress(bean);
            });
        }
        activity.runOnUiThread(() -> {
            adapter.onComplete(bean);
        });
        if (list.size() == 0) {
            return null;
        }
        else {
            int maxDownloadTask = activity.launcherSetting.maxDownloadTask;
            if (activity.launcherSetting.autoDownloadTaskQuantity) {
                maxDownloadTask = 64;
            }

            DownloadUtil.DownloadMultipleFilesCallback downloadCallback = new DownloadUtil.DownloadMultipleFilesCallback() {
                @Override
                public void onTaskStart(DownloadTaskListBean bean) {
                    if (!isCancelled()) adapter.addDownloadTask(bean);
                }

                @Override
                public void onTaskProgress(DownloadTaskListBean bean) {
                    if (!isCancelled()) adapter.onProgress(bean);
                }

                @Override
                public void onTaskFinish(DownloadTaskListBean bean) {
                    if (!isCancelled()) adapter.onComplete(bean);
                }

                @Override
                public void onFailed(Exception e) {
                    e.printStackTrace();
                    if (!isCancelled()) callback.onFinish(e);
                    cancel(true);
                }
            };

            ArrayList<DownloadTaskListBean> failedAssets = DownloadUtil.downloadMultipleFiles(list, maxDownloadTask, this, activity, downloadCallback);
            if (failedAssets.size() > 0) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("The following files failed to download:");
                for (DownloadTaskListBean bean : failedAssets) {
                    stringBuilder.append("\n\n  ").append(bean.name);
                }
                Exception e = new Exception(stringBuilder.toString());
                e.printStackTrace();
                if (!isCancelled()) return e;
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        if (!isCancelled()) {
            callback.onFinish(e);
        }
    }

    public static boolean isRightFile(String path,String sha1) {
        if (new File(path).exists()) {
            if (sha1 != null && !sha1.equals("")) {
                return Objects.equals(FileUtils.getFileSha1(path), sha1);
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }

    public interface AssetsUpdateCallback{
        void onStart();
        void onFinish(Exception e);
    }
}
