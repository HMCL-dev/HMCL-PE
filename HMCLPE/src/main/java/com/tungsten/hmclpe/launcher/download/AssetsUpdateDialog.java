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

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.game.AssetsUpdateTask;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.utils.io.NetSpeed;
import com.tungsten.hmclpe.utils.io.NetSpeedTimer;

import java.util.Objects;

public class AssetsUpdateDialog extends Dialog implements View.OnClickListener,Handler.Callback {

    private MainActivity activity;
    private String name;

    private RecyclerView taskListView;
    private DownloadTaskListAdapter downloadTaskListAdapter;

    private NetSpeedTimer netSpeedTimer;
    private TextView speedText;
    private Button cancelButton;

    private AssetsUpdateTask assetsUpdateTask;

    public AssetsUpdateDialog(@NonNull Context context, MainActivity activity,String name) {
        super(context);
        this.activity = activity;
        this.name = name;
        setContentView(R.layout.dialog_install_assets);
        setCancelable(false);
        init();
    }

    private void init(){
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
        cancelButton = findViewById(R.id.cancel_install_assets);
        cancelButton.setOnClickListener(this);

        Handler handler = new Handler(this);
        netSpeedTimer = new NetSpeedTimer(getContext(), new NetSpeed(), handler).setDelayTime(0).setPeriodTime(1000);
        netSpeedTimer.startSpeedTimer();

        update();
    }

    public void update() {
        assetsUpdateTask = new AssetsUpdateTask(activity, downloadTaskListAdapter, new AssetsUpdateTask.AssetsUpdateCallback() {
            @Override
            public void onStart() {

            }

            @Override
            public void onFinish(Exception e) {
                if (e == null) {
                    exit();
                }
                else {
                    throwException(e);
                }
            }
        });
        assetsUpdateTask.execute(name);
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
        if (assetsUpdateTask != null && assetsUpdateTask.getStatus() != null && assetsUpdateTask.getStatus() == AsyncTask.Status.RUNNING) {
            assetsUpdateTask.cancel(true);
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
