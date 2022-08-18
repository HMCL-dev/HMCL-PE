package com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.tools.ColorSelectorDialog;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExteriorSettingUI extends BaseUI implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final int PICK_BACKGROUND_REQUEST = 4000;

    public LinearLayout exteriorSettingUI;

    private LinearLayout selectTheme;
    private View colorView;
    private TextView colorText;
    private SwitchCompat transBarSwitch;
    private SwitchCompat fullscreenSwitch;
    private LinearLayout fullscreenSetting;
    private RadioButton defaultRadio;
    private RadioButton classicRadio;
    private RadioButton customRadio;
    private RadioButton onlineRadio;
    private EditText editBgPath;
    private EditText editBgUrl;
    private ImageButton selectBgPath;

    public ExteriorSettingUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onCreate() {
        super.onCreate();
        exteriorSettingUI = activity.findViewById(R.id.ui_setting_exterior);

        selectTheme = activity.findViewById(R.id.select_theme);
        colorView = activity.findViewById(R.id.theme_color_view);
        colorText = activity.findViewById(R.id.theme_color_text);
        transBarSwitch = activity.findViewById(R.id.switch_trans_bar);
        fullscreenSwitch = activity.findViewById(R.id.switch_full_screen);
        fullscreenSetting = activity.findViewById(R.id.fullscreen_layout);
        defaultRadio = activity.findViewById(R.id.select_bg_default);
        classicRadio = activity.findViewById(R.id.select_bg_classic);
        customRadio = activity.findViewById(R.id.select_bg_custom);
        onlineRadio = activity.findViewById(R.id.select_bg_online);
        editBgPath = activity.findViewById(R.id.edit_bg_path);
        editBgUrl = activity.findViewById(R.id.edit_bg_url);
        selectBgPath = activity.findViewById(R.id.select_bg_path);

        if (activity.launcherSetting.launcherBackground.type == 0){
            defaultRadio.setChecked(true);
            editBgPath.setEnabled(false);
            editBgUrl.setEnabled(false);
            selectBgPath.setEnabled(false);
            activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background));
        }
        else if (activity.launcherSetting.launcherBackground.type == 1){
            classicRadio.setChecked(true);
            editBgPath.setEnabled(false);
            editBgUrl.setEnabled(false);
            selectBgPath.setEnabled(false);
            activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background_classic));
        }
        else if (activity.launcherSetting.launcherBackground.type == 2){
            customRadio.setChecked(true);
            editBgPath.setEnabled(true);
            editBgUrl.setEnabled(false);
            selectBgPath.setEnabled(true);
            if (new File(activity.launcherSetting.launcherBackground.path).exists() && isImageFile(activity.launcherSetting.launcherBackground.path)){
                Bitmap bitmap = BitmapFactory.decodeFile(activity.launcherSetting.launcherBackground.path);
                activity.launcherLayout.setBackground(new BitmapDrawable(bitmap));
            }
            else {
                activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background));
            }
        }
        else{
            onlineRadio.setChecked(true);
            editBgPath.setEnabled(false);
            editBgUrl.setEnabled(true);
            selectBgPath.setEnabled(false);
            new Thread(() -> {
                try {
                    URL url = new URL(activity.launcherSetting.launcherBackground.url);
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    handler.post(() -> activity.launcherLayout.setBackground(new BitmapDrawable(bitmap)));
                } catch (IOException e) {
                    handler.post(() -> activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background)));
                    e.printStackTrace();
                }
            }).start();
        }
        editBgPath.setText(activity.launcherSetting.launcherBackground.path);
        editBgUrl.setText(activity.launcherSetting.launcherBackground.url);

        selectTheme.setOnClickListener(this);
        transBarSwitch.setOnCheckedChangeListener(this);
        fullscreenSwitch.setOnCheckedChangeListener(this);
        defaultRadio.setOnCheckedChangeListener(this);
        classicRadio.setOnCheckedChangeListener(this);
        customRadio.setOnCheckedChangeListener(this);
        onlineRadio.setOnCheckedChangeListener(this);
        selectBgPath.setOnClickListener(this);

        editBgPath.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                activity.launcherSetting.launcherBackground.path = editBgPath.getText().toString();
                GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
                if (new File(editBgPath.getText().toString()).exists() && isImageFile(editBgPath.getText().toString())){
                    Bitmap bitmap = BitmapFactory.decodeFile(editBgPath.getText().toString());
                    activity.launcherLayout.setBackground(new BitmapDrawable(bitmap));
                }
                else {
                    activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background));
                }
            }
        });
        editBgUrl.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                activity.launcherSetting.launcherBackground.url = editBgUrl.getText().toString();
                GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
                new Thread(() -> {
                    try {
                        URL url = new URL(editBgUrl.getText().toString());
                        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        handler.post(() -> activity.launcherLayout.setBackground(new BitmapDrawable(bitmap)));
                    } catch (IOException e) {
                        handler.post(() -> activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background)));
                        e.printStackTrace();
                    }
                }).start();
            }
        });

        transBarSwitch.setChecked(activity.launcherSetting.transBar);
        fullscreenSwitch.setChecked(activity.launcherSetting.fullscreen);

        if (!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)){
            fullscreenSetting.setVisibility(View.GONE);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(exteriorSettingUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startExteriorSettingUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
        colorView.setBackgroundColor(Color.parseColor(getThemeColor(context,activity.launcherSetting.launcherTheme)));
        colorText.setText(getThemeColor(context,activity.launcherSetting.launcherTheme));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(exteriorSettingUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.settingUI.startExteriorSettingUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_BACKGROUND_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                editBgPath.setText(UriUtils.getRealPathFromUri_AboveApi19(context,uri));
            }
        }
    }

    public static String getThemeColor(Context context,String color){
        if (color.equals("DEFAULT")){
            return "#" + Integer.toHexString(context.getColor(R.color.colorAccent));
        }else {
            return color;
        }
    }

    public static boolean isImageFile(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        if (options.outWidth == -1) {
            return false;
        }
        return true;
    }

    @Override
    public void onClick(View v) {
        if (v == selectTheme){
            ColorSelectorDialog dialog = new ColorSelectorDialog(context,true,Color.parseColor(getThemeColor(context,activity.launcherSetting.launcherTheme)));
            dialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @SuppressLint("SetTextI18n")
                @Override
                public void onColorSelected(int color) {
                    activity.exteriorConfig.primaryColor(color);
                    activity.exteriorConfig.accentColor(color);
                    activity.exteriorConfig.apply(activity);
                    activity.appBar.setBackgroundColor(activity.launcherSetting.transBar ? context.getResources().getColor(R.color.launcher_ui_background) : color);
                    colorView.setBackgroundColor(color);
                    colorText.setText("#" + Integer.toHexString(color));
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onPositive(int destColor) {
                    activity.launcherSetting.launcherTheme = "#" + Integer.toHexString(destColor);
                    GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
                    activity.exteriorConfig.primaryColor(destColor);
                    activity.exteriorConfig.accentColor(destColor);
                    activity.exteriorConfig.apply(activity);
                    activity.appBar.setBackgroundColor(activity.launcherSetting.transBar ? context.getResources().getColor(R.color.launcher_ui_background) : destColor);
                    colorView.setBackgroundColor(destColor);
                    colorText.setText("#" + Integer.toHexString(destColor));
                }

                @SuppressLint("SetTextI18n")
                @Override
                public void onNegative(int initColor) {
                    activity.exteriorConfig.primaryColor(initColor);
                    activity.exteriorConfig.accentColor(initColor);
                    activity.exteriorConfig.apply(activity);
                    activity.appBar.setBackgroundColor(activity.launcherSetting.transBar ? context.getResources().getColor(R.color.launcher_ui_background) : initColor);
                    colorView.setBackgroundColor(initColor);
                    colorText.setText("#" + Integer.toHexString(initColor));
                }
            });
            dialog.show();
        }
        if (v == selectBgPath){
            Intent intent = new Intent(context, FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "png;jpg");
            intent.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getAbsolutePath());
            activity.startActivityForResult(intent, PICK_BACKGROUND_REQUEST);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView == transBarSwitch){
            activity.launcherSetting.transBar = isChecked;
            GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
            if (isChecked){
                activity.appBar.setBackgroundColor(context.getResources().getColor(R.color.launcher_ui_background));
            }
            else {
                activity.appBar.setBackgroundColor(Color.parseColor(getThemeColor(context,activity.launcherSetting.launcherTheme)));
            }
        }
        if (buttonView == fullscreenSwitch){
            activity.launcherSetting.fullscreen = isChecked;
            GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                if (isChecked) {
                    activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                } else {
                    activity.getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
                }
            }
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN, WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        }
        if (buttonView == defaultRadio && isChecked){
            classicRadio.setChecked(false);
            customRadio.setChecked(false);
            onlineRadio.setChecked(false);
            editBgPath.setEnabled(false);
            editBgUrl.setEnabled(false);
            selectBgPath.setEnabled(false);
            activity.launcherSetting.launcherBackground.type = 0;
            activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background));
        }
        if (buttonView == classicRadio && isChecked){
            defaultRadio.setChecked(false);
            customRadio.setChecked(false);
            onlineRadio.setChecked(false);
            editBgPath.setEnabled(false);
            editBgUrl.setEnabled(false);
            selectBgPath.setEnabled(false);
            activity.launcherSetting.launcherBackground.type = 1;
            activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background_classic));
        }
        if (buttonView == customRadio && isChecked){
            defaultRadio.setChecked(false);
            classicRadio.setChecked(false);
            onlineRadio.setChecked(false);
            editBgPath.setEnabled(true);
            editBgUrl.setEnabled(false);
            selectBgPath.setEnabled(true);
            activity.launcherSetting.launcherBackground.type = 2;
            activity.launcherSetting.launcherBackground.path = editBgPath.getText().toString();
            if (new File(editBgPath.getText().toString()).exists() && isImageFile(editBgPath.getText().toString())){
                Bitmap bitmap = BitmapFactory.decodeFile(editBgPath.getText().toString());
                activity.launcherLayout.setBackground(new BitmapDrawable(bitmap));
            }
            else {
                activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background));
            }
        }
        if (buttonView == onlineRadio && isChecked){
            defaultRadio.setChecked(false);
            classicRadio.setChecked(false);
            customRadio.setChecked(false);
            editBgPath.setEnabled(false);
            editBgUrl.setEnabled(true);
            selectBgPath.setEnabled(false);
            activity.launcherSetting.launcherBackground.type = 3;
            activity.launcherSetting.launcherBackground.path = editBgUrl.getText().toString();
            new Thread(() -> {
                try {
                    URL url = new URL(editBgUrl.getText().toString());
                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                    httpURLConnection.setDoInput(true);
                    httpURLConnection.connect();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    handler.post(() -> activity.launcherLayout.setBackground(new BitmapDrawable(bitmap)));
                } catch (IOException e) {
                    handler.post(() -> activity.launcherLayout.setBackground(context.getDrawable(R.drawable.ic_background)));
                    e.printStackTrace();
                }
            }).start();
        }
        GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
    }

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
}
