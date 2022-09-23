package com.tungsten.hmclpe.launcher.uis.game.download.right.game;

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

import com.google.gson.Gson;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.quilt.QuiltGameVersion;
import com.tungsten.hmclpe.launcher.download.quilt.QuiltLoaderVersion;
import com.tungsten.hmclpe.launcher.list.download.minecraft.DownloadQuiltListAdapter;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.io.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DownloadQuiltUI extends BaseUI implements View.OnClickListener {

    public LinearLayout downloadQuiltUI;

    public String version;
    public boolean install;

    private LinearLayout hintLayout;

    private ListView quiltListView;
    private ProgressBar progressBar;
    private TextView back;

    private static final String LOADER_META_URL = "https://meta.quiltmc.org/v3/versions/loader";
    private static final String GAME_META_URL = "https://meta.quiltmc.org/v3/versions/game";

    public DownloadQuiltUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadQuiltUI = activity.findViewById(R.id.ui_install_quilt_list);

        hintLayout = activity.findViewById(R.id.download_quilt_hint_layout);
        hintLayout.setOnClickListener(this);

        quiltListView = activity.findViewById(R.id.quilt_version_list);
        progressBar = activity.findViewById(R.id.loading_quilt_list_progress);
        back = activity.findViewById(R.id.back_to_install_ui_quilt);

        back.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.quilt_list_ui_title),false,true);
        CustomAnimationUtils.showViewFromLeft(downloadQuiltUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(downloadQuiltUI,activity,context,true);
    }

    private void init(){
        new Thread(() -> {
            loadingHandler.sendEmptyMessage(0);
            ArrayList<QuiltGameVersion> gameVersions = new ArrayList<>();
            ArrayList<QuiltLoaderVersion> loaderVersions = new ArrayList<>();
            try {
                String gameResponse = NetworkUtils.doGet(NetworkUtils.toURL(GAME_META_URL));
                Gson gson = new Gson();
                QuiltGameVersion[] quiltGameVersions = gson.fromJson(gameResponse, QuiltGameVersion[].class);
                gameVersions.addAll(Arrays.asList(quiltGameVersions));
                ArrayList<String> mcVersions = new ArrayList<>();
                for (QuiltGameVersion version : gameVersions){
                    mcVersions.add(version.version);
                }
                String loaderResponse = NetworkUtils.doGet(NetworkUtils.toURL(LOADER_META_URL));
                QuiltLoaderVersion[] quiltLoaderVersions = gson.fromJson(loaderResponse, QuiltLoaderVersion[].class);
                loaderVersions.addAll(Arrays.asList(quiltLoaderVersions));
                if (!mcVersions.contains(version)){
                    loadingHandler.sendEmptyMessage(2);
                }
                else {
                    DownloadQuiltListAdapter downloadQuiltListAdapter = new DownloadQuiltListAdapter(context,activity,version,loaderVersions,install);
                    activity.runOnUiThread(() -> quiltListView.setAdapter(downloadQuiltListAdapter));
                    loadingHandler.sendEmptyMessage(1);
                }
            } catch (IOException e) {
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
                quiltListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
            }
            if (msg.what == 1){
                quiltListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
            if (msg.what == 2){
                quiltListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
            }
        }
    };
}
