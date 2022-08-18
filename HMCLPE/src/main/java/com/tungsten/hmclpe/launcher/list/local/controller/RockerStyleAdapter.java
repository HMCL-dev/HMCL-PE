package com.tungsten.hmclpe.launcher.list.local.controller;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.bean.rocker.RockerStyle;
import com.tungsten.hmclpe.control.view.RockerView;
import com.tungsten.hmclpe.launcher.dialogs.control.RockerStyleManagerDialog;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;

import java.util.ArrayList;

public class RockerStyleAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RockerStyle> list;
    RockerStyleManagerDialog dialog;

    public RockerStyleAdapter (Context context, ArrayList<RockerStyle> list, RockerStyleManagerDialog dialog) {
        this.context = context;
        this.list = list;
        this.dialog = dialog;
    }

    private class ViewHolder {
        RelativeLayout container;
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_rocker_style,null);
            viewHolder.container = view.findViewById(R.id.rocker_style);
            viewHolder.styleName = view.findViewById(R.id.rocker_style_name);
            viewHolder.delete = view.findViewById(R.id.delete_rocker_style);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        RockerStyle rockerStyle = list.get(i);
        GradientDrawable drawableNormal = new GradientDrawable();
        drawableNormal.setCornerRadius(ConvertUtils.dip2px(context,rockerStyle.cornerRadius));
        drawableNormal.setStroke(ConvertUtils.dip2px(context,rockerStyle.strokeWidth), Color.parseColor(rockerStyle.strokeColor));
        drawableNormal.setColor(Color.parseColor(rockerStyle.fillColor));
        GradientDrawable drawablePress = new GradientDrawable();
        drawablePress.setCornerRadius(ConvertUtils.dip2px(context,rockerStyle.cornerRadiusPress));
        drawablePress.setStroke(ConvertUtils.dip2px(context,rockerStyle.strokeWidthPress), Color.parseColor(rockerStyle.strokeColorPress));
        drawablePress.setColor(Color.parseColor(rockerStyle.fillColorPress));
        RockerView rockerView = new RockerView(context);
        rockerView.setPointerColor(rockerStyle.pointerColor);
        rockerView.setPointerColorPress(rockerStyle.pointerColorPress);
        rockerView.setFollowType(0);
        rockerView.setDoubleClick(false);
        rockerView.setOnShakeListener(new RockerView.OnShakeListener() {
            @Override
            public void onTouch(RockerView view) {
                view.setBackground(drawablePress);
            }

            @Override
            public void onShake(RockerView view, RockerView.Direction direction) {

            }

            @Override
            public void onCenterDoubleClick(RockerView view) {

            }

            @Override
            public void onFinish(RockerView view) {
                view.setBackground(drawableNormal);
            }
        });
        rockerView.setBackground(drawableNormal);
        viewHolder.container.addView(rockerView);
        rockerView.setSize(ConvertUtils.dip2px(context,30));
        viewHolder.styleName.setText(rockerStyle.name);
        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                list.remove(i);
                SettingUtils.saveRockerStyle(list);
                dialog.refreshStyleList();
            }
        });
        return view;
    }
}
