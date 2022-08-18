package com.tungsten.hmclpe.launcher.download.game;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.AssetIndex;
import com.tungsten.hmclpe.launcher.game.AssetObject;
import com.tungsten.hmclpe.launcher.game.Library;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.launcher.uis.game.download.DownloadUrlSource;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.DownloadUtil;
import com.tungsten.hmclpe.utils.io.NetworkUtils;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.io.IOException;
import java.util.ArrayList;

public class MinecraftInstallTask extends AsyncTask<VersionManifest.Version,Integer, Version> {

    private MainActivity activity;
    private String name;
    private DownloadTaskListAdapter adapter;
    private InstallMinecraftCallback callback;

    private DownloadTaskListBean priBean;
    private DownloadTaskListBean secBean;
    private DownloadTaskListBean thiBean;

    public MinecraftInstallTask(MainActivity activity, String name, DownloadTaskListAdapter adapter, InstallMinecraftCallback callback) {
        this.activity = activity;
        this.name = name;
        this.adapter = adapter;
        this.callback = callback;

        this.priBean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_install_game),"","","");
        this.secBean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_install_game_libs),"","","");
        this.thiBean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_install_game_assets),"","","");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
    }

    @Override
    protected Version doInBackground(VersionManifest.Version... versions) {
        VersionManifest.Version gameVersion = versions[0];

        activity.runOnUiThread(() -> {
            onProgressUpdate(0);
        });

        String versionJsonUrl = DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.VERSION_JSON) + gameVersion.url.replace("https://launchermeta.mojang.com", "").replace("https://piston-meta.mojang.com", "");

        String versionJson = null;
        try {
            versionJson = NetworkUtils.doGet(NetworkUtils.toURL(versionJsonUrl));
        } catch (IOException e) {
            e.printStackTrace();
            if (!isCancelled()) callback.onFailed(e);
            cancel(true);
        }
        Gson gson = JsonUtils.defaultGsonBuilder()
                .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                .registerTypeAdapter(Bits.class, new Bits.Serializer())
                .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                .create();
        Version rawPatch = gson.fromJson(versionJson, Version.class);

        String assetIndexUrl = DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.ASSETS_INDEX_JSON) + rawPatch.getAssetIndex().getUrl().replace("https://launchermeta.mojang.com","").replace("https://piston-meta.mojang.com", "");
        String assetIndexJson = null;
        try {
            assetIndexJson = NetworkUtils.doGet(NetworkUtils.toURL(assetIndexUrl));
        } catch (IOException e) {
            e.printStackTrace();
            if (!isCancelled()) callback.onFailed(e);
            cancel(true);
        }
        AssetIndex assetIndex = gson.fromJson(assetIndexJson,AssetIndex.class);

        ArrayList<DownloadTaskListBean> libList = new ArrayList<>();
        libList.add(new DownloadTaskListBean(name + ".jar",
                DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.VERSION_JAR) + rawPatch.getDownloadInfo().getUrl().replace("https://launcher.mojang.com","").replace("https://piston-data.mojang.com", ""),
                activity.launcherSetting.gameFileDirectory + "/versions/" + name + "/" + name + ".jar",
                rawPatch.getDownloadInfo().getSha1()));
        for (Library library : rawPatch.getLibraries()){
            if (!library.getPath().contains("tv/twitch") && !library.getPath().contains("lwjgl-platform-2.9.1-nightly")) {
                DownloadTaskListBean bean = new DownloadTaskListBean(library.getArtifactFileName(),
                        DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.LIBRARIES) + "/" + library.getPath(),
                        activity.launcherSetting.gameFileDirectory + "/libraries/" +library.getPath(),
                        library.getDownload().getSha1());
                libList.add(bean);
            }
        }

        ArrayList<DownloadTaskListBean> assetsList = new ArrayList<>();
        assetsList.add(new DownloadTaskListBean(rawPatch.getAssetIndex().id + ".json",
                assetIndexUrl,
                activity.launcherSetting.gameFileDirectory + "/assets/indexes/" + rawPatch.getAssetIndex().id + ".json",
                rawPatch.getAssetIndex().getSha1()));
        for (AssetObject object : assetIndex.getObjects().values()){
            DownloadTaskListBean bean = new DownloadTaskListBean(object.getHash(),
                    DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.ASSETS_OBJ) + "/" + object.getLocation(),
                    activity.launcherSetting.gameFileDirectory + "/assets/objects/" + object.getLocation(),
                    object.getHash());
            assetsList.add(bean);
        }

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
                if (!isCancelled()) callback.onFailed(e);
                cancel(true);
            }
        };

        activity.runOnUiThread(() -> {
            onProgressUpdate(1);
        });
        ArrayList<DownloadTaskListBean> failedLibs = DownloadUtil.downloadMultipleFiles(libList, maxDownloadTask, this, activity, downloadCallback);
        if (failedLibs.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The following files failed to download:");
            for (DownloadTaskListBean bean : failedLibs) {
                stringBuilder.append("\n\n  ").append(bean.name).append("\n\n").append(bean.url);
            }
            Exception e = new Exception(stringBuilder.toString());
            e.printStackTrace();
            if (!isCancelled()) callback.onFailed(e);
            cancel(true);
        }

        activity.runOnUiThread(() -> {
            onProgressUpdate(2);
        });
        ArrayList<DownloadTaskListBean> failedAssets = DownloadUtil.downloadMultipleFiles(assetsList, maxDownloadTask, this, activity, downloadCallback);
        if (failedAssets.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The following files failed to download:");
            for (DownloadTaskListBean bean : failedAssets) {
                stringBuilder.append("\n\n  ").append(bean.name);
            }
            Exception e = new Exception(stringBuilder.toString());
            e.printStackTrace();
            if (!isCancelled()) callback.onFailed(e);
            cancel(true);
        }

        return rawPatch.addPatch(rawPatch.setId("game").setVersion(rawPatch.getId()).setPriority(0));
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        switch (values[0]) {
            case 0:
                if (!isCancelled()) adapter.addDownloadTask(priBean);
                break;
            case 1:
                if (!isCancelled()) adapter.onComplete(priBean);
                if (!isCancelled()) adapter.addDownloadTask(secBean);
                break;
            case 2:
                if (!isCancelled()) adapter.onComplete(secBean);
                if (!isCancelled()) adapter.addDownloadTask(thiBean);
                break;
        }
    }

    @Override
    protected void onPostExecute(Version version) {
        super.onPostExecute(version);
        if (!isCancelled()) adapter.onComplete(thiBean);
        callback.onFinish(version);
    }

    public interface InstallMinecraftCallback{
        void onStart();
        void onFailed(Exception e);
        void onFinish(Version version);
    }
}
