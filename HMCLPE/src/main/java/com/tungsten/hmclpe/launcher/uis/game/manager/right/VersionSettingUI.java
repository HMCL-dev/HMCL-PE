package com.tungsten.hmclpe.launcher.uis.game.manager.right;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.widget.SwitchCompat;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.filepicker.FolderChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.ControlPatternActivity;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.VerifyInterface;
import com.tungsten.hmclpe.launcher.dialogs.control.ControllerManagerDialog;
import com.tungsten.hmclpe.launcher.list.local.game.GameListBean;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.animation.HiddenAnimationUtils;
import com.tungsten.hmclpe.utils.file.DrawableUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.platform.MemoryUtils;

import java.io.File;
import java.util.ArrayList;

public class VersionSettingUI extends BaseUI implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    public LinearLayout versionSettingUI;

    public static final int PICK_GAME_DIR_REQUEST_ISOLATED = 7300;
    public static final int SELECT_ICON_REQUEST = 8600;

    public String versionName;

    private ImageView icon;
    private ImageButton editVersionIcon;
    private ImageButton deleteVersionIcon;

    private CheckBox checkIsolateSetting;
    private Button switchToGlobalSetting;

    private LinearLayout isolateSettingLayout;

    private PrivateGameSetting privateGameSetting;

    private LinearLayout showJavaSetting;
    private TextView javaPathText;
    private ImageView showJava;
    private LinearLayout javaSetting;
    private int javaSettingHeight;
    private LinearLayout showGameDirSetting;
    private TextView gameDirText;
    private ImageView showGameDir;
    private LinearLayout gameDirSetting;
    private int gameDirSettingHeight;

    private LinearLayout showGameLauncherSetting;
    private TextView currentLauncher;
    private ImageView showGameLauncher;
    private LinearLayout gameLauncherSetting;
    private int gameLauncherSettingHeight;
    private LinearLayout showBoatRendererSetting;
    private TextView currentBoatRenderer;
    private ImageView showBoatRenderer;
    private LinearLayout boatRendererSetting;
    private int boatRendererSettingHeight;
    private LinearLayout showPojavRendererSetting;
    private TextView currentPojavRenderer;
    private ImageView showPojavRenderer;
    private LinearLayout pojavRendererSetting;
    private int pojavRendererSettingHeight;

    private RadioButton checkJavaAuto;
    private RadioButton checkJava8;
    private RadioButton checkJava17;
    private TextView java8Path;
    private TextView java17Path;

    private RadioButton checkGameDirDefault;
    private RadioButton checkGameDirIsolate;
    private RadioButton checkGameDirCustom;
    private EditText editGameDir;
    private ImageButton selectGameDir;

    private RadioButton launchByBoat;
    private RadioButton launchByPojav;

    private RadioButton boatRendererGL4ES114;
    private RadioButton boatRendererVirGL;

    private RadioButton pojavRendererGL4ES114;
    private RadioButton pojavRendererVirGL;

    private CheckBox checkAutoRam;
    private SeekBar ramSeekBar;
    private EditText editRam;
    private ProgressBar ramProgressBar;
    private TextView usedRamText;
    private TextView actualRamText;

    private SeekBar scaleFactorSeekBar;
    private EditText editScaleFactor;

    private SwitchCompat checkLog;

    private SwitchCompat notCheckGameFile;
    private SwitchCompat notCheckForge;
    private SwitchCompat notCheckJVM;

    private EditText editServer;

    private EditText editJVMArgs;

    private Button manageController;
    private TextView currentControlPattern;
    private ControllerManagerDialog controllerManagerDialog;

    private SwitchCompat checkTouchInjector;

    public VersionSettingUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate() {
        super.onCreate();
        versionSettingUI = activity.findViewById(R.id.ui_version_setting);

        icon = activity.findViewById(R.id.version_icon_view);
        editVersionIcon = activity.findViewById(R.id.edit_version_icon);
        deleteVersionIcon = activity.findViewById(R.id.reset_version_icon);

        checkIsolateSetting = activity.findViewById(R.id.check_isolated_setting);
        switchToGlobalSetting = activity.findViewById(R.id.start_global_game_setting_from_private);
        isolateSettingLayout = activity.findViewById(R.id.isolate_game_setting);

        showJavaSetting = activity.findViewById(R.id.show_java_selector_isolate);
        javaPathText = activity.findViewById(R.id.java_path_text_isolate);
        showJava = activity.findViewById(R.id.show_java_isolate);
        javaSetting = activity.findViewById(R.id.java_setting_isolate);
        showGameDirSetting = activity.findViewById(R.id.show_game_directory_selector_isolate);
        gameDirText = activity.findViewById(R.id.game_directory_text_isolate);
        showGameDir = activity.findViewById(R.id.show_game_dir_isolate);
        gameDirSetting = activity.findViewById(R.id.game_dir_setting_isolate);

        showGameLauncherSetting = activity.findViewById(R.id.show_game_launcher_selector_isolate);
        currentLauncher = activity.findViewById(R.id.current_launcher_isolate);
        showGameLauncher = activity.findViewById(R.id.show_game_launcher_isolate);
        gameLauncherSetting = activity.findViewById(R.id.game_launcher_selector_isolate);
        showBoatRendererSetting = activity.findViewById(R.id.show_boat_render_selector_isolate);
        currentBoatRenderer = activity.findViewById(R.id.current_boat_renderer_isolate);
        showBoatRenderer = activity.findViewById(R.id.show_boat_renderer_isolate);
        boatRendererSetting = activity.findViewById(R.id.boat_render_selector_isolate);
        showPojavRendererSetting = activity.findViewById(R.id.show_pojav_render_selector_isolate);
        currentPojavRenderer = activity.findViewById(R.id.current_pojav_renderer_isolate);
        showPojavRenderer = activity.findViewById(R.id.show_pojav_renderer_isolate);
        pojavRendererSetting = activity.findViewById(R.id.pojav_render_selector_isolate);

        checkJavaAuto = activity.findViewById(R.id.check_java_path_auto_isolate);
        checkJava8 = activity.findViewById(R.id.check_java_path_8_isolate);
        checkJava17 = activity.findViewById(R.id.check_java_path_17_isolate);
        java8Path = activity.findViewById(R.id.java_8_path_isolate);
        java17Path = activity.findViewById(R.id.java_17_path_isolate);
        java8Path.setText(AppManifest.JAVA_DIR + "/default");
        java17Path.setText(AppManifest.JAVA_DIR + "/JRE17");

        checkGameDirDefault = activity.findViewById(R.id.check_default_game_dir_isolate);
        checkGameDirIsolate = activity.findViewById(R.id.check_isolate_game_dir_isolate);
        checkGameDirCustom = activity.findViewById(R.id.check_custom_game_dir_isolate);
        editGameDir = activity.findViewById(R.id.edit_game_dir_path_isolate);
        selectGameDir = activity.findViewById(R.id.select_game_dir_path_isolate);

        launchByBoat = activity.findViewById(R.id.launch_by_boat_isolate);
        launchByPojav = activity.findViewById(R.id.launch_by_pojav_isolate);

        boatRendererGL4ES114 = activity.findViewById(R.id.boat_renderer_gl4es_114_isolate);
        boatRendererVirGL = activity.findViewById(R.id.boat_renderer_virgl_isolate);

        pojavRendererGL4ES114 = activity.findViewById(R.id.pojav_renderer_gl4es_114_isolate);
        pojavRendererVirGL = activity.findViewById(R.id.pojav_renderer_virgl_isolate);

        checkAutoRam = activity.findViewById(R.id.check_auto_ram_isolate);
        ramSeekBar = activity.findViewById(R.id.ram_seek_bar_isolate);
        editRam = activity.findViewById(R.id.edit_ram_isolate);
        usedRamText = activity.findViewById(R.id.used_ram_text_isolate);
        actualRamText = activity.findViewById(R.id.actual_ram_text_isolate);
        ramSeekBar.setMax(MemoryUtils.getTotalDeviceMemory(context));

        ramProgressBar = activity.findViewById(R.id.ram_progress_bar_isolate);
        ramProgressBar.setMax(MemoryUtils.getTotalDeviceMemory(context));

        scaleFactorSeekBar = activity.findViewById(R.id.edit_scale_factor_isolate);
        editScaleFactor = activity.findViewById(R.id.edit_scale_factor_text_isolate);
        scaleFactorSeekBar.setMax(750);

        checkLog = activity.findViewById(R.id.switch_log_isolate);

        notCheckGameFile = activity.findViewById(R.id.switch_check_mc_isolate);
        notCheckForge = activity.findViewById(R.id.switch_check_forge_isolate);
        notCheckJVM = activity.findViewById(R.id.switch_check_runtime_isolate);

        manageController = activity.findViewById(R.id.manage_control_layout_isolate);
        manageController.setOnClickListener(this);
        currentControlPattern = activity.findViewById(R.id.control_layout_isolate);

        checkTouchInjector = activity.findViewById(R.id.switch_touch_injector_isolate);

        editServer = activity.findViewById(R.id.edit_mc_server_isolate);
        editServer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (privateGameSetting != null) {
                    privateGameSetting.server = editServer.getText().toString();
                    GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
                }
            }
        });

        editJVMArgs = activity.findViewById(R.id.edit_jvm_arg_isolate);
        editJVMArgs.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (privateGameSetting != null) {
                    privateGameSetting.extraJavaFlags = editJVMArgs.getText().toString();
                    GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
                }
            }
        });

        editVersionIcon.setOnClickListener(this);
        deleteVersionIcon.setOnClickListener(this);

        checkIsolateSetting.setOnCheckedChangeListener(this);
        switchToGlobalSetting.setOnClickListener(this);

        checkLog.setOnCheckedChangeListener(this);

        notCheckGameFile.setOnCheckedChangeListener(this);
        notCheckForge.setOnCheckedChangeListener(this);
        notCheckJVM.setOnCheckedChangeListener(this);

        showJavaSetting.setOnClickListener(this);
        showJava.setOnClickListener(this);
        showGameDirSetting.setOnClickListener(this);
        showGameDir.setOnClickListener(this);

        showGameLauncherSetting.setOnClickListener(this);
        showGameLauncher.setOnClickListener(this);
        showBoatRendererSetting.setOnClickListener(this);
        showBoatRenderer.setOnClickListener(this);
        showPojavRendererSetting.setOnClickListener(this);
        showPojavRenderer.setOnClickListener(this);

        checkJavaAuto.setOnClickListener(this);
        checkJava8.setOnClickListener(this);
        checkJava17.setOnClickListener(this);

        checkTouchInjector.setOnCheckedChangeListener(this);

        checkGameDirDefault.setOnClickListener(this);
        checkGameDirIsolate.setOnClickListener(this);
        checkGameDirCustom.setOnClickListener(this);
        selectGameDir.setOnClickListener(this);
        editGameDir.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (privateGameSetting != null) {
                    privateGameSetting.gameDirSetting.path = editGameDir.getText().toString();
                    GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
                }
            }
        });

        launchByBoat.setOnClickListener(this);
        launchByPojav.setOnClickListener(this);

        boatRendererGL4ES114.setOnClickListener(this);
        boatRendererVirGL.setOnClickListener(this);

        pojavRendererGL4ES114.setOnClickListener(this);
        pojavRendererVirGL.setOnClickListener(this);

        checkAutoRam.setOnCheckedChangeListener(this);
        ramSeekBar.setOnSeekBarChangeListener(this);
        editRam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (privateGameSetting != null) {
                    if (!editRam.getText().toString().equals("")){
                        privateGameSetting.ramSetting.minRam = Integer.parseInt(editRam.getText().toString());
                        privateGameSetting.ramSetting.maxRam = Integer.parseInt(editRam.getText().toString());
                        ramSeekBar.setProgress(Integer.parseInt(editRam.getText().toString()));
                    }
                    else {
                        privateGameSetting.ramSetting.minRam = 0;
                        privateGameSetting.ramSetting.maxRam = 0;
                        ramSeekBar.setProgress(0);
                    }
                    GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
                }
            }
        });

        scaleFactorSeekBar.setOnSeekBarChangeListener(this);
        editScaleFactor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (privateGameSetting != null) {
                    if (!editScaleFactor.getText().toString().equals("")){
                        privateGameSetting.scaleFactor = Integer.parseInt(editScaleFactor.getText().toString()) / 100F;
                        scaleFactorSeekBar.setProgress((Integer.parseInt(editScaleFactor.getText().toString()) * 10) - 250);
                    }
                    else {
                        privateGameSetting.scaleFactor = 0.25F;
                        scaleFactorSeekBar.setProgress(0);
                    }
                    GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
                }
            }
        });

        javaSetting.post(() -> {
            javaSettingHeight = javaSetting.getHeight();
            javaSetting.setVisibility(View.GONE);
        });
        gameDirSetting.post(() -> {
            gameDirSettingHeight = gameDirSetting.getHeight();
            gameDirSetting.setVisibility(View.GONE);
        });
        gameLauncherSetting.post(() -> {
            gameLauncherSettingHeight = gameLauncherSetting.getHeight();
            gameLauncherSetting.setVisibility(View.GONE);
        });
        boatRendererSetting.post(() -> {
            boatRendererSettingHeight = boatRendererSetting.getHeight();
            boatRendererSetting.setVisibility(View.GONE);
        });
        pojavRendererSetting.post(() -> {
            pojavRendererSettingHeight = pojavRendererSetting.getHeight();
            pojavRendererSetting.setVisibility(View.GONE);
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(versionSettingUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.gameManagerUI.startGameSetting.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(versionSettingUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.gameManagerUI.startGameSetting.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onLoaded() {
        activity.uiManager.gameManagerUI.startGameSetting.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_GAME_DIR_REQUEST_ISOLATED && data != null) {
            if (resultCode == RESULT_OK && privateGameSetting != null) {
                Uri uri = data.getData();
                gameDirText.setText(UriUtils.getRealPathFromUri_AboveApi19(context,uri));
                editGameDir.setText(UriUtils.getRealPathFromUri_AboveApi19(context,uri));
                privateGameSetting.gameDirSetting.path = UriUtils.getRealPathFromUri_AboveApi19(context,uri);
                GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
            }
        }
        if (requestCode == ControlPatternActivity.CONTROL_PATTERN_REQUEST_CODE_ISOLATE && controllerManagerDialog != null && data != null){
            Uri uri = data.getData();
            String pattern = uri.toString();
            currentControlPattern.setText(pattern);
            privateGameSetting.controlLayout = pattern;
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
            controllerManagerDialog.currentPattern = pattern;
            controllerManagerDialog.loadList();
        }
        if (requestCode == ControllerManagerDialog.IMPORT_PATTERN_REQUEST_CODE_ISOLATED && controllerManagerDialog != null && data != null){
            controllerManagerDialog.onResult(requestCode,resultCode,data);
        }
        if (requestCode == SELECT_ICON_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                String path = UriUtils.getRealPathFromUri_AboveApi19(context,uri);
                new Thread(() -> {
                    FileUtils.copyFile(path,activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/icon.png");
                    activity.runOnUiThread(() -> {
                        icon.setBackground(DrawableUtils.getDrawableFromFile(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/icon.png"));
                    });
                }).start();
            }
        }
    }

    public void refresh(String versionName){
        this.versionName = versionName;
        String settingPath = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg";
        if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null && (GsonUtils.getPrivateGameSettingFromFile(settingPath).forceEnable || GsonUtils.getPrivateGameSettingFromFile(settingPath).enable)) {
            checkIsolateSetting.setChecked(true);
            enableSettingLayout();
            privateGameSetting = GsonUtils.getPrivateGameSettingFromFile(settingPath);
        }
        else {
            checkIsolateSetting.setChecked(false);
            disableSettingLayout();
            privateGameSetting = null;
        }
        onRefresh();
    }

    private void enableSettingLayout(){
        for (View view : getAllChild(isolateSettingLayout)) {
            view.setAlpha(1f);
            view.setEnabled(true);
        }
        switchToGlobalSetting.setEnabled(false);
        Log.e("enable","true");
    }

    private void disableSettingLayout(){
        for (View view : getAllChild(isolateSettingLayout)) {
            if (!(view instanceof ViewGroup)) {
                view.setAlpha(0.4f);
            }
            view.setEnabled(false);
        }
        switchToGlobalSetting.setEnabled(true);
        Log.e("disable","true");
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    private void onRefresh() {
        if (new File(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/icon.png").exists()) {
            icon.setBackground(DrawableUtils.getDrawableFromFile(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/icon.png"));
        }
        else {
            String v = null;
            for (GameListBean bean : activity.uiManager.versionListUI.gameList) {
                if (bean.name.equals(versionName)) {
                    v = bean.version;
                    break;
                }
            }
            if (v == null || !v.contains(",")) {
                icon.setBackground(context.getDrawable(R.drawable.ic_grass));
            }
            else {
                icon.setBackground(context.getDrawable(R.drawable.ic_furnace));
            }
        }

        PrivateGameSetting setting = privateGameSetting == null ? activity.privateGameSetting : privateGameSetting;
        checkAutoRam.setChecked(setting.ramSetting.autoRam);
        ramProgressBar.setProgress(MemoryUtils.getTotalDeviceMemory(context) - MemoryUtils.getFreeDeviceMemory(context));
        ramSeekBar.setProgress(setting.ramSetting.minRam);
        editRam.setText(setting.ramSetting.minRam + "");
        usedRamText.setText(context.getString(R.string.game_setting_ui_used_ram).replace("%s", Float.toString((float) Math.round(((MemoryUtils.getTotalDeviceMemory(context) - MemoryUtils.getFreeDeviceMemory(context)) / 1024F) * 10) / 10)) + " / " + (float) Math.round((MemoryUtils.getTotalDeviceMemory(context) / 1024F) * 10) / 10 + " GB");
        actualRamText.setText(context.getString(R.string.game_setting_ui_min_distribution).replace("%s", Float.toString((float) Math.round(((setting.ramSetting.minRam) / 1024F) * 10) / 10)) + " / " + context.getString(R.string.game_setting_ui_actual_distribution).replace("%s", Float.toString((float) Math.round(((setting.ramSetting.minRam) / 1024F) * 10) / 10)));
        scaleFactorSeekBar.setProgress((int) (setting.scaleFactor * 1000) - 250);
        editScaleFactor.setText(((int) (setting.scaleFactor * 100)) + "");
        checkLog.setChecked(setting.log);
        notCheckGameFile.setChecked(setting.notCheckMinecraft);
        notCheckForge.setChecked(setting.notCheckForge);
        notCheckJVM.setChecked(setting.notCheckJvm);
        checkTouchInjector.setChecked(setting.touchInjector);
        editGameDir.setText(setting.gameDirSetting.path);
        editServer.setText(setting.server);
        editJVMArgs.setText(setting.extraJavaFlags);
        currentControlPattern.setText(setting.controlLayout);
        if (setting.javaSetting.autoSelect){
            javaPathText.setText(context.getString(R.string.game_setting_ui_java_path_auto));
            checkJavaAuto.setChecked(true);
            checkJava8.setChecked(false);
            checkJava17.setChecked(false);
        }
        else {
            if (setting.javaSetting.name.equals("default")){
                javaPathText.setText(AppManifest.JAVA_DIR + "/default");
                checkJavaAuto.setChecked(false);
                checkJava8.setChecked(true);
                checkJava17.setChecked(false);
            }
            if (setting.javaSetting.name.equals("JRE17")){
                javaPathText.setText(AppManifest.JAVA_DIR + "/JRE17");
                checkJavaAuto.setChecked(false);
                checkJava8.setChecked(false);
                checkJava17.setChecked(true);
            }
        }
        if (setting.gameDirSetting.type == 0){
            editGameDir.setEnabled(false);
            selectGameDir.setEnabled(false);
            gameDirText.setText(activity.launcherSetting.gameFileDirectory);
            checkGameDirDefault.setChecked(true);
            checkGameDirIsolate.setChecked(false);
            checkGameDirCustom.setChecked(false);
        }
        else if (setting.gameDirSetting.type == 1){
            editGameDir.setEnabled(false);
            selectGameDir.setEnabled(false);
            gameDirText.setText(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName);
            checkGameDirDefault.setChecked(false);
            checkGameDirIsolate.setChecked(true);
            checkGameDirCustom.setChecked(false);
        }
        else {
            editGameDir.setEnabled(true);
            selectGameDir.setEnabled(true);
            gameDirText.setText(setting.gameDirSetting.path);
            checkGameDirDefault.setChecked(false);
            checkGameDirIsolate.setChecked(false);
            checkGameDirCustom.setChecked(true);
        }
        if (setting.boatLauncherSetting.enable){
            launchByBoat.setChecked(true);
            launchByPojav.setChecked(false);
            currentLauncher.setText(context.getText(R.string.game_setting_ui_game_launcher_boat));
        }
        else {
            launchByBoat.setChecked(false);
            launchByPojav.setChecked(true);
            currentLauncher.setText(context.getText(R.string.game_setting_ui_game_launcher_pojav));
        }
        if (setting.boatLauncherSetting.renderer.equals("libGL112.so.1") || setting.boatLauncherSetting.renderer.equals("libGL115.so.1") || setting.boatLauncherSetting.renderer.equals("libgl4es_114.so") || setting.boatLauncherSetting.renderer.equals("libvgpu.so") || setting.boatLauncherSetting.renderer.equals("GL4ES115") || setting.boatLauncherSetting.renderer.equals("GL4ES114")){
            boatRendererGL4ES114.setChecked(true);
            boatRendererVirGL.setChecked(false);
            currentBoatRenderer.setText(context.getText(R.string.game_setting_ui_boat_renderer_gl4es_114));
        }
        else if (setting.boatLauncherSetting.renderer.equals("VirGL")){
            boatRendererGL4ES114.setChecked(false);
            boatRendererVirGL.setChecked(true);
            currentBoatRenderer.setText(context.getText(R.string.game_setting_ui_boat_renderer_virgl));
        }
        if (setting.pojavLauncherSetting.renderer.equals("opengles2") || setting.pojavLauncherSetting.renderer.equals("opengles2_5") || setting.pojavLauncherSetting.renderer.equals("opengles3") || setting.pojavLauncherSetting.renderer.equals("opengles3_vgpu")){
            pojavRendererGL4ES114.setChecked(true);
            pojavRendererVirGL.setChecked(false);
            currentPojavRenderer.setText(context.getText(R.string.game_setting_ui_pojav_renderer_gl4es_114));
        }
        else if (setting.pojavLauncherSetting.renderer.equals("opengles3_virgl")){
            pojavRendererGL4ES114.setChecked(false);
            pojavRendererVirGL.setChecked(true);
            currentPojavRenderer.setText(context.getText(R.string.game_setting_ui_pojav_renderer_virgl));
        }
    }

    private ArrayList<View> getAllChild(ViewGroup viewGroup) {
        ArrayList<View> list = new ArrayList<>();
        for (int i = 0;i < viewGroup.getChildCount();i++) {
            if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                list.addAll(getAllChild((ViewGroup) viewGroup.getChildAt(i)));
            }
            list.add(viewGroup.getChildAt(i));
        }
        return list;
    }

    @SuppressLint({"SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    public void onClick(View v) {
        if (v == editVersionIcon) {
            Intent intent = new Intent(context, FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "png;jpg");
            intent.putExtra(Constants.INITIAL_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath());
            activity.startActivityForResult(intent, SELECT_ICON_REQUEST);
        }
        if (v == deleteVersionIcon) {
            if (new File(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/icon.png").exists()) {
                new File(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/icon.png").delete();
            }
            String ve = null;
            for (GameListBean bean : activity.uiManager.versionListUI.gameList) {
                if (bean.name.equals(versionName)) {
                    ve = bean.version;
                    break;
                }
            }
            if (ve == null || !ve.contains(",")) {
                icon.setBackground(context.getDrawable(R.drawable.ic_grass));
            }
            else {
                icon.setBackground(context.getDrawable(R.drawable.ic_furnace));
            }
        }
        if (v == switchToGlobalSetting) {
            activity.uiManager.switchMainUI(activity.uiManager.settingUI);
            activity.uiManager.settingUI.settingUIManager.switchSettingUIs(activity.uiManager.settingUI.settingUIManager.universalGameSettingUI);
        }
        if (v == showJavaSetting || v == showJava){
            HiddenAnimationUtils.newInstance(context,javaSetting,showJava,javaSettingHeight).toggle();
        }
        if (v == showGameDirSetting || v == showGameDir){
            HiddenAnimationUtils.newInstance(context,gameDirSetting,showGameDir,gameDirSettingHeight).toggle();
        }
        if (v == showGameLauncherSetting || v == showGameLauncher){
            HiddenAnimationUtils.newInstance(context,gameLauncherSetting,showGameLauncher,gameLauncherSettingHeight).toggle();
        }
        if (v == showBoatRendererSetting || v == showBoatRenderer){
            HiddenAnimationUtils.newInstance(context,boatRendererSetting,showBoatRenderer,boatRendererSettingHeight).toggle();
        }
        if (v == showPojavRendererSetting || v == showPojavRenderer){
            HiddenAnimationUtils.newInstance(context,pojavRendererSetting,showPojavRenderer,pojavRendererSettingHeight).toggle();
        }
        if (v == checkJavaAuto && privateGameSetting != null){
            javaPathText.setText(context.getString(R.string.game_setting_ui_java_path_auto));
            checkJava8.setChecked(false);
            checkJava17.setChecked(false);
            privateGameSetting.javaSetting.autoSelect = true;
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
        }
        if (v == checkJava8 && privateGameSetting != null){
            javaPathText.setText(AppManifest.JAVA_DIR + "/default");
            checkJavaAuto.setChecked(false);
            checkJava17.setChecked(false);
            privateGameSetting.javaSetting.autoSelect = false;
            privateGameSetting.javaSetting.name = "default";
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
        }
        if (v == checkJava17 && privateGameSetting != null){
            javaPathText.setText(AppManifest.JAVA_DIR + "/JRE17");
            checkJavaAuto.setChecked(false);
            checkJava8.setChecked(false);
            privateGameSetting.javaSetting.autoSelect = false;
            privateGameSetting.javaSetting.name = "JRE17";
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
        }
        if (v == checkGameDirDefault && privateGameSetting != null){
            editGameDir.setEnabled(false);
            selectGameDir.setEnabled(false);
            gameDirText.setText(activity.launcherSetting.gameFileDirectory);
            checkGameDirIsolate.setChecked(false);
            checkGameDirCustom.setChecked(false);
            privateGameSetting.gameDirSetting.type = 0;
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
        }
        if (v == checkGameDirIsolate && privateGameSetting != null){
            editGameDir.setEnabled(false);
            selectGameDir.setEnabled(false);
            gameDirText.setText(activity.launcherSetting.gameFileDirectory + "/versions/" + versionName);
            checkGameDirDefault.setChecked(false);
            checkGameDirCustom.setChecked(false);
            privateGameSetting.gameDirSetting.type = 1;
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
        }
        if (v == checkGameDirCustom && privateGameSetting != null){
            editGameDir.setEnabled(true);
            selectGameDir.setEnabled(true);
            gameDirText.setText(privateGameSetting.gameDirSetting.path);
            checkGameDirDefault.setChecked(false);
            checkGameDirIsolate.setChecked(false);
            privateGameSetting.gameDirSetting.type = 2;
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
        }
        if (v == selectGameDir && privateGameSetting != null){
            Intent intent = new Intent(context, FolderChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.INITIAL_DIRECTORY, new File(AppManifest.DEFAULT_GAME_DIR).getAbsolutePath());
            activity.startActivityForResult(intent, PICK_GAME_DIR_REQUEST_ISOLATED);
        }
        if (v == launchByBoat && privateGameSetting != null){
            launchByPojav.setChecked(false);
            privateGameSetting.boatLauncherSetting.enable = true;
            privateGameSetting.pojavLauncherSetting.enable = false;
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
            currentLauncher.setText(context.getText(R.string.game_setting_ui_game_launcher_boat));
        }
        if (v == launchByPojav && privateGameSetting != null){
            launchByBoat.setChecked(false);
            privateGameSetting.boatLauncherSetting.enable = false;
            privateGameSetting.pojavLauncherSetting.enable = true;
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
            currentLauncher.setText(context.getText(R.string.game_setting_ui_game_launcher_pojav));
        }
        if (v == boatRendererGL4ES114){
            boatRendererVirGL.setChecked(false);
            privateGameSetting.boatLauncherSetting.renderer = "GL4ES114";
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
            currentBoatRenderer.setText(context.getText(R.string.game_setting_ui_boat_renderer_gl4es_114));
        }
        if (v == boatRendererVirGL){
            boatRendererGL4ES114.setChecked(false);
            privateGameSetting.boatLauncherSetting.renderer = "VirGL";
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
            currentBoatRenderer.setText(context.getText(R.string.game_setting_ui_boat_renderer_virgl));
        }
        if (v == pojavRendererGL4ES114 && privateGameSetting != null){
            pojavRendererVirGL.setChecked(false);
            privateGameSetting.pojavLauncherSetting.renderer = "opengles2";
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
            currentPojavRenderer.setText(context.getText(R.string.game_setting_ui_pojav_renderer_gl4es_114));
        }
        if (v == pojavRendererVirGL && privateGameSetting != null){
            pojavRendererGL4ES114.setChecked(false);
            privateGameSetting.pojavLauncherSetting.renderer = "opengles3_virgl";
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
            currentPojavRenderer.setText(context.getText(R.string.game_setting_ui_pojav_renderer_virgl));
        }
        if (v == manageController && privateGameSetting != null){
            controllerManagerDialog = new ControllerManagerDialog(context,activity,activity.launcherSetting.fullscreen, privateGameSetting.controlLayout, new ControllerManagerDialog.OnPatternChangeListener() {
                @Override
                public void onPatternChange(String pattern) {
                    currentControlPattern.setText(pattern);
                    privateGameSetting.controlLayout = pattern;
                    GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
                }
            },true);
            controllerManagerDialog.show();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == checkIsolateSetting) {
            String settingPath = activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg";
            Log.e("privateSettingPath",settingPath);
            if (b) {
                enableSettingLayout();
                if (!new File(settingPath).exists() || GsonUtils.getPrivateGameSettingFromFile(settingPath) == null) {
                    try {
                        privateGameSetting = (PrivateGameSetting) activity.privateGameSetting.clone();
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                else {
                    Log.e("setting","exist");
                    privateGameSetting = GsonUtils.getPrivateGameSettingFromFile(settingPath);
                }
                privateGameSetting.enable = true;
                GsonUtils.savePrivateGameSetting(privateGameSetting,settingPath);
            }
            else {
                disableSettingLayout();
                if (new File(settingPath).exists() && GsonUtils.getPrivateGameSettingFromFile(settingPath) != null) {
                    privateGameSetting.enable = false;
                    try {
                        GsonUtils.savePrivateGameSetting((PrivateGameSetting) privateGameSetting.clone(),settingPath);
                    } catch (CloneNotSupportedException e) {
                        e.printStackTrace();
                    }
                }
                privateGameSetting = null;
            }
            onRefresh();
        }
        else {
            if (compoundButton == checkAutoRam && privateGameSetting != null){
                privateGameSetting.ramSetting.autoRam = b;
            }
            if (compoundButton == checkLog && privateGameSetting != null){
                privateGameSetting.log = b;
            }
            if (compoundButton == notCheckGameFile && privateGameSetting != null){
                privateGameSetting.notCheckMinecraft = b;
            }
            if (compoundButton == notCheckForge && privateGameSetting != null){
                privateGameSetting.notCheckForge = b;
            }
            if (compoundButton == notCheckJVM && privateGameSetting != null){
                privateGameSetting.notCheckJvm = b;
            }
            if (compoundButton == checkTouchInjector && privateGameSetting != null) {
                privateGameSetting.touchInjector = b;
            }
            GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == ramSeekBar && b && privateGameSetting != null){
            privateGameSetting.ramSetting.minRam = i;
            privateGameSetting.ramSetting.maxRam = i;
            editRam.setText(i + "");
        }
        if (seekBar == scaleFactorSeekBar && b && privateGameSetting != null){
            privateGameSetting.scaleFactor = (i + 250.0F) / 1000F;
            editScaleFactor.setText(((i / 10) + 25) + "");
        }
        GsonUtils.savePrivateGameSetting(privateGameSetting, activity.launcherSetting.gameFileDirectory + "/versions/" + versionName + "/hmclpe.cfg");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
