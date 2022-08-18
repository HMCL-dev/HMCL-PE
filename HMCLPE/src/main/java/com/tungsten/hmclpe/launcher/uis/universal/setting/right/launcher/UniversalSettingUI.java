package com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FolderChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.update.UpdateChecker;
import com.tungsten.hmclpe.utils.LocaleUtils;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.animation.HiddenAnimationUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.io.File;
import java.util.ArrayList;

public class UniversalSettingUI extends BaseUI implements View.OnClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, TextWatcher {

    private static final int PICK_CACHE_FOLDER_REQUEST = 1002;

    public LinearLayout universalSettingUI;

    private LinearLayout showUpdateSetting;
    private LinearLayout showCacheSetting;
    private ImageView showUpdate;
    private ImageView showCache;
    private TextView updateStateText;
    private TextView cachePathText;
    private Spinner switchLang;
    private Button clearCache;
    private Button exportLog;
    private LinearLayout updateSetting;
    private RadioButton checkRelease;
    private RadioButton checkBeta;
    private LinearLayout cacheSetting;
    private RadioButton checkDefault;
    private RadioButton checkCustom;
    private EditText editCacheContent;
    private ImageButton selectCachePath;

    private int updateSettingHeight;
    private int cacheSettingHeight;

    private UpdateChecker.UpdateCallback updateCallback = new UpdateChecker.UpdateCallback() {
        @Override
        public void onCheck() {
            updateStateText.setText(context.getString(R.string.universal_setting_ui_update_state_checking));
        }

        @Override
        public void onFinish(boolean latest) {
            if (latest) {
                updateStateText.setText(context.getString(R.string.universal_setting_ui_update_state_latest));
            }
            else {
                updateStateText.setText(context.getString(R.string.universal_setting_ui_update_state_update));
                updateStateText.setTextColor(Color.RED);
            }
        }
    };

    public UniversalSettingUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        universalSettingUI = activity.findViewById(R.id.ui_setting_universal);

        showUpdateSetting = activity.findViewById(R.id.show_update_setting);
        updateStateText = activity.findViewById(R.id.update_state_text);
        showUpdate = activity.findViewById(R.id.show_update);
        showCacheSetting = activity.findViewById(R.id.show_cache_setting);
        cachePathText = activity.findViewById(R.id.cache_content_text);
        showCache = activity.findViewById(R.id.show_cache);
        clearCache = activity.findViewById(R.id.clear_cache);
        switchLang = activity.findViewById(R.id.language_spinner);
        exportLog = activity.findViewById(R.id.export_log);

        showUpdateSetting.setOnClickListener(this);
        showCacheSetting.setOnClickListener(this);
        clearCache.setOnClickListener(this);
        exportLog.setOnClickListener(this);

        updateSetting = activity.findViewById(R.id.update_setting);
        checkRelease = activity.findViewById(R.id.update_to_rec);
        checkBeta = activity.findViewById(R.id.update_to_beta);

        checkRelease.setOnCheckedChangeListener(this);
        checkBeta.setOnCheckedChangeListener(this);

        cacheSetting = activity.findViewById(R.id.cache_setting);
        checkDefault = activity.findViewById(R.id.check_default_cache_path);
        checkCustom = activity.findViewById(R.id.check_custom_cache_path);
        editCacheContent = activity.findViewById(R.id.edit_cache_path);
        selectCachePath = activity.findViewById(R.id.select_cache_path);

        checkDefault.setOnCheckedChangeListener(this);
        checkCustom.setOnCheckedChangeListener(this);
        editCacheContent.addTextChangedListener(this);
        selectCachePath.setOnClickListener(this);

        ArrayList<String> languages = new ArrayList<>();
        languages.add(context.getString(R.string.universal_setting_ui_lang_sys));
        languages.add("English");
        languages.add("简体中文");
        languages.add("繁體中文");
        ArrayAdapter<String> langAdapter = new ArrayAdapter<>(context, R.layout.item_spinner, languages);
        switchLang.setAdapter(langAdapter);
        SharedPreferences sharedPreferences = context.getSharedPreferences("lang", Context.MODE_PRIVATE);
        switchLang.setSelection(sharedPreferences.getInt("lang", 0));
        switchLang.setOnItemSelectedListener(this);

        updateSetting.post(() -> {
            updateSettingHeight = updateSetting.getHeight();
            updateSetting.setVisibility(View.GONE);
        });
        cacheSetting.post(() -> {
            cacheSettingHeight = cacheSetting.getHeight();
            cacheSetting.setVisibility(View.GONE);
        });

        activity.updateChecker.check(activity.launcherSetting.getBetaVersion,updateCallback);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(universalSettingUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startUniversalSettingUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
        if (activity.launcherSetting.getBetaVersion){
            checkBeta.setChecked(true);
            checkRelease.setChecked(false);
        }
        else {
            checkBeta.setChecked(false);
            checkRelease.setChecked(true);
        }
        if (activity.launcherSetting.cachePath.equals(AppManifest.DEFAULT_CACHE_DIR)){
            checkDefault.setChecked(true);
        }
        else {
            checkCustom.setChecked(true);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(universalSettingUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startUniversalSettingUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_CACHE_FOLDER_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                editCacheContent.setText(UriUtils.getRealPathFromUri_AboveApi19(context,uri));
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == showUpdateSetting){
            HiddenAnimationUtils.newInstance(context,updateSetting,showUpdate,updateSettingHeight).toggle();
        }
        if (v == showCacheSetting){
            HiddenAnimationUtils.newInstance(context,cacheSetting,showCache,cacheSettingHeight).toggle();
        }
        if (v == clearCache){
            FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR);
        }
        if (v == exportLog){

        }
        if (v == selectCachePath){
            Intent intent = new Intent(context, FolderChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getAbsolutePath());
            activity.startActivityForResult(intent, PICK_CACHE_FOLDER_REQUEST);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == switchLang) {
            LocaleUtils.changeLanguage(context, position);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == checkRelease){
            if (isChecked){
                checkBeta.setChecked(false);
                activity.launcherSetting.getBetaVersion = false;
                GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
                activity.updateChecker.check(false,updateCallback);
            }
        }
        if (buttonView == checkBeta){
            if (isChecked){
                checkRelease.setChecked(false);
                activity.launcherSetting.getBetaVersion = true;
                GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
                activity.updateChecker.check(true,updateCallback);
            }
        }
        if (buttonView == checkDefault){
            if (isChecked){
                checkCustom.setChecked(false);
                editCacheContent.setEnabled(false);
                selectCachePath.setEnabled(false);
                activity.launcherSetting.cachePath = AppManifest.DEFAULT_CACHE_DIR;
                GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
                cachePathText.setText(activity.launcherSetting.cachePath);
                editCacheContent.setText(activity.launcherSetting.cachePath);
            }
        }
        if (buttonView == checkCustom){
            if (isChecked){
                checkDefault.setChecked(false);
                editCacheContent.setEnabled(true);
                selectCachePath.setEnabled(true);
                cachePathText.setText(activity.launcherSetting.cachePath);
                editCacheContent.setText(activity.launcherSetting.cachePath);
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        activity.launcherSetting.cachePath = editCacheContent.getText().toString();
        GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
        cachePathText.setText(activity.launcherSetting.cachePath);
    }
}
