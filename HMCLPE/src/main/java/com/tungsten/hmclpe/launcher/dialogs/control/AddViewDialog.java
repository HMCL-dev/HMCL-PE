package com.tungsten.hmclpe.launcher.dialogs.control;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SwitchCompat;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.bean.BaseButtonInfo;
import com.tungsten.hmclpe.control.bean.BaseRockerViewInfo;
import com.tungsten.hmclpe.control.bean.ViewPosition;
import com.tungsten.hmclpe.control.bean.button.ButtonSize;
import com.tungsten.hmclpe.control.bean.button.ButtonStyle;
import com.tungsten.hmclpe.control.bean.rocker.RockerSize;
import com.tungsten.hmclpe.control.bean.rocker.RockerStyle;
import com.tungsten.hmclpe.launcher.dialogs.tools.ColorSelectorDialog;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import java.util.ArrayList;
import java.util.UUID;

public class AddViewDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, TextWatcher {

    private String pattern;
    private String child;
    private int screenWidth;
    private int screenHeight;
    private OnViewCreateListener onViewCreateListener;

    private BaseButtonInfo baseButtonInfo;
    private BaseRockerViewInfo baseRockerViewInfo;

    private Button showButton;
    private Button showRocker;
    private LinearLayout editButton;
    private LinearLayout editRocker;

    private Button positive;
    private Button negative;

    private int viewType;

    private ArrayAdapter<String> showTypeAdapter;
    private ArrayAdapter<String> sizeTypeAdapter;
    private ArrayAdapter<String> positionTypeAdapter;
    private ArrayAdapter<String> sizeObjectAdapter;
    private ArrayAdapter<String> functionTypeAdapter;
    private ArrayAdapter<String> buttonStyleAdapter;
    private ArrayAdapter<String> followTypeAdapter;
    private ArrayAdapter<String> rockerStyleAdapter;

    private EditText editButtonText;
    private Spinner buttonShowTypeSpinner;
    private Spinner buttonSizeTypeSpinner;
    private Spinner buttonPositionTypeSpinner;
    private LinearLayout widthObjectLayout;
    private Spinner widthObjectSpinner;
    private LinearLayout heightObjectLayout;
    private Spinner heightObjectSpinner;
    private SeekBar buttonWidthSeekbar;
    private SeekBar buttonHeightSeekbar;
    private SeekBar buttonXSeekbar;
    private SeekBar buttonYSeekbar;
    private TextView buttonWidthText;
    private TextView buttonHeightText;
    private TextView buttonXText;
    private TextView buttonYText;
    private Spinner functionTypeSpinner;
    private SwitchCompat checkViewMove;
    private SwitchCompat checkAutoKeep;
    private SwitchCompat checkAutoClick;
    private SwitchCompat checkOpenMenu;
    private SwitchCompat checkMovable;
    private SwitchCompat checkTouchMode;
    private SwitchCompat checkSensor;
    private SwitchCompat checkLeftPad;
    private SwitchCompat checkOpenInput;
    private Button childVisibility;
    private EditText editOutputText;
    private Button outputKeycode;
    private Spinner selectExist;
    private SwitchCompat checkUsingExist;
    private Button createButtonStyle;
    private LinearLayout buttonStyleLayout;
    private SeekBar textSizeSeekbar;
    private SeekBar cornerRadiusSeekbar;
    private SeekBar strokeWidthSeekbar;
    private SeekBar textSizePressedSeekbar;
    private SeekBar cornerRadiusPressedSeekbar;
    private SeekBar strokeWidthPressedSeekbar;
    private TextView textSizeText;
    private TextView cornerRadiusText;
    private TextView strokeWidthText;
    private TextView textSizePressedText;
    private TextView cornerRadiusPressedText;
    private TextView strokeWidthPressedText;
    private Button selectTextColor;
    private Button selectStrokeColor;
    private Button selectFillColor;
    private Button selectTextColorPressed;
    private Button selectStrokeColorPressed;
    private Button selectFillColorPressed;
    private View textColorPre;
    private View strokeColorPre;
    private View fillColorPre;
    private View textColorPressedPre;
    private View strokeColorPressedPre;
    private View fillColorPressedPre;
    private TextView textColorText;
    private TextView strokeColorText;
    private TextView fillColorText;
    private TextView textColorPressedText;
    private TextView strokeColorPressedText;
    private TextView fillColorPressedText;
    private ImageButton addButtonWidth;
    private ImageButton addButtonHeight;
    private ImageButton addButtonX;
    private ImageButton addButtonY;
    private ImageButton addButtonTextSize;
    private ImageButton addButtonCornerRadius;
    private ImageButton addButtonStrokeWidth;
    private ImageButton addButtonTextSizePress;
    private ImageButton addButtonCornerRadiusPress;
    private ImageButton addButtonStrokeWidthPress;
    private ImageButton reduceButtonWidth;
    private ImageButton reduceButtonHeight;
    private ImageButton reduceButtonX;
    private ImageButton reduceButtonY;
    private ImageButton reduceButtonTextSize;
    private ImageButton reduceButtonCornerRadius;
    private ImageButton reduceButtonStrokeWidth;
    private ImageButton reduceButtonTextSizePress;
    private ImageButton reduceButtonCornerRadiusPress;
    private ImageButton reduceButtonStrokeWidthPress;

    private Spinner rockerShowTypeSpinner;
    private Spinner rockerSizeTypeSpinner;
    private Spinner rockerPositionTypeSpinner;
    private LinearLayout sizeObjectLayout;
    private Spinner sizeObjectSpinner;
    private SeekBar rockerSizeSeekbar;
    private SeekBar rockerXSeekbar;
    private SeekBar rockerYSeekbar;
    private TextView rockerSizeText;
    private TextView rockerXText;
    private TextView rockerYText;
    private Spinner followTypeSpinner;
    private SwitchCompat checkShift;
    private Spinner selectExistRockerStyle;
    private SwitchCompat checkUsingExistRockerStyle;
    private Button createRockerStyle;
    private LinearLayout rockerStyleLayout;
    private SeekBar rockerCornerRadiusSeekbar;
    private SeekBar rockerCornerRadiusPressSeekbar;
    private SeekBar rockerStrokeWidthSeekbar;
    private SeekBar rockerStrokeWidthPressSeekbar;
    private TextView rockerCornerRadiusText;
    private TextView rockerStrokeWidthText;
    private TextView rockerCornerRadiusPressedText;
    private TextView rockerStrokeWidthPressedText;
    private Button selectPointerColor;
    private Button selectRockerStrokeColor;
    private Button selectRockerFillColor;
    private Button selectPointerColorPressed;
    private Button selectRockerStrokeColorPressed;
    private Button selectRockerFillColorPressed;
    private View pointerColorPre;
    private View rockerStrokeColorPre;
    private View rockerFillColorPre;
    private View pointerColorPressedPre;
    private View rockerStrokeColorPressedPre;
    private View rockerFillColorPressedPre;
    private TextView pointerColorText;
    private TextView rockerStrokeColorText;
    private TextView rockerFillColorText;
    private TextView pointerColorPressedText;
    private TextView rockerStrokeColorPressedText;
    private TextView rockerFillColorPressedText;
    private ImageButton addRockerSize;
    private ImageButton addRockerX;
    private ImageButton addRockerY;
    private ImageButton addRockerCornerRadius;
    private ImageButton addRockerStrokeWidth;
    private ImageButton addRockerCornerRadiusPress;
    private ImageButton addRockerStrokeWidthPress;
    private ImageButton reduceRockerSize;
    private ImageButton reduceRockerX;
    private ImageButton reduceRockerY;
    private ImageButton reduceRockerCornerRadius;
    private ImageButton reduceRockerStrokeWidth;
    private ImageButton reduceRockerCornerRadiusPress;
    private ImageButton reduceRockerStrokeWidthPress;

    private ButtonStyle selectedButtonStyle;

    private RockerStyle selectedRockerStyle;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public AddViewDialog(@NonNull Context context, String pattern,String child, int screenWidth, int screenHeight, OnViewCreateListener onViewCreateListener,boolean fullscreen) {
        super(context);
        this.pattern = pattern;
        this.child = child;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.onViewCreateListener = onViewCreateListener;
        viewType = 0;
        setContentView(R.layout.dialog_add_view);
        setCancelable(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if (fullscreen) {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
            } else {
                getWindow().getAttributes().layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_NEVER;
            }
        }
        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void init(){
        ButtonStyle styleB = SettingUtils.getButtonStyleList().get(0);
        baseButtonInfo = new BaseButtonInfo(UUID.randomUUID().toString(),
                pattern,
                child,
                "",
                BaseButtonInfo.SHOW_TYPE_ALWAYS,
                BaseButtonInfo.SIZE_TYPE_ABSOLUTE,
                new ButtonSize(50,0.06f,BaseButtonInfo.SIZE_OBJECT_WIDTH),
                new ButtonSize(50,0.06f,BaseButtonInfo.SIZE_OBJECT_WIDTH),
                BaseButtonInfo.POSITION_TYPE_PERCENT,
                new ViewPosition(0,0),
                new ViewPosition(0,0),
                BaseButtonInfo.FUNCTION_TYPE_TOUCH,
                false,false,false,false,false,false,false,false,false,
                new ArrayList<>(),
                "",
                new ArrayList<>(),
                true,
                styleB);

        RockerStyle styleR = SettingUtils.getRockerStyleList().get(0);
        baseRockerViewInfo = new BaseRockerViewInfo(UUID.randomUUID().toString(),
                pattern,
                child,
                BaseRockerViewInfo.SHOW_TYPE_ALWAYS,
                BaseRockerViewInfo.SIZE_TYPE_ABSOLUTE,
                new RockerSize(160,0.2f,BaseRockerViewInfo.SIZE_OBJECT_WIDTH),
                BaseRockerViewInfo.POSITION_TYPE_PERCENT,
                new ViewPosition(0,0),
                new ViewPosition(0,0),
                BaseRockerViewInfo.FUNCTION_FOLLOW_NONE,
                true,
                true,
                styleR);

        showButton = findViewById(R.id.add_button);
        showRocker = findViewById(R.id.add_rocker);
        editButton = findViewById(R.id.add_button_layout);
        editRocker = findViewById(R.id.add_rocker_layout);

        positive = findViewById(R.id.add_current_view);
        negative = findViewById(R.id.exit);

        showButton.setOnClickListener(this);
        showRocker.setOnClickListener(this);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        ArrayList<String> showType = new ArrayList<>();
        ArrayList<String> sizeType = new ArrayList<>();
        ArrayList<String> positionType = new ArrayList<>();
        ArrayList<String> sizeObject = new ArrayList<>();
        ArrayList<String> functionType = new ArrayList<>();
        ArrayList<String> followType = new ArrayList<>();
        showType.add(getContext().getString(R.string.dialog_add_view_always));
        showType.add(getContext().getString(R.string.dialog_add_view_only_in_game));
        showType.add(getContext().getString(R.string.dialog_add_view_only_out_game));
        sizeType.add(getContext().getString(R.string.dialog_add_view_size_type_percent));
        sizeType.add(getContext().getString(R.string.dialog_add_view_size_type_absolute));
        positionType.add(getContext().getString(R.string.dialog_add_view_position_type_percent));
        positionType.add(getContext().getString(R.string.dialog_add_view_position_type_absolute));
        sizeObject.add(getContext().getString(R.string.dialog_add_view_size_type_percent_object_width));
        sizeObject.add(getContext().getString(R.string.dialog_add_view_size_type_percent_object_height));
        functionType.add(getContext().getString(R.string.dialog_add_view_button_function_type_click));
        functionType.add(getContext().getString(R.string.dialog_add_view_button_function_type_double_click));
        followType.add(getContext().getString(R.string.dialog_add_view_rocker_function_follow_none));
        followType.add(getContext().getString(R.string.dialog_add_view_rocker_function_follow_part));
        followType.add(getContext().getString(R.string.dialog_add_view_rocker_function_follow_all));
        showTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, showType);
        sizeTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, sizeType);
        positionTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, positionType);
        sizeObjectAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, sizeObject);
        functionTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, functionType);
        followTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, followType);

        initButtonLayout();

        initRockerLayout();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private void initButtonLayout(){
        editButtonText = findViewById(R.id.edit_button_text);
        buttonShowTypeSpinner = findViewById(R.id.button_show_type);
        buttonSizeTypeSpinner = findViewById(R.id.button_size_type);
        buttonPositionTypeSpinner = findViewById(R.id.button_position_type);
        widthObjectLayout = findViewById(R.id.width_object_layout);
        widthObjectSpinner = findViewById(R.id.width_object);
        heightObjectLayout = findViewById(R.id.height_object_layout);
        heightObjectSpinner = findViewById(R.id.height_object);
        buttonWidthSeekbar = findViewById(R.id.button_width_seekbar);
        buttonHeightSeekbar = findViewById(R.id.button_height_seekbar);
        buttonXSeekbar = findViewById(R.id.button_x_seekbar);
        buttonYSeekbar = findViewById(R.id.button_y_seekbar);
        buttonWidthText = findViewById(R.id.width_text);
        buttonHeightText = findViewById(R.id.height_text);
        buttonXText = findViewById(R.id.button_x_text);
        buttonYText = findViewById(R.id.button_y_text);
        functionTypeSpinner = findViewById(R.id.function_type);
        checkViewMove = findViewById(R.id.function_extra_perspective);
        checkAutoKeep = findViewById(R.id.function_extra_keep);
        checkAutoClick = findViewById(R.id.function_extra_auto_click);
        checkOpenMenu = findViewById(R.id.function_open_menu);
        checkMovable = findViewById(R.id.function_movable);
        checkTouchMode = findViewById(R.id.function_touch_mode);
        checkSensor = findViewById(R.id.function_sensor);
        checkLeftPad = findViewById(R.id.function_left_touch);
        checkOpenInput = findViewById(R.id.function_open_input);
        childVisibility = findViewById(R.id.function_child_visibility);
        editOutputText = findViewById(R.id.function_output_keychar);
        outputKeycode = findViewById(R.id.function_output_keycode);
        selectExist = findViewById(R.id.exterior_use_exist_button);
        checkUsingExist = findViewById(R.id.switch_exterior_use_exist_button);
        createButtonStyle = findViewById(R.id.exterior_add_button_style);
        buttonStyleLayout = findViewById(R.id.button_style_layout);
        textSizeSeekbar = findViewById(R.id.exterior_text_size_seekbar);
        cornerRadiusSeekbar = findViewById(R.id.exterior_corner_radius_seekbar);
        strokeWidthSeekbar = findViewById(R.id.exterior_stroke_width_seekbar);
        textSizePressedSeekbar = findViewById(R.id.exterior_text_size_seekbar_pressed);
        cornerRadiusPressedSeekbar = findViewById(R.id.exterior_corner_radius_seekbar_pressed);
        strokeWidthPressedSeekbar = findViewById(R.id.exterior_stroke_width_seekbar_pressed);
        textSizeText = findViewById(R.id.text_size_text);
        cornerRadiusText = findViewById(R.id.corner_radius_text);
        strokeWidthText = findViewById(R.id.stroke_width_text);
        textSizePressedText = findViewById(R.id.text_size_pressed_text);
        cornerRadiusPressedText = findViewById(R.id.corner_radius_pressed_text);
        strokeWidthPressedText = findViewById(R.id.stroke_width_pressed_text);
        selectTextColor = findViewById(R.id.exterior_text_color);
        selectStrokeColor = findViewById(R.id.exterior_stroke_color);
        selectFillColor = findViewById(R.id.exterior_fill_color);
        selectTextColorPressed = findViewById(R.id.exterior_text_color_pressed);
        selectStrokeColorPressed = findViewById(R.id.exterior_stroke_color_pressed);
        selectFillColorPressed = findViewById(R.id.exterior_fill_color_pressed);
        textColorPre = findViewById(R.id.text_color_preview);
        strokeColorPre = findViewById(R.id.stroke_color_preview);
        fillColorPre = findViewById(R.id.fill_color_preview);
        textColorPressedPre = findViewById(R.id.text_color_pressed_preview);
        strokeColorPressedPre = findViewById(R.id.stroke_color_pressed_preview);
        fillColorPressedPre = findViewById(R.id.fill_color_pressed_preview);
        textColorText = findViewById(R.id.text_color_text);
        strokeColorText = findViewById(R.id.stroke_color_text);
        fillColorText = findViewById(R.id.fill_color_text);
        textColorPressedText = findViewById(R.id.text_color_pressed_text);
        strokeColorPressedText = findViewById(R.id.stroke_color_pressed_text);
        fillColorPressedText = findViewById(R.id.fill_color_pressed_text);
        addButtonWidth = findViewById(R.id.add_button_width);
        addButtonHeight = findViewById(R.id.add_button_height);
        addButtonX = findViewById(R.id.add_button_x);
        addButtonY = findViewById(R.id.add_button_y);
        addButtonTextSize = findViewById(R.id.add_text_size);
        addButtonCornerRadius = findViewById(R.id.add_corner_radius);
        addButtonStrokeWidth = findViewById(R.id.add_stroke_width);
        addButtonTextSizePress = findViewById(R.id.add_text_size_pressed);
        addButtonCornerRadiusPress = findViewById(R.id.add_corner_radius_pressed);
        addButtonStrokeWidthPress = findViewById(R.id.add_stroke_width_pressed);
        reduceButtonWidth = findViewById(R.id.reduce_button_width);
        reduceButtonHeight = findViewById(R.id.reduce_button_height);
        reduceButtonX = findViewById(R.id.reduce_button_x);
        reduceButtonY = findViewById(R.id.reduce_button_y);
        reduceButtonTextSize = findViewById(R.id.reduce_text_size);
        reduceButtonCornerRadius = findViewById(R.id.reduce_corner_radius);
        reduceButtonStrokeWidth = findViewById(R.id.reduce_stroke_width);
        reduceButtonTextSizePress = findViewById(R.id.reduce_text_size_pressed);
        reduceButtonCornerRadiusPress = findViewById(R.id.reduce_corner_radius_pressed);
        reduceButtonStrokeWidthPress = findViewById(R.id.reduce_stroke_width_pressed);

        editButtonText.setText(baseButtonInfo.text);
        editOutputText.setText(baseButtonInfo.outputText);
        buttonShowTypeSpinner.setAdapter(showTypeAdapter);
        buttonShowTypeSpinner.setSelection(baseButtonInfo.showType);
        buttonSizeTypeSpinner.setAdapter(sizeTypeAdapter);
        buttonSizeTypeSpinner.setSelection(baseButtonInfo.sizeType);
        buttonPositionTypeSpinner.setAdapter(positionTypeAdapter);
        buttonPositionTypeSpinner.setSelection(baseButtonInfo.positionType);
        if (baseButtonInfo.sizeType == BaseButtonInfo.SIZE_TYPE_ABSOLUTE) {
            widthObjectLayout.setVisibility(View.GONE);
            heightObjectLayout.setVisibility(View.GONE);
        }
        widthObjectSpinner.setAdapter(sizeObjectAdapter);
        widthObjectSpinner.setSelection(baseButtonInfo.width.object);
        heightObjectSpinner.setAdapter(sizeObjectAdapter);
        heightObjectSpinner.setSelection(baseButtonInfo.height.object);
        functionTypeSpinner.setAdapter(functionTypeAdapter);
        functionTypeSpinner.setSelection(baseButtonInfo.functionType);
        if (baseButtonInfo.sizeType == BaseButtonInfo.SIZE_TYPE_PERCENT) {
            buttonWidthSeekbar.setMin(1);
            buttonHeightSeekbar.setMin(1);
            buttonWidthSeekbar.setMax(1000);
            buttonHeightSeekbar.setMax(1000);
            buttonWidthSeekbar.setProgress((int) (1000 * baseButtonInfo.width.percentSize));
            buttonHeightSeekbar.setProgress((int) (1000 * baseButtonInfo.height.percentSize));
            buttonWidthText.setText(((int) (100 * baseButtonInfo.width.percentSize)) / 10f + " %");
            buttonHeightText.setText(((int) (100 * baseButtonInfo.height.percentSize)) / 10f + " %");
        }
        else {
            buttonWidthSeekbar.setMin(1);
            buttonHeightSeekbar.setMin(1);
            buttonWidthSeekbar.setMax(200);
            buttonHeightSeekbar.setMax(200);
            buttonWidthSeekbar.setProgress(baseButtonInfo.width.absoluteSize);
            buttonHeightSeekbar.setProgress(baseButtonInfo.height.absoluteSize);
            buttonWidthText.setText(baseButtonInfo.width.absoluteSize + " dp");
            buttonHeightText.setText(baseButtonInfo.height.absoluteSize + " dp");
        }
        if (baseButtonInfo.positionType == BaseButtonInfo.POSITION_TYPE_PERCENT) {
            buttonXSeekbar.setMax(1000);
            buttonYSeekbar.setMax(1000);
            buttonXSeekbar.setProgress((int) (1000 * baseButtonInfo.xPosition.percentPosition));
            buttonYSeekbar.setProgress((int) (1000 * baseButtonInfo.yPosition.percentPosition));
            buttonXText.setText(((int) (1000 * baseButtonInfo.xPosition.percentPosition)) / 10f + " %");
            buttonYText.setText(((int) (1000 * baseButtonInfo.yPosition.percentPosition)) / 10f + " %");
        }
        else {
            buttonXSeekbar.setMax(ConvertUtils.px2dip(getContext(),screenWidth));
            buttonYSeekbar.setMax(ConvertUtils.px2dip(getContext(),screenHeight));
            buttonXSeekbar.setProgress(baseButtonInfo.xPosition.absolutePosition);
            buttonYSeekbar.setProgress(baseButtonInfo.yPosition.absolutePosition);
            buttonXText.setText(baseButtonInfo.xPosition.absolutePosition + " dp");
            buttonYText.setText(baseButtonInfo.yPosition.absolutePosition + " dp");
        }
        onButtonSizeChange();
        checkViewMove.setChecked(baseButtonInfo.viewMove);
        checkAutoKeep.setChecked(baseButtonInfo.autoKeep);
        checkAutoClick.setChecked(baseButtonInfo.autoClick);
        checkOpenMenu.setChecked(baseButtonInfo.openMenu);
        checkMovable.setChecked(baseButtonInfo.movable);
        checkTouchMode.setChecked(baseButtonInfo.switchTouchMode);
        checkSensor.setChecked(baseButtonInfo.switchSensor);
        checkLeftPad.setChecked(baseButtonInfo.switchLeftPad);
        checkOpenInput.setChecked(baseButtonInfo.showInputDialog);
        refreshButtonStyleList(true);
        ArrayList<String> names = new ArrayList<>();
        for (ButtonStyle style : SettingUtils.getButtonStyleList()){
            names.add(style.name);
        }
        if (baseButtonInfo.usingExist && names.contains(baseButtonInfo.buttonStyle.name)) {
            buttonStyleLayout.setVisibility(View.GONE);
            checkUsingExist.setChecked(true);
            selectExist.setSelection(buttonStyleAdapter.getPosition(baseButtonInfo.buttonStyle.name));
            selectedButtonStyle = baseButtonInfo.buttonStyle;
        }
        else {
            baseButtonInfo.usingExist = false;
            baseButtonInfo.buttonStyle.name = "";
            selectedButtonStyle = SettingUtils.getButtonStyleList().get(0);
        }
        refreshButtonStyleEditor();

        buttonShowTypeSpinner.setOnItemSelectedListener(this);
        buttonSizeTypeSpinner.setOnItemSelectedListener(this);
        buttonPositionTypeSpinner.setOnItemSelectedListener(this);
        widthObjectSpinner.setOnItemSelectedListener(this);
        heightObjectSpinner.setOnItemSelectedListener(this);
        functionTypeSpinner.setOnItemSelectedListener(this);
        selectExist.setOnItemSelectedListener(this);
        buttonWidthSeekbar.setOnSeekBarChangeListener(this);
        buttonHeightSeekbar.setOnSeekBarChangeListener(this);
        buttonXSeekbar.setOnSeekBarChangeListener(this);
        buttonYSeekbar.setOnSeekBarChangeListener(this);
        textSizeSeekbar.setOnSeekBarChangeListener(this);
        cornerRadiusSeekbar.setOnSeekBarChangeListener(this);
        strokeWidthSeekbar.setOnSeekBarChangeListener(this);
        textSizePressedSeekbar.setOnSeekBarChangeListener(this);
        cornerRadiusPressedSeekbar.setOnSeekBarChangeListener(this);
        strokeWidthPressedSeekbar.setOnSeekBarChangeListener(this);
        checkViewMove.setOnCheckedChangeListener(this);
        checkAutoKeep.setOnCheckedChangeListener(this);
        checkAutoClick.setOnCheckedChangeListener(this);
        checkOpenMenu.setOnCheckedChangeListener(this);
        checkMovable.setOnCheckedChangeListener(this);
        checkTouchMode.setOnCheckedChangeListener(this);
        checkSensor.setOnCheckedChangeListener(this);
        checkLeftPad.setOnCheckedChangeListener(this);
        checkOpenInput.setOnCheckedChangeListener(this);
        checkUsingExist.setOnCheckedChangeListener(this);
        childVisibility.setOnClickListener(this);
        outputKeycode.setOnClickListener(this);
        createButtonStyle.setOnClickListener(this);
        selectTextColor.setOnClickListener(this);
        selectStrokeColor.setOnClickListener(this);
        selectFillColor.setOnClickListener(this);
        selectTextColorPressed.setOnClickListener(this);
        selectStrokeColorPressed.setOnClickListener(this);
        selectFillColorPressed.setOnClickListener(this);
        editButtonText.addTextChangedListener(this);
        editOutputText.addTextChangedListener(this);
        addButtonWidth.setOnClickListener(this);
        addButtonHeight.setOnClickListener(this);
        addButtonX.setOnClickListener(this);
        addButtonY.setOnClickListener(this);
        addButtonTextSize.setOnClickListener(this);
        addButtonStrokeWidth.setOnClickListener(this);
        addButtonCornerRadius.setOnClickListener(this);
        addButtonTextSizePress.setOnClickListener(this);
        addButtonStrokeWidthPress.setOnClickListener(this);
        addButtonCornerRadiusPress.setOnClickListener(this);
        reduceButtonWidth.setOnClickListener(this);
        reduceButtonHeight.setOnClickListener(this);
        reduceButtonX.setOnClickListener(this);
        reduceButtonY.setOnClickListener(this);
        reduceButtonTextSize.setOnClickListener(this);
        reduceButtonStrokeWidth.setOnClickListener(this);
        reduceButtonCornerRadius.setOnClickListener(this);
        reduceButtonTextSizePress.setOnClickListener(this);
        reduceButtonStrokeWidthPress.setOnClickListener(this);
        reduceButtonCornerRadiusPress.setOnClickListener(this);
    }

    public void refreshButtonStyleList(boolean first){
        ArrayList<ButtonStyle> styles = SettingUtils.getButtonStyleList();
        ArrayList<String> names = new ArrayList<>();
        for (ButtonStyle style : styles){
            names.add(style.name);
        }
        buttonStyleAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner,names);
        selectExist.setAdapter(buttonStyleAdapter);
        if (!first) {
            if (baseButtonInfo.usingExist && names.contains(baseButtonInfo.buttonStyle.name)) {
                buttonStyleLayout.setVisibility(View.GONE);
                checkUsingExist.setChecked(true);
                selectExist.setSelection(buttonStyleAdapter.getPosition(baseButtonInfo.buttonStyle.name));
                selectedButtonStyle = baseButtonInfo.buttonStyle;
            }
            else {
                buttonStyleLayout.setVisibility(View.VISIBLE);
                checkUsingExist.setChecked(false);
                baseButtonInfo.usingExist = false;
                baseButtonInfo.buttonStyle.name = "";
                selectedButtonStyle = SettingUtils.getButtonStyleList().get(0);
            }
            refreshButtonStyleEditor();
        }
    }

    @SuppressLint("SetTextI18n")
    private void onButtonSizeChange(){
        int xMax;
        int yMax;
        if (baseButtonInfo.sizeType == BaseButtonInfo.SIZE_TYPE_PERCENT) {
            int widthObject = baseButtonInfo.width.object == BaseButtonInfo.SIZE_OBJECT_WIDTH ? screenWidth : screenHeight;
            int heightObject = baseButtonInfo.height.object == BaseButtonInfo.SIZE_OBJECT_WIDTH ? screenWidth : screenHeight;
            xMax = (int) (ConvertUtils.px2dip(getContext(),screenWidth) - (ConvertUtils.px2dip(getContext(),widthObject) * baseButtonInfo.width.percentSize));
            yMax = (int) (ConvertUtils.px2dip(getContext(),screenHeight) - (ConvertUtils.px2dip(getContext(),heightObject) * baseButtonInfo.height.percentSize));
        }
        else {
            xMax = ConvertUtils.px2dip(getContext(),screenWidth) - baseButtonInfo.width.absoluteSize;
            yMax = ConvertUtils.px2dip(getContext(),screenHeight) - baseButtonInfo.height.absoluteSize;
        }
        if (baseButtonInfo.positionType == BaseButtonInfo.POSITION_TYPE_ABSOLUTE){
            if (baseButtonInfo.xPosition.absolutePosition > xMax){
                baseButtonInfo.xPosition.absolutePosition = xMax;
                buttonXSeekbar.setProgress(xMax);
                buttonXText.setText(xMax + " dp");
            }
            if (baseButtonInfo.yPosition.absolutePosition > yMax){
                baseButtonInfo.yPosition.absolutePosition = yMax;
                buttonYSeekbar.setProgress(yMax);
                buttonYText.setText(yMax + " dp");
            }
            buttonXSeekbar.setMax(xMax);
            buttonYSeekbar.setMax(yMax);
        }
        else {
            if (baseButtonInfo.xPosition.absolutePosition > xMax){
                baseButtonInfo.xPosition.absolutePosition = xMax;
            }
            if (baseButtonInfo.yPosition.absolutePosition > yMax){
                baseButtonInfo.yPosition.absolutePosition = yMax;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void refreshButtonStyleEditor(){
        textSizeSeekbar.setProgress(baseButtonInfo.buttonStyle.textSize);
        cornerRadiusSeekbar.setProgress(baseButtonInfo.buttonStyle.cornerRadius);
        strokeWidthSeekbar.setProgress((int) (baseButtonInfo.buttonStyle.strokeWidth * 10));
        textSizePressedSeekbar.setProgress(baseButtonInfo.buttonStyle.textSizePress);
        cornerRadiusPressedSeekbar.setProgress(baseButtonInfo.buttonStyle.cornerRadiusPress);
        strokeWidthPressedSeekbar.setProgress((int) (baseButtonInfo.buttonStyle.strokeWidthPress * 10));
        textSizeText.setText(baseButtonInfo.buttonStyle.textSize + " sp");
        cornerRadiusText.setText(baseButtonInfo.buttonStyle.cornerRadius + " dp");
        strokeWidthText.setText(baseButtonInfo.buttonStyle.strokeWidth + " dp");
        textSizePressedText.setText(baseButtonInfo.buttonStyle.textSizePress + " sp");
        cornerRadiusPressedText.setText(baseButtonInfo.buttonStyle.cornerRadiusPress + " dp");
        strokeWidthPressedText.setText(baseButtonInfo.buttonStyle.strokeWidthPress + " dp");
        textColorPre.setBackgroundColor(Color.parseColor(baseButtonInfo.buttonStyle.textColor));
        strokeColorPre.setBackgroundColor(Color.parseColor(baseButtonInfo.buttonStyle.strokeColor));
        fillColorPre.setBackgroundColor(Color.parseColor(baseButtonInfo.buttonStyle.fillColor));
        textColorPressedPre.setBackgroundColor(Color.parseColor(baseButtonInfo.buttonStyle.textColorPress));
        strokeColorPressedPre.setBackgroundColor(Color.parseColor(baseButtonInfo.buttonStyle.strokeColorPress));
        fillColorPressedPre.setBackgroundColor(Color.parseColor(baseButtonInfo.buttonStyle.fillColorPress));
        textColorText.setText(baseButtonInfo.buttonStyle.textColor);
        strokeColorText.setText(baseButtonInfo.buttonStyle.strokeColor);
        fillColorText.setText(baseButtonInfo.buttonStyle.fillColor);
        textColorPressedText.setText(baseButtonInfo.buttonStyle.textColorPress);
        strokeColorPressedText.setText(baseButtonInfo.buttonStyle.strokeColorPress);
        fillColorPressedText.setText(baseButtonInfo.buttonStyle.fillColorPress);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("SetTextI18n")
    private void initRockerLayout(){
        rockerShowTypeSpinner = findViewById(R.id.rocker_show_type);
        rockerSizeTypeSpinner = findViewById(R.id.rocker_size_type);
        rockerPositionTypeSpinner = findViewById(R.id.rocker_position_type);
        sizeObjectLayout = findViewById(R.id.size_object_layout);
        sizeObjectSpinner = findViewById(R.id.size_object);
        rockerSizeSeekbar = findViewById(R.id.rocker_size_seekbar);
        rockerXSeekbar = findViewById(R.id.rocker_x_seekbar);
        rockerYSeekbar = findViewById(R.id.rocker_y_seekbar);
        rockerSizeText = findViewById(R.id.size_text);
        rockerXText = findViewById(R.id.rocker_x_text);
        rockerYText = findViewById(R.id.rocker_y_text);
        followTypeSpinner = findViewById(R.id.function_follow);
        checkShift = findViewById(R.id.function_shift);
        selectExistRockerStyle = findViewById(R.id.exterior_use_exist_rocker);
        checkUsingExistRockerStyle = findViewById(R.id.switch_exterior_use_exist_rocker);
        createRockerStyle = findViewById(R.id.exterior_add_rocker_style);
        rockerStyleLayout = findViewById(R.id.rocker_style_layout);
        rockerCornerRadiusSeekbar = findViewById(R.id.rocker_exterior_corner_radius_seekbar);
        rockerCornerRadiusPressSeekbar = findViewById(R.id.rocker_exterior_corner_radius_seekbar_pressed);
        rockerStrokeWidthSeekbar = findViewById(R.id.rocker_exterior_stroke_width_seekbar);
        rockerStrokeWidthPressSeekbar = findViewById(R.id.rocker_exterior_stroke_width_seekbar_pressed);
        rockerCornerRadiusText = findViewById(R.id.rocker_corner_radius_text);
        rockerCornerRadiusPressedText = findViewById(R.id.rocker_corner_radius_press_text);
        rockerStrokeWidthText = findViewById(R.id.rocker_stroke_width_text);
        rockerStrokeWidthPressedText = findViewById(R.id.rocker_stroke_width_press_text);
        selectPointerColor = findViewById(R.id.exterior_pointer_color);
        selectRockerStrokeColor = findViewById(R.id.rocker_exterior_stroke_color);
        selectRockerFillColor = findViewById(R.id.rocker_exterior_fill_color);
        selectPointerColorPressed = findViewById(R.id.exterior_pointer_color_pressed);
        selectRockerStrokeColorPressed = findViewById(R.id.rocker_exterior_stroke_color_pressed);
        selectRockerFillColorPressed = findViewById(R.id.rocker_exterior_fill_color_pressed);
        pointerColorPre = findViewById(R.id.pointer_color_preview);
        rockerStrokeColorPre = findViewById(R.id.rocker_stroke_color_preview);
        rockerFillColorPre = findViewById(R.id.rocker_fill_color_preview);
        pointerColorPressedPre = findViewById(R.id.pointer_pressed_color_preview);
        rockerStrokeColorPressedPre = findViewById(R.id.rocker_stroke_pressed_color_preview);
        rockerFillColorPressedPre = findViewById(R.id.rocker_fill_pressed_color_preview);
        pointerColorText = findViewById(R.id.pointer_color_text);
        rockerStrokeColorText = findViewById(R.id.rocker_stroke_color_text);
        rockerFillColorText = findViewById(R.id.rocker_fill_color_text);
        pointerColorPressedText = findViewById(R.id.pointer_pressed_color_text);
        rockerStrokeColorPressedText = findViewById(R.id.rocker_stroke_pressed_color_text);
        rockerFillColorPressedText = findViewById(R.id.rocker_fill_pressed_color_text);
        addRockerSize = findViewById(R.id.add_rocker_size);
        addRockerX = findViewById(R.id.add_rocker_x);
        addRockerY = findViewById(R.id.add_rocker_y);
        addRockerCornerRadius = findViewById(R.id.add_rocker_corner_radius);
        addRockerStrokeWidth = findViewById(R.id.add_rocker_stroke_width);
        addRockerCornerRadiusPress = findViewById(R.id.add_rocker_corner_radius_pressed);
        addRockerStrokeWidthPress = findViewById(R.id.add_rocker_stroke_width_pressed);
        reduceRockerSize = findViewById(R.id.reduce_rocker_size);
        reduceRockerX = findViewById(R.id.reduce_rocker_x);
        reduceRockerY = findViewById(R.id.reduce_rocker_y);
        reduceRockerCornerRadius = findViewById(R.id.reduce_rocker_corner_radius);
        reduceRockerStrokeWidth = findViewById(R.id.reduce_rocker_stroke_width);
        reduceRockerCornerRadiusPress = findViewById(R.id.reduce_rocker_corner_radius_pressed);
        reduceRockerStrokeWidthPress = findViewById(R.id.reduce_rocker_stroke_width_pressed);

        rockerShowTypeSpinner.setAdapter(showTypeAdapter);
        rockerShowTypeSpinner.setSelection(baseRockerViewInfo.showType);
        rockerSizeTypeSpinner.setAdapter(sizeTypeAdapter);
        rockerSizeTypeSpinner.setSelection(baseRockerViewInfo.sizeType);
        rockerPositionTypeSpinner.setAdapter(positionTypeAdapter);
        rockerPositionTypeSpinner.setSelection(baseRockerViewInfo.positionType);
        if (baseRockerViewInfo.sizeType == BaseRockerViewInfo.SIZE_TYPE_ABSOLUTE) {
            sizeObjectLayout.setVisibility(View.GONE);
        }
        sizeObjectSpinner.setAdapter(sizeObjectAdapter);
        sizeObjectSpinner.setSelection(baseRockerViewInfo.size.object);
        followTypeSpinner.setAdapter(followTypeAdapter);
        followTypeSpinner.setSelection(baseRockerViewInfo.followType);
        if (baseRockerViewInfo.sizeType == BaseRockerViewInfo.SIZE_TYPE_PERCENT) {
            rockerSizeSeekbar.setMin(1);
            rockerSizeSeekbar.setMax(1000);
            rockerSizeSeekbar.setProgress((int) (1000 * baseRockerViewInfo.size.percentSize));
            rockerSizeText.setText(((int) (100 * baseRockerViewInfo.size.percentSize)) / 10f + " %");
        }
        else {
            rockerSizeSeekbar.setMin(1);
            rockerSizeSeekbar.setMax(300);
            rockerSizeSeekbar.setProgress(baseRockerViewInfo.size.absoluteSize);
            rockerSizeText.setText(baseRockerViewInfo.size.absoluteSize + " dp");
        }
        if (baseRockerViewInfo.positionType == BaseRockerViewInfo.POSITION_TYPE_PERCENT) {
            rockerXSeekbar.setMax(1000);
            rockerYSeekbar.setMax(1000);
            rockerXSeekbar.setProgress((int) (1000 * baseRockerViewInfo.xPosition.percentPosition));
            rockerYSeekbar.setProgress((int) (1000 * baseRockerViewInfo.yPosition.percentPosition));
            rockerXText.setText(((int) (1000 * baseRockerViewInfo.xPosition.percentPosition)) / 10f + " %");
            rockerYText.setText(((int) (1000 * baseRockerViewInfo.yPosition.percentPosition)) / 10f + " %");
        }
        else {
            rockerXSeekbar.setMax(ConvertUtils.px2dip(getContext(),screenWidth));
            rockerYSeekbar.setMax(ConvertUtils.px2dip(getContext(),screenHeight));
            rockerXSeekbar.setProgress(baseRockerViewInfo.xPosition.absolutePosition);
            rockerYSeekbar.setProgress(baseRockerViewInfo.yPosition.absolutePosition);
            rockerXText.setText(baseRockerViewInfo.xPosition.absolutePosition + " dp");
            rockerYText.setText(baseRockerViewInfo.yPosition.absolutePosition + " dp");
        }
        onRockerSizeChange();
        checkShift.setChecked(baseRockerViewInfo.shift);
        refreshRockerStyleList(true);
        ArrayList<String> names = new ArrayList<>();
        for (RockerStyle style : SettingUtils.getRockerStyleList()){
            names.add(style.name);
        }
        if (baseRockerViewInfo.usingExist && names.contains(baseRockerViewInfo.rockerStyle.name)) {
            rockerStyleLayout.setVisibility(View.GONE);
            checkUsingExistRockerStyle.setChecked(true);
            selectExistRockerStyle.setSelection(rockerStyleAdapter.getPosition(baseRockerViewInfo.rockerStyle.name));
            selectedRockerStyle = baseRockerViewInfo.rockerStyle;
        }
        else {
            baseRockerViewInfo.usingExist = false;
            baseRockerViewInfo.rockerStyle.name = "";
            selectedRockerStyle = SettingUtils.getRockerStyleList().get(0);
        }
        refreshRockerStyleEditor();

        rockerShowTypeSpinner.setOnItemSelectedListener(this);
        rockerSizeTypeSpinner.setOnItemSelectedListener(this);
        rockerPositionTypeSpinner.setOnItemSelectedListener(this);
        sizeObjectSpinner.setOnItemSelectedListener(this);
        followTypeSpinner.setOnItemSelectedListener(this);
        selectExistRockerStyle.setOnItemSelectedListener(this);
        rockerSizeSeekbar.setOnSeekBarChangeListener(this);
        rockerXSeekbar.setOnSeekBarChangeListener(this);
        rockerYSeekbar.setOnSeekBarChangeListener(this);
        rockerCornerRadiusSeekbar.setOnSeekBarChangeListener(this);
        rockerCornerRadiusPressSeekbar.setOnSeekBarChangeListener(this);
        rockerStrokeWidthSeekbar.setOnSeekBarChangeListener(this);
        rockerStrokeWidthPressSeekbar.setOnSeekBarChangeListener(this);
        checkShift.setOnCheckedChangeListener(this);
        checkUsingExistRockerStyle.setOnCheckedChangeListener(this);
        createRockerStyle.setOnClickListener(this);
        selectPointerColor.setOnClickListener(this);
        selectPointerColorPressed.setOnClickListener(this);
        selectRockerFillColor.setOnClickListener(this);
        selectRockerFillColorPressed.setOnClickListener(this);
        selectRockerStrokeColor.setOnClickListener(this);
        selectRockerStrokeColorPressed.setOnClickListener(this);
        addRockerSize.setOnClickListener(this);
        addRockerX.setOnClickListener(this);
        addRockerY.setOnClickListener(this);
        addRockerStrokeWidth.setOnClickListener(this);
        addRockerCornerRadius.setOnClickListener(this);
        addRockerStrokeWidthPress.setOnClickListener(this);
        addRockerCornerRadiusPress.setOnClickListener(this);
        reduceRockerSize.setOnClickListener(this);
        reduceRockerX.setOnClickListener(this);
        reduceRockerY.setOnClickListener(this);
        reduceRockerStrokeWidth.setOnClickListener(this);
        reduceRockerCornerRadius.setOnClickListener(this);
        reduceRockerStrokeWidthPress.setOnClickListener(this);
        reduceRockerCornerRadiusPress.setOnClickListener(this);
    }

    public void refreshRockerStyleList(boolean first){
        ArrayList<RockerStyle> styles = SettingUtils.getRockerStyleList();
        ArrayList<String> names = new ArrayList<>();
        for (RockerStyle style : styles){
            names.add(style.name);
        }
        rockerStyleAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner,names);
        selectExistRockerStyle.setAdapter(rockerStyleAdapter);
        if (!first) {
            if (baseRockerViewInfo.usingExist && names.contains(baseRockerViewInfo.rockerStyle.name)) {
                rockerStyleLayout.setVisibility(View.GONE);
                checkUsingExistRockerStyle.setChecked(true);
                selectExistRockerStyle.setSelection(rockerStyleAdapter.getPosition(baseRockerViewInfo.rockerStyle.name));
                selectedRockerStyle = baseRockerViewInfo.rockerStyle;
            }
            else {
                rockerStyleLayout.setVisibility(View.VISIBLE);
                checkUsingExistRockerStyle.setChecked(false);
                baseRockerViewInfo.usingExist = false;
                baseRockerViewInfo.rockerStyle.name = "";
                selectedRockerStyle = SettingUtils.getRockerStyleList().get(0);
            }
            refreshRockerStyleEditor();
        }
    }

    @SuppressLint("SetTextI18n")
    private void onRockerSizeChange(){
        int xMax;
        int yMax;
        if (baseRockerViewInfo.sizeType == BaseRockerViewInfo.SIZE_TYPE_PERCENT) {
            int sizeObject = baseRockerViewInfo.size.object == BaseRockerViewInfo.SIZE_OBJECT_WIDTH ? screenWidth : screenHeight;
            xMax = (int) (ConvertUtils.px2dip(getContext(),screenWidth) - (ConvertUtils.px2dip(getContext(),sizeObject) * baseRockerViewInfo.size.percentSize));
            yMax = (int) (ConvertUtils.px2dip(getContext(),screenHeight) - (ConvertUtils.px2dip(getContext(),sizeObject) * baseRockerViewInfo.size.percentSize));
        }
        else {
            xMax = ConvertUtils.px2dip(getContext(),screenWidth) - baseRockerViewInfo.size.absoluteSize;
            yMax = ConvertUtils.px2dip(getContext(),screenHeight) - baseRockerViewInfo.size.absoluteSize;
        }
        if (baseRockerViewInfo.positionType == BaseRockerViewInfo.POSITION_TYPE_ABSOLUTE){
            if (baseRockerViewInfo.xPosition.absolutePosition > xMax){
                baseRockerViewInfo.xPosition.absolutePosition = xMax;
                rockerXSeekbar.setProgress(xMax);
                rockerXText.setText(xMax + " dp");
            }
            if (baseRockerViewInfo.yPosition.absolutePosition > yMax){
                baseRockerViewInfo.yPosition.absolutePosition = yMax;
                rockerYSeekbar.setProgress(yMax);
                rockerYText.setText(yMax + " dp");
            }
            rockerXSeekbar.setMax(xMax);
            rockerYSeekbar.setMax(yMax);
        }
        else {
            if (baseRockerViewInfo.xPosition.absolutePosition > xMax){
                baseRockerViewInfo.xPosition.absolutePosition = xMax;
            }
            if (baseRockerViewInfo.yPosition.absolutePosition > yMax){
                baseRockerViewInfo.yPosition.absolutePosition = yMax;
            }
        }
    }

    @SuppressLint("SetTextI18n")
    public void refreshRockerStyleEditor(){
        rockerCornerRadiusSeekbar.setProgress(baseRockerViewInfo.rockerStyle.cornerRadius);
        rockerStrokeWidthSeekbar.setProgress((int) (baseRockerViewInfo.rockerStyle.strokeWidth * 10));
        rockerCornerRadiusPressSeekbar.setProgress(baseRockerViewInfo.rockerStyle.cornerRadiusPress);
        rockerStrokeWidthPressSeekbar.setProgress((int) (baseRockerViewInfo.rockerStyle.strokeWidthPress * 10));
        rockerCornerRadiusText.setText(baseRockerViewInfo.rockerStyle.cornerRadius + " dp");
        rockerStrokeWidthText.setText(baseRockerViewInfo.rockerStyle.strokeWidth + " dp");
        rockerCornerRadiusPressedText.setText(baseRockerViewInfo.rockerStyle.cornerRadiusPress + " dp");
        rockerStrokeWidthPressedText.setText(baseRockerViewInfo.rockerStyle.strokeWidthPress + " dp");
        pointerColorPre.setBackgroundColor(Color.parseColor(baseRockerViewInfo.rockerStyle.pointerColor));
        rockerStrokeColorPre.setBackgroundColor(Color.parseColor(baseRockerViewInfo.rockerStyle.strokeColor));
        rockerFillColorPre.setBackgroundColor(Color.parseColor(baseRockerViewInfo.rockerStyle.fillColor));
        pointerColorPressedPre.setBackgroundColor(Color.parseColor(baseRockerViewInfo.rockerStyle.pointerColorPress));
        rockerStrokeColorPressedPre.setBackgroundColor(Color.parseColor(baseRockerViewInfo.rockerStyle.strokeColorPress));
        rockerFillColorPressedPre.setBackgroundColor(Color.parseColor(baseRockerViewInfo.rockerStyle.fillColorPress));
        pointerColorText.setText(baseRockerViewInfo.rockerStyle.pointerColor);
        rockerStrokeColorText.setText(baseRockerViewInfo.rockerStyle.strokeColor);
        rockerFillColorText.setText(baseRockerViewInfo.rockerStyle.fillColor);
        pointerColorPressedText.setText(baseRockerViewInfo.rockerStyle.pointerColorPress);
        rockerStrokeColorPressedText.setText(baseRockerViewInfo.rockerStyle.strokeColorPress);
        rockerFillColorPressedText.setText(baseRockerViewInfo.rockerStyle.fillColorPress);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if (view == showButton){
            viewType = 0;
            showButton.setBackgroundColor(getContext().getColor(R.color.colorPureWhite));
            showRocker.setBackgroundColor(getContext().getColor(R.color.colorLightGray));
            editButton.setVisibility(View.VISIBLE);
            editRocker.setVisibility(View.GONE);
        }
        if (view == showRocker){
            viewType = 1;
            showButton.setBackgroundColor(getContext().getColor(R.color.colorLightGray));
            showRocker.setBackgroundColor(getContext().getColor(R.color.colorPureWhite));
            editButton.setVisibility(View.GONE);
            editRocker.setVisibility(View.VISIBLE);
        }
        if (view == positive){
            if (viewType == 0){
                onViewCreateListener.onButtonCreate(baseButtonInfo);
            }
            if (viewType == 1){
                onViewCreateListener.onRockerCreate(baseRockerViewInfo);
            }
            dismiss();
        }
        if (view == negative){
            dismiss();
        }

        if (view == childVisibility) {
            ChildVisibilityDialog dialog = new ChildVisibilityDialog(getContext(),pattern, baseButtonInfo.visibilityControl, list -> baseButtonInfo.visibilityControl = list);
            dialog.show();
        }
        if (view == outputKeycode) {
            SelectKeycodeDialog dialog = new SelectKeycodeDialog(getContext(), baseButtonInfo.outputKeycode, list -> baseButtonInfo.outputKeycode = list);
            dialog.show();
        }
        if (view == createButtonStyle) {
            ButtonStyleManagerDialog dialog = new ButtonStyleManagerDialog(getContext(), () -> {
                refreshButtonStyleList(false);
            });
            dialog.show();
        }
        if (view == selectTextColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseButtonInfo.buttonStyle.textColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    textColorPre.setBackgroundColor(destColor);
                    textColorText.setText("#" + Integer.toHexString(destColor));
                    baseButtonInfo.buttonStyle.textColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectStrokeColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseButtonInfo.buttonStyle.strokeColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    strokeColorPre.setBackgroundColor(destColor);
                    strokeColorText.setText("#" + Integer.toHexString(destColor));
                    baseButtonInfo.buttonStyle.strokeColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectFillColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseButtonInfo.buttonStyle.fillColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    fillColorPre.setBackgroundColor(destColor);
                    fillColorText.setText("#" + Integer.toHexString(destColor));
                    baseButtonInfo.buttonStyle.fillColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectTextColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseButtonInfo.buttonStyle.textColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    textColorPressedPre.setBackgroundColor(destColor);
                    textColorPressedText.setText("#" + Integer.toHexString(destColor));
                    baseButtonInfo.buttonStyle.textColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectStrokeColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseButtonInfo.buttonStyle.strokeColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    strokeColorPressedPre.setBackgroundColor(destColor);
                    strokeColorPressedText.setText("#" + Integer.toHexString(destColor));
                    baseButtonInfo.buttonStyle.strokeColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectFillColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseButtonInfo.buttonStyle.fillColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    fillColorPressedPre.setBackgroundColor(destColor);
                    fillColorPressedText.setText("#" + Integer.toHexString(destColor));
                    baseButtonInfo.buttonStyle.fillColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == addButtonWidth) {
            buttonWidthSeekbar.setProgress(buttonWidthSeekbar.getProgress() + 1);
            int i = buttonWidthSeekbar.getProgress();
            if (baseButtonInfo.sizeType == BaseButtonInfo.SIZE_TYPE_PERCENT) {
                baseButtonInfo.width.percentSize = ((float) i / 1000f);
                buttonWidthText.setText(((int) (1000 * baseButtonInfo.width.percentSize)) / 10f + " %");
            }
            else {
                baseButtonInfo.width.absoluteSize = i;
                buttonWidthText.setText(i + " dp");
            }
            onButtonSizeChange();
        }
        if (view == addButtonHeight) {
            buttonHeightSeekbar.setProgress(buttonHeightSeekbar.getProgress() + 1);
            int i = buttonHeightSeekbar.getProgress();
            if (baseButtonInfo.sizeType == BaseButtonInfo.SIZE_TYPE_PERCENT) {
                baseButtonInfo.height.percentSize = ((float) i / 1000f);
                buttonHeightText.setText(((int) (1000 * baseButtonInfo.height.percentSize)) / 10f + " %");
            }
            else {
                baseButtonInfo.height.absoluteSize = i;
                buttonHeightText.setText(i + " dp");
            }
            onButtonSizeChange();
        }
        if (view == addButtonX) {
            buttonXSeekbar.setProgress(buttonXSeekbar.getProgress() + 1);
            int i = buttonXSeekbar.getProgress();
            if (baseButtonInfo.positionType == BaseButtonInfo.POSITION_TYPE_PERCENT) {
                baseButtonInfo.xPosition.percentPosition = ((float) i / 1000f);
                buttonXText.setText(((int) (1000 * baseButtonInfo.xPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseButtonInfo.xPosition.absolutePosition = i;
                buttonXText.setText(i + " dp");
            }
        }
        if (view == addButtonY) {
            buttonYSeekbar.setProgress(buttonYSeekbar.getProgress() + 1);
            int i = buttonYSeekbar.getProgress();
            if (baseButtonInfo.positionType == BaseButtonInfo.POSITION_TYPE_PERCENT) {
                baseButtonInfo.yPosition.percentPosition = ((float) i / 1000f);
                buttonYText.setText(((int) (1000 * baseButtonInfo.yPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseButtonInfo.yPosition.absolutePosition = i;
                buttonYText.setText(i + " dp");
            }
        }
        if (view == addButtonTextSize) {
            int i = textSizeSeekbar.getProgress() + 1;
            textSizeSeekbar.setProgress(i);
        }
        if (view == addButtonStrokeWidth) {
            int i = strokeWidthSeekbar.getProgress() + 1;
            strokeWidthSeekbar.setProgress(i);
        }
        if (view == addButtonCornerRadius) {
            int i = cornerRadiusSeekbar.getProgress() + 1;
            cornerRadiusSeekbar.setProgress(i);
        }
        if (view == addButtonTextSizePress) {
            int i = textSizePressedSeekbar.getProgress() + 1;
            textSizePressedSeekbar.setProgress(i);
        }
        if (view == addButtonStrokeWidthPress) {
            int i = strokeWidthPressedSeekbar.getProgress() + 1;
            strokeWidthPressedSeekbar.setProgress(i);
        }
        if (view == addButtonCornerRadiusPress) {
            int i = cornerRadiusPressedSeekbar.getProgress() + 1;
            cornerRadiusPressedSeekbar.setProgress(i);
        }
        if (view == reduceButtonWidth) {
            buttonWidthSeekbar.setProgress(buttonWidthSeekbar.getProgress() - 1);
            int i = buttonWidthSeekbar.getProgress();
            if (baseButtonInfo.sizeType == BaseButtonInfo.SIZE_TYPE_PERCENT) {
                baseButtonInfo.width.percentSize = ((float) i / 1000f);
                buttonWidthText.setText(((int) (1000 * baseButtonInfo.width.percentSize)) / 10f + " %");
            }
            else {
                baseButtonInfo.width.absoluteSize = i;
                buttonWidthText.setText(i + " dp");
            }
            onButtonSizeChange();
        }
        if (view == reduceButtonHeight) {
            buttonHeightSeekbar.setProgress(buttonHeightSeekbar.getProgress() - 1);
            int i = buttonHeightSeekbar.getProgress();
            if (baseButtonInfo.sizeType == BaseButtonInfo.SIZE_TYPE_PERCENT) {
                baseButtonInfo.height.percentSize = ((float) i / 1000f);
                buttonHeightText.setText(((int) (1000 * baseButtonInfo.height.percentSize)) / 10f + " %");
            }
            else {
                baseButtonInfo.height.absoluteSize = i;
                buttonHeightText.setText(i + " dp");
            }
            onButtonSizeChange();
        }
        if (view == reduceButtonX) {
            buttonXSeekbar.setProgress(buttonXSeekbar.getProgress() - 1);
            int i = buttonXSeekbar.getProgress();
            if (baseButtonInfo.positionType == BaseButtonInfo.POSITION_TYPE_PERCENT) {
                baseButtonInfo.xPosition.percentPosition = ((float) i / 1000f);
                buttonXText.setText(((int) (1000 * baseButtonInfo.xPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseButtonInfo.xPosition.absolutePosition = i;
                buttonXText.setText(i + " dp");
            }
        }
        if (view == reduceButtonY) {
            buttonYSeekbar.setProgress(buttonYSeekbar.getProgress() - 1);
            int i = buttonYSeekbar.getProgress();
            if (baseButtonInfo.positionType == BaseButtonInfo.POSITION_TYPE_PERCENT) {
                baseButtonInfo.yPosition.percentPosition = ((float) i / 1000f);
                buttonYText.setText(((int) (1000 * baseButtonInfo.yPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseButtonInfo.yPosition.absolutePosition = i;
                buttonYText.setText(i + " dp");
            }
        }
        if (view == reduceButtonTextSize) {
            int i = textSizeSeekbar.getProgress() - 1;
            textSizeSeekbar.setProgress(i);
        }
        if (view == reduceButtonStrokeWidth) {
            int i = strokeWidthSeekbar.getProgress() - 1;
            strokeWidthSeekbar.setProgress(i);
        }
        if (view == reduceButtonCornerRadius) {
            int i = cornerRadiusSeekbar.getProgress() - 1;
            cornerRadiusSeekbar.setProgress(i);
        }
        if (view == reduceButtonTextSizePress) {
            int i = textSizePressedSeekbar.getProgress() - 1;
            textSizePressedSeekbar.setProgress(i);
        }
        if (view == reduceButtonStrokeWidthPress) {
            int i = strokeWidthPressedSeekbar.getProgress() - 1;
            strokeWidthPressedSeekbar.setProgress(i);
        }
        if (view == reduceButtonCornerRadiusPress) {
            int i = cornerRadiusPressedSeekbar.getProgress() - 1;
            cornerRadiusPressedSeekbar.setProgress(i);
        }

        if (view == createRockerStyle) {
            RockerStyleManagerDialog dialog = new RockerStyleManagerDialog(getContext(), () -> {
                refreshRockerStyleList(false);
            });
            dialog.show();
        }
        if (view == selectPointerColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseRockerViewInfo.rockerStyle.pointerColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    pointerColorPre.setBackgroundColor(destColor);
                    pointerColorText.setText("#" + Integer.toHexString(destColor));
                    baseRockerViewInfo.rockerStyle.pointerColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectRockerStrokeColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseRockerViewInfo.rockerStyle.strokeColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    rockerStrokeColorPre.setBackgroundColor(destColor);
                    rockerStrokeColorText.setText("#" + Integer.toHexString(destColor));
                    baseRockerViewInfo.rockerStyle.strokeColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectRockerFillColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseRockerViewInfo.rockerStyle.fillColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    rockerFillColorPre.setBackgroundColor(destColor);
                    rockerFillColorText.setText("#" + Integer.toHexString(destColor));
                    baseRockerViewInfo.rockerStyle.fillColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectPointerColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseRockerViewInfo.rockerStyle.pointerColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    pointerColorPressedPre.setBackgroundColor(destColor);
                    pointerColorPressedText.setText("#" + Integer.toHexString(destColor));
                    baseRockerViewInfo.rockerStyle.pointerColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectRockerStrokeColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseRockerViewInfo.rockerStyle.strokeColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    rockerStrokeColorPressedPre.setBackgroundColor(destColor);
                    rockerStrokeColorPressedText.setText("#" + Integer.toHexString(destColor));
                    baseRockerViewInfo.rockerStyle.strokeColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectRockerFillColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(baseRockerViewInfo.rockerStyle.fillColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    rockerFillColorPressedPre.setBackgroundColor(destColor);
                    rockerFillColorPressedText.setText("#" + Integer.toHexString(destColor));
                    baseRockerViewInfo.rockerStyle.fillColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == addRockerSize) {
            rockerSizeSeekbar.setProgress(rockerSizeSeekbar.getProgress() + 1);
            int i = rockerSizeSeekbar.getProgress();
            if (baseRockerViewInfo.sizeType == BaseRockerViewInfo.SIZE_TYPE_PERCENT) {
                baseRockerViewInfo.size.percentSize = ((float) i / 1000f);
                rockerSizeText.setText(((int) (1000 * baseRockerViewInfo.size.percentSize)) / 10f + " %");
            }
            else {
                baseRockerViewInfo.size.absoluteSize = i;
                rockerSizeText.setText(i + " dp");
            }
            onRockerSizeChange();
        }
        if (view == addRockerX) {
            rockerXSeekbar.setProgress(rockerXSeekbar.getProgress() + 1);
            int i = rockerXSeekbar.getProgress();
            if (baseRockerViewInfo.positionType == BaseRockerViewInfo.POSITION_TYPE_PERCENT) {
                baseRockerViewInfo.xPosition.percentPosition = ((float) i / 1000f);
                rockerXText.setText(((int) (1000 * baseRockerViewInfo.xPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseRockerViewInfo.xPosition.absolutePosition = i;
                rockerXText.setText(i + " dp");
            }
        }
        if (view == addRockerY) {
            rockerYSeekbar.setProgress(rockerYSeekbar.getProgress() + 1);
            int i = rockerYSeekbar.getProgress();
            if (baseRockerViewInfo.positionType == BaseRockerViewInfo.POSITION_TYPE_PERCENT) {
                baseRockerViewInfo.yPosition.percentPosition = ((float) i / 1000f);
                rockerYText.setText(((int) (1000 * baseRockerViewInfo.yPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseRockerViewInfo.yPosition.absolutePosition = i;
                rockerYText.setText(i + " dp");
            }
        }
        if (view == addRockerStrokeWidth) {
            int i = rockerStrokeWidthSeekbar.getProgress() + 1;
            rockerStrokeWidthSeekbar.setProgress(i);
        }
        if (view == addRockerCornerRadius) {
            int i = rockerCornerRadiusSeekbar.getProgress() + 1;
            rockerCornerRadiusSeekbar.setProgress(i);
        }
        if (view == addRockerStrokeWidthPress) {
            int i = rockerStrokeWidthPressSeekbar.getProgress() + 1;
            rockerStrokeWidthPressSeekbar.setProgress(i);
        }
        if (view == addRockerCornerRadiusPress) {
            int i = rockerCornerRadiusPressSeekbar.getProgress() + 1;
            rockerCornerRadiusPressSeekbar.setProgress(i);
        }
        if (view == reduceRockerSize) {
            rockerSizeSeekbar.setProgress(rockerSizeSeekbar.getProgress() - 1);
            int i = rockerSizeSeekbar.getProgress();
            if (baseRockerViewInfo.sizeType == BaseRockerViewInfo.SIZE_TYPE_PERCENT) {
                baseRockerViewInfo.size.percentSize = ((float) i / 1000f);
                rockerSizeText.setText(((int) (1000 * baseRockerViewInfo.size.percentSize)) / 10f + " %");
            }
            else {
                baseRockerViewInfo.size.absoluteSize = i;
                rockerSizeText.setText(i + " dp");
            }
            onRockerSizeChange();
        }
        if (view == reduceRockerX) {
            rockerXSeekbar.setProgress(rockerXSeekbar.getProgress() - 1);
            int i = rockerXSeekbar.getProgress();
            if (baseRockerViewInfo.positionType == BaseRockerViewInfo.POSITION_TYPE_PERCENT) {
                baseRockerViewInfo.xPosition.percentPosition = ((float) i / 1000f);
                rockerXText.setText(((int) (1000 * baseRockerViewInfo.xPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseRockerViewInfo.xPosition.absolutePosition = i;
                rockerXText.setText(i + " dp");
            }
        }
        if (view == reduceRockerY) {
            rockerYSeekbar.setProgress(rockerYSeekbar.getProgress() - 1);
            int i = rockerYSeekbar.getProgress();
            if (baseRockerViewInfo.positionType == BaseRockerViewInfo.POSITION_TYPE_PERCENT) {
                baseRockerViewInfo.yPosition.percentPosition = ((float) i / 1000f);
                rockerYText.setText(((int) (1000 * baseRockerViewInfo.yPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseRockerViewInfo.yPosition.absolutePosition = i;
                rockerYText.setText(i + " dp");
            }
        }
        if (view == reduceRockerStrokeWidth) {
            int i = rockerStrokeWidthSeekbar.getProgress() - 1;
            rockerStrokeWidthSeekbar.setProgress(i);
        }
        if (view == reduceRockerCornerRadius) {
            int i = rockerCornerRadiusSeekbar.getProgress() - 1;
            rockerCornerRadiusSeekbar.setProgress(i);
        }
        if (view == reduceRockerStrokeWidthPress) {
            int i = rockerStrokeWidthPressSeekbar.getProgress() - 1;
            rockerStrokeWidthPressSeekbar.setProgress(i);
        }
        if (view == reduceRockerCornerRadiusPress) {
            int i = rockerCornerRadiusPressSeekbar.getProgress() - 1;
            rockerCornerRadiusPressSeekbar.setProgress(i);
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == buttonShowTypeSpinner) {
            baseButtonInfo.showType = i;
        }
        if (adapterView == buttonSizeTypeSpinner) {
            baseButtonInfo.sizeType = i;
            if (i == BaseButtonInfo.SIZE_TYPE_PERCENT) {
                widthObjectLayout.setVisibility(View.VISIBLE);
                heightObjectLayout.setVisibility(View.VISIBLE);
                buttonWidthSeekbar.setMax(1000);
                buttonHeightSeekbar.setMax(1000);
                buttonWidthSeekbar.setProgress((int) (baseButtonInfo.width.percentSize * 1000));
                buttonHeightSeekbar.setProgress((int) (baseButtonInfo.height.percentSize * 1000));
                buttonWidthText.setText(((int) (1000 * baseButtonInfo.width.percentSize)) / 10f + " %");
                buttonHeightText.setText(((int) (1000 * baseButtonInfo.height.percentSize)) / 10f + " %");
            }
            else {
                widthObjectLayout.setVisibility(View.GONE);
                heightObjectLayout.setVisibility(View.GONE);
                buttonWidthSeekbar.setMax(200);
                buttonHeightSeekbar.setMax(200);
                buttonWidthSeekbar.setProgress(baseButtonInfo.width.absoluteSize);
                buttonHeightSeekbar.setProgress(baseButtonInfo.height.absoluteSize);
                buttonWidthText.setText(baseButtonInfo.width.absoluteSize + " dp");
                buttonHeightText.setText(baseButtonInfo.height.absoluteSize + " dp");
            }
            onButtonSizeChange();
        }
        if (adapterView == buttonPositionTypeSpinner) {
            baseButtonInfo.positionType = i;
            if (i == BaseButtonInfo.POSITION_TYPE_PERCENT) {
                buttonXSeekbar.setMax(1000);
                buttonYSeekbar.setMax(1000);
                buttonXSeekbar.setProgress((int) (baseButtonInfo.xPosition.percentPosition * 1000));
                buttonYSeekbar.setProgress((int) (baseButtonInfo.yPosition.percentPosition * 1000));
                buttonXText.setText(((int) (1000 * baseButtonInfo.xPosition.percentPosition)) / 10f + " %");
                buttonYText.setText(((int) (1000 * baseButtonInfo.yPosition.percentPosition)) / 10f + " %");
            }
            else {
                buttonXSeekbar.setMax(ConvertUtils.px2dip(getContext(),screenWidth));
                buttonYSeekbar.setMax(ConvertUtils.px2dip(getContext(),screenHeight));
                buttonXSeekbar.setProgress(baseButtonInfo.xPosition.absolutePosition);
                buttonYSeekbar.setProgress(baseButtonInfo.yPosition.absolutePosition);
                buttonXText.setText(baseButtonInfo.xPosition.absolutePosition + " dp");
                buttonYText.setText(baseButtonInfo.yPosition.absolutePosition + " dp");
            }
            onButtonSizeChange();
        }
        if (adapterView == widthObjectSpinner) {
            baseButtonInfo.width.object = i;
            onButtonSizeChange();
        }
        if (adapterView == heightObjectSpinner) {
            baseButtonInfo.height.object = i;
            onButtonSizeChange();
        }
        if (adapterView == functionTypeSpinner) {
            baseButtonInfo.functionType = i;
        }
        if (adapterView == selectExist) {
            for (ButtonStyle style : SettingUtils.getButtonStyleList()){
                if (style.name.equals(selectExist.getItemAtPosition(i))){
                    selectedButtonStyle = style;
                }
            }
            if (baseButtonInfo.usingExist){
                baseButtonInfo.buttonStyle = selectedButtonStyle;
            }
            refreshButtonStyleEditor();
        }

        if (adapterView == rockerShowTypeSpinner) {
            baseRockerViewInfo.showType = i;
        }
        if (adapterView == rockerSizeTypeSpinner) {
            baseRockerViewInfo.sizeType = i;
            if (i == BaseRockerViewInfo.SIZE_TYPE_PERCENT) {
                sizeObjectLayout.setVisibility(View.VISIBLE);
                rockerSizeSeekbar.setMax(1000);
                rockerSizeSeekbar.setProgress((int) (baseRockerViewInfo.size.percentSize * 1000));
                rockerSizeText.setText(((int) (1000 * baseRockerViewInfo.size.percentSize)) / 10f + " %");
            }
            else {
                sizeObjectLayout.setVisibility(View.GONE);
                rockerSizeSeekbar.setMax(300);
                rockerSizeSeekbar.setProgress(baseRockerViewInfo.size.absoluteSize);
                rockerSizeText.setText(baseRockerViewInfo.size.absoluteSize + " dp");
            }
            onRockerSizeChange();
        }
        if (adapterView == rockerPositionTypeSpinner) {
            baseRockerViewInfo.positionType = i;
            if (i == BaseRockerViewInfo.POSITION_TYPE_PERCENT) {
                rockerXSeekbar.setMax(1000);
                rockerYSeekbar.setMax(1000);
                rockerXSeekbar.setProgress((int) (baseRockerViewInfo.xPosition.percentPosition * 1000));
                rockerYSeekbar.setProgress((int) (baseRockerViewInfo.yPosition.percentPosition * 1000));
                rockerXText.setText(((int) (1000 * baseRockerViewInfo.xPosition.percentPosition)) / 10f + " %");
                rockerYText.setText(((int) (1000 * baseRockerViewInfo.yPosition.percentPosition)) / 10f + " %");
            }
            else {
                rockerXSeekbar.setMax(ConvertUtils.px2dip(getContext(),screenWidth));
                rockerYSeekbar.setMax(ConvertUtils.px2dip(getContext(),screenHeight));
                rockerXSeekbar.setProgress(baseRockerViewInfo.xPosition.absolutePosition);
                rockerYSeekbar.setProgress(baseRockerViewInfo.yPosition.absolutePosition);
                rockerXText.setText(baseRockerViewInfo.xPosition.absolutePosition + " dp");
                rockerYText.setText(baseRockerViewInfo.yPosition.absolutePosition + " dp");
            }
            onRockerSizeChange();
        }
        if (adapterView == sizeObjectSpinner) {
            baseRockerViewInfo.size.object = i;
            onRockerSizeChange();
        }
        if (adapterView == followTypeSpinner) {
            baseRockerViewInfo.followType = i;
        }
        if (adapterView == selectExistRockerStyle) {
            for (RockerStyle style : SettingUtils.getRockerStyleList()){
                if (style.name.equals(selectExistRockerStyle.getItemAtPosition(i))){
                    selectedRockerStyle = style;
                }
            }
            if (baseRockerViewInfo.usingExist){
                baseRockerViewInfo.rockerStyle = selectedRockerStyle;
            }
            refreshRockerStyleEditor();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == buttonWidthSeekbar && b) {
            if (baseButtonInfo.sizeType == BaseButtonInfo.SIZE_TYPE_PERCENT) {
                baseButtonInfo.width.percentSize = ((float) i / 1000f);
                buttonWidthText.setText(((int) (1000 * baseButtonInfo.width.percentSize)) / 10f + " %");
            }
            else {
                baseButtonInfo.width.absoluteSize = i;
                buttonWidthText.setText(i + " dp");
            }
            onButtonSizeChange();
        }
        if (seekBar == buttonHeightSeekbar && b) {
            if (baseButtonInfo.sizeType == BaseButtonInfo.SIZE_TYPE_PERCENT) {
                baseButtonInfo.height.percentSize = ((float) i / 1000f);
                buttonHeightText.setText(((int) (1000 * baseButtonInfo.height.percentSize)) / 10f + " %");
            }
            else {
                baseButtonInfo.height.absoluteSize = i;
                buttonHeightText.setText(i + " dp");
            }
            onButtonSizeChange();
        }
        if (seekBar == buttonXSeekbar && b) {
            if (baseButtonInfo.positionType == BaseButtonInfo.POSITION_TYPE_PERCENT) {
                baseButtonInfo.xPosition.percentPosition = ((float) i / 1000f);
                buttonXText.setText(((int) (1000 * baseButtonInfo.xPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseButtonInfo.xPosition.absolutePosition = i;
                buttonXText.setText(i + " dp");
            }
        }
        if (seekBar == buttonYSeekbar && b) {
            if (baseButtonInfo.positionType == BaseButtonInfo.POSITION_TYPE_PERCENT) {
                baseButtonInfo.yPosition.percentPosition = ((float) i / 1000f);
                buttonYText.setText(((int) (1000 * baseButtonInfo.yPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseButtonInfo.yPosition.absolutePosition = i;
                buttonYText.setText(i + " dp");
            }
        }
        if (seekBar == textSizeSeekbar) {
            textSizeText.setText(i + " sp");
            baseButtonInfo.buttonStyle.textSize = i;
        }
        if (seekBar == cornerRadiusSeekbar) {
            cornerRadiusText.setText(i + " dp");
            baseButtonInfo.buttonStyle.cornerRadius = i;
        }
        if (seekBar == strokeWidthSeekbar) {
            strokeWidthText.setText((float) i / 10f + " dp");
            baseButtonInfo.buttonStyle.strokeWidth = (float) i / 10f;
        }
        if (seekBar == textSizePressedSeekbar) {
            textSizePressedText.setText(i + " sp");
            baseButtonInfo.buttonStyle.textSizePress = i;
        }
        if (seekBar == cornerRadiusPressedSeekbar) {
            cornerRadiusPressedText.setText(i + " dp");
            baseButtonInfo.buttonStyle.cornerRadiusPress = i;
        }
        if (seekBar == strokeWidthPressedSeekbar) {
            strokeWidthPressedText.setText((float) i / 10f + " dp");
            baseButtonInfo.buttonStyle.strokeWidthPress = (float) i / 10f;
        }

        if (seekBar == rockerSizeSeekbar && b) {
            if (baseRockerViewInfo.sizeType == BaseRockerViewInfo.SIZE_TYPE_PERCENT) {
                baseRockerViewInfo.size.percentSize = ((float) i / 1000f);
                rockerSizeText.setText(((int) (1000 * baseRockerViewInfo.size.percentSize)) / 10f + " %");
            }
            else {
                baseRockerViewInfo.size.absoluteSize = i;
                rockerSizeText.setText(i + " dp");
            }
            onRockerSizeChange();
        }
        if (seekBar == rockerXSeekbar && b) {
            if (baseRockerViewInfo.positionType == BaseRockerViewInfo.POSITION_TYPE_PERCENT) {
                baseRockerViewInfo.xPosition.percentPosition = ((float) i / 1000f);
                rockerXText.setText(((int) (1000 * baseRockerViewInfo.xPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseRockerViewInfo.xPosition.absolutePosition = i;
                rockerXText.setText(i + " dp");
            }
        }
        if (seekBar == rockerYSeekbar && b) {
            if (baseRockerViewInfo.positionType == BaseRockerViewInfo.POSITION_TYPE_PERCENT) {
                baseRockerViewInfo.yPosition.percentPosition = ((float) i / 1000f);
                rockerYText.setText(((int) (1000 * baseRockerViewInfo.yPosition.percentPosition)) / 10f + " %");
            }
            else {
                baseRockerViewInfo.yPosition.absolutePosition = i;
                rockerYText.setText(i + " dp");
            }
        }
        if (seekBar == rockerCornerRadiusSeekbar) {
            rockerCornerRadiusText.setText(i + " dp");
            baseRockerViewInfo.rockerStyle.cornerRadius = i;
        }
        if (seekBar == rockerStrokeWidthSeekbar) {
            rockerStrokeWidthText.setText((float) i / 10f + " dp");
            baseRockerViewInfo.rockerStyle.strokeWidth = (float) i / 10f;
        }
        if (seekBar == rockerCornerRadiusPressSeekbar) {
            rockerCornerRadiusPressedText.setText(i + " dp");
            baseRockerViewInfo.rockerStyle.cornerRadiusPress = i;
        }
        if (seekBar == rockerStrokeWidthPressSeekbar) {
            rockerStrokeWidthPressedText.setText((float) i / 10f + " dp");
            baseRockerViewInfo.rockerStyle.strokeWidthPress = (float) i / 10f;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        if (compoundButton == checkViewMove) {
            baseButtonInfo.viewMove = b;
        }
        if (compoundButton == checkAutoKeep) {
            baseButtonInfo.autoKeep = b;
        }
        if (compoundButton == checkAutoClick) {
            baseButtonInfo.autoClick = b;
        }
        if (compoundButton == checkOpenMenu) {
            baseButtonInfo.openMenu = b;
        }
        if (compoundButton == checkMovable) {
            baseButtonInfo.movable = b;
        }
        if (compoundButton == checkTouchMode) {
            baseButtonInfo.switchTouchMode = b;
        }
        if (compoundButton == checkSensor) {
            baseButtonInfo.switchSensor = b;
        }
        if (compoundButton == checkLeftPad) {
            baseButtonInfo.switchLeftPad = b;
        }
        if (compoundButton == checkOpenInput) {
            baseButtonInfo.showInputDialog = b;
        }
        if (compoundButton == checkUsingExist) {
            baseButtonInfo.usingExist = b;
            if (b) {
                buttonStyleLayout.setVisibility(View.GONE);
                baseButtonInfo.buttonStyle = selectedButtonStyle;
            }
            else {
                buttonStyleLayout.setVisibility(View.VISIBLE);
                baseButtonInfo.buttonStyle.name = "";
            }
            refreshButtonStyleEditor();
        }

        if (compoundButton == checkShift) {
            baseRockerViewInfo.shift = b;
        }
        if (compoundButton == checkUsingExistRockerStyle) {
            baseRockerViewInfo.usingExist = b;
            if (b) {
                rockerStyleLayout.setVisibility(View.GONE);
                baseRockerViewInfo.rockerStyle = selectedRockerStyle;
            }
            else {
                rockerStyleLayout.setVisibility(View.VISIBLE);
                baseRockerViewInfo.rockerStyle.name = "";
            }
            refreshRockerStyleEditor();
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
        baseButtonInfo.text = editButtonText.getText().toString();
        baseButtonInfo.outputText = editOutputText.getText().toString();
    }

    public interface OnViewCreateListener{
        void onButtonCreate(BaseButtonInfo baseButtonInfo);
        void onRockerCreate(BaseRockerViewInfo baseRockerViewInfo);
    }
}
