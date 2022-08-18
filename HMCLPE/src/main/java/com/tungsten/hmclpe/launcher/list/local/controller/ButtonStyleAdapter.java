package com.tungsten.hmclpe.launcher.list.local.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.bean.button.ButtonStyle;
import com.tungsten.hmclpe.launcher.dialogs.control.ButtonStyleManagerDialog;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import java.util.ArrayList;

public class ButtonStyleAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ButtonStyle> list;
    private ButtonStyleManagerDialog dialog;

    public ButtonStyleAdapter (Context context, ArrayList<ButtonStyle> list, ButtonStyleManagerDialog dialog) {
        this.context = context;
        this.list = list;
        this.dialog = dialog;
    }

    private class ViewHolder {
        Button styleButton;
        TextView styleName;
        ImageButton delete;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int i) {
        return list.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_button_style,null);
            viewHolder.styleButton = view.findViewById(R.id.button_style);
            viewHolder.styleName = view.findViewById(R.id.button_style_name);
            viewHolder.delete = view.findViewById(R.id.delete_button_style);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ButtonStyle buttonStyle = list.get(i);
        GradientDrawable drawableNormal = new GradientDrawable();
        drawableNormal.setCornerRadius(ConvertUtils.dip2px(context,buttonStyle.cornerRadius));
        drawableNormal.setStroke(ConvertUtils.dip2px(context,buttonStyle.strokeWidth), Color.parseColor(buttonStyle.strokeColor));
        drawableNormal.setColor(Color.parseColor(buttonStyle.fillColor));
        GradientDrawable drawablePress = new GradientDrawable();
        drawablePress.setCornerRadius(ConvertUtils.dip2px(context,buttonStyle.cornerRadiusPress));
        drawablePress.setStroke(ConvertUtils.dip2px(context,buttonStyle.strokeWidthPress), Color.parseColor(buttonStyle.strokeColorPress));
        drawablePress.setColor(Color.parseColor(buttonStyle.fillColorPress));
        viewHolder.styleButton.setGravity(Gravity.CENTER);
        viewHolder.styleButton.setPadding(0,0,0,0);
        viewHolder.styleButton.setText("S");
        viewHolder.styleButton.setAllCaps(false);
        viewHolder.styleButton.setTextSize(buttonStyle.textSize);
        viewHolder.styleButton.setTextColor(Color.parseColor(buttonStyle.textColor));
        viewHolder.styleButton.setBackground(drawableNormal);
        viewHolder.styleButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_DOWN) {
                    ((Button) view).setTextSize(buttonStyle.textSizePress);
                    ((Button) view).setTextColor(Color.parseColor(buttonStyle.textColorPress));
                    ((Button) view).setBackground(drawablePress);
                }
                if (motionEvent.getActionMasked() == MotionEvent.ACTION_UP || motionEvent.getActionMasked() == MotionEvent.ACTION_CANCEL) {
                    ((Button) view).setTextSize(buttonStyle.textSize);
                    ((Button) view).setTextColor(Color.parseColor(buttonStyle.textColor));
                    ((Button) view).setBackground(drawableNormal);
                }
                return true;
            }
        });
        viewHolder.styleName.setText(buttonStyle.name);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(i);
                SettingUtils.saveButtonStyle(list);
                dialog.refreshStyleList();
            }
        });
        return view;
    }
}
