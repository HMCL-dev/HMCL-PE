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
import com.tungsten.hmclpe.control.bean.button.ButtonStyle;
import com.tungsten.hmclpe.launcher.dialogs.tools.ColorSelectorDialog;

import java.util.ArrayList;

public class CreateButtonStyleDialog extends Dialog implements SeekBar.OnSeekBarChangeListener, View.OnClickListener, TextWatcher {

    private ArrayList<ButtonStyle> list;
    private OnButtonStyleCreateListener onButtonStyleCreateListener;

    private ButtonStyle buttonStyle;

    private Button positive;
    private Button negative;

    private EditText editName;
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

    public CreateButtonStyleDialog(@NonNull Context context, ArrayList<ButtonStyle> list, OnButtonStyleCreateListener onButtonStyleCreateListener) {
        super(context);
        this.list = list;
        this.onButtonStyleCreateListener = onButtonStyleCreateListener;
        setContentView(R.layout.dialog_create_button_style);
        setCancelable(false);
        init();
    }

    @SuppressLint("SetTextI18n")
    private void init () {
        buttonStyle = new ButtonStyle();

        positive = findViewById(R.id.create_button_style);
        negative = findViewById(R.id.exit);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        editName = findViewById(R.id.edit_button_style_name);
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

        textSizeSeekbar.setProgress(buttonStyle.textSize);
        cornerRadiusSeekbar.setProgress(buttonStyle.cornerRadius);
        strokeWidthSeekbar.setProgress((int) (buttonStyle.strokeWidth * 10));
        textSizePressedSeekbar.setProgress(buttonStyle.textSizePress);
        cornerRadiusPressedSeekbar.setProgress(buttonStyle.cornerRadiusPress);
        strokeWidthPressedSeekbar.setProgress((int) (buttonStyle.strokeWidthPress * 10));
        textSizeText.setText(buttonStyle.textSize + " sp");
        cornerRadiusText.setText(buttonStyle.cornerRadius + " dp");
        strokeWidthText.setText(buttonStyle.strokeWidth + " dp");
        textSizePressedText.setText(buttonStyle.textSizePress + " sp");
        cornerRadiusPressedText.setText(buttonStyle.cornerRadiusPress + " dp");
        strokeWidthPressedText.setText(buttonStyle.strokeWidthPress + " dp");
        textColorPre.setBackgroundColor(Color.parseColor(buttonStyle.textColor));
        strokeColorPre.setBackgroundColor(Color.parseColor(buttonStyle.strokeColor));
        fillColorPre.setBackgroundColor(Color.parseColor(buttonStyle.fillColor));
        textColorPressedPre.setBackgroundColor(Color.parseColor(buttonStyle.textColorPress));
        strokeColorPressedPre.setBackgroundColor(Color.parseColor(buttonStyle.strokeColorPress));
        fillColorPressedPre.setBackgroundColor(Color.parseColor(buttonStyle.fillColorPress));
        textColorText.setText(buttonStyle.textColor);
        strokeColorText.setText(buttonStyle.strokeColor);
        fillColorText.setText(buttonStyle.fillColor);
        textColorPressedText.setText(buttonStyle.textColorPress);
        strokeColorPressedText.setText(buttonStyle.strokeColorPress);
        fillColorPressedText.setText(buttonStyle.fillColorPress);

        editName.addTextChangedListener(this);
        textSizeSeekbar.setOnSeekBarChangeListener(this);
        cornerRadiusSeekbar.setOnSeekBarChangeListener(this);
        strokeWidthSeekbar.setOnSeekBarChangeListener(this);
        textSizePressedSeekbar.setOnSeekBarChangeListener(this);
        cornerRadiusPressedSeekbar.setOnSeekBarChangeListener(this);
        strokeWidthPressedSeekbar.setOnSeekBarChangeListener(this);
        selectTextColor.setOnClickListener(this);
        selectStrokeColor.setOnClickListener(this);
        selectFillColor.setOnClickListener(this);
        selectTextColorPressed.setOnClickListener(this);
        selectStrokeColorPressed.setOnClickListener(this);
        selectFillColorPressed.setOnClickListener(this);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        if (seekBar == textSizeSeekbar) {
            textSizeText.setText(i + " sp");
            buttonStyle.textSize = i;
        }
        if (seekBar == cornerRadiusSeekbar) {
            cornerRadiusText.setText(i + " dp");
            buttonStyle.cornerRadius = i;
        }
        if (seekBar == strokeWidthSeekbar) {
            strokeWidthText.setText((float) i / 10f + " dp");
            buttonStyle.strokeWidth = (float) i / 10f;
        }
        if (seekBar == textSizePressedSeekbar) {
            textSizePressedText.setText(i + " sp");
            buttonStyle.textSizePress = i;
        }
        if (seekBar == cornerRadiusPressedSeekbar) {
            cornerRadiusPressedText.setText(i + " dp");
            buttonStyle.cornerRadiusPress = i;
        }
        if (seekBar == strokeWidthPressedSeekbar) {
            strokeWidthPressedText.setText((float) i / 10f + " dp");
            buttonStyle.strokeWidthPress = (float) i / 10f;
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        if (view == positive) {
            ArrayList<String> names = new ArrayList<>();
            for (ButtonStyle style : list) {
                names.add(style.name);
            }
            if (editName.getText().toString().equals("")) {
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_create_button_style_name_empty),Toast.LENGTH_SHORT).show();
            }
            else if (names.contains(editName.getText().toString())) {
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_create_button_style_name_exist),Toast.LENGTH_SHORT).show();
            }
            else {
                onButtonStyleCreateListener.onButtonStyleCreate(buttonStyle);
                dismiss();
            }
        }
        if (view == negative) {
            dismiss();
        }

        if (view == selectTextColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(buttonStyle.textColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    textColorPre.setBackgroundColor(destColor);
                    textColorText.setText("#" + Integer.toHexString(destColor));
                    buttonStyle.textColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectStrokeColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(buttonStyle.strokeColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    strokeColorPre.setBackgroundColor(destColor);
                    strokeColorText.setText("#" + Integer.toHexString(destColor));
                    buttonStyle.strokeColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectFillColor) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(buttonStyle.fillColor));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    fillColorPre.setBackgroundColor(destColor);
                    fillColorText.setText("#" + Integer.toHexString(destColor));
                    buttonStyle.fillColor = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectTextColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(buttonStyle.textColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    textColorPressedPre.setBackgroundColor(destColor);
                    textColorPressedText.setText("#" + Integer.toHexString(destColor));
                    buttonStyle.textColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectStrokeColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(buttonStyle.strokeColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    strokeColorPressedPre.setBackgroundColor(destColor);
                    strokeColorPressedText.setText("#" + Integer.toHexString(destColor));
                    buttonStyle.strokeColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
        }
        if (view == selectFillColorPressed) {
            ColorSelectorDialog colorSelectorDialog = new ColorSelectorDialog(getContext(),false,Color.parseColor(buttonStyle.fillColorPress));
            colorSelectorDialog.setColorSelectorDialogListener(new ColorSelectorDialog.ColorSelectorDialogListener() {
                @Override
                public void onColorSelected(int color) {

                }

                @Override
                public void onPositive(int destColor) {
                    fillColorPressedPre.setBackgroundColor(destColor);
                    fillColorPressedText.setText("#" + Integer.toHexString(destColor));
                    buttonStyle.fillColorPress = "#" + Integer.toHexString(destColor);
                }

                @Override
                public void onNegative(int initColor) {

                }
            });
            colorSelectorDialog.show();
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
        buttonStyle.name = editName.getText().toString();
    }

    public interface OnButtonStyleCreateListener{
        void onButtonStyleCreate(ButtonStyle buttonStyle);
    }

}
