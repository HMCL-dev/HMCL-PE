package com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.util.ArrayList;

public class DownloadSettingUI extends BaseUI implements CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener, AdapterView.OnItemSelectedListener, TextWatcher {

    public LinearLayout downloadSettingUI;

    private CheckBox checkAutoSelect;
    private CheckBox checkAutoDownload;
    private LinearLayout autoSourceLayout;
    private LinearLayout fixSourceLayout;
    private LinearLayout taskSizeLayout;
    private Spinner autoSourceSpinner;
    private Spinner fixSourceSpinner;
    private SeekBar taskSizeSeekbar;
    private EditText editTaskSize;

    public DownloadSettingUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onCreate() {
        super.onCreate();
        downloadSettingUI = activity.findViewById(R.id.ui_setting_download);

        checkAutoSelect = activity.findViewById(R.id.auto_select_source);
        checkAutoDownload = activity.findViewById(R.id.auto_select_download_num);
        autoSourceLayout = activity.findViewById(R.id.auto_source_layout);
        fixSourceLayout = activity.findViewById(R.id.fix_source_layout);
        taskSizeLayout = activity.findViewById(R.id.task_size_layout);
        autoSourceSpinner = activity.findViewById(R.id.auto_source_spinner);
        fixSourceSpinner = activity.findViewById(R.id.fix_source_spinner);
        taskSizeSeekbar = activity.findViewById(R.id.task_size_seekbar);
        editTaskSize = activity.findViewById(R.id.edit_download_task_size);

        ArrayList<String> autoTypeList = new ArrayList<>();
        autoTypeList.add(context.getString(R.string.download_setting_ui_auto_official));
        autoTypeList.add(context.getString(R.string.download_setting_ui_auto_balance));
        autoTypeList.add(context.getString(R.string.download_setting_ui_auto_mirror));
        ArrayAdapter<String> autoTypeAdapter = new ArrayAdapter<>(context,R.layout.item_spinner,autoTypeList);
        autoSourceSpinner.setAdapter(autoTypeAdapter);

        ArrayList<String> fixTypeList = new ArrayList<>();
        fixTypeList.add(context.getString(R.string.download_setting_ui_source_official));
        fixTypeList.add(context.getString(R.string.download_setting_ui_source_bmclapi));
        fixTypeList.add(context.getString(R.string.download_setting_ui_source_mcbbs));
        ArrayAdapter<String> fixTypeAdapter = new ArrayAdapter<>(context,R.layout.item_spinner,fixTypeList);
        fixSourceSpinner.setAdapter(fixTypeAdapter);

        checkAutoSelect.setChecked(activity.launcherSetting.downloadUrlSource.autoSelect);
        autoSourceSpinner.setSelection(activity.launcherSetting.downloadUrlSource.autoSourceType);
        fixSourceSpinner.setSelection(activity.launcherSetting.downloadUrlSource.fixSourceType);
        checkAutoDownload.setChecked(activity.launcherSetting.autoDownloadTaskQuantity);
        taskSizeSeekbar.setProgress(activity.launcherSetting.maxDownloadTask);
        editTaskSize.setText(Integer.toString(activity.launcherSetting.maxDownloadTask));
        refreshSourceLayout(activity.launcherSetting.downloadUrlSource.autoSelect);
        refreshSizeLayout(activity.launcherSetting.autoDownloadTaskQuantity);

        checkAutoSelect.setOnCheckedChangeListener(this);
        checkAutoDownload.setOnCheckedChangeListener(this);
        autoSourceSpinner.setOnItemSelectedListener(this);
        fixSourceSpinner.setOnItemSelectedListener(this);
        taskSizeSeekbar.setOnSeekBarChangeListener(this);
        editTaskSize.addTextChangedListener(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(downloadSettingUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startDownloadSettingUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(downloadSettingUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startDownloadSettingUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    private void refreshSourceLayout(boolean auto) {
        if (auto) {
            for (View view : getAllChild(autoSourceLayout)) {
                view.setAlpha(1f);
                view.setEnabled(true);
            }
            for (View view : getAllChild(fixSourceLayout)) {
                view.setAlpha(0.4f);
                view.setEnabled(false);
            }
        }
        else {
            for (View view : getAllChild(autoSourceLayout)) {
                view.setAlpha(0.4f);
                view.setEnabled(false);
            }
            for (View view : getAllChild(fixSourceLayout)) {
                view.setAlpha(1f);
                view.setEnabled(true);
            }
        }
    }

    private void refreshSizeLayout(boolean auto) {
        if (auto) {
            for (View view : getAllChild(taskSizeLayout)) {
                view.setAlpha(0.4f);
                view.setEnabled(false);
            }
        }
        else {
            for (View view : getAllChild(taskSizeLayout)) {
                view.setAlpha(1f);
                view.setEnabled(true);
            }
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

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == checkAutoSelect) {
            activity.launcherSetting.downloadUrlSource.autoSelect = b;
            refreshSourceLayout(b);
        }
        if (compoundButton == checkAutoDownload) {
            activity.launcherSetting.autoDownloadTaskQuantity = b;
            refreshSizeLayout(b);
        }
        GsonUtils.saveLauncherSetting(activity.launcherSetting, AppManifest.SETTING_DIR + "/launcher_setting.json");
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (b && seekBar == taskSizeSeekbar) {
            activity.launcherSetting.maxDownloadTask = i;
            editTaskSize.setText(Integer.toString(i));
        }
        GsonUtils.saveLauncherSetting(activity.launcherSetting, AppManifest.SETTING_DIR + "/launcher_setting.json");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == autoSourceSpinner) {
            activity.launcherSetting.downloadUrlSource.autoSourceType = i;
        }
        if (adapterView == fixSourceSpinner) {
            activity.launcherSetting.downloadUrlSource.fixSourceType = i;
        }
        GsonUtils.saveLauncherSetting(activity.launcherSetting, AppManifest.SETTING_DIR + "/launcher_setting.json");
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        if (!editTaskSize.getText().toString().equals("")) {
            if (Integer.parseInt(editTaskSize.getText().toString()) > 128) {
                activity.launcherSetting.maxDownloadTask = 128;
            }
            else {
                activity.launcherSetting.maxDownloadTask = Math.max(Integer.parseInt(editTaskSize.getText().toString()), 1);
            }
            taskSizeSeekbar.setProgress(activity.launcherSetting.maxDownloadTask);
            GsonUtils.saveLauncherSetting(activity.launcherSetting, AppManifest.SETTING_DIR + "/launcher_setting.json");
        }
    }
}
