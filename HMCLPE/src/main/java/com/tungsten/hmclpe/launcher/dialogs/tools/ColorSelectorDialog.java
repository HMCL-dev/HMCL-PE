package com.tungsten.hmclpe.launcher.dialogs.tools;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.jaredrummler.android.colorpicker.ColorPickerView;
import com.tungsten.hmclpe.R;

public class ColorSelectorDialog extends Dialog implements ColorPickerView.OnColorChangedListener , View.OnClickListener, TextView.OnEditorActionListener {

    public Context context;
    public ColorPickerView colorPickerView;
    public boolean showRecommendBar;
    public int initColor;
    public int currentColor;
    public ColorSelectorDialogListener colorSelectorDialogListener;
    public LinearLayout recommendColorBar;
    public Button colorPri;
    public Button colorSec;
    public Button colorThi;
    public Button colorFor;
    public Button colorFif;
    public Button colorSix;

    public View initColorBar;
    public View destColorBar;
    public EditText editColor;
    public Button positive;
    public Button negative;

    public ColorSelectorDialog(@NonNull Context context,boolean showRecommendBar,int initColor) {
        super(context);
        setContentView(R.layout.dialog_color_selector);
        setCancelable(false);
        this.context = context;
        this.showRecommendBar = showRecommendBar;
        this.initColor = initColor;
        init();
    }

    private void init(){
        colorPickerView = findViewById(R.id.color_picker);
        colorPickerView.setOnColorChangedListener(this);
        colorPickerView.setColor(initColor);
        currentColor = initColor;

        recommendColorBar = findViewById(R.id.recommend_color_bar);
        if (showRecommendBar){
            recommendColorBar.setVisibility(View.VISIBLE);
        }
        colorPri = findViewById(R.id.recommend_color_pri);
        colorSec = findViewById(R.id.recommend_color_sec);
        colorThi = findViewById(R.id.recommend_color_thi);
        colorFor = findViewById(R.id.recommend_color_for);
        colorFif = findViewById(R.id.recommend_color_fif);
        colorSix = findViewById(R.id.recommend_color_six);
        colorPri.setOnClickListener(this);
        colorSec.setOnClickListener(this);
        colorThi.setOnClickListener(this);
        colorFor.setOnClickListener(this);
        colorFif.setOnClickListener(this);
        colorSix.setOnClickListener(this);

        initColorBar = findViewById(R.id.init_color);
        destColorBar = findViewById(R.id.dest_color);
        initColorBar.setBackgroundColor(initColor);
        destColorBar.setBackgroundColor(initColor);
        editColor = findViewById(R.id.color_text);
        editColor.setOnEditorActionListener(this);
        editColor.setText("#" + Integer.toHexString(initColor));

        positive = findViewById(R.id.color_picker_positive);
        negative = findViewById(R.id.color_picker_negative);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    private void setColor(int color){
        currentColor = color;
        colorPickerView.setColor(color);
        editColor.setText("#" + Integer.toHexString(color));
        destColorBar.setBackgroundColor(color);
        colorSelectorDialogListener.onColorSelected(color);
    }

    @Override
    public void onColorChanged(int newColor) {
        setColor(newColor);
    }

    @Override
    public void onClick(View v) {
        if (v == colorPri){
            setColor(context.getResources().getColor(R.color.colorPrimary));
        }
        if (v == colorSec){
            setColor(context.getResources().getColor(R.color.colorSecondary));
        }
        if (v == colorThi){
            setColor(context.getResources().getColor(R.color.colorThird));
        }
        if (v == colorFor){
            setColor(context.getResources().getColor(R.color.colorForth));
        }
        if (v == colorFif){
            setColor(context.getResources().getColor(R.color.colorFifth));
        }
        if (v == colorSix){
            setColor(context.getResources().getColor(R.color.colorSixth));
        }
        if (v == positive){
            colorSelectorDialogListener.onPositive(currentColor);
            this.dismiss();
        }
        if (v == negative){
            colorSelectorDialogListener.onNegative(initColor);
            this.dismiss();
        }
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        setColor(Color.parseColor(v.getText().toString()));
        return false;
    }

    public interface ColorSelectorDialogListener{
        void onColorSelected(int color);
        void onPositive(int destColor);
        void onNegative(int initColor);
    }

    public void setColorSelectorDialogListener(ColorSelectorDialogListener colorSelectorDialogListener){
        this.colorSelectorDialogListener = colorSelectorDialogListener;
    }
}

