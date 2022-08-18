package com.tungsten.hmclpe.launcher.list.local.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.dialogs.control.ChildVisibilityDialog;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;

import java.util.ArrayList;

public class ChildVisibilityAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ChildLayout> list;
    private ArrayList<String> currentList;
    private ChildVisibilityDialog dialog;

    public ChildVisibilityAdapter (Context context, ArrayList<String> currentList, String pattern, ChildVisibilityDialog dialog) {
        this.context = context;
        this.currentList = currentList;
        this.dialog = dialog;

        list = SettingUtils.getChildList(pattern);
    }

    private class ViewHolder{
        TextView name;
        CheckBox checkBox;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_child_visibility,null);
            viewHolder.name = view.findViewById(R.id.child_name);
            viewHolder.checkBox = view.findViewById(R.id.check_child_visibility);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        ChildLayout childLayout = list.get(i);
        viewHolder.name.setText(childLayout.name);
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(currentList.contains(childLayout.name));
        viewHolder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                dialog.changeChildList(childLayout.name,b);
            }
        });
        return view;
    }
}
