package com.tungsten.hmclpe.launcher.uis.game.manager.right;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.GameInstallLocalDialog;
import com.tungsten.hmclpe.launcher.download.PatchMerger;
import com.tungsten.hmclpe.launcher.game.Argument;
import com.tungsten.hmclpe.launcher.game.Artifact;
import com.tungsten.hmclpe.launcher.game.RuledArgument;
import com.tungsten.hmclpe.launcher.game.Version;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.platform.Bits;

import java.io.File;

public class AutoInstallUI extends BaseUI implements View.OnClickListener {

    private static final int SELECT_INSTALLER_REQUEST_CODE = 4700;

    public String versionName;
    private Version version;

    public LinearLayout autoInstallUI;

    private TextView gameVersionText;
    private TextView forgeVersionText;
    private TextView liteLoaderVersionText;
    private TextView optiFineVersionText;
    private TextView fabricVersionText;
    private TextView fabricAPIVersionText;

    private ImageButton deleteForgeVersion;
    private ImageButton deleteLiteLoaderVersion;
    private ImageButton deleteOptiFineVersion;
    private ImageButton deleteFabricVersion;

    private LinearLayout selectForgeVersion;
    private LinearLayout selectLiteLoaderVersion;
    private LinearLayout selectOptiFineVersion;
    private LinearLayout selectFabricVersion;
    private LinearLayout selectFabricAPIVersion;

    private ImageView selectForge;
    private ImageView selectLiteLoader;
    private ImageView selectOptiFine;
    private ImageView selectFabric;

    private LinearLayout installLocal;

    public String gameVersion;
    public String forgeVersion;
    public String optifineVersion;
    public String liteLoaderVersion;
    public String fabricVersion;

    public AutoInstallUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        autoInstallUI = activity.findViewById(R.id.ui_auto_install);

        installLocal = activity.findViewById(R.id.install_from_local);
        installLocal.setOnClickListener(this);

        gameVersionText = activity.findViewById(R.id.current_minecraft_version_text);
        forgeVersionText = activity.findViewById(R.id.current_forge_version_text);
        liteLoaderVersionText = activity.findViewById(R.id.current_liteloader_version_text);
        optiFineVersionText = activity.findViewById(R.id.current_optifine_version_text);
        fabricVersionText = activity.findViewById(R.id.current_fabric_version_text);
        fabricAPIVersionText = activity.findViewById(R.id.current_fabric_api_version_text);

        deleteForgeVersion = activity.findViewById(R.id.uninstall_forge);
        deleteLiteLoaderVersion = activity.findViewById(R.id.uninstall_liteloader);
        deleteOptiFineVersion = activity.findViewById(R.id.uninstall_optifine);
        deleteFabricVersion = activity.findViewById(R.id.uninstall_fabric);
        deleteForgeVersion.setOnClickListener(this);
        deleteLiteLoaderVersion.setOnClickListener(this);
        deleteOptiFineVersion.setOnClickListener(this);
        deleteFabricVersion.setOnClickListener(this);

        selectForgeVersion = activity.findViewById(R.id.update_forge_version);
        selectLiteLoaderVersion = activity.findViewById(R.id.update_liteloader_version);
        selectOptiFineVersion = activity.findViewById(R.id.update_optifine_version);
        selectFabricVersion = activity.findViewById(R.id.update_fabric_version);
        selectFabricAPIVersion = activity.findViewById(R.id.update_fabric_api_version);
        selectForgeVersion.setOnClickListener(this);
        selectLiteLoaderVersion.setOnClickListener(this);
        selectOptiFineVersion.setOnClickListener(this);
        selectFabricVersion.setOnClickListener(this);
        selectFabricAPIVersion.setOnClickListener(this);

        selectForge = activity.findViewById(R.id.update_forge);
        selectLiteLoader = activity.findViewById(R.id.update_lite_loader);
        selectOptiFine = activity.findViewById(R.id.update_optifine);
        selectFabric = activity.findViewById(R.id.update_fabric);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(autoInstallUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.gameManagerUI.startAutoInstall.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(autoInstallUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.gameManagerUI.startAutoInstall.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_INSTALLER_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            GameInstallLocalDialog dialog = new GameInstallLocalDialog(context,activity,versionName,gameVersion,UriUtils.getRealPathFromUri_AboveApi19(context,uri));
            dialog.show();
        }
    }

    public void refresh(String versionName){
        this.versionName = versionName;
        this.forgeVersion = null;
        this.liteLoaderVersion = null;
        this.optifineVersion = null;
        this.fabricVersion = null;
        String gameJsonText = FileStringUtils.getStringFromFile(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName +"/" + versionName + ".json");
        Gson gson = JsonUtils.defaultGsonBuilder()
                .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                .registerTypeAdapter(Bits.class, new Bits.Serializer())
                .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                .create();
        version = gson.fromJson(gameJsonText, Version.class);
        if (version.getPatches() != null && version.getPatches().size() > 0) {
            for (Version p : version.getPatches()) {
                switch (p.getId()) {
                    case "game":
                        gameVersion = p.getVersion();
                        break;
                    case "forge":
                        forgeVersion = p.getVersion();
                        break;
                    case "optifine":
                        optifineVersion = p.getVersion();
                        break;
                    case "fabric":
                        fabricVersion = p.getVersion();
                        break;
                    case "liteloader":
                        liteLoaderVersion = p.getVersion();
                        break;
                }
            }
        }
        else {
            gameVersion = version.getId();
        }
        refreshView();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void refreshView(){
        gameVersionText.setText(gameVersion);
        if (forgeVersion != null || optifineVersion != null){
            forgeVersionText.setText(forgeVersion == null ? context.getString(R.string.install_game_ui_none) : forgeVersion);
            optiFineVersionText.setText(optifineVersion == null ? context.getString(R.string.install_game_ui_none) : optifineVersion);
            fabricVersionText.setText(optifineVersion != null ? context.getString(R.string.install_game_ui_optifine_not_compatible) : context.getString(R.string.install_game_ui_forge_not_compatible));
            fabricAPIVersionText.setText(optifineVersion != null ? context.getString(R.string.install_game_ui_optifine_not_compatible) : context.getString(R.string.install_game_ui_forge_not_compatible));
            deleteForgeVersion.setVisibility(forgeVersion != null ? View.VISIBLE : View.GONE);
            deleteOptiFineVersion.setVisibility(optifineVersion != null ? View.VISIBLE : View.GONE);
            selectFabric.setVisibility(View.GONE);
        }
        else {
            forgeVersionText.setText(context.getString(R.string.install_game_ui_none));
            optiFineVersionText.setText(context.getString(R.string.install_game_ui_none));
            fabricVersionText.setText(context.getString(R.string.install_game_ui_none));
            fabricAPIVersionText.setText(context.getString(R.string.install_game_ui_none));
            deleteForgeVersion.setVisibility(View.GONE);
            deleteOptiFineVersion.setVisibility(View.GONE);
            selectFabric.setVisibility(View.VISIBLE);
        }
        if (fabricVersion != null){
            forgeVersionText.setText(context.getString(R.string.install_game_ui_fabric_not_compatible));
            optiFineVersionText.setText(context.getString(R.string.install_game_ui_fabric_not_compatible));
            liteLoaderVersionText.setText(context.getString(R.string.install_game_ui_fabric_not_compatible));
            fabricVersionText.setText(fabricVersion);
            deleteFabricVersion.setVisibility(View.VISIBLE);
            selectForge.setVisibility(View.GONE);
            selectLiteLoader.setVisibility(View.GONE);
            selectOptiFine.setVisibility(View.GONE);
        }
        else {
            if (forgeVersion == null){
                forgeVersionText.setText(context.getString(R.string.install_game_ui_none));
            }
            if (optifineVersion == null){
                optiFineVersionText.setText(context.getString(R.string.install_game_ui_none));
            }
            if (liteLoaderVersion == null){
                liteLoaderVersionText.setText(context.getString(R.string.install_game_ui_none));
            }
            deleteFabricVersion.setVisibility(View.GONE);
            selectForge.setVisibility(View.VISIBLE);
            selectLiteLoader.setVisibility(View.VISIBLE);
            selectOptiFine.setVisibility(View.VISIBLE);
        }
        if (liteLoaderVersion != null){
            liteLoaderVersionText.setText(liteLoaderVersion);
            deleteLiteLoaderVersion.setVisibility(View.VISIBLE);
        }
        else {
            if (fabricVersion == null){
                liteLoaderVersionText.setText(context.getString(R.string.install_game_ui_none));
            }
            deleteLiteLoaderVersion.setVisibility(View.GONE);
        }
        selectForge.setBackground(deleteForgeVersion.getVisibility() == View.VISIBLE ? context.getDrawable(R.drawable.ic_baseline_update_black) : context.getDrawable(R.drawable.ic_baseline_arrow_forward_black));
        selectLiteLoader.setBackground(deleteLiteLoaderVersion.getVisibility() == View.VISIBLE ? context.getDrawable(R.drawable.ic_baseline_update_black) : context.getDrawable(R.drawable.ic_baseline_arrow_forward_black));
        selectOptiFine.setBackground(deleteOptiFineVersion.getVisibility() == View.VISIBLE ? context.getDrawable(R.drawable.ic_baseline_update_black) : context.getDrawable(R.drawable.ic_baseline_arrow_forward_black));
        selectFabric.setBackground(deleteFabricVersion.getVisibility() == View.VISIBLE ? context.getDrawable(R.drawable.ic_baseline_update_black) : context.getDrawable(R.drawable.ic_baseline_arrow_forward_black));
    }

    public void uninstall(String id) {
        version = PatchMerger.reMergePatch(context, version, null, id, () -> {});
        String path = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName +"/" + versionName + ".json";
        Gson gson = JsonUtils.defaultGsonBuilder()
                .registerTypeAdapter(Artifact.class, new Artifact.Serializer())
                .registerTypeAdapter(Bits.class, new Bits.Serializer())
                .registerTypeAdapter(RuledArgument.class, new RuledArgument.Serializer())
                .registerTypeAdapter(Argument.class, new Argument.Deserializer())
                .create();
        String s = gson.toJson(version);
        FileStringUtils.writeFile(path,s);
        refresh(versionName);
        new Thread(() -> {
            activity.uiManager.versionListUI.refreshVersionList();
        }).start();
    }

    @Override
    public void onClick(View view) {
        if (view == installLocal){
            Intent intent = new Intent(context, FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "jar");
            intent.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getAbsolutePath());
            activity.startActivityForResult(intent, SELECT_INSTALLER_REQUEST_CODE);
        }

        if (view == deleteForgeVersion){
            if (forgeVersion != null){
                forgeVersion = null;
                uninstall("forge");
            }
        }
        if (view == deleteLiteLoaderVersion){
            if (liteLoaderVersion != null){
                liteLoaderVersion = null;
                uninstall("liteloader");
            }
        }
        if (view == deleteOptiFineVersion){
            if (optifineVersion != null){
                optifineVersion = null;
                uninstall("optifine");
            }
        }
        if (view == deleteFabricVersion){
            if (fabricVersion != null){
                fabricVersion = null;
                uninstall("fabric");
            }
        }
        if (view == selectForgeVersion && fabricVersion == null){
            activity.uiManager.downloadForgeUI.version = gameVersion;
            activity.uiManager.downloadForgeUI.install = true;
            activity.uiManager.switchMainUI(activity.uiManager.downloadForgeUI);
        }
        if (view == selectLiteLoaderVersion && fabricVersion == null){
            activity.uiManager.downloadLiteLoaderUI.version = gameVersion;
            activity.uiManager.downloadLiteLoaderUI.install = true;
            activity.uiManager.switchMainUI(activity.uiManager.downloadLiteLoaderUI);
        }
        if (view == selectOptiFineVersion && fabricVersion == null){
            activity.uiManager.downloadOptifineUI.version = gameVersion;
            activity.uiManager.downloadOptifineUI.install = true;
            activity.uiManager.switchMainUI(activity.uiManager.downloadOptifineUI);
        }
        if (view == selectFabricVersion && forgeVersion == null && optifineVersion == null){
            activity.uiManager.downloadFabricUI.version = gameVersion;
            activity.uiManager.downloadFabricUI.install = true;
            activity.uiManager.switchMainUI(activity.uiManager.downloadFabricUI);
        }
        if (view == selectFabricAPIVersion && forgeVersion == null && optifineVersion == null){
            activity.uiManager.downloadFabricAPIUI.version = gameVersion;
            activity.uiManager.downloadFabricAPIUI.install = true;
            activity.uiManager.switchMainUI(activity.uiManager.downloadFabricAPIUI);
        }
    }
}
