package com.tungsten.hmclpe.control;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.gson.Gson;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.bean.BaseButtonInfo;
import com.tungsten.hmclpe.control.bean.BaseRockerViewInfo;
import com.tungsten.hmclpe.control.view.LayoutPanel;
import com.tungsten.hmclpe.control.view.TouchCharInput;
import com.tungsten.hmclpe.launcher.dialogs.control.AddViewDialog;
import com.tungsten.hmclpe.launcher.dialogs.control.ChildManagerDialog;
import com.tungsten.hmclpe.launcher.dialogs.control.EditControlPatternDialog;
import com.tungsten.hmclpe.launcher.dialogs.hin2n.Hin2nMenuDialog;
import com.tungsten.hmclpe.launcher.dialogs.hin2n.JoinCommunityDialog;
import com.tungsten.hmclpe.launcher.list.local.controller.ChildLayout;
import com.tungsten.hmclpe.launcher.list.local.controller.ControlPattern;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.InitializeSetting;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.launcher.setting.game.GameMenuSetting;
import com.tungsten.hmclpe.multiplayer.Hin2nService;
import com.tungsten.hmclpe.utils.file.AssetsUtils;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;

import java.util.ArrayList;

import wang.switchy.hin2n.model.N2NSettingInfo;

public class MenuHelper implements CompoundButton.OnCheckedChangeListener, View.OnClickListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener {

    public Context context;
    public AppCompatActivity activity;
    public boolean fullscreen;
    public String gameDir;
    public DrawerLayout drawerLayout;
    public LayoutPanel baseLayout;
    public int launcher;
    public float scaleFactor;

    public int screenWidth;
    public int screenHeight;

    public GameMenuSetting gameMenuSetting;

    public TouchCharInput touchCharInput;

    public SwitchCompat switchMenuFloat;
    public SwitchCompat switchMenuView;
    public SwitchCompat switchMenuSlide;
    public SwitchCompat switchFloatMovable;
    public SwitchCompat switchAdvanceInput;
    public SwitchCompat switchSensor;
    public SwitchCompat switchHalfScreen;
    public Spinner spinnerTouchMode;
    public Spinner spinnerMouseMode;
    public TextView sensitivityText;
    public SeekBar sensitivitySeekbar;
    public TextView mouseSpeedText;
    public SeekBar mouseSpeedSeekbar;
    public TextView mouseSizeText;
    public SeekBar mouseSizeSeekbar;
    public SwitchCompat switchHideUI;
    public Button openHin2nMenu;
    public Button forceExit;

    public Spinner patternSpinner;
    public SwitchCompat editModeSwitch;
    public SwitchCompat showOutlineSwitch;
    public Button editInfo;
    public Button manageChild;
    public Spinner childSpinner;
    public Button addView;

    public ArrayList<ControlPattern> patternList;
    public ControlPattern currentPattern;
    public String initialPattern;
    public String currentChild;
    public boolean editMode;
    public boolean showOutline;
    public boolean enableNameEditor;
    public ArrayList<String> childLayoutList;
    public ArrayAdapter<String> childAdapter;

    public int gameCursorMode = 0;

    public ViewManager viewManager;
    public MKManager mkManager;

    public boolean enterLock;

    public float cursorX;
    public float cursorY;
    public float pointerX;
    public float pointerY;
    public float currentX;
    public float currentY;

    public MenuHelper(Context context, AppCompatActivity activity,boolean fullscreen,String gameDir, DrawerLayout drawerLayout, LayoutPanel baseLayout,boolean editMode,String currentPattern,int launcher,float scaleFactor){
        this.context = context;
        this.activity = activity;
        this.fullscreen = fullscreen;
        this.gameDir = gameDir;
        this.drawerLayout = drawerLayout;
        this.baseLayout = baseLayout;
        this.editMode = editMode;
        this.showOutline = false;
        this.enableNameEditor = editMode;
        this.launcher = launcher;
        this.scaleFactor = scaleFactor;
        patternList = SettingUtils.getControlPatternList();
        if (patternList.size() == 0) {
            InitializeSetting.initializeControlPattern(activity, new AssetsUtils.FileOperateCallback() {
                @Override
                public void onSuccess() {
                    patternList = SettingUtils.getControlPatternList();
                    preInit(baseLayout,editMode,currentPattern);
                }

                @Override
                public void onFailed(String error) {

                }
            });
        }
        else {
            preInit(baseLayout,editMode,currentPattern);
        }
    }

    public void enableCursor() {
        gameCursorMode = 0;
        if (viewManager != null) {
            viewManager.enableCursor();
        }
        if (mkManager != null) {
            mkManager.enableCursor();
        }
    }

    public void disableCursor(){
        gameCursorMode = 1;
        if (viewManager != null) {
            viewManager.disableCursor();
        }
        if (mkManager != null) {
            mkManager.disableCursor();
        }
    }

    public void preInit (LayoutPanel baseLayout,boolean editMode,String currentPattern) {
        for (ControlPattern controlPattern : patternList){
            if (controlPattern.name.equals(currentPattern)){
                this.currentPattern = controlPattern;
            }
        }
        if (currentPattern == null) {
            currentPattern = SettingUtils.getControlPatternList().get(0).name;
        }
        currentChild = SettingUtils.getChildList(currentPattern).size() > 0 ? SettingUtils.getChildList(currentPattern).get(0).name : null;
        if (launcher == 0){
            baseLayout.showBackground();
        }

        gameMenuSetting = GameMenuSetting.getGameMenuSetting();
        init();
    }

    @SuppressLint("SetTextI18n")
    public void init(){
        touchCharInput = activity.findViewById(R.id.input_scanner);
        touchCharInput.setCharacterSender(this, new LwjglCharSender());

        switchMenuFloat = activity.findViewById(R.id.switch_float_button);
        switchMenuView = activity.findViewById(R.id.switch_bar);
        switchMenuSlide = activity.findViewById(R.id.switch_gesture);
        switchFloatMovable = activity.findViewById(R.id.switch_float_movable);
        switchAdvanceInput = activity.findViewById(R.id.switch_advance_input);
        switchSensor = activity.findViewById(R.id.switch_control_sensor);
        switchHalfScreen = activity.findViewById(R.id.switch_half_screen);
        spinnerTouchMode = activity.findViewById(R.id.spinner_touch_mode);
        spinnerMouseMode = activity.findViewById(R.id.spinner_mouse_mode);
        sensitivityText = activity.findViewById(R.id.sensitivity_text);
        sensitivitySeekbar = activity.findViewById(R.id.sensor_sensitivity);
        mouseSpeedText = activity.findViewById(R.id.mouse_speed_text);
        mouseSpeedSeekbar = activity.findViewById(R.id.mouse_speed);
        mouseSizeText = activity.findViewById(R.id.mouse_size_text);
        mouseSizeSeekbar = activity.findViewById(R.id.mouse_size);
        switchHideUI = activity.findViewById(R.id.switch_hide_ui);
        openHin2nMenu = activity.findViewById(R.id.open_hin2n_menu);
        forceExit = activity.findViewById(R.id.force_exit);

        switchMenuFloat.setChecked(gameMenuSetting.menuFloatSetting.enable);
        switchMenuView.setChecked(gameMenuSetting.menuViewSetting.enable);
        switchMenuSlide.setChecked(gameMenuSetting.menuSlideSetting);
        switchFloatMovable.setChecked(gameMenuSetting.menuFloatSetting.movable);
        switchAdvanceInput.setChecked(gameMenuSetting.advanceInput);
        switchSensor.setChecked(gameMenuSetting.enableSensor);
        switchHalfScreen.setChecked(gameMenuSetting.disableHalfScreen);
        switchHideUI.setChecked(gameMenuSetting.hideUI);

        switchMenuFloat.setOnCheckedChangeListener(this);
        switchMenuView.setOnCheckedChangeListener(this);
        switchMenuSlide.setOnCheckedChangeListener(this);
        switchFloatMovable.setOnCheckedChangeListener(this);
        switchAdvanceInput.setOnCheckedChangeListener(this);
        switchSensor.setOnCheckedChangeListener(this);
        switchHalfScreen.setOnCheckedChangeListener(this);
        switchHideUI.setOnCheckedChangeListener(this);
        openHin2nMenu.setOnClickListener(this);
        forceExit.setOnClickListener(this);

        ArrayList<String> touchModes = new ArrayList<>();
        touchModes.add(context.getString(R.string.drawer_game_menu_control_touch_mode_create));
        touchModes.add(context.getString(R.string.drawer_game_menu_control_touch_mode_attack));
        ArrayAdapter<String> touchModeAdapter = new ArrayAdapter<>(context,R.layout.item_spinner_drop_down_small,touchModes);
        spinnerTouchMode.setAdapter(touchModeAdapter);
        spinnerTouchMode.setSelection(gameMenuSetting.touchMode);
        spinnerTouchMode.setOnItemSelectedListener(this);

        ArrayList<String> mouseModes = new ArrayList<>();
        mouseModes.add(context.getString(R.string.drawer_game_menu_control_mouse_mode_click));
        mouseModes.add(context.getString(R.string.drawer_game_menu_control_mouse_mode_slide));
        ArrayAdapter<String> mouseModeAdapter = new ArrayAdapter<>(context,R.layout.item_spinner_drop_down_small,mouseModes);
        spinnerMouseMode.setAdapter(mouseModeAdapter);
        spinnerMouseMode.setSelection(gameMenuSetting.mouseMode);
        spinnerMouseMode.setOnItemSelectedListener(this);

        patternSpinner = activity.findViewById(R.id.current_pattern_spinner);
        editModeSwitch = activity.findViewById(R.id.switch_edit_mode);
        showOutlineSwitch = activity.findViewById(R.id.switch_show_outline);
        editInfo = activity.findViewById(R.id.edit_pattern_info);
        manageChild = activity.findViewById(R.id.manage_child_layout);
        childSpinner = activity.findViewById(R.id.current_child_spinner);
        addView = activity.findViewById(R.id.add_view);

        sensitivityText.setText(Integer.toString(gameMenuSetting.sensitivity));
        sensitivitySeekbar.setProgress(gameMenuSetting.sensitivity);
        sensitivitySeekbar.setOnSeekBarChangeListener(this);

        mouseSpeedText.setText(Float.toString(gameMenuSetting.mouseSpeed * 100));
        mouseSpeedSeekbar.setProgress((int) (gameMenuSetting.mouseSpeed * 100));
        mouseSpeedSeekbar.setOnSeekBarChangeListener(this);

        mouseSizeText.setText(Integer.toString(gameMenuSetting.mouseSize));
        mouseSizeSeekbar.setProgress(gameMenuSetting.mouseSize);
        mouseSizeSeekbar.setOnSeekBarChangeListener(this);

        ArrayList<String> patterns = new ArrayList<>();
        for (ControlPattern controlPattern : patternList){
            patterns.add(controlPattern.name);
        }
        ArrayAdapter<String> patternAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_drop_down_small,patterns);
        patternSpinner.setAdapter(patternAdapter);
        patternSpinner.setSelection(patternAdapter.getPosition(currentPattern.name));

        ArrayList<ChildLayout> list = SettingUtils.getChildList(currentPattern.name);
        childLayoutList = new ArrayList<>();
        for (ChildLayout childLayout : list){
            childLayoutList.add(childLayout.name);
        }
        childAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_drop_down_small,childLayoutList);
        childSpinner.setAdapter(childAdapter);

        if (editMode) {
            editInfo.setEnabled(true);
            manageChild.setEnabled(true);
            childSpinner.setEnabled(true);
            addView.setEnabled(true);
        }
        else {
            editInfo.setEnabled(false);
            manageChild.setEnabled(false);
            childSpinner.setEnabled(false);
            addView.setEnabled(false);
        }
        editModeSwitch.setChecked(editMode);

        childSpinner.setSelection(0);

        patternSpinner.setOnItemSelectedListener(this);
        editModeSwitch.setOnCheckedChangeListener(this);
        showOutlineSwitch.setOnCheckedChangeListener(this);
        editInfo.setOnClickListener(this);
        manageChild.setOnClickListener(this);
        childSpinner.setOnItemSelectedListener(this);
        addView.setOnClickListener(this);

        baseLayout.post(() -> {
            screenWidth = baseLayout.getWidth();
            screenHeight = baseLayout.getHeight();
            viewManager = new ViewManager(context,activity,this,baseLayout,launcher);
            mkManager = new MKManager(this);
            checkOpenMenuSetting();
        });

        if (gameMenuSetting.menuSlideSetting){
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }
        else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        }
    }

    private void checkOpenMenuSetting(){
        if (!gameMenuSetting.menuFloatSetting.enable && !gameMenuSetting.menuViewSetting.enable && !gameMenuSetting.menuSlideSetting){
            switchMenuFloat.setChecked(true);
        }
    }

    public void refreshChildSpinner(){
        ArrayList<ChildLayout> list = SettingUtils.getChildList(currentPattern.name);
        childLayoutList = new ArrayList<>();
        for (ChildLayout childLayout : list){
            childLayoutList.add(childLayout.name);
        }
        childAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_drop_down_small,childLayoutList);
        childSpinner.setAdapter(childAdapter);
        if (childLayoutList.size() == 0){
            currentChild = null;
        }
        else {
            if (childLayoutList.contains(currentChild)){
                childSpinner.setSelection(childAdapter.getPosition(currentChild));
            }
            else {
                childSpinner.setSelection(0);
                currentChild = childLayoutList.get(0);
            }
        }
        viewManager.refreshLayout(currentPattern.name,currentChild,editMode);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Hin2nService.VPN_REQUEST_CODE_CREATE && resultCode == RESULT_OK) {
            Intent intent = new Intent(context, Hin2nService.class);
            Bundle bundle = new Bundle();
            N2NSettingInfo n2NSettingInfo = new N2NSettingInfo(Hin2nService.getCreatorModel());
            bundle.putParcelable("n2nSettingInfo", n2NSettingInfo);
            intent.putExtra("Setting", bundle);
            activity.startService(intent);
        }
        if (requestCode == Hin2nService.VPN_REQUEST_CODE_JOIN && resultCode == RESULT_OK) {
            Intent intent = new Intent(context, Hin2nService.class);
            Bundle bundle = new Bundle();
            new Thread(() -> {
                N2NSettingInfo n2NSettingInfo = new N2NSettingInfo(Hin2nService.getPlayerModel());
                activity.runOnUiThread(() -> {
                    bundle.putParcelable("n2nSettingInfo", n2NSettingInfo);
                    intent.putExtra("Setting", bundle);
                    activity.startService(intent);
                    JoinCommunityDialog.getInstance().progressBar.setVisibility(View.GONE);
                    JoinCommunityDialog.getInstance().positive.setVisibility(View.VISIBLE);
                    JoinCommunityDialog.getInstance().negative.setEnabled(true);
                    JoinCommunityDialog.getInstance().dismiss();
                });
            }).start();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == switchMenuFloat){
            gameMenuSetting.menuFloatSetting.enable = b;
            if (b){
                baseLayout.addView(viewManager.menuFloat);
            }
            else {
                baseLayout.removeView(viewManager.menuFloat);
            }
            checkOpenMenuSetting();
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
        }
        if (compoundButton == switchMenuView){
            gameMenuSetting.menuViewSetting.enable = b;
            if (b){
                baseLayout.addView(viewManager.menuView);
            }
            else {
                baseLayout.removeView(viewManager.menuView);
            }
            checkOpenMenuSetting();
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
        }
        if (compoundButton == switchMenuSlide){
            gameMenuSetting.menuSlideSetting = b;
            if (b){
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
            }
            else {
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            }
            checkOpenMenuSetting();
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
        }
        if (compoundButton == switchFloatMovable) {
            gameMenuSetting.menuFloatSetting.movable = b;
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
        }
        if (compoundButton == switchAdvanceInput) {
            gameMenuSetting.advanceInput = b;
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
        }
        if (compoundButton == switchSensor){
            gameMenuSetting.enableSensor = b;
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
            if (viewManager != null){
                viewManager.setSensorEnable(b);
            }
        }
        if (compoundButton == switchHalfScreen){
            gameMenuSetting.disableHalfScreen = b;
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
        }
        if (compoundButton == switchHideUI){
            gameMenuSetting.hideUI = b;
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
            viewManager.hideUI(b);
        }
        if (compoundButton == editModeSwitch){
            editMode = b;
            if (b) {
                editInfo.setEnabled(true);
                manageChild.setEnabled(true);
                childSpinner.setEnabled(true);
                addView.setEnabled(true);
            }
            else {
                editInfo.setEnabled(false);
                manageChild.setEnabled(false);
                childSpinner.setEnabled(false);
                addView.setEnabled(false);
            }
            viewManager.refreshLayout(currentPattern.name,currentChild,b);
        }
        if (compoundButton == showOutlineSwitch) {
            showOutline = b;
            if (viewManager != null) {
                viewManager.refreshViews();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == openHin2nMenu) {
            if (launcher != 0) {
                Hin2nMenuDialog dialog = new Hin2nMenuDialog(context, this);
                dialog.show();
            }
        }
        if (view == forceExit) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.dialog_force_exit_title));
            builder.setMessage(context.getString(R.string.dialog_force_exit_message));
            builder.setPositiveButton(context.getString(R.string.dialog_force_exit_positive), (dialogInterface, i) -> {
                android.os.Process.killProcess(android.os.Process.myPid());
            });
            builder.setNegativeButton(context.getString(R.string.dialog_force_exit_negative), (dialogInterface, i) -> {});
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (view == editInfo){
            EditControlPatternDialog dialog = new EditControlPatternDialog(context,activity,enableNameEditor, new EditControlPatternDialog.OnPatternInfoChangeListener() {
                @Override
                public void OnInfoChange(ControlPattern controlPattern) {
                    if (currentPattern.name.equals(initialPattern)){
                        initialPattern = controlPattern.name;
                    }
                    FileUtils.rename(AppManifest.CONTROLLER_DIR + "/" + currentPattern.name,controlPattern.name);
                    Gson gson = new Gson();
                    String string = gson.toJson(controlPattern);
                    FileStringUtils.writeFile(AppManifest.CONTROLLER_DIR + "/" + controlPattern.name + "/info.json",string);
                    for (ChildLayout child : SettingUtils.getChildList(controlPattern.name)) {
                        for (BaseButtonInfo info : child.baseButtonList) {
                            info.pattern = controlPattern.name;
                        }
                        for (BaseRockerViewInfo info : child.baseRockerViewList) {
                            info.pattern = controlPattern.name;
                        }
                        ChildLayout.saveChildLayout(controlPattern.name,child);
                    }
                    patternList = SettingUtils.getControlPatternList();
                    currentPattern = controlPattern;
                    ArrayList<String> patterns = new ArrayList<>();
                    for (ControlPattern pattern : patternList){
                        patterns.add(pattern.name);
                    }
                    ArrayAdapter<String> patternAdapter = new ArrayAdapter<>(context, R.layout.item_spinner_drop_down_small,patterns);
                    patternSpinner.setAdapter(patternAdapter);
                    patternSpinner.setSelection(patternAdapter.getPosition(currentPattern.name));
                }
            },currentPattern);
            dialog.show();
        }
        if (view == manageChild){
            ChildManagerDialog dialog = new ChildManagerDialog(context,this,currentPattern);
            dialog.show();
        }
        if (view == addView){
            if (currentChild == null){
                Toast.makeText(context,context.getString(R.string.drawer_custom_menu_warn),Toast.LENGTH_SHORT).show();
            }
            else {
                AddViewDialog dialog = new AddViewDialog(context, currentPattern.name,currentChild, screenWidth, screenHeight, new AddViewDialog.OnViewCreateListener() {
                    @Override
                    public void onButtonCreate(BaseButtonInfo baseButtonInfo) {
                        viewManager.addButton(baseButtonInfo,View.VISIBLE);
                    }

                    @Override
                    public void onRockerCreate(BaseRockerViewInfo baseRockerViewInfo) {
                        viewManager.addRocker(baseRockerViewInfo,View.VISIBLE);
                    }
                },fullscreen);
                dialog.show();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == spinnerTouchMode){
            gameMenuSetting.touchMode = i;
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
        }
        if (adapterView == spinnerMouseMode){
            gameMenuSetting.mouseMode = i;
            GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
        }
        if (adapterView == patternSpinner){
            String str = (String) patternSpinner.getItemAtPosition(i);
            for (ControlPattern controlPattern : patternList){
                if (controlPattern.name.equals(str)){
                    currentPattern = controlPattern;
                    break;
                }
            }
            refreshChildSpinner();
        }
        if (adapterView == childSpinner){
            currentChild = (String) childSpinner.getItemAtPosition(i);
            if (editMode) {
                viewManager.refreshLayout(currentPattern.name,(String) childSpinner.getItemAtPosition(i), true);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == sensitivitySeekbar){
            gameMenuSetting.sensitivity = i;
            sensitivityText.setText(Integer.toString(i));
        }
        if (seekBar == mouseSpeedSeekbar){
            gameMenuSetting.mouseSpeed = (float) i / 100f;
            mouseSpeedText.setText(Integer.toString(i));
        }
        if (seekBar == mouseSizeSeekbar){
            gameMenuSetting.mouseSize = i;
            mouseSizeText.setText(Integer.toString(i));
        }
        GameMenuSetting.saveGameMenuSetting(gameMenuSetting);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }
}
