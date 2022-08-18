package com.tungsten.hmclpe.launcher.dialogs;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.launcher.mod.LocalModFile;
import com.tungsten.hmclpe.launcher.mod.ModManager;
import com.tungsten.hmclpe.launcher.mod.curse.CurseForgeRemoteModRepository;
import com.tungsten.hmclpe.utils.io.NetSpeed;
import com.tungsten.hmclpe.utils.io.NetSpeedTimer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class CheckModUpdateDialog extends Dialog implements View.OnClickListener, Handler.Callback {

    private MainActivity activity;
    private ArrayList<LocalModFile> list;
    private ModManager modManager;
    private String versionId;

    private TextView textView;
    private RecyclerView taskListView;
    private DownloadTaskListAdapter downloadTaskListAdapter;

    private NetSpeedTimer netSpeedTimer;
    private TextView speedText;
    private Button cancelButton;

    private Thread thread;

    public CheckModUpdateDialog(@NonNull Context context, MainActivity activity, ArrayList<LocalModFile> list, ModManager modManager, String versionId) {
        super(context);
        this.activity = activity;
        this.list = list;
        this.modManager = modManager;
        this.versionId = versionId;
        setContentView(R.layout.dialog_check_mod_update);
        setCancelable(false);
        init();
    }

    private void init() {
        textView = findViewById(R.id.text);
        taskListView = findViewById(R.id.download_task_list);

        taskListView.setLayoutManager(new LinearLayoutManager(getContext()));
        downloadTaskListAdapter = new DownloadTaskListAdapter(getContext());
        taskListView.setAdapter(downloadTaskListAdapter);
        Objects.requireNonNull(taskListView.getItemAnimator()).setAddDuration(0L);
        taskListView.getItemAnimator().setChangeDuration(0L);
        taskListView.getItemAnimator().setMoveDuration(0L);
        taskListView.getItemAnimator().setRemoveDuration(0L);
        ((SimpleItemAnimator)taskListView.getItemAnimator()).setSupportsChangeAnimations(false);
        downloadTaskListAdapter.setCheckMod(true);

        speedText = findViewById(R.id.download_speed_text);
        cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(this);

        Handler handler = new Handler(this);
        netSpeedTimer = new NetSpeedTimer(getContext(), new NetSpeed(), handler).setDelayTime(0).setPeriodTime(1000);
        netSpeedTimer.startSpeedTimer();

        check();
    }

    private void check() {
        ArrayList<LocalModFile.ModUpdate> modUpdates = new ArrayList<>();
        ArrayList<IOException> exceptions = new ArrayList<>();
        thread = new Thread(() -> {
            int progress = 0;
            int finalProgress = progress;
            activity.runOnUiThread(() -> {
                textView.setText(getContext().getString(R.string.dialog_check_mod_update_task).replace("%c",Integer.toString(finalProgress)).replace("%t",Integer.toString(list.size())));
                for (LocalModFile mod : list) {
                    downloadTaskListAdapter.addDownloadTask(new DownloadTaskListBean(mod.getFileName(),"","",""));
                }
            });
            for (LocalModFile mod : list) {
                try {
                    LocalModFile.ModUpdate modUpdate = mod.checkUpdates(versionId, CurseForgeRemoteModRepository.MODS);
                    if (modUpdate != null) {
                        modUpdates.add(modUpdate);
                    }
                } catch (IOException e) {
                    exceptions.add(e);
                    e.printStackTrace();
                }
                progress++;
                int currentProgress = progress;
                activity.runOnUiThread(() -> {
                    downloadTaskListAdapter.onComplete(new DownloadTaskListBean(mod.getFileName(),"","",""));
                    textView.setText(getContext().getString(R.string.dialog_check_mod_update_task).replace("%c",Integer.toString(currentProgress)).replace("%t",Integer.toString(list.size())));
                });
            }
            activity.runOnUiThread(() -> {
                if (isShowing() && exceptions.size() == 0) {
                    finish(modManager,modUpdates);
                }
                else {
                    throwException();
                }
            });
        });
        thread.start();
    }

    private void finish(ModManager modManager, ArrayList<LocalModFile.ModUpdate> modUpdates) {
        System.out.println("Mod update check task finish!");
        dismiss();
        activity.uiManager.modUpdateUI.modManager = modManager;
        activity.uiManager.modUpdateUI.modUpdates = modUpdates;
        activity.uiManager.switchMainUI(activity.uiManager.modUpdateUI);
    }

    private void throwException() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        dismiss();
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Failed");
        builder.setMessage("Failed to check updates");
        builder.setPositiveButton(getContext().getString(R.string.dialog_install_fail_positive), (dialogInterface, i) -> {});
        builder.create().show();
    }

    @Override
    public void onClick(View view) {
        if (view == cancelButton) {
            if (thread != null && thread.isAlive()) {
                thread.interrupt();
            }
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
