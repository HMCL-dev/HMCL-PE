package com.tungsten.hmclpe.launcher.download.optifine;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.ApiService;
import com.tungsten.hmclpe.launcher.download.LibraryAnalyzer;
import com.tungsten.hmclpe.launcher.game.Arguments;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.LibrariesDownloadInfo;
import com.tungsten.hmclpe.launcher.game.Library;
import com.tungsten.hmclpe.launcher.game.LibraryDownloadInfo;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.launcher.uis.game.download.DownloadUrlSource;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.io.DownloadUtil;
import com.tungsten.hmclpe.utils.io.FileUtils;
import com.tungsten.hmclpe.utils.io.SocketServer;
import com.tungsten.hmclpe.utils.io.ZipTools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class OptifineInstallTask extends AsyncTask<OptifineVersion,Integer, Version> {

    private MainActivity activity;
    private String name;
    private DownloadTaskListAdapter adapter;
    private InstallOptifineCallback callback;

    private DownloadTaskListBean bean;

    private Library optiFineLibrary;
    private Library optiFineInstallerLibrary;

    private ArrayList<String> args;

    private SocketServer server;

    boolean canceled = false;

    public OptifineInstallTask(MainActivity activity,String name,DownloadTaskListAdapter adapter, InstallOptifineCallback callback) {
        this.activity = activity;
        this.name = name;
        this.adapter = adapter;
        this.callback = callback;

        this.bean = new DownloadTaskListBean(activity.getString(R.string.dialog_install_game_install_optifine),"","","");
    }

    public void cancelBuild() {
        canceled = true;
        if (server != null) {
            server.stop();
        }
        Intent service = new Intent(activity, ApiService.class);
        activity.stopService(service);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
    }

    @Override
    protected Version doInBackground(OptifineVersion... optifineVersions) {
        OptifineVersion optifineVersion = optifineVersions[0];
        activity.runOnUiThread(() -> {
            adapter.addDownloadTask(bean);
        });
        try {
            ZipTools.unzipFile(AppManifest.INSTALL_DIR + "/optifine/" + optifineVersion.fileName, AppManifest.INSTALL_DIR + "/optifine/installer/",false);
        } catch (Exception e) {
            e.printStackTrace();
            if (!isCancelled()) callback.onFailed(e);
            cancel(true);
        }
        String mavenVersion = optifineVersion.mcVersion + "_" + optifineVersion.type + "_" + optifineVersion.patch;
        optiFineLibrary = new Library(new Artifact("optifine", "OptiFine", mavenVersion));
        optiFineInstallerLibrary = new Library(
                new Artifact("optifine", "OptiFine", mavenVersion, "installer"), null,
                new LibrariesDownloadInfo(new LibraryDownloadInfo(
                        "optifine/OptiFine/" + mavenVersion + "/OptiFine-" + mavenVersion + "-installer.jar",
                        ""))
        );
        List<Library> libraries = new ArrayList<>(4);
        libraries.add(optiFineLibrary);
        // Install launch wrapper modified by OptiFine
        boolean hasLaunchWrapper = false;
        String tempFold = AppManifest.INSTALL_DIR + "/optifine/installer/";
        try {
            FileUtils.copyFile(new File(AppManifest.INSTALL_DIR + "/optifine/" + optifineVersion.fileName).toPath(), new File(activity.launcherSetting.gameFileDirectory + "/libraries/" + optiFineInstallerLibrary.getPath()).toPath());
            if (new File(tempFold + "optifine/Patcher.class").exists()) {
                String javaPath = AppManifest.JAVA_DIR + "/default";
                String[] command = {
                        javaPath + "/bin/java",
                        "-cp",
                        AppManifest.INSTALL_DIR + "/optifine/" + optifineVersion.fileName,
                        "-Djava.library.path=" + javaPath + "/lib/aarch64/jli:" + javaPath + "/lib/aarch64",
                        "-Djava.io.tmpdir=" + AppManifest.DEFAULT_CACHE_DIR,
                        "optifine.Patcher",
                        activity.launcherSetting.gameFileDirectory + "/versions/" + name + "/" + name + ".jar",
                        AppManifest.INSTALL_DIR + "/optifine/" + optifineVersion.fileName,
                        activity.launcherSetting.gameFileDirectory + "/libraries/" + optiFineLibrary.getPath()
                };
                args = new ArrayList<>();
                Collections.addAll(args, command);
                args.add("-Xms1024M");
                args.add("-Xmx1024M");
            }
            else {
                FileUtils.copyFile(new File(AppManifest.INSTALL_DIR + "/optifine/" + optifineVersion.fileName).toPath(), new File(activity.launcherSetting.gameFileDirectory + "/libraries/" + optiFineLibrary.getPath()).toPath());
            }

            Path launchWrapper2 = new File(tempFold + "launchwrapper-2.0.jar").toPath();
            if (Files.exists(launchWrapper2)) {
                Library launchWrapper = new Library(new Artifact("optifine", "launchwrapper", "2.0"));
                File launchWrapperFile = new File(activity.launcherSetting.gameFileDirectory + "/libraries/" + launchWrapper.getPath());
                FileUtils.makeDirectory(Objects.requireNonNull(launchWrapperFile.getAbsoluteFile().getParentFile()));
                FileUtils.copyFile(launchWrapper2, launchWrapperFile.toPath());
                hasLaunchWrapper = true;
                libraries.add(launchWrapper);
            }

            Path launchWrapperVersionText = new File(tempFold + "launchwrapper-of.txt").toPath();
            if (Files.exists(launchWrapperVersionText)) {
                String launchWrapperVersion = FileUtils.readText(launchWrapperVersionText).trim();
                Path launchWrapperJar = new File(tempFold + "launchwrapper-of-" + launchWrapperVersion + ".jar").toPath();

                Library launchWrapper = new Library(new Artifact("optifine", "launchwrapper-of", launchWrapperVersion));

                if (Files.exists(launchWrapperJar)) {
                    File launchWrapperFile = new File(activity.launcherSetting.gameFileDirectory + "/libraries/" + launchWrapper.getPath());
                    FileUtils.makeDirectory(Objects.requireNonNull(launchWrapperFile.getAbsoluteFile().getParentFile()));
                    FileUtils.copyFile(launchWrapperJar, launchWrapperFile.toPath());

                    hasLaunchWrapper = true;
                    libraries.add(launchWrapper);
                }
            }
            if (!hasLaunchWrapper) {
                Library launchWrapperLib = new Library(new Artifact("net.minecraft", "launchwrapper", "1.12"));
                libraries.add(launchWrapperLib);
                DownloadUtil.downloadFile(DownloadUrlSource.getSubUrl(DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource),DownloadUrlSource.LIBRARIES) + "/" + launchWrapperLib.getPath(),activity.launcherSetting.gameFileDirectory + "/libraries/" + launchWrapperLib.getPath(),null,null);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
            if (!isCancelled()) callback.onFailed(e);
            cancel(true);
        }
        return new Version(
                LibraryAnalyzer.LibraryType.OPTIFINE.getPatchId(),
                optifineVersion.type + "_" + optifineVersion.patch,
                10000,
                new Arguments().addGameArguments("--tweakClass", "optifine.OptiFineTweaker"),
                LibraryAnalyzer.LAUNCH_WRAPPER_MAIN,
                libraries
        );
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
        Intent service = new Intent(activity, ApiService.class);
        activity.stopService(service);
    }

    @Override
    protected void onPostExecute(Version version) {
        super.onPostExecute(version);
        if (args != null) {
            Intent service = new Intent(activity, ApiService.class);
            Bundle bundle = new Bundle();
            bundle.putStringArrayList("commands",args);
            service.putExtras(bundle);
            server = new SocketServer("127.0.0.1", ApiService.API_SERVICE_PORT, (server1, msg) -> {
                adapter.onComplete(bean);
                if (Integer.parseInt(msg) != 0){
                    callback.onFailed(new IOException("OptiFine patcher failed"));
                }
                else {
                    callback.onFinish(version);
                }
                server1.stop();
            });
            server.start();
            activity.startService(service);
        }
        else {
            adapter.onComplete(bean);
            callback.onFinish(version);
        }
    }

    public interface InstallOptifineCallback{
        void onStart();
        void onFailed(Exception e);
        void onFinish(Version version);
    }
}
