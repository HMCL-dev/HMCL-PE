package com.tungsten.hmclpe.launcher.download.forge;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.gson.Gson;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.ApiService;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.Library;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.launcher.uis.game.download.DownloadUrlSource;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.DownloadUtil;
import com.tungsten.hmclpe.utils.io.SocketServer;
import com.tungsten.hmclpe.utils.io.ZipTools;
import com.tungsten.hmclpe.utils.platform.Bits;
import com.tungsten.hmclpe.utils.string.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class ForgeInstallTask extends AsyncTask<ForgeVersion,Integer, Version> {

    private MainActivity activity;
    private String name;
    private DownloadTaskListAdapter adapter;
    private InstallForgeCallback callback;

    private DownloadTaskListBean bean;

    private ArrayList<String> args;

    private SocketServer server;
    private SocketServer progressServer;

    boolean canceled = false;

    public ForgeInstallTask (MainActivity activity, String name, DownloadTaskListAdapter adapter, InstallForgeCallback callback) {
        this.activity = activity;
        this.name = name;
        this.adapter = adapter;
        this.callback = callback;

        this.bean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_install_forge),"","","");
    }

    public void cancelBuild() {
        canceled = true;
        if (server != null) {
            server.stop();
        }
        if (progressServer != null) {
            progressServer.stop();
        }
        Intent service = new Intent(activity, ApiService.class);
        activity.stopService(service);
    }

    public boolean isNewInstaller() {
        try {
            String string = FileStringUtils.getStringFromFile(AppManifest.INSTALL_DIR + "/forge/installer/install_profile.json");
            Map<?, ?> installProfile = JsonUtils.fromNonNullJson(string, Map.class);
            if (installProfile.containsKey("spec")) {
                return true;
            } else if (installProfile.containsKey("install") && installProfile.containsKey("versionInfo")) {
                return false;
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            if (!isCancelled()) callback.onFailed(e);
            cancel(true);
        }
        return new File(AppManifest.INSTALL_DIR + "/forge/installer/version.json").exists();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
    }

    @Override
    protected Version doInBackground(ForgeVersion... forgeVersions) {
        ForgeVersion forgeVersion = forgeVersions[0];
        activity.runOnUiThread(() -> {
            adapter.addDownloadTask(bean);
        });
        try {
            ZipTools.unzipFile(AppManifest.INSTALL_DIR + "/forge/forge-installer.jar", AppManifest.INSTALL_DIR + "/forge/installer/",false);
        } catch (Exception e) {
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
        ArrayList<DownloadTaskListBean> list = new ArrayList<>();
        Version patch;
        ForgeInstallProfile oldProfile = null;
        boolean isNew = isNewInstaller();
        if (isNew) {
            String vString = FileStringUtils.getStringFromFile(AppManifest.INSTALL_DIR + "/forge/installer/version.json");
            String iString = FileStringUtils.getStringFromFile(AppManifest.INSTALL_DIR + "/forge/installer/install_profile.json");
            patch = gson.fromJson(vString,Version.class);
            ForgeNewInstallProfile installProfile = gson.fromJson(iString,ForgeNewInstallProfile.class);
            for (Library library : patch.getLibraries()){
                String url;
                if (DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource) == 0 && library.getDownload().getUrl() != null && !library.getDownload().getUrl().equals("")) {
                    url = library.getDownload().getUrl();
                }
                else {
                    url = DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.FORGE_LIBRARIES) + "/" + library.getPath();
                }
                DownloadTaskListBean bean = new DownloadTaskListBean(library.getArtifactFileName(),
                        url,
                        activity.launcherSetting.gameFileDirectory + "/libraries/" + library.getPath(),
                        library.getDownload().getSha1());
                if (library.hasDownloadURL()) {
                    list.add(bean);
                }
            }
            for (Library library : installProfile.getLibraries()){
                String url;
                if (DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource) == 0 && library.getDownload().getUrl() != null && !library.getDownload().getUrl().equals("")) {
                    url = library.getDownload().getUrl();
                }
                else {
                    url = DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.FORGE_LIBRARIES) + "/" + library.getPath();
                }
                DownloadTaskListBean bean = new DownloadTaskListBean(library.getArtifactFileName(),
                        url,
                        activity.launcherSetting.gameFileDirectory + "/libraries/" +library.getPath(),
                        library.getDownload().getSha1());
                if (library.hasDownloadURL()) {
                    list.add(bean);
                }
            }
        }
        else {
            String iString = FileStringUtils.getStringFromFile(AppManifest.INSTALL_DIR + "/forge/installer/install_profile.json");
            ForgeInstallProfile installProfile = gson.fromJson(iString,ForgeInstallProfile.class);
            oldProfile = installProfile;
            patch = installProfile.getVersionInfo();
            for (Library library : patch.getLibraries()){
                if (!library.getPath().equals(installProfile.getInstall().getPath().getPath()) && !library.getPath().contains("lwjgl-platform-2.9.1-nightly-20130708-debug3")) {
                    String url;
                    if (DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource) == 0) {
                        url = library.getDownload().getUrl();
                    }
                    else {
                        url = DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.FORGE_LIBRARIES) + "/" + library.getPath();
                    }
                    DownloadTaskListBean bean = new DownloadTaskListBean(library.getArtifactFileName(),
                            url,
                            activity.launcherSetting.gameFileDirectory + "/libraries/" + library.getPath(),
                            library.getDownload().getSha1());
                    if (library.hasDownloadURL()) {
                        list.add(bean);
                    }
                }
            }
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
        if (failedFiles.size() == 0) {
            if (isNew) {
                String javaPath = AppManifest.JAVA_DIR + "/default";
                args = new ArrayList<>();
                args.add(javaPath + "/bin/java");
                args.add("-Djava.io.tmpdir=" + AppManifest.DEFAULT_CACHE_DIR);
                args.add("-Dos.name=Linux");
                args.add("-Djava.library.path=" + javaPath + "/lib/aarch64/jli:" + javaPath + "/lib/aarch64");
                args.add("-classpath");
                args.add(".:" + AppManifest.PLUGIN_DIR + "/installer/forge-install-bootstrapper.jar:" + AppManifest.INSTALL_DIR + "/forge/forge-installer.jar");
                args.add("com.bangbang93.ForgeInstaller");
                args.add(activity.launcherSetting.gameFileDirectory);
                args.add("-Xms1024M");
                args.add("-Xmx1024M");
                return patch.setId("forge").setVersion(forgeVersion.getVersion()).setPriority(30000);
            }
            else {
                String universalName = oldProfile.getInstall().getFilePath();
                FileUtils.createDirectory(new File(activity.launcherSetting.gameFileDirectory + "/libraries/" + oldProfile.getInstall().getPath().getPath()).getParent());
                if (FileUtils.copyFile(AppManifest.INSTALL_DIR + "/forge/installer/" + universalName,activity.launcherSetting.gameFileDirectory + "/libraries/" + oldProfile.getInstall().getPath().getPath())) {
                    return patch.setId("forge").setVersion(forgeVersion.getVersion()).setPriority(30000);
                }
                else {
                    Exception e = new Exception("Failed to copy universal jar");
                    e.printStackTrace();
                    if (!isCancelled()) callback.onFailed(e);
                    cancel(true);
                }
            }
        }
        else {
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
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (server != null) {
            server.stop();
        }
        if (progressServer != null) {
            progressServer.stop();
        }
        Intent service = new Intent(activity, ApiService.class);
        activity.stopService(service);
    }

    @Override
    protected void onPostExecute(Version version) {
        super.onPostExecute(version);
        if (version == null) {
            callback.onFailed(new Exception("Unknown error"));
        }
        else {
            if (args == null) {
                adapter.onComplete(bean);
                callback.onFinish(version);
            }
            else {
                DownloadTaskListBean buildBean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_install_forge_build),"","","");
                Intent service = new Intent(activity, ApiService.class);
                Bundle bundle = new Bundle();
                bundle.putStringArrayList("commands",args);
                service.putExtras(bundle);
                progressServer = new SocketServer("127.0.0.1", 6666, (server1, msg) -> {
                    buildBean.progress = (int) (Float.parseFloat(msg) * 100);
                    adapter.onProgress(buildBean);
                });
                progressServer.start();
                server = new SocketServer("127.0.0.1", ApiService.API_SERVICE_PORT, (server1, msg) -> {
                    adapter.onComplete(buildBean);
                    adapter.onComplete(bean);
                    if (Integer.parseInt(msg) != 0){
                        callback.onFailed(new IOException("Game processor exited abnormally with code " + msg));
                    }
                    else {
                        callback.onFinish(version);
                    }
                    if (progressServer != null) {
                        progressServer.stop();
                    }
                    server1.stop();
                });
                server.start();
                adapter.addDownloadTask(buildBean);
                activity.startService(service);
            }
        }
    }

    public interface InstallForgeCallback{
        void onStart();
        void onFailed(Exception e);
        void onFinish(Version version);
    }
}
