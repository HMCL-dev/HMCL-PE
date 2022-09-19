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
import com.tungsten.hmclpe.launcher.download.forge.ForgeInstallTask;
import com.tungsten.hmclpe.launcher.download.forge.ForgeVersion;
import com.tungsten.hmclpe.launcher.download.optifine.OptifineInstallTask;
import com.tungsten.hmclpe.launcher.download.optifine.OptifineVersion;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.NetSpeed;
import com.tungsten.hmclpe.utils.io.NetSpeedTimer;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.util.Objects;

public class GameInstallLocalDialog extends Dialog implements View.OnClickListener,Handler.Callback {

    private MainActivity activity;
    private String name;
    private String gameVersion;
    private String path;

    private RecyclerView taskListView;
    private DownloadTaskListAdapter downloadTaskListAdapter;

    private NetSpeedTimer netSpeedTimer;
    private TextView speedText;
    private Button cancelButton;

    private ForgeInstallTask forgeInstallTask;
    private OptifineInstallTask optifineInstallTask;

    private Version gameVersionJson;

    public GameInstallLocalDialog(@NonNull Context context, MainActivity activity,String name, String gameVersion, String path) {
        super(context);
        this.activity = activity;
        this.name = name;
        this.gameVersion = gameVersion;
        this.path = path;
        setContentView(R.layout.dialog_install_local);
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

        speedText = findViewById(R.id.download_speed_text);
        cancelButton = findViewById(R.id.cancel_install_local);
        cancelButton.setOnClickListener(this);

        Handler handler = new Handler(this);
        netSpeedTimer = new NetSpeedTimer(getContext(), new NetSpeed(), handler).setDelayTime(0).setPeriodTime(1000);
        netSpeedTimer.startSpeedTimer();

        check();
    }

    private void check() {
        DownloadTaskListBean bean = new DownloadTaskListBean(getContext().getString(R.string.dialog_install_local_recognize),"","","");
        InstallerAnalyzer.checkType(path, new InstallerAnalyzer.CheckInstallerTypeCallback() {
            @Override
            public void onStart() {
                downloadTaskListAdapter.addDownloadTask(bean);
            }

            @Override
            public void onFinish(InstallerAnalyzer.Type type, Object installer) {
                if (isShowing()) {
                    boolean fabric = false;
                    boolean quilt = false;
                    for (Version v : gameVersionJson.getPatches()) {
                        if (v.getId().equals("fabric")) {
                            fabric = true;
                            break;
                        }
                        if (v.getId().equals("quilt")) {
                            quilt = true;
                            break;
                        }
                    }
                    downloadTaskListAdapter.onComplete(bean);
                    if (type == InstallerAnalyzer.Type.FORGE) {
                        if (((ForgeVersion) installer).getGameVersion().equals(gameVersion)) {
                            if (fabric) {
                                fabricFailed();
                            }
                            else if (quilt) {
                                quiltFailed();
                            }
                            else {
                                installForge(((ForgeVersion) installer));
                            }
                        }
                        else {
                            incorrectVersion(gameVersion,((ForgeVersion) installer).getGameVersion());
                        }
                    }
                    else if (type == InstallerAnalyzer.Type.OPTIFINE) {
                        if (((OptifineVersion) installer).mcVersion.equals(gameVersion)) {
                            if (fabric) {
                                fabricFailed();
                            }
                            else if (quilt) {
                                quiltFailed();
                            }
                            else {
                                installOptifine(((OptifineVersion) installer));
                            }
                        }
                        else {
                            incorrectVersion(gameVersion,((OptifineVersion) installer).mcVersion);
                        }
                    }
                    else {
                        unrecognizedInstaller();
                    }
                }
            }
        });
    }

    private void unrecognizedInstaller() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.dialog_incorrect_installer_title));
        builder.setMessage(getContext().getString(R.string.dialog_incorrect_installer_msg));
        builder.setPositiveButton(getContext().getString(R.string.dialog_incorrect_installer_positive), (dialogInterface, i) -> {});
        dismiss();
        builder.create().show();
    }

    private void incorrectVersion(String expectVersion,String currentVersion) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.dialog_incorrect_version_title));
        builder.setMessage(getContext().getString(R.string.dialog_incorrect_version_msg).replace("%s1",currentVersion).replace("%s2",expectVersion));
        builder.setPositiveButton(getContext().getString(R.string.dialog_incorrect_version_positive), (dialogInterface, i) -> {});
        dismiss();
        builder.create().show();
    }

    private void fabricFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.dialog_incorrect_fabric_title));
        builder.setMessage(getContext().getString(R.string.dialog_incorrect_fabric_msg));
        builder.setPositiveButton(getContext().getString(R.string.dialog_incorrect_fabric_positive), (dialogInterface, i) -> {});
        dismiss();
        builder.create().show();
    }

    private void quiltFailed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(getContext().getString(R.string.dialog_incorrect_quilt_title));
        builder.setMessage(getContext().getString(R.string.dialog_incorrect_quilt_msg));
        builder.setPositiveButton(getContext().getString(R.string.dialog_incorrect_quilt_positive), (dialogInterface, i) -> {});
        dismiss();
        builder.create().show();
    }

    private void installForge(ForgeVersion forgeVersion) {
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
                gameVersionJson = PatchMerger.reMergePatch(getContext(), gameVersionJson, version, "forge", () -> {
                    dismiss();
                });
                saveVersion();
            }
        });
        forgeInstallTask.execute(forgeVersion);
    }

    private void installOptifine(OptifineVersion optifineVersion) {
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

    private void exit() {
        if (forgeInstallTask != null && forgeInstallTask.getStatus() != null && forgeInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            forgeInstallTask.cancel(true);
        }
        if (optifineInstallTask != null && optifineInstallTask.getStatus() != null && optifineInstallTask.getStatus() == AsyncTask.Status.RUNNING) {
            optifineInstallTask.cancel(true);
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
