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
import com.tungsten.hmclpe.control.ViewManager;
import com.tungsten.hmclpe.control.bean.BaseButtonInfo;
import com.tungsten.hmclpe.control.bean.button.ButtonStyle;
import com.tungsten.hmclpe.control.view.BaseButton;
import com.tungsten.hmclpe.launcher.dialogs.tools.ColorSelectorDialog;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import java.util.ArrayList;
import java.util.UUID;

public class EditButtonDialog extends Dialog implements View.OnClickListener, AdapterView.OnItemSelectedListener, SeekBar.OnSeekBarChangeListener, CompoundButton.OnCheckedChangeListener, TextWatcher {

    private ViewManager viewManager;
    private String pattern;
    private String child;
    private int screenWidth;
    private int screenHeight;
    private BaseButton baseButton;

    private BaseButtonInfo baseButtonInfo;

    private Button positive;
    private Button negative;
    private Button copy;

    private ArrayAdapter<String> showTypeAdapter;
    private ArrayAdapter<String> sizeTypeAdapter;
    private ArrayAdapter<String> positionTypeAdapter;
    private ArrayAdapter<String> sizeObjectAdapter;
    private ArrayAdapter<String> functionTypeAdapter;
    private ArrayAdapter<String> buttonStyleAdapter;

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

    private ButtonStyle selectedButtonStyle;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public EditButtonDialog(@NonNull Context context,ViewManager viewManager, String pattern, String child, int screenWidth, int screenHeight, BaseButton baseButton,boolean fullscreen) {
        super(context);
        this.viewManager = viewManager;
        this.pattern = pattern;
        this.child = child;
        this.screenWidth = screenWidth;
        this.screenHeight = screenHeight;
        this.baseButton = baseButton;
        setContentView(R.layout.dialog_edit_button);
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
        try {
            baseButtonInfo = (BaseButtonInfo) baseButton.info.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }

        positive = findViewById(R.id.apply_button_change);
        negative = findViewById(R.id.exit);
        copy = findViewById(R.id.copy_button);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
        copy.setOnClickListener(this);

        ArrayList<String> showType = new ArrayList<>();
        ArrayList<String> sizeType = new ArrayList<>();
        ArrayList<String> positionType = new ArrayList<>();
        ArrayList<String> sizeObject = new ArrayList<>();
        ArrayList<String> functionType = new ArrayList<>();
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
        showTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, showType);
        sizeTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, sizeType);
        positionTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, positionType);
        sizeObjectAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, sizeObject);
        functionTypeAdapter = new ArrayAdapter<>(getContext(),R.layout.item_spinner, functionType);

        initButtonLayout();
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
        onSizeChange();
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
    private void onSizeChange(){
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if (view == positive){
            baseButton.info.refresh(baseButtonInfo);
            baseButton.updateSizeAndPosition(baseButtonInfo);
            baseButton.refreshStyle(baseButtonInfo);
            baseButton.saveButtonInfo();
            dismiss();
        }
        if (view == negative){
            dismiss();
        }
        if (view == copy) {
            baseButtonInfo.uuid = UUID.randomUUID().toString();
            baseButtonInfo.xPosition.percentPosition = 0f;
            baseButtonInfo.xPosition.absolutePosition = 0;
            baseButtonInfo.yPosition.percentPosition = 0f;
            baseButtonInfo.yPosition.absolutePosition = 0;
            viewManager.addButton(baseButtonInfo,View.VISIBLE);
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
            onSizeChange();
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
            onSizeChange();
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
            onSizeChange();
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
            onSizeChange();
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
            onSizeChange();
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
            onSizeChange();
        }
        if (adapterView == widthObjectSpinner) {
            baseButtonInfo.width.object = i;
            onSizeChange();
        }
        if (adapterView == heightObjectSpinner) {
            baseButtonInfo.height.object = i;
            onSizeChange();
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
            onSizeChange();
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
            onSizeChange();
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

}
