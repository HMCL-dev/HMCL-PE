package com.tungsten.hmclpe.launcher.uis.game.download.right.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.GameInstallDialog;
import com.tungsten.hmclpe.launcher.download.fabric.FabricLoaderVersion;
import com.tungsten.hmclpe.launcher.download.forge.ForgeVersion;
import com.tungsten.hmclpe.launcher.download.game.VersionManifest;
import com.tungsten.hmclpe.launcher.download.liteloader.LiteLoaderVersion;
import com.tungsten.hmclpe.launcher.download.optifine.OptifineVersion;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

public class InstallGameUI extends BaseUI implements View.OnClickListener, TextWatcher {

    public LinearLayout installGameUI;

    public String name;

    public VersionManifest.Version version;
    public ForgeVersion forgeVersion;
    public OptifineVersion optifineVersion;
    public LiteLoaderVersion liteLoaderVersion;
    public FabricLoaderVersion fabricVersion;
    public RemoteMod.Version fabricAPIVersion;

    private EditText editName;

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
    private ImageButton deleteFabricAPIVersion;

    private LinearLayout selectForgeVersion;
    private LinearLayout selectLiteLoaderVersion;
    private LinearLayout selectOptiFineVersion;
    private LinearLayout selectFabricVersion;
    private LinearLayout selectFabricAPIVersion;

    private ImageView selectForge;
    private ImageView selectLiteLoader;
    private ImageView selectOptiFine;
    private ImageView selectFabric;
    private ImageView selectFabricAPI;

    private Button install;

    public InstallGameUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        installGameUI = activity.findViewById(R.id.ui_install_game);

        editName = activity.findViewById(R.id.edit_game_name);
        editName.addTextChangedListener(this);

        gameVersionText = activity.findViewById(R.id.minecraft_version_text);
        forgeVersionText = activity.findViewById(R.id.forge_version_text);
        liteLoaderVersionText = activity.findViewById(R.id.liteloader_version_text);
        optiFineVersionText = activity.findViewById(R.id.optifine_version_text);
        fabricVersionText = activity.findViewById(R.id.fabric_version_text);
        fabricAPIVersionText = activity.findViewById(R.id.fabric_api_version_text);

        deleteForgeVersion = activity.findViewById(R.id.call_off_install_forge);
        deleteLiteLoaderVersion = activity.findViewById(R.id.call_off_install_liteloader);
        deleteOptiFineVersion = activity.findViewById(R.id.call_off_install_optifine);
        deleteFabricVersion = activity.findViewById(R.id.call_off_install_fabric);
        deleteFabricAPIVersion = activity.findViewById(R.id.call_off_install_fabric_api);
        deleteForgeVersion.setOnClickListener(this);
        deleteLiteLoaderVersion.setOnClickListener(this);
        deleteOptiFineVersion.setOnClickListener(this);
        deleteFabricVersion.setOnClickListener(this);
        deleteFabricAPIVersion.setOnClickListener(this);

        selectForgeVersion = activity.findViewById(R.id.select_forge_version);
        selectLiteLoaderVersion = activity.findViewById(R.id.select_liteloader_version);
        selectOptiFineVersion = activity.findViewById(R.id.select_optifine_version);
        selectFabricVersion = activity.findViewById(R.id.select_fabric_version);
        selectFabricAPIVersion = activity.findViewById(R.id.select_fabric_api_version);
        selectForgeVersion.setOnClickListener(this);
        selectLiteLoaderVersion.setOnClickListener(this);
        selectOptiFineVersion.setOnClickListener(this);
        selectFabricVersion.setOnClickListener(this);
        selectFabricAPIVersion.setOnClickListener(this);

        selectForge = activity.findViewById(R.id.select_forge);
        selectLiteLoader = activity.findViewById(R.id.select_lite_loader);
        selectOptiFine = activity.findViewById(R.id.select_optifine);
        selectFabric = activity.findViewById(R.id.select_fabric);
        selectFabricAPI = activity.findViewById(R.id.select_fabric_api);

        install = activity.findViewById(R.id.install_game);
        install.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.install_game_ui_title),false,true);
        CustomAnimationUtils.showViewFromLeft(installGameUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(installGameUI,activity,context,true);
    }

    @Override
    public void onClick(View v) {
        if (v == deleteForgeVersion){
            if (forgeVersion != null){
                forgeVersion = null;
                init();
            }
        }
        if (v == deleteLiteLoaderVersion){
            if (liteLoaderVersion != null){
                liteLoaderVersion = null;
                init();
            }
        }
        if (v == deleteOptiFineVersion){
            if (optifineVersion != null){
                optifineVersion = null;
                init();
            }
        }
        if (v == deleteFabricVersion){
            if (fabricVersion != null){
                fabricVersion = null;
                init();
            }
        }
        if (v == deleteFabricAPIVersion){
            if (fabricAPIVersion != null){
                fabricAPIVersion = null;
                init();
            }
        }
        if (v == selectForgeVersion && fabricVersion == null){
            activity.uiManager.downloadForgeUI.version = version.id;
            activity.uiManager.downloadForgeUI.install = false;
            activity.uiManager.switchMainUI(activity.uiManager.downloadForgeUI);
        }
        if (v == selectLiteLoaderVersion && fabricVersion == null){
            activity.uiManager.downloadLiteLoaderUI.version = version.id;
            activity.uiManager.downloadLiteLoaderUI.install = false;
            activity.uiManager.switchMainUI(activity.uiManager.downloadLiteLoaderUI);
        }
        if (v == selectOptiFineVersion && fabricVersion == null){
            activity.uiManager.downloadOptifineUI.version = version.id;
            activity.uiManager.downloadOptifineUI.install = false;
            activity.uiManager.switchMainUI(activity.uiManager.downloadOptifineUI);
        }
        if (v == selectFabricVersion && forgeVersion == null && optifineVersion == null){
            activity.uiManager.downloadFabricUI.version = version.id;
            activity.uiManager.downloadFabricUI.install = false;
            activity.uiManager.switchMainUI(activity.uiManager.downloadFabricUI);
        }
        if (v == selectFabricAPIVersion && forgeVersion == null && optifineVersion == null){
            activity.uiManager.downloadFabricAPIUI.version = version.id;
            activity.uiManager.downloadFabricAPIUI.install = false;
            activity.uiManager.switchMainUI(activity.uiManager.downloadFabricAPIUI);
        }
        if (v == install){
            boolean exist = SettingUtils.getLocalVersionNames(activity.launcherSetting.gameFileDirectory).contains(editName.getText().toString());
            if (exist){
                Toast.makeText(context,context.getString(R.string.install_game_ui_exist),Toast.LENGTH_SHORT).show();
            }
            else {
                if (forgeVersion != null || optifineVersion != null) {
                    fabricAPIVersion = null;
                }
                GameInstallDialog gameInstallDialog = new GameInstallDialog(context,activity,editName.getText().toString(),version,forgeVersion,optifineVersion,liteLoaderVersion,fabricVersion,fabricAPIVersion);
                gameInstallDialog.show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init(){
        editName.setText(name);
        gameVersionText.setText(version.id);
        if (forgeVersion != null || optifineVersion != null){
            forgeVersionText.setText(forgeVersion == null ? context.getString(R.string.install_game_ui_none) : forgeVersion.getVersion());
            optiFineVersionText.setText(optifineVersion == null ? context.getString(R.string.install_game_ui_none) : optifineVersion.type + "_" + optifineVersion.patch);
            fabricVersionText.setText(optifineVersion != null ? context.getString(R.string.install_game_ui_optifine_not_compatible) : context.getString(R.string.install_game_ui_forge_not_compatible));
            fabricAPIVersionText.setText(optifineVersion != null ? context.getString(R.string.install_game_ui_optifine_not_compatible) : context.getString(R.string.install_game_ui_forge_not_compatible));
            deleteForgeVersion.setVisibility(forgeVersion != null ? View.VISIBLE : View.GONE);
            deleteOptiFineVersion.setVisibility(optifineVersion != null ? View.VISIBLE : View.GONE);
            selectFabric.setVisibility(View.GONE);
            selectFabricAPI.setVisibility(View.GONE);
        }
        else {
            forgeVersionText.setText(context.getString(R.string.install_game_ui_none));
            optiFineVersionText.setText(context.getString(R.string.install_game_ui_none));
            fabricVersionText.setText(context.getString(R.string.install_game_ui_none));
            fabricAPIVersionText.setText(context.getString(R.string.install_game_ui_none));
            deleteForgeVersion.setVisibility(View.GONE);
            deleteOptiFineVersion.setVisibility(View.GONE);
            selectFabric.setVisibility(View.VISIBLE);
            selectFabricAPI.setVisibility(View.VISIBLE);
        }
        if (fabricVersion != null){
            forgeVersionText.setText(context.getString(R.string.install_game_ui_fabric_not_compatible));
            optiFineVersionText.setText(context.getString(R.string.install_game_ui_fabric_not_compatible));
            liteLoaderVersionText.setText(context.getString(R.string.install_game_ui_fabric_not_compatible));
            fabricVersionText.setText(fabricVersion.version);
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
            liteLoaderVersionText.setText(liteLoaderVersion.getVersion());
            deleteLiteLoaderVersion.setVisibility(View.VISIBLE);
        }
        else {
            if (fabricVersion == null){
                liteLoaderVersionText.setText(context.getString(R.string.install_game_ui_none));
            }
            deleteLiteLoaderVersion.setVisibility(View.GONE);
        }
        if (forgeVersion != null || optifineVersion != null) {
            fabricAPIVersionText.setText(optifineVersion != null ? context.getString(R.string.install_game_ui_optifine_not_compatible) : context.getString(R.string.install_game_ui_forge_not_compatible));
            deleteFabricAPIVersion.setVisibility(View.GONE);
            selectFabricAPI.setVisibility(View.GONE);
        }
        else if (fabricAPIVersion == null) {
            fabricAPIVersionText.setText(context.getString(R.string.install_game_ui_none));
            deleteFabricAPIVersion.setVisibility(View.GONE);
            selectFabricAPI.setVisibility(View.VISIBLE);
        }
        else {
            fabricAPIVersionText.setText(fabricAPIVersion.getVersion());
            deleteFabricAPIVersion.setVisibility(View.VISIBLE);
            selectFabricAPI.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        name = editName.getText().toString();
    }
}
