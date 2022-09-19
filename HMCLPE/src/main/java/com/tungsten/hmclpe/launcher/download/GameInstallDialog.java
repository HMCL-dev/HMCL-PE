package com.tungsten.hmclpe.launcher.download;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.google.gson.Gson;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.fabric.FabricAPIInstallTask;
import com.tungsten.hmclpe.launcher.download.fabric.FabricInstallTask;
import com.tungsten.hmclpe.launcher.download.fabric.FabricLoaderVersion;
import com.tungsten.hmclpe.launcher.download.forge.ForgeDownloadTask;
import com.tungsten.hmclpe.launcher.download.forge.ForgeInstallTask;
import com.tungsten.hmclpe.launcher.download.forge.ForgeVersion;
import com.tungsten.hmclpe.launcher.download.game.MinecraftInstallTask;
import com.tungsten.hmclpe.launcher.download.game.VersionManifest;
import com.tungsten.hmclpe.launcher.download.liteloader.LiteLoaderInstallTask;
import com.tungsten.hmclpe.launcher.download.liteloader.LiteLoaderVersion;
import com.tungsten.hmclpe.launcher.download.optifine.OptifineDownloadTask;
import com.tungsten.hmclpe.launcher.download.optifine.OptifineInstallTask;
import com.tungsten.hmclpe.launcher.download.optifine.OptifineVersion;
import com.tungsten.hmclpe.launcher.download.quilt.QuiltAPIInstallTask;
import com.tungsten.hmclpe.launcher.download.quilt.QuiltInstallTask;
import com.tungsten.hmclpe.launcher.download.quilt.QuiltLoaderVersion;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.uis.game.download.DownloadUrlSource;
import com.tungsten.hmclpe.utils.file.AssetsUtils;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.NetSpeed;
import com.tungsten.hmclpe.utils.io.NetSpeedTimer;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.io.File;
import java.util.Objects;

public class GameInstallDialog extends Dialog implements View.OnClickListener, Handler.Callback {

    private final Context context;
    private final MainActivity activity;

    private final String name;
    private final VersionManifest.Version version;
    private final ForgeVersion forgeVersion;
    private final OptifineVersion optifineVersion;
    private final LiteLoaderVersion liteLoaderVersion;
    private final FabricLoaderVersion fabricVersion;
    private final RemoteMod.Version fabricAPIVersion;
    private final QuiltLoaderVersion quiltVersion;
    private final RemoteMod.Version quiltAPIVersion;
    
    private MinecraftInstallTask minecraftInstallTask;
    private LiteLoaderInstallTask liteLoaderInstallTask;
    private ForgeDownloadTask forgeDownloadTask;
    private ForgeInstallTask forgeInstallTask;
    private OptifineDownloadTask optifineDownloadTask;
    private OptifineInstallTask optifineInstallTask;
    private FabricInstallTask fabricInstallTask;
    private FabricAPIInstallTask fabricAPIInstallTask;
    private QuiltInstallTask quiltInstallTask;
    private QuiltAPIInstallTask quiltAPIInstallTask;

    private Version gameVersionJson;

    private RecyclerView taskListView;
    private DownloadTaskListAdapter downloadTaskListAdapter;

    private NetSpeedTimer netSpeedTimer;
    private TextView speedText;
    private Button cancelButton;

    public GameInstallDialog(@NonNull Context context, MainActivity activity, String name, VersionManifest.Version version, ForgeVersion forgeVersion, OptifineVersion optifineVersion, LiteLoaderVersion liteLoaderVersion, FabricLoaderVersion fabricVersion, RemoteMod.Version fabricAPIVersion, QuiltLoaderVersion quiltVersion, RemoteMod.Version quiltAPIVersion) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.name = name;
        this.version = version;
        this.forgeVersion = forgeVersion;
        this.optifineVersion = optifineVersion;
        this.liteLoaderVersion = liteLoaderVersion;
        this.fabricVersion = fabricVersion;
        this.fabricAPIVersion = fabricAPIVersion;
        this.quiltVersion = quiltVersion;
        this.quiltAPIVersion = quiltAPIVersion;
        setContentView(R.layout.dialog_install_game);
        setCancelable(false);
        init();
    }

    @Override
    public void onClick(View v) {
        if (v == cancelButton){
            exit();
            activity.backToLastUI();
            new Thread(() -> {
                activity.uiManager.versionListUI.refreshVersionList();
            }).start();
        }
    }

    private void init(){
        taskListView = findViewById(R.id.download_task_list);

        taskListView.setLayoutManager(new LinearLayoutManager(context));
        downloadTaskListAdapter = new DownloadTaskListAdapter(context);
        taskListView.setAdapter(downloadTaskListAdapter);
        Objects.requireNonNull(taskListView.getItemAnimator()).setAddDuration(0L);
        taskListView.getItemAnimator().setChangeDuration(0L);
        taskListView.getItemAnimator().setMoveDuration(0L);
        taskListView.getItemAnimator().setRemoveDuration(0L);
        ((SimpleItemAnimator)taskListView.getItemAnimator()).setSupportsChangeAnimations(false);

        speedText = findViewById(R.id.download_speed_text);
        cancelButton = findViewById(R.id.cancel_install_game);
        cancelButton.setOnClickListener(this);

        Handler handler = new Handler(this);
        netSpeedTimer = new NetSpeedTimer(context, new NetSpeed(), handler).setDelayTime(0).setPeriodTime(1000);
        netSpeedTimer.startSpeedTimer();

        startDownloadTasks();
    }

    private void startDownloadTasks(){
        System.out.println("---------------------------------------------------------------source:" + DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource));
        if (!new File(activity.launcherSetting.gameFileDirectory + "/launcher_profiles.json").exists()) {
            AssetsUtils.getInstance(activity.getApplicationContext()).copyAssetsToSD("launcher_profiles.json", activity.launcherSetting.gameFileDirectory + "/launcher_profiles.json");
        }
        downloadMinecraft();
    }

    public void downloadMinecraft(){
        minecraftInstallTask = new MinecraftInstallTask(activity, name, downloadTaskListAdapter, new MinecraftInstallTask.InstallMinecraftCallback() {
            @Override
            public void onStart() {
                
            }

            @Override
            public void onFailed(Exception e) {
                throwException(e);
            }

            @Override
            public void onFinish(Version version) {
                gameVersionJson = version;
                downloadLiteLoader();
            }
        });
        minecraftInstallTask.execute(version);
    }

    public void downloadLiteLoader(){
        if (liteLoaderVersion != null) {
            liteLoaderInstallTask = new LiteLoaderInstallTask(activity, downloadTaskListAdapter, new LiteLoaderInstallTask.InstallLiteLoaderCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFailed(Exception e) {
                    throwException(e);
                }

                @Override
                public void onFinish(Version version) {
                    gameVersionJson = PatchMerger.mergePatch(gameVersionJson,version);
                    downloadForge();
                }
            });
            liteLoaderInstallTask.execute(liteLoaderVersion);
        }
        else {
            downloadForge();
        }
    }

    public void downloadForge(){
        if (forgeVersion != null) {
            forgeDownloadTask = new ForgeDownloadTask(activity, downloadTaskListAdapter, new ForgeDownloadTask.DownloadForgeCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish(Exception e) {
                    if (e == null) {
                        installForge();
                    }
                    else {
                        throwException(e);
                    }
                }
            });
            forgeDownloadTask.execute(forgeVersion);
        }
        else {
            downloadOptifine();
        }
    }

    public void installForge() {
        forgeInstallTask = new ForgeInstallTask(activity, name, downloadTaskListAdapter, new ForgeInstallTask.InstallForgeCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFailed(Exception e) {
                throwException(e);
            }

            @Override
            public void onFinish(Version version) {
                gameVersionJson = PatchMerger.mergePatch(gameVersionJson,version);
                downloadOptifine();
            }
        });
        forgeInstallTask.execute(forgeVersion);
    }

    public void downloadOptifine() {
        if (optifineVersion != null) {
            optifineDownloadTask = new OptifineDownloadTask(activity, downloadTaskListAdapter, new OptifineDownloadTask.DownloadOptifineCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish(Exception e) {
                    if (e == null) {
                        installOptifine();
                    }
                    else {
                        throwException(e);
                    }
                }
            });
            optifineDownloadTask.execute(optifineVersion);
        }
        else {
            downloadFabric();
        }
    }

    public void installOptifine() {
        optifineInstallTask = new OptifineInstallTask(activity, name, downloadTaskListAdapter, new OptifineInstallTask.InstallOptifineCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFailed(Exception e) {
                throwException(e);
            }

            @Override
            public void onFinish(Version version) {
                gameVersionJson = PatchMerger.mergeOptifinePatch(gameVersionJson,version);
                downloadFabric();
            }
        });
        optifineInstallTask.execute(optifineVersion);
    }

    public void downloadFabric(){
        if (fabricVersion != null) {
            fabricInstallTask = new FabricInstallTask(activity, downloadTaskListAdapter, version.id, new FabricInstallTask.InstallFabricCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFailed(Exception e) {
                    throwException(e);
                }

                @Override
                public void onFinish(Version version) {
                    gameVersionJson = PatchMerger.mergePatch(gameVersionJson,version);
                    downloadFabricAPI();
                }
            });
            fabricInstallTask.execute(fabricVersion);
        }
        else {
            downloadFabricAPI();
        }
    }

    public void downloadFabricAPI(){
        if (fabricAPIVersion != null) {
            fabricAPIInstallTask = new FabricAPIInstallTask(activity, name, downloadTaskListAdapter, new FabricAPIInstallTask.InstallFabricAPICallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish(Exception e) {
                    if (e == null) {
                        downloadQuilt();
                    }
                    else {
                        throwException(e);
                    }
                }
            });
            fabricAPIInstallTask.execute(fabricAPIVersion);
        }
        else {
            downloadQuilt();
        }
    }

    public void downloadQuilt(){
        if (quiltVersion != null) {
            quiltInstallTask = new QuiltInstallTask(activity, downloadTaskListAdapter, version.id, new QuiltInstallTask.InstallQuiltCallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFailed(Exception e) {
                    throwException(e);
                }

                @Override
                public void onFinish(Version version) {
                    gameVersionJson = PatchMerger.mergePatch(gameVersionJson,version);
                    downloadQuiltAPI();
                }
            });
            quiltInstallTask.execute(quiltVersion);
        }
        else {
            downloadQuiltAPI();
        }
    }

    public void downloadQuiltAPI(){
        if (quiltAPIVersion != null) {
            quiltAPIInstallTask = new QuiltAPIInstallTask(activity, name, downloadTaskListAdapter, new QuiltAPIInstallTask.InstallQuiltAPICallback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onFinish(Exception e) {
                    if (e == null) {
                        installJson();
                    }
                    else {
                        throwException(e);
                    }
                }
            });
            quiltAPIInstallTask.execute(quiltAPIVersion);
        }
        else {
            installJson();
        }
    }

    public void installJson(){
        String gameFilePath = activity.launcherSetting.gameFileDirectory;
        Gson gson = JsonUtils.defaultGsonBuilder()
                .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                .registerTypeAdapter(Bits.class, new Bits.Serializer())
                .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                .create();
        String string = gson.toJson(gameVersionJson);
        FileStringUtils.writeFile(gameFilePath + "/versions/" + name + "/" + name + ".json",string);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.dialog_install_success_title));
        builder.setMessage(context.getString(R.string.dialog_install_success_text));
        builder.setCancelable(false);
        builder.setPositiveButton(context.getString(R.string.dialog_install_success_positive), (dialogInterface, i) -> {
            activity.backToLastUI();
            new Thread(() -> {
                activity.uiManager.versionListUI.refreshVersionList();
            }).start();
        });
        exit();
        builder.create().show();
    }
    
    public void throwException(Exception e) {
        activity.runOnUiThread(() -> {
            exit();
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getContext().getString(R.string.dialog_install_fail_title));
            builder.setMessage(e.toString());
            builder.setPositiveButton(getContext().getString(R.string.dialog_install_fail_positive), (dialogInterface, i) -> {});
            builder.create().show();
        });
    }

    private void exit(){
        if (minecraftInstallTask != null && minecraftInstallTask.getStatus() != null && minecraftInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            minecraftInstallTask.cancel(true);
        }
        if (liteLoaderInstallTask != null && liteLoaderInstallTask.getStatus() != null && liteLoaderInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            liteLoaderInstallTask.cancel(true);
        }
        if (forgeDownloadTask != null && forgeDownloadTask.getStatus() != null && forgeDownloadTask.getStatus() == AsyncTask.Status.RUNNING) {
            forgeDownloadTask.cancel(true);
        }
        if (forgeInstallTask != null && forgeInstallTask.getStatus() != null && forgeInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            forgeInstallTask.cancel(true);
        }
        if (optifineDownloadTask != null && optifineDownloadTask.getStatus() != null && optifineDownloadTask.getStatus() == AsyncTask.Status.RUNNING) {
            optifineDownloadTask.cancel(true);
        }
        if (optifineInstallTask != null && optifineInstallTask.getStatus() != null && optifineInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            optifineInstallTask.cancel(true);
        }
        if (fabricInstallTask != null && fabricInstallTask.getStatus() != null && fabricInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            fabricInstallTask.cancel(true);
        }
        if (fabricAPIInstallTask != null && fabricAPIInstallTask.getStatus() != null && fabricAPIInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            fabricAPIInstallTask.cancel(true);
        }
        if (quiltInstallTask != null && quiltInstallTask.getStatus() != null && quiltInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            quiltInstallTask.cancel(true);
        }
        if (quiltAPIInstallTask != null && quiltAPIInstallTask.getStatus() != null && quiltAPIInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            quiltAPIInstallTask.cancel(true);
        }
        if (forgeInstallTask != null) {
            forgeInstallTask.cancelBuild();
        }
        if (optifineInstallTask != null) {
            optifineInstallTask.cancelBuild();
        }
        netSpeedTimer.stopSpeedTimer();
        dismiss();
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        if (msg.what == NetSpeedTimer.NET_SPEED_TIMER_DEFAULT) {
            String speed = (String) msg.obj;
            speedText.setText(speed);
        }
        return false;
    }

}
