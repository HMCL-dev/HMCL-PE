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
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.NetSpeed;
import com.tungsten.hmclpe.utils.io.NetSpeedTimer;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.util.Objects;

public class GameUpdateDialog extends Dialog implements View.OnClickListener, Handler.Callback {

    private Context context;
    private MainActivity activity;
    private String name;
    private int apiType;

    private String version;
    private ForgeVersion forgeVersion;
    private OptifineVersion optifineVersion;
    private LiteLoaderVersion liteLoaderVersion;
    private FabricLoaderVersion fabricVersion;
    private RemoteMod.Version fabricAPIVersion;
    private QuiltLoaderVersion quiltVersion;
    private RemoteMod.Version quiltAPIVersion;

    private LiteLoaderInstallTask liteLoaderInstallTask;
    private ForgeDownloadTask forgeDownloadTask;
    private ForgeInstallTask forgeInstallTask;
    private OptifineDownloadTask optifineDownloadTask;
    private OptifineInstallTask optifineInstallTask;
    private FabricInstallTask fabricInstallTask;
    private FabricAPIInstallTask fabricAPIInstallTask;
    private QuiltInstallTask quiltInstallTask;
    private QuiltAPIInstallTask quiltAPIInstallTask;

    private RecyclerView taskListView;
    private DownloadTaskListAdapter downloadTaskListAdapter;

    private TextView stateText;
    private NetSpeedTimer netSpeedTimer;
    private TextView speedText;
    private Button cancelButton;

    private Version gameVersionJson;

    public GameUpdateDialog(@NonNull Context context,MainActivity activity,String name, String gameVersion,int apiType,Object apiVersion) {
        super(context);
        this.context = context;
        this.activity = activity;
        this.name = name;
        this.version = gameVersion;
        this.apiType = apiType;
        switch (apiType) {
            case 0:
                this.forgeVersion = (ForgeVersion) apiVersion;
                break;
            case 1:
                this.liteLoaderVersion = (LiteLoaderVersion) apiVersion;
                break;
            case 2:
                this.optifineVersion = (OptifineVersion) apiVersion;
                break;
            case 3:
                this.fabricVersion = (FabricLoaderVersion) apiVersion;
                break;
            case 4:
                this.fabricAPIVersion = (RemoteMod.Version) apiVersion;
                break;
            case 5:
                this.quiltVersion = (QuiltLoaderVersion) apiVersion;
                break;
            case 6:
                this.quiltAPIVersion = (RemoteMod.Version) apiVersion;
                break;
        }
        setContentView(R.layout.dialog_install_update);
        setCancelable(false);
        init();
    }

    private void init() {
        String s = FileStringUtils.getStringFromFile(activity.launcherSetting.gameFileDirectory + "/versions/" + name + "/" + name + ".json");
        Gson gson = JsonUtils.defaultGsonBuilder()
                .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                .registerTypeAdapter(Bits.class, new Bits.Serializer())
                .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                .create();
        gameVersionJson = gson.fromJson(s,Version.class);

        taskListView = findViewById(R.id.download_task_list);

        taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
        downloadTaskListAdapter = new DownloadTaskListAdapter(getContext());
        taskListView.setAdapter(downloadTaskListAdapter);
        Objects.requireNonNull(taskListView.getItemAnimator()).setAddDuration(0L);
        taskListView.getItemAnimator().setChangeDuration(0L);
        taskListView.getItemAnimator().setMoveDuration(0L);
        taskListView.getItemAnimator().setRemoveDuration(0L);
        ((SimpleItemAnimator)taskListView.getItemAnimator()).setSupportsChangeAnimations(false);

        stateText = findViewById(R.id.state_text);
        speedText = findViewById(R.id.download_speed_text);
        cancelButton = findViewById(R.id.cancel_install_update);
        cancelButton.setOnClickListener(this);

        Handler handler = new Handler(this);
        netSpeedTimer = new NetSpeedTimer(getContext(), new NetSpeed(), handler).setDelayTime(0).setPeriodTime(1000);
        netSpeedTimer.startSpeedTimer();

        switch (apiType) {
            case 0:
                downloadForge();
                break;
            case 1:
                downloadLiteLoader();
                break;
            case 2:
                downloadOptifine();
                break;
            case 3:
                downloadFabric();
                break;
            case 4:
                downloadFabricAPI();
                break;
            case 5:
                downloadQuilt();
                break;
            case 6:
                downloadQuiltAPI();
                break;
        }
    }

    public void downloadLiteLoader(){
        liteLoaderInstallTask = new LiteLoaderInstallTask(activity, downloadTaskListAdapter, new LiteLoaderInstallTask.InstallLiteLoaderCallback() {
            @Override
            public void onStart() {
                stateText.setText(context.getString(R.string.dialog_install_update_state).replace("%s","LiteLoader").replace("%v",liteLoaderVersion.getVersion()));
            }

            @Override
            public void onFailed(Exception e) {
                throwException(e);
            }

            @Override
            public void onFinish(Version version) {
                gameVersionJson = PatchMerger.reMergePatch(context, gameVersionJson, version, "liteloader", () -> {
                    exit();
                });
                saveVersion();
            }
        });
        liteLoaderInstallTask.execute(liteLoaderVersion);
    }

    public void downloadForge() {
        forgeDownloadTask = new ForgeDownloadTask(activity, downloadTaskListAdapter, new ForgeDownloadTask.DownloadForgeCallback() {
            @Override
            public void onStart() {
                stateText.setText(context.getString(R.string.dialog_install_update_state).replace("%s","Forge").replace("%v",forgeVersion.getVersion()));
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
                gameVersionJson = PatchMerger.reMergePatch(context, gameVersionJson, version, "forge", () -> {
                    exit();
                });
                saveVersion();
            }
        });
        forgeInstallTask.execute(forgeVersion);
    }

    public void downloadOptifine() {
        optifineDownloadTask = new OptifineDownloadTask(activity, downloadTaskListAdapter, new OptifineDownloadTask.DownloadOptifineCallback() {
            @Override
            public void onStart() {
                stateText.setText(context.getString(R.string.dialog_install_update_state).replace("%s","OptiFine").replace("%v",optifineVersion.type + "_" + optifineVersion.patch));
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
                gameVersionJson = PatchMerger.reMergePatch(getContext(), gameVersionJson, version, "optifine", () -> {
                    dismiss();
                });
                saveVersion();
            }
        });
        optifineInstallTask.execute(optifineVersion);
    }

    public void downloadFabric(){
        fabricInstallTask = new FabricInstallTask(activity, downloadTaskListAdapter, version, new FabricInstallTask.InstallFabricCallback() {
            @Override
            public void onStart() {
                stateText.setText(context.getString(R.string.dialog_install_update_state).replace("%s","Fabric").replace("%v",fabricVersion.version));
            }

            @Override
            public void onFailed(Exception e) {
                throwException(e);
            }

            @Override
            public void onFinish(Version version) {
                gameVersionJson = PatchMerger.reMergePatch(getContext(), gameVersionJson, version, "fabric", () -> {
                    dismiss();
                });
                saveVersion();
            }
        });
        fabricInstallTask.execute(fabricVersion);
    }

    public void downloadFabricAPI(){
        fabricAPIInstallTask = new FabricAPIInstallTask(activity, name, downloadTaskListAdapter, new FabricAPIInstallTask.InstallFabricAPICallback() {
            @Override
            public void onStart() {
                stateText.setText(context.getString(R.string.dialog_install_update_state).replace("%s","Fabric API").replace("%v",fabricAPIVersion.getVersion()));
            }

            @Override
            public void onFinish(Exception e) {
                if (e == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getContext().getString(R.string.dialog_install_success_title));
                    builder.setMessage(getContext().getString(R.string.dialog_install_success_text));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getContext().getString(R.string.dialog_install_success_positive), (dialogInterface, i) -> {
                        activity.backToLastUI();
                    });
                    exit();
                    builder.create().show();
                }
                else {
                    throwException(e);
                }
            }
        });
        fabricAPIInstallTask.execute(fabricAPIVersion);
    }

    public void downloadQuilt(){
        quiltInstallTask = new QuiltInstallTask(activity, downloadTaskListAdapter, version, new QuiltInstallTask.InstallQuiltCallback() {
            @Override
            public void onStart() {
                stateText.setText(context.getString(R.string.dialog_install_update_state).replace("%s","Quilt").replace("%v",quiltVersion.version));
            }

            @Override
            public void onFailed(Exception e) {
                throwException(e);
            }

            @Override
            public void onFinish(Version version) {
                gameVersionJson = PatchMerger.reMergePatch(getContext(), gameVersionJson, version, "quilt", () -> {
                    dismiss();
                });
                saveVersion();
            }
        });
        quiltInstallTask.execute(quiltVersion);
    }

    public void downloadQuiltAPI(){
        quiltAPIInstallTask = new QuiltAPIInstallTask(activity, name, downloadTaskListAdapter, new QuiltAPIInstallTask.InstallQuiltAPICallback() {
            @Override
            public void onStart() {
                stateText.setText(context.getString(R.string.dialog_install_update_state).replace("%s","Quilt API").replace("%v",quiltAPIVersion.getVersion()));
            }

            @Override
            public void onFinish(Exception e) {
                if (e == null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getContext().getString(R.string.dialog_install_success_title));
                    builder.setMessage(getContext().getString(R.string.dialog_install_success_text));
                    builder.setCancelable(false);
                    builder.setPositiveButton(getContext().getString(R.string.dialog_install_success_positive), (dialogInterface, i) -> {
                        activity.backToLastUI();
                    });
                    exit();
                    builder.create().show();
                }
                else {
                    throwException(e);
                }
            }
        });
        quiltAPIInstallTask.execute(quiltAPIVersion);
    }

    public void saveVersion() {
        String versionPath = activity.launcherSetting.gameFileDirectory + "/versions/" + name + "/" + name + ".json";
        Gson gson = JsonUtils.defaultGsonBuilder()
                .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                .registerTypeAdapter(Bits.class, new Bits.Serializer())
                .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                .create();
        String s = gson.toJson(gameVersionJson);
        FileStringUtils.writeFile(versionPath,s);
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.dialog_install_success_title));
        builder.setMessage(getContext().getString(R.string.dialog_install_success_text));
        builder.setCancelable(false);
        builder.setPositiveButton(getContext().getString(R.string.dialog_install_success_positive), (dialogInterface, i) -> {
            activity.backToLastUI();
            activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.refresh(name);
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
    public void onClick(View view) {
        if (view == cancelButton) {
            exit();
            activity.backToLastUI();
            new Thread(() -> {
                activity.uiManager.versionListUI.refreshVersionList();
            }).start();
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        if (message.what == NetSpeedTimer.NET_SPEED_TIMER_DEFAULT) {
            String speed = (String) message.obj;
            speedText.setText(speed);
        }
        return false;
    }
}
