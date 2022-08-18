package com.tungsten.hmclpe.launcher.uis.game.version;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.info.contents.ContentListAdapter;
import com.tungsten.hmclpe.launcher.list.info.contents.ContentListBean;
import com.tungsten.hmclpe.launcher.list.local.game.GameListAdapter;
import com.tungsten.hmclpe.launcher.list.local.game.GameListBean;
import com.tungsten.hmclpe.launcher.view.list.ContentListView;
import com.tungsten.hmclpe.launcher.setting.InitializeSetting;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import java.util.ArrayList;

public class VersionListUI extends BaseUI implements View.OnClickListener {

    public LinearLayout versionListUI;

    private LinearLayout contentListParent;
    private ContentListView gameDirList;
    private ListView versionList;

    public ArrayList<ContentListBean> contentList;
    public ArrayList<GameListBean> gameList;

    private ContentListAdapter contentListAdapter;
    private GameListAdapter gameListAdapter;

    private LinearLayout startAddGameDirUI;
    private LinearLayout startDownloadMcUI;
    private LinearLayout startInstallPackageUI;
    private LinearLayout refresh;
    private LinearLayout startGlobalSettingUI;

    private TextView startDownloadMcUIText;
    private ProgressBar progressBar;

    public VersionListUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        versionListUI = activity.findViewById(R.id.ui_version_list);

        gameDirList = activity.findViewById(R.id.game_file_directory_list);
        versionList = activity.findViewById(R.id.local_version_list);

        startDownloadMcUIText = activity.findViewById(R.id.start_download_mc_ui);
        startDownloadMcUIText.setOnClickListener(this);

        startAddGameDirUI = activity.findViewById(R.id.start_add_game_directory_ui);
        startAddGameDirUI.setOnClickListener(this);
        startDownloadMcUI = activity.findViewById(R.id.start_ui_download_minecraft);
        startDownloadMcUI.setOnClickListener(this);
        startInstallPackageUI = activity.findViewById(R.id.start_install_package_ui);
        startInstallPackageUI.setOnClickListener(this);
        refresh = activity.findViewById(R.id.refresh_local_version_list);
        refresh.setOnClickListener(this);
        startGlobalSettingUI = activity.findViewById(R.id.start_ui_global_setting);
        startGlobalSettingUI.setOnClickListener(this);
        progressBar = activity.findViewById(R.id.loading_local_version_progress);

        contentListParent = activity.findViewById(R.id.content_list_parent);
        startAddGameDirUI.post(() -> gameDirList.setMaxHeight(contentListParent.getHeight() - startAddGameDirUI.getHeight() - ConvertUtils.dip2px(context,10)));

        new Thread(this::refreshVersionList).start();
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.version_list_ui_title),activity.uiManager.uis.get(activity.uiManager.uis.size() - 2) != activity.uiManager.mainUI,false);
        CustomAnimationUtils.showViewFromLeft(versionListUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(versionListUI,activity,context,true);
    }

    @Override
    public void onClick(View v) {
        if (v == startDownloadMcUIText || v == startDownloadMcUI){
            activity.uiManager.switchMainUI(activity.uiManager.downloadUI);
            activity.uiManager.downloadUI.downloadUIManager.switchDownloadUI(activity.uiManager.downloadUI.downloadUIManager.downloadMinecraftUI);
        }
        if (v == startAddGameDirUI){
            activity.uiManager.switchMainUI(activity.uiManager.addGameDirectoryUI);
        }
        if (v == startInstallPackageUI) {
            activity.uiManager.switchMainUI(activity.uiManager.installPackageUI);
        }
        if (v == refresh){
            new Thread(this::refreshVersionList).start();
        }
        if (v == startGlobalSettingUI){
            activity.uiManager.switchMainUI(activity.uiManager.settingUI);
            activity.uiManager.settingUI.settingUIManager.switchSettingUIs(activity.uiManager.settingUI.settingUIManager.universalGameSettingUI);
        }
    }

    private void init(){
        contentList = InitializeSetting.initializeContents(context);
        contentListAdapter = new ContentListAdapter(context,activity,contentList);
        gameDirList.setAdapter(contentListAdapter);
        gameListAdapter.refreshCurrentVersion(activity.publicGameSetting.currentVersion);
    }

    public void refreshVersionList(){
        activity.runOnUiThread(() -> {
            progressBar.setVisibility(View.VISIBLE);
            startDownloadMcUIText.setVisibility(View.GONE);
            versionList.setVisibility(View.GONE);
        });
        gameList = SettingUtils.getLocalVersionInfo(activity.launcherSetting.gameFileDirectory,activity.publicGameSetting.currentVersion);
        gameListAdapter = new GameListAdapter(context,activity,gameList);
        activity.runOnUiThread(() -> {
            versionList.setAdapter(gameListAdapter);
            if (gameList.size() != 0){
                startDownloadMcUIText.setVisibility(View.GONE);
                versionList.setVisibility(View.VISIBLE);
            }
            else {
                startDownloadMcUIText.setVisibility(View.VISIBLE);
                versionList.setVisibility(View.GONE);
            }
            progressBar.setVisibility(View.GONE);
        });
    }
}
