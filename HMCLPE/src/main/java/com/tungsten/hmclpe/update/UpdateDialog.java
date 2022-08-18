package com.tungsten.hmclpe.update;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.task.DownloadTask;
import com.tungsten.hmclpe.task.LanzouUrlGetTask;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.io.DownloadUtil;

import java.io.File;
import java.util.ArrayList;

public class UpdateDialog extends Dialog implements View.OnClickListener {

    private MainActivity activity;
    private LauncherVersion version;
    private boolean isBeta;

    private TextView versionName;
    private TextView date;
    private TextView type;
    private TextView log;

    private ProgressBar progressBar;
    private Button update;
    private Button ignore;

    private Handler handler;

    public UpdateDialog(@NonNull Context context, MainActivity activity, LauncherVersion version, boolean isBeta) {
        super(context);
        setContentView(R.layout.dialog_update_launcher);
        setCancelable(false);
        this.activity = activity;
        this.version = version;
        this.isBeta = isBeta;
        handler = new Handler();
        init();
    }

    private void init(){
        versionName = findViewById(R.id.update_version_name);
        date = findViewById(R.id.update_date);
        type = findViewById(R.id.update_type);
        log = findViewById(R.id.update_log);

        versionName.setText(version.versionName);
        date.setText(version.date);
        type.setText(getType(isBeta));
        CharSequence charSequence;
        charSequence = Html.fromHtml(version.updateLog, 0);
        log.setText(charSequence);

        progressBar = findViewById(R.id.update_progress);
        update = findViewById(R.id.update);
        ignore = findViewById(R.id.ignore);
        update.setOnClickListener(this);
        ignore.setOnClickListener(this);
    }

    private String getType(boolean isBeta) {
        if (isBeta) {
            return getContext().getString(R.string.dialog_update_beta);
        }
        else {
            return getContext().getString(R.string.dialog_update_release);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == update) {
            update.setEnabled(false);
            ignore.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            LanzouUrlGetTask task = new LanzouUrlGetTask(activity, new LanzouUrlGetTask.Callback() {
                @Override
                public void onStart() {

                }

                @Override
                public void onError(Exception e) {
                    String finalUrl;
                    if (version.url.size() > 1) {
                        finalUrl = version.url.get(1);
                    }
                    else {
                        return;
                    }
                    new Thread(() -> {
                        if (FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/update")) {
                            DownloadUtil.downloadSingleFile(getContext(), new DownloadTaskListBean("", finalUrl, AppManifest.DEFAULT_CACHE_DIR + "/update/latest.apk",null), new DownloadTask.Feedback() {
                                @Override
                                public void addTask(DownloadTaskListBean bean) {

                                }

                                @Override
                                public void updateProgress(DownloadTaskListBean bean) {
                                    handler.post(() -> progressBar.setProgress(bean.progress));
                                }

                                @Override
                                public void updateSpeed(String speed) {

                                }

                                @Override
                                public void removeTask(DownloadTaskListBean bean) {

                                }

                                @Override
                                public void onFinished(ArrayList<DownloadTaskListBean> failedFile) {
                                    handler.post(() -> {
                                        update.setEnabled(true);
                                        ignore.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Uri apkUri = FileProvider.getUriForFile(getContext(), getContext().getString(R.string.filebrowser_provider), new File(AppManifest.DEFAULT_CACHE_DIR + "/update/latest.apk"));
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                        getContext().startActivity(intent);
                                    });
                                }

                                @Override
                                public void onCancelled() {

                                }
                            });
                        }
                    }).start();
                }

                @Override
                public void onFinish(String url) {
                    if (url == null){
                        if (version.url.size() > 1) {
                            url = version.url.get(1);
                        }
                        else {
                            return;
                        }
                    }
                    String finalUrl = url;
                    new Thread(() -> {
                        if (FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/update")) {
                            DownloadUtil.downloadSingleFile(getContext(), new DownloadTaskListBean("", finalUrl, AppManifest.DEFAULT_CACHE_DIR + "/update/latest.apk",null), new DownloadTask.Feedback() {
                                @Override
                                public void addTask(DownloadTaskListBean bean) {

                                }

                                @Override
                                public void updateProgress(DownloadTaskListBean bean) {
                                    handler.post(() -> progressBar.setProgress(bean.progress));
                                }

                                @Override
                                public void updateSpeed(String speed) {

                                }

                                @Override
                                public void removeTask(DownloadTaskListBean bean) {

                                }

                                @Override
                                public void onFinished(ArrayList<DownloadTaskListBean> failedFile) {
                                    handler.post(() -> {
                                        update.setEnabled(true);
                                        ignore.setEnabled(true);
                                        progressBar.setVisibility(View.GONE);
                                        Intent intent = new Intent(Intent.ACTION_VIEW);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                        Uri apkUri = FileProvider.getUriForFile(getContext(), getContext().getString(R.string.filebrowser_provider), new File(AppManifest.DEFAULT_CACHE_DIR + "/update/latest.apk"));
                                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
                                        getContext().startActivity(intent);
                                    });
                                }

                                @Override
                                public void onCancelled() {

                                }
                            });
                        }
                    }).start();
                }
            });
            task.execute(version.url.get(0));
        }
        if (view == ignore) {
            dismiss();
        }
    }
}
