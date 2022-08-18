package com.tungsten.hmclpe.launcher.uis.game.manager.right;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileBrowser;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.CheckModUpdateDialog;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.list.local.mod.LocalModListAdapter;
import com.tungsten.hmclpe.launcher.list.local.save.DatapackListAdapter;
import com.tungsten.hmclpe.launcher.mod.Datapack;
import com.tungsten.hmclpe.launcher.mod.LocalModFile;
import com.tungsten.hmclpe.launcher.mod.ModManager;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.launcher.uis.game.manager.universal.PackMcManagerUI;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

public class ModManagerUI extends BaseUI implements View.OnClickListener {

    private static final int ADD_MOD_REQUEST = 4100;

    public LinearLayout modManagerUI;

    private String versionName;
    private String versionId;
    private String modsDir;
    public ModManager modManager;

    public LinearLayout mainBar;
    private LinearLayout refresh;
    private LinearLayout addMod;
    private LinearLayout openFolder;
    private LinearLayout checkUpdate;
    private LinearLayout download;

    public LinearLayout subBar;
    private LinearLayout delete;
    private LinearLayout enable;
    private LinearLayout disable;
    private LinearLayout selectAll;
    private LinearLayout cancel;

    private ProgressBar progressBar;
    private ListView modList;

    private ArrayList<LocalModFile> allModList;
    private LocalModListAdapter localModListAdapter;

    public ArrayList<LocalModFile> selectedMods;

    public ModManagerUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        modManagerUI = activity.findViewById(R.id.ui_mod_manager);

        mainBar = activity.findViewById(R.id.mod_manager_main_bar);
        refresh = activity.findViewById(R.id.refresh_local_mod);
        addMod = activity.findViewById(R.id.add_new_mod);
        openFolder = activity.findViewById(R.id.open_mod_folder);
        checkUpdate = activity.findViewById(R.id.check_mod_update);
        download = activity.findViewById(R.id.download_new_mod);

        subBar = activity.findViewById(R.id.mod_manager_sub_bar);
        delete = activity.findViewById(R.id.delete_local_mod);
        enable = activity.findViewById(R.id.enable_local_mod);
        disable = activity.findViewById(R.id.disable_local_mod);
        selectAll = activity.findViewById(R.id.select_all_local_mod);
        cancel = activity.findViewById(R.id.cancel_manage_local_mod);

        progressBar = activity.findViewById(R.id.load_mod_progress);
        modList = activity.findViewById(R.id.local_mod_list);

        refresh.setOnClickListener(this);
        addMod.setOnClickListener(this);
        openFolder.setOnClickListener(this);
        checkUpdate.setOnClickListener(this);
        download.setOnClickListener(this);

        delete.setOnClickListener(this);
        enable.setOnClickListener(this);
        disable.setOnClickListener(this);
        selectAll.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(modManagerUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.gameManagerUI.startModManager.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(modManagerUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.gameManagerUI.startModManager.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    public void refresh(String versionName){
        selectedMods = new ArrayList<>();
        this.versionName = versionName;
        String gameJsonText = FileStringUtils.getStringFromFile(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName +"/" + versionName + ".json");
        Gson gson = JsonUtils.defaultGsonBuilder()
                .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                .registerTypeAdapter(Bits.class, new Bits.Serializer())
                .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                .create();
        Version version = gson.fromJson(gameJsonText, Version.class);
        this.versionId = version.getId();
        PrivateGameSetting privateGameSetting;
        String settingPath = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg";
        if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null && (GsonUtils.getPrivateGameSettingFromFile(settingPath).forceEnable || GsonUtils.getPrivateGameSettingFromFile(settingPath).enable)) {
            privateGameSetting = GsonUtils.getPrivateGameSettingFromFile(settingPath);
        }
        else {
            privateGameSetting = activity.privateGameSetting;
        }
        if (privateGameSetting.gameDirSetting.type == 0){
            modsDir = activity.launcherSetting.gameFileDirectory + "/mods";
        }
        else if (privateGameSetting.gameDirSetting.type == 1){
            modsDir = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/mods";
        }
        else {
            modsDir = privateGameSetting.gameDirSetting.path + "/mods";
        }
        refreshList();
    }

    public void refreshList() {
        selectedMods = new ArrayList<>();
        new Thread(() -> {
            activity.runOnUiThread(() -> {
                progressBar.setVisibility(View.VISIBLE);
                modList.setVisibility(View.GONE);
            });
            modManager = new ModManager(modsDir);
            allModList = new ArrayList<>();
            try {
                allModList.addAll(modManager.getMods());
            } catch (IOException e) {
                Log.e("getModsException",e.toString());
                e.printStackTrace();
            }
            localModListAdapter = new LocalModListAdapter(context,activity,allModList,this);
            activity.runOnUiThread(() -> {
                modList.setAdapter(localModListAdapter);
                progressBar.setVisibility(View.GONE);
                modList.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_MOD_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                ArrayList<Uri> selectedFiles  = data.getParcelableArrayListExtra(Constants.SELECTED_ITEMS);
                ArrayList<Path> list = new ArrayList<>();
                for (Uri uri : selectedFiles) {
                    String path = UriUtils.getRealPathFromUri_AboveApi19(context,uri);
                    if (path != null) {
                        list.add(new File(path).toPath());
                    }
                }
                new Thread(() -> {
                    for (Path path : list) {
                        try {
                            modManager.addMod(path);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    activity.runOnUiThread(this::refreshList);
                }).start();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == refresh) {
            refreshList();
        }
        if (view == addMod) {
            Intent intent = new Intent(context, FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.MULTIPLE_SELECTION.ordinal());
            intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "jar;zip");
            intent.putExtra(Constants.INITIAL_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath());
            activity.startActivityForResult(intent, ADD_MOD_REQUEST);
        }
        if (view == openFolder) {
            Intent intent = new Intent(context, FileBrowser.class);
            FileUtils.createDirectory(modsDir);
            intent.putExtra(Constants.INITIAL_DIRECTORY, modsDir);
            context.startActivity(intent);
        }
        if (view == checkUpdate) {
            CheckModUpdateDialog dialog = new CheckModUpdateDialog(context, activity, allModList, modManager, versionId);
            dialog.show();
        }
        if (view == download) {
            activity.uiManager.switchMainUI(activity.uiManager.downloadUI);
            activity.uiManager.downloadUI.downloadUIManager.switchDownloadUI(activity.uiManager.downloadUI.downloadUIManager.downloadModUI);
        }
        if (view == delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.dialog_delete_mod_title));
            builder.setMessage(context.getString(R.string.dialog_delete_mod_msg));
            builder.setPositiveButton(context.getString(R.string.dialog_delete_mod_positive), (dialogInterface, i) -> {
                progressBar.setVisibility(View.VISIBLE);
                modList.setVisibility(View.GONE);
                new Thread(() -> {
                    try {
                        modManager.removeMods(selectedMods.toArray(new LocalModFile[0]));
                        activity.runOnUiThread(this::refreshList);
                    } catch (IOException e) {
                        activity.runOnUiThread(() -> {
                            Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                        });
                        e.printStackTrace();
                    }
                    activity.runOnUiThread(() -> {
                        selectedMods = new ArrayList<>();
                        mainBar.setVisibility(View.VISIBLE);
                        subBar.setVisibility(View.GONE);
                    });
                }).start();
            });
            builder.setNegativeButton(context.getString(R.string.dialog_delete_mod_negative), (dialogInterface, i) -> {});
            builder.create().show();
        }
        if (view == enable) {
            for (LocalModFile mod : selectedMods) {
                try {
                    mod.setActive(true);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            localModListAdapter.notifyDataSetChanged();
        }
        if (view == disable) {
            for (LocalModFile mod : selectedMods) {
                try {
                    mod.setActive(false);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            localModListAdapter.notifyDataSetChanged();
        }
        if (view == selectAll) {
            selectedMods.clear();
            selectedMods.addAll(localModListAdapter.getList());
            localModListAdapter.notifyDataSetChanged();
        }
        if (view == cancel) {
            selectedMods = new ArrayList<>();
            localModListAdapter = new LocalModListAdapter(context,activity,allModList,this);
            modList.setAdapter(localModListAdapter);
            if (mainBar.getVisibility() == View.GONE && subBar.getVisibility() == View.VISIBLE) {
                mainBar.setVisibility(View.VISIBLE);
                subBar.setVisibility(View.GONE);
            }
        }
    }
}
