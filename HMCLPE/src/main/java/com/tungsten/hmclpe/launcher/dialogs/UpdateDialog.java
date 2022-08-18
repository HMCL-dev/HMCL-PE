package com.tungsten.hmclpe.launcher.dialogs;

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
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListAdapter;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.task.DownloadTask;
import com.tungsten.hmclpe.utils.io.NetSpeed;
import com.tungsten.hmclpe.utils.io.NetSpeedTimer;

import java.util.ArrayList;
import java.util.Objects;

public class UpdateDialog extends Dialog implements View.OnClickListener, Handler.Callback {

    private MainActivity activity;
    private ArrayList<DownloadTaskListBean> list;
    private OnUpdateFinish onUpdateFinish;

    private RecyclerView taskListView;
    private DownloadTaskListAdapter downloadTaskListAdapter;

    private NetSpeedTimer netSpeedTimer;
    private TextView speedText;
    private Button cancelButton;

    private Handler handler;
    private DownloadTask downloadTask;

    public UpdateDialog(@NonNull Context context, MainActivity activity, ArrayList<DownloadTaskListBean> list, OnUpdateFinish onUpdateFinish) {
        super(context);
        this.activity = activity;
        this.list = list;
        this.onUpdateFinish = onUpdateFinish;
        setContentView(R.layout.dialog_update);
        setCancelable(false);
        init();
    }

    private void init() {
        handler = new Handler();
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
        cancelButton = findViewById(R.id.cancel);
        cancelButton.setOnClickListener(this);

        Handler handler = new Handler(this);
        netSpeedTimer = new NetSpeedTimer(getContext(), new NetSpeed(), handler).setDelayTime(0).setPeriodTime(1000);
        netSpeedTimer.startSpeedTimer();

        int maxDownloadTask = activity.launcherSetting.maxDownloadTask;
        if (activity.launcherSetting.autoDownloadTaskQuantity) {
            maxDownloadTask = 64;
        }

        downloadTask = new DownloadTask(getContext(), new DownloadTask.Feedback() {
            @Override
            public void addTask(DownloadTaskListBean bean) {
                UpdateDialog.this.handler.post(() -> {
                    downloadTaskListAdapter.addDownloadTask(bean);
                });
            }

            @Override
            public void updateProgress(DownloadTaskListBean bean) {
                UpdateDialog.this.handler.post(() -> {
                    downloadTaskListAdapter.onProgress(bean);
                });
            }

            @Override
            public void updateSpeed(String speed) {

            }

            @Override
            public void removeTask(DownloadTaskListBean bean) {
                UpdateDialog.this.handler.post(() -> {
                    downloadTaskListAdapter.onComplete(bean);
                });
            }

            @Override
            public void onFinished(ArrayList<DownloadTaskListBean> failedFile) {
                UpdateDialog.this.handler.post(() -> {
                    if (failedFile.size() > 0) {
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("The following files failed to download:");
                        for (DownloadTaskListBean bean : failedFile) {
                            stringBuilder.append("\n\n  ").append(bean.name);
                        }
                        Exception e = new Exception(stringBuilder.toString());
                        e.printStackTrace();
                        throwException(e);
                    }
                    else {
                        onUpdateFinish.onFinish();
                        exit();
                    }
                });
            }

            @Override
            public void onCancelled() {

            }
        });
        downloadTask.setMaxTask(maxDownloadTask);
        downloadTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, list);
    }

    private void throwException(Exception e) {
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
        if (downloadTask != null && downloadTask.getStatus() != null && downloadTask.getStatus() == AsyncTask.Status.RUNNING) {
            downloadTask.cancel(true);
        }
        netSpeedTimer.stopSpeedTimer();
        dismiss();
    }

    @Override
    public boolean handleMessage(@NonNull Message message) {
        if (message.what == NetSpeedTimer.NET_SPEED_TIMER_DEFAULT) {
            String speed = (String) message.obj;
            speedText.setText(speed);
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view == cancelButton) {
            exit();
        }
    }

    public interface OnUpdateFinish {
        void onFinish();
    }
}
