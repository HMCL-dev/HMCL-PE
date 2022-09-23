package com.tungsten.hmclpe.launcher.uis.game.download.right.game;

import static java.util.stream.Collectors.toList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.download.minecraft.DownloadFabricAPIListAdapter;
import com.tungsten.hmclpe.launcher.list.download.minecraft.DownloadQuiltAPIListAdapter;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.mod.modrinth.ModrinthRemoteModRepository;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class DownloadQuiltAPIUI extends BaseUI implements View.OnClickListener {

    public LinearLayout downloadQuiltAPIUI;

    public String version;
    public boolean install;

    private LinearLayout hintLayout;

    private ListView quiltAPIListView;
    private ProgressBar progressBar;
    private TextView refreshText;
    private TextView back;

    private static final String QUILT_API_ID = "qsl";

    public DownloadQuiltAPIUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadQuiltAPIUI = activity.findViewById(R.id.ui_install_quilt_api_list);

        hintLayout = activity.findViewById(R.id.download_quilt_api_hint_layout);
        hintLayout.setOnClickListener(this);

        quiltAPIListView = activity.findViewById(R.id.quilt_api_version_list);
        progressBar = activity.findViewById(R.id.loading_quilt_api_list_progress);
        refreshText = activity.findViewById(R.id.refresh_quilt_api_list);
        back = activity.findViewById(R.id.back_to_install_ui_quilt_api);

        refreshText.setOnClickListener(this);
        back.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.quilt_api_list_ui_title),false,true);
        CustomAnimationUtils.showViewFromLeft(downloadQuiltAPIUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(downloadQuiltAPIUI,activity,context,true);
    }

    private void init(){
        refresh();
    }

    private void refresh() {
        new Thread(() -> {
            loadingHandler.sendEmptyMessage(0);
            ArrayList<RemoteMod.Version> apiVersions = new ArrayList<>();
            ArrayList<RemoteMod.Version> availableVersions = new ArrayList<>();
            try {
                Stream<RemoteMod.Version> stream = ModrinthRemoteModRepository.MODS.getRemoteVersionsById(QUILT_API_ID);
                List<RemoteMod.Version> list = stream.collect(toList());
                apiVersions.addAll(list);
                boolean exist = false;
                for (RemoteMod.Version v : apiVersions) {
                    if (v.getGameVersions().contains(version)){
                        exist = true;
                        availableVersions.add(v);
                    }
                }
                if (exist) {
                    DownloadQuiltAPIListAdapter adapter = new DownloadQuiltAPIListAdapter(context,activity,version,availableVersions,install);
                    loadingHandler.post(() -> quiltAPIListView.setAdapter(adapter));
                    loadingHandler.sendEmptyMessage(1);
                }
                else {
                    loadingHandler.sendEmptyMessage(2);
                }
            } catch (Exception e) {
                loadingHandler.sendEmptyMessage(3);
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (view == hintLayout){
            Uri uri = Uri.parse("https://afdian.net/@bangbang93");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        if (view == refreshText) {
            refresh();
        }
        if (view == back){
            activity.backToLastUI();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler loadingHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                quiltAPIListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                refreshText.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
            if (msg.what == 1){
                quiltAPIListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                refreshText.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
            if (msg.what == 2){
                quiltAPIListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                refreshText.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
            }
            if (msg.what == 3){
                quiltAPIListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                refreshText.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
            }
        }
    };
}
