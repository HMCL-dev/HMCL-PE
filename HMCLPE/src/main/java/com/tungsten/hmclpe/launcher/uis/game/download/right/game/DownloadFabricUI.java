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
import com.tungsten.hmclpe.launcher.download.fabric.FabricGameVersion;
import com.tungsten.hmclpe.launcher.download.fabric.FabricLoaderVersion;
import com.tungsten.hmclpe.launcher.list.download.minecraft.DownloadFabricListAdapter;
import com.tungsten.hmclpe.launcher.uis.game.download.DownloadUrlSource;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.io.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class DownloadFabricUI extends BaseUI implements View.OnClickListener {

    public LinearLayout downloadFabricUI;

    public String version;
    public boolean install;

    private LinearLayout hintLayout;

    private ListView fabricListView;
    private ProgressBar progressBar;
    private TextView back;

    private static final String OFFICIAL_LOADER_META_URL = "https://meta.fabricmc.net/v2/versions/loader";
    private static final String OFFICIAL_GAME_META_URL = "https://meta.fabricmc.net/v2/versions/game";

    private static final String BMCLAPI_LOADER_META_URL = "https://bmclapi2.bangbang93.com/fabric-meta/v2/versions/loader";
    private static final String BMCLAPI_GAME_META_URL = "https://bmclapi2.bangbang93.com/fabric-meta/v2/versions/game";

    public DownloadFabricUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadFabricUI = activity.findViewById(R.id.ui_install_fabric_list);

        hintLayout = activity.findViewById(R.id.download_fabric_hint_layout);
        hintLayout.setOnClickListener(this);

        fabricListView = activity.findViewById(R.id.fabric_version_list);
        progressBar = activity.findViewById(R.id.loading_fabric_list_progress);
        back = activity.findViewById(R.id.back_to_install_ui_fabric);

        back.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.fabric_list_ui_title),false,true);
        CustomAnimationUtils.showViewFromLeft(downloadFabricUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(downloadFabricUI,activity,context,true);
    }

    private void init(){
        new Thread(() -> {
            loadingHandler.sendEmptyMessage(0);
            String loaderUrl;
            String gameUrl;
            if (DownloadUrlSource.getSource(activity.launcherSetting.downloadUrlSource) == 0) {
                loaderUrl = OFFICIAL_LOADER_META_URL;
                gameUrl = OFFICIAL_GAME_META_URL;
            }
            else {
                loaderUrl = BMCLAPI_LOADER_META_URL;
                gameUrl = BMCLAPI_GAME_META_URL;
            }
            ArrayList<FabricGameVersion> gameVersions = new ArrayList<>();
            ArrayList<FabricLoaderVersion> loaderVersions = new ArrayList<>();
            try {
                String gameResponse = NetworkUtils.doGet(NetworkUtils.toURL(gameUrl));
                Gson gson = new Gson();
                FabricGameVersion[] fabricGameVersions = gson.fromJson(gameResponse, FabricGameVersion[].class);
                gameVersions.addAll(Arrays.asList(fabricGameVersions));
                ArrayList<String> mcVersions = new ArrayList<>();
                for (FabricGameVersion version : gameVersions){
                    mcVersions.add(version.version);
                }
                String loaderResponse = NetworkUtils.doGet(NetworkUtils.toURL(loaderUrl));
                FabricLoaderVersion[] fabricLoaderVersions = gson.fromJson(loaderResponse, FabricLoaderVersion[].class);
                loaderVersions.addAll(Arrays.asList(fabricLoaderVersions));
                if (!mcVersions.contains(version)){
                    loadingHandler.sendEmptyMessage(2);
                }
                else {
                    DownloadFabricListAdapter downloadFabricListAdapter = new DownloadFabricListAdapter(context,activity,version,loaderVersions,install);
                    activity.runOnUiThread(() -> fabricListView.setAdapter(downloadFabricListAdapter));
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
                fabricListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
            }
            if (msg.what == 1){
                fabricListView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
            if (msg.what == 2){
                fabricListView.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
            }
        }
    };
}
