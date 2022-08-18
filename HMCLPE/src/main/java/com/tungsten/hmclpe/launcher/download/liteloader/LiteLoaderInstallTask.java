package com.tungsten.hmclpe.launcher.download.liteloader;

import android.os.AsyncTask;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.LibraryAnalyzer;
import com.tungsten.hmclpe.launcher.game.Arguments;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.Library;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.launcher.uis.game.download.DownloadUrlSource;
import com.tungsten.hmclpe.utils.Lang;
import com.tungsten.hmclpe.utils.io.DownloadUtil;

import java.util.ArrayList;
import java.util.Collections;

public class LiteLoaderInstallTask extends AsyncTask<LiteLoaderVersion,Integer, Version> {

    private MainActivity activity;
    private DownloadTaskListAdapter adapter;
    private InstallLiteLoaderCallback callback;

    private DownloadTaskListBean bean;
    
    public LiteLoaderInstallTask(MainActivity activity, DownloadTaskListAdapter adapter, InstallLiteLoaderCallback callback) {
        this.activity = activity;
        this.adapter = adapter;
        this.callback = callback;

        this.bean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_install_lite_loader),"","","");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
        if (!isCancelled()) adapter.addDownloadTask(bean);
    }

    @Override
    protected Version doInBackground(LiteLoaderVersion... liteLoaderVersions) {
        LiteLoaderVersion liteLoaderVersion = liteLoaderVersions[0];
        Library library = new Library(
                new Artifact("com.mumfrey", "liteloader", liteLoaderVersion.getVersion()),
                "http://dl.liteloader.com/versions/"
        );
        Version rawPatch = new Version(LibraryAnalyzer.LibraryType.LITELOADER.getPatchId(),
                liteLoaderVersion.getVersion(),
                60000,
                new Arguments().addGameArguments("--tweakClass", "com.mumfrey.liteloader.launch.LiteLoaderTweaker"),
                LibraryAnalyzer.LAUNCH_WRAPPER_MAIN,
                Lang.merge(liteLoaderVersion.getLibraries(), Collections.singleton(library)))
                .setLogging(Collections.emptyMap());
        ArrayList<DownloadTaskListBean> list = new ArrayList<>();
        String head;
        for (Library lib : rawPatch.getLibraries()){
            String url;
            if (lib.getDownload().getUrl() == null || lib.getDownload().getUrl().equals("") || lib.getDownload().getUrl().startsWith("https://libraries.minecraft.net")) {
                if (DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource) == 1) {
                    head = "https://bmclapi2.bangbang93.com/maven/";
                }
                else {
                    head = "https://download.mcbbs.net/maven/";
                }
                url = head + lib.getPath();
            }
            else if (lib.getDownload().getUrl().startsWith("http://dl.liteloader.com/versions/")) {
                url = "https://bmclapi2.bangbang93.com/liteloader/download?version=" + liteLoaderVersion.getVersion();
            }
            else {
                url = lib.getDownload().getUrl();
            }
            DownloadTaskListBean bean = new DownloadTaskListBean(lib.getArtifactFileName(),
                    url,
                    activity.launcherSetting.gameFileDirectory + "/libraries/" + lib.getPath(),
                    lib.getDownload().getSha1());
            list.add(bean);
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

        ArrayList<DownloadTaskListBean> failedFiles = DownloadUtil.downloadMultipleFiles(list, maxDownloadTask, this, activity, downloadCallback);
        if (failedFiles.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("The following files failed to download:");
            for (DownloadTaskListBean bean : failedFiles) {
                stringBuilder.append("\n\n  ").append(bean.name);
            }
            Exception e = new Exception(stringBuilder.toString());
            e.printStackTrace();
            if (!isCancelled()) callback.onFailed(e);
            cancel(true);
        }
        return rawPatch;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Version version) {
        super.onPostExecute(version);
        adapter.onComplete(bean);
        callback.onFinish(version);
    }

    public interface InstallLiteLoaderCallback{
        void onStart();
        void onFailed(Exception e);
        void onFinish(Version version);
    }
}
