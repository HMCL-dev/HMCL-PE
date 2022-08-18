package com.tungsten.hmclpe.launcher.dialogs.control;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.bean.rocker.RockerStyle;
import com.tungsten.hmclpe.launcher.dialogs.tools.ColorSelectorDialog;

import java.util.ArrayList;

public class CreateRockerStyleDialog extends Dialog implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, TextWatcher {

    private ArrayList<RockerStyle> list;
    private OnRockerStyleCreateListener onRockerStyleCreateListener;

    private RockerStyle rockerStyle;

    private Button positive;
    private Button negative;

    private EditText editName;
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
    
    public CreateRockerStyleDialog(@NonNull Context context,ArrayList<RockerStyle> list,OnRockerStyleCreateListener onRockerStyleCreateListener) {
        super(context);
        this.list = list;
        this.onRockerStyleCreateListener = onRockerStyleCreateListener;
        setContentView(R.layout.dialog_create_rocker_style);
        setCancelable(false);
        init();
    }
    
    @SuppressLint("SetTextI18n")
    private void init(){
        rockerStyle = new RockerStyle();

        positive = findViewById(R.id.create_rocker_style);
        negative = findViewById(R.id.exit);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        editName = findViewById(R.id.edit_rocker_style_name);
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

        rockerCornerRadiusSeekbar.setProgress(rockerStyle.cornerRadius);
        rockerStrokeWidthSeekbar.setProgress((int) (rockerStyle.strokeWidth * 10));
        rockerCornerRadiusPressSeekbar.setProgress(rockerStyle.cornerRadiusPress);
        rockerStrokeWidthPressSeekbar.setProgress((int) (rockerStyle.strokeWidthPress * 10));
        rockerCornerRadiusText.setText(rockerStyle.cornerRadius + " dp");
        rockerStrokeWidthText.setText(rockerStyle.strokeWidth + " dp");
        rockerCornerRadiusPressedText.setText(rockerStyle.cornerRadiusPress + " dp");
        rockerStrokeWidthPressedText.setText(rockerStyle.strokeWidthPress + " dp");
        pointerColorPre.setBackgroundColor(Color.parseColor(rockerStyle.pointerColor));
        rockerStrokeColorPre.setBackgroundColor(Color.parseColor(rockerStyle.strokeColor));
        rockerFillColorPre.setBackgroundColor(Color.parseColor(rockerStyle.fillColor));
        pointerColorPressedPre.setBackgroundColor(Color.parseColor(rockerStyle.pointerColorPress));
        rockerStrokeColorPressedPre.setBackgroundColor(Color.parseColor(rockerStyle.strokeColorPress));
        rockerFillColorPressedPre.setBackgroundColor(Color.parseColor(rockerStyle.fillColorPress));
        pointerColorText.setText(rockerStyle.pointerColor);
        rockerStrokeColorText.setText(rockerStyle.strokeColor);
        rockerFillColorText.setText(rockerStyle.fillColor);
        pointerColorPressedText.setText(rockerStyle.pointerColorPress);
        rockerStrokeColorPressedText.setText(rockerStyle.strokeColorPress);
        rockerFillColorPressedText.setText(rockerStyle.fillColorPress);

        editName.addTextChangedListener(this);
        rockerCornerRadiusSeekbar.setOnSeekBarChangeListener(this);
        rockerCornerRadiusPressSeekbar.setOnSeekBarChangeListener(this);
        rockerStrokeWidthSeekbar.setOnSeekBarChangeListener(this);
        rockerStrokeWidthPressSeekbar.setOnSeekBarChangeListener(this);
        selectPointerColor.setOnClickListener(this);
        selectPointerColorPressed.setOnClickListener(this);
        selectRockerFillColor.setOnClickListener(this);
        selectRockerFillColorPressed.setOnClickListener(this);
        selectRockerStrokeColor.setOnClickListener(this);
        selectRockerStrokeColorPressed.setOnClickListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        rockerStyle.name = editName.getText().toString();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if (view == positive) {
            ArrayList<String> names = new ArrayList<>();
            for (RockerStyle style : list) {
                names.add(style.name);
            }
            if (editName.getText().toString().equals("")) {
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_create_rocker_style_name_empty),Toast.LENGTH_SHORT).show();
            }
            else if (names.contains(editName.getText().toString())) {
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_create_rocker_style_name_exist),Toast.LENGTH_SHORT).show();
            }
            else {
                onRockerStyleCreateListener.onRockerStyleCreate(rockerStyle);
                dismiss();
            }
        }
        if (view == negative) {
            dismiss();
        }

        if (view == selectPointerColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(rockerStyle.pointerColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    pointerColorPre.setBackgroundColor(destColor);
                    pointerColorText.setText("#" + Integer.toHexString(destColor));
                    rockerStyle.pointerColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectRockerStrokeColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(rockerStyle.strokeColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    rockerStrokeColorPre.setBackgroundColor(destColor);
                    rockerStrokeColorText.setText("#" + Integer.toHexString(destColor));
                    rockerStyle.strokeColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectRockerFillColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(rockerStyle.fillColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    rockerFillColorPre.setBackgroundColor(destColor);
                    rockerFillColorText.setText("#" + Integer.toHexString(destColor));
                    rockerStyle.fillColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectPointerColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(rockerStyle.pointerColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    pointerColorPressedPre.setBackgroundColor(destColor);
                    pointerColorPressedText.setText("#" + Integer.toHexString(destColor));
                    rockerStyle.pointerColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectRockerStrokeColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(rockerStyle.strokeColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    rockerStrokeColorPressedPre.setBackgroundColor(destColor);
                    rockerStrokeColorPressedText.setText("#" + Integer.toHexString(destColor));
                    rockerStyle.strokeColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectRockerFillColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(rockerStyle.fillColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    rockerFillColorPressedPre.setBackgroundColor(destColor);
                    rockerFillColorPressedText.setText("#" + Integer.toHexString(destColor));
                    rockerStyle.fillColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == rockerCornerRadiusSeekbar) {
            rockerCornerRadiusText.setText(i + " dp");
            rockerStyle.cornerRadius = i;
        }
        if (seekBar == rockerStrokeWidthSeekbar) {
            rockerStrokeWidthText.setText((float) i / 10f + " dp");
            rockerStyle.strokeWidth = (float) i / 10f;
        }
        if (seekBar == rockerCornerRadiusPressSeekbar) {
            rockerCornerRadiusPressedText.setText(i + " dp");
            rockerStyle.cornerRadiusPress = i;
        }
        if (seekBar == rockerStrokeWidthPressSeekbar) {
            rockerStrokeWidthPressedText.setText((float) i / 10f + " dp");
            rockerStyle.strokeWidthPress = (float) i / 10f;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public interface OnRockerStyleCreateListener{
        void onRockerStyleCreate(RockerStyle rockerStyle);
    }
    
}
