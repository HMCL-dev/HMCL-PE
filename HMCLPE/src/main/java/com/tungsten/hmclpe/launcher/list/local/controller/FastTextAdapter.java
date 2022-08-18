package com.tungsten.hmclpe.launcher.list.local.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.dialogs.control.InputDialog;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;

import java.util.ArrayList;

public class FastTextAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<String> list;
    private InputDialog dialog;

    public FastTextAdapter (Context context, ArrayList<String> list, InputDialog dialog) {
        this.context = context;
        this.list = list;
        this.dialog = dialog;
    }

    private final class ViewHolder{
        LinearLayout item;
        TextView textView;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_fast_text,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.textView = view.findViewById(R.id.fast_text);
            viewHolder.delete = view.findViewById(R.id.delete);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.textView.setText(list.get(i));
        viewHolder.item.setOnClickListener(view12 -> dialog.editText.setText(">" + list.get(i)));
        viewHolder.delete.setOnClickListener(view1 -> {
            list.remove(i);
            SettingUtils.saveFastText(list);
            notifyDataSetChanged();
        });
        return view;
    }
}
