package com.tungsten.hmclpe.launcher.uis.game.download.right.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.optifine.OptifineVersion;
import com.tungsten.hmclpe.launcher.list.download.minecraft.DownloadOptifineListAdapter;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.io.NetworkUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

public class DownloadOptifineUI extends BaseUI implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    public LinearLayout downloadOptifineUI;

    public static final String OPTIFINE_VERSION_MANIFEST = "https://bmclapi2.bangbang93.com/optifine/versionlist";

    public String version;
    public boolean install;

    private LinearLayout hintLayout;

    private LinearLayout listLayout;

    private CheckBox checkRelease;
    private CheckBox checkSnapshot;
    private CheckBox checkOld;

    private LinearLayout refresh;

    private ListView optifineListView;
    private ProgressBar progressBar;
    private TextView back;

    private ArrayList<OptifineVersion> allList;

    public DownloadOptifineUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadOptifineUI = activity.findViewById(R.id.ui_install_optifine_list);

        hintLayout = activity.findViewById(R.id.download_forge_hint_layout);
        hintLayout.setOnClickListener(this);

        listLayout = activity.findViewById(R.id.optifine_list_layout);

        checkRelease = activity.findViewById(R.id.optifine_checkbox_release);
        checkSnapshot = activity.findViewById(R.id.optifine_checkbox_snapshot);
        checkOld = activity.findViewById(R.id.optifine_checkbox_old);

        refresh = activity.findViewById(R.id.refresh_optifine_list);

        checkRelease.setChecked(true);

        checkRelease.setOnCheckedChangeListener(this);
        checkSnapshot.setOnCheckedChangeListener(this);
        checkOld.setOnCheckedChangeListener(this);

        refresh.setOnClickListener(this);

        optifineListView = activity.findViewById(R.id.optifine_version_list);
        progressBar = activity.findViewById(R.id.loading_optifine_list_progress);
        back = activity.findViewById(R.id.back_to_install_ui_optifine);

        back.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.optifine_list_ui_title),false,true);
        CustomAnimationUtils.showViewFromLeft(downloadOptifineUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(downloadOptifineUI,activity,context,true);
    }

    private void init(){
        ArrayList<OptifineVersion> list = new ArrayList<>();
        allList = new ArrayList<>();
        new Thread(() -> {
            loadingHandler.sendEmptyMessage(0);
            try {
                String response = NetworkUtils.doGet(NetworkUtils.toURL(OPTIFINE_VERSION_MANIFEST));
                Gson gson = new Gson();
                OptifineVersion[] optifineVersions = gson.fromJson(response,OptifineVersion[].class);
                for (OptifineVersion versions : optifineVersions){
                    if (versions.mcVersion.equals(version) && checkRelease.isChecked() && !(versions.patch.startsWith("pre") || versions.patch.startsWith("alpha"))){
                        list.add(versions);
                    }
                    if (versions.mcVersion.equals(version) && checkSnapshot.isChecked() && (versions.patch.startsWith("pre") || versions.patch.startsWith("alpha"))){
                        list.add(versions);
                    }
                    if (versions.mcVersion.equals(version)){
                        allList.add(versions);
                    }
                }
                list.sort(new OptifineCompareTool());
                DownloadOptifineListAdapter downloadOptifineListAdapter = new DownloadOptifineListAdapter(context,activity,list,install);
                activity.runOnUiThread(() -> optifineListView.setAdapter(downloadOptifineListAdapter));
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (allList.size() == 0){
                loadingHandler.sendEmptyMessage(2);
            }
            else {
                loadingHandler.sendEmptyMessage(1);
            }
        }).start();
    }

    private void refresh(){
        ArrayList<OptifineVersion> list = new ArrayList<>();
        for (OptifineVersion versions : allList){
            if (versions.mcVersion.equals(version) && checkRelease.isChecked() && !(versions.patch.startsWith("pre") || versions.patch.startsWith("alpha"))){
                list.add(versions);
            }
            if (versions.mcVersion.equals(version) && checkSnapshot.isChecked() && (versions.patch.startsWith("pre") || versions.patch.startsWith("alpha"))){
                list.add(versions);
            }
        }
        list.sort(new OptifineCompareTool());
        DownloadOptifineListAdapter downloadOptifineListAdapter = new DownloadOptifineListAdapter(context,activity,list,install);
        optifineListView.setAdapter(downloadOptifineListAdapter);
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
        if (view == refresh){
            init();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler loadingHandler = new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                listLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                back.setVisibility(View.GONE);
            }
            if (msg.what == 1){
                listLayout.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
            if (msg.what == 2){
                listLayout.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                back.setVisibility(View.VISIBLE);
            }
        }
    };

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        refresh();
    }

    private static class OptifineCompareTool implements Comparator<OptifineVersion> {
        @Override
        public int compare(OptifineVersion versionPri, OptifineVersion versionSec) {
            if ((versionPri.patch.length() == versionSec.patch.length()) || (versionPri.patch.startsWith("pre") && versionSec.patch.startsWith("pre"))) {
                if (versionPri.patch.length() == 2) {
                    if(versionPri.patch.charAt(0) > versionSec.patch.charAt(0)) {
                        return -1;
                    }
                    else if(versionPri.patch.charAt(0) == versionSec.patch.charAt(0)) {
                        return Character.compare(versionSec.patch.charAt(1), versionPri.patch.charAt(1));
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    if(versionPri.type.substring(versionPri.type.length() - 2).charAt(0) > versionSec.type.substring(versionSec.type.length() - 2).charAt(0)) {
                        return -1;
                    }
                    else if(versionPri.type.substring(versionPri.type.length() - 2).charAt(0) == versionSec.type.substring(versionSec.type.length() - 2).charAt(0)) {
                        if(versionPri.type.substring(versionPri.type.length() - 2).charAt(1) > versionSec.type.substring(versionSec.type.length() - 2).charAt(1)) {
                            return -1;
                        }
                        else if(versionPri.type.substring(versionPri.type.length() - 2).charAt(1) == versionSec.type.substring(versionSec.type.length() - 2).charAt(1)) {
                            return Integer.compare(Integer.parseInt(versionSec.patch.replace("pre", "")), Integer.parseInt(versionPri.patch.replace("pre", "")));
                        }
                        else {
                            return 1;
                        }
                    }
                    else {
                        return 1;
                    }
                }
            }
            else {
                String priPatch;
                String secPatch;
                if (versionPri.patch.startsWith("pre")) {
                    priPatch = versionPri.type.substring(versionPri.type.length() - 2);
                    secPatch = versionSec.patch;
                }
                else {
                    priPatch = versionPri.patch;
                    secPatch = versionSec.type.substring(versionSec.type.length() - 2);
                }
                if(priPatch.charAt(0) > secPatch.charAt(0)) {
                    return -1;
                }
                else if(priPatch.charAt(0) == secPatch.charAt(0)) {
                    if(priPatch.charAt(1) > secPatch.charAt(1)) {
                        return -1;
                    }
                    else if(priPatch.charAt(1) == secPatch.charAt(1)) {
                        return Integer.compare(versionPri.patch.length(),versionSec.patch.length());
                    }
                    else {
                        return 1;
                    }
                }
                else {
                    return 1;
                }
            }
        }
    }
}
