package com.tungsten.hmclpe.launcher.view.spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.mod.RemoteModRepository;
import com.tungsten.hmclpe.launcher.mod.curse.CurseAddon;

import java.util.ArrayList;

public class CategorySpinnerAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<RemoteModRepository.Category> list;
    private int rootId;

    public CategorySpinnerAdapter(Context context, ArrayList<RemoteModRepository.Category> list, int rootId){
        this.context = context;
        this.list = list;
        this.rootId = rootId;
    }

    private static class ViewHolder{
        CheckedTextView checkedTextView;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner_drop_down,null);
            viewHolder.checkedTextView = convertView.findViewById(R.id.checkedTextViewCustom);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        RemoteModRepository.Category category = list.get(position);
        boolean isCurse = category.getSelf() instanceof CurseAddon.Category;
        String c;
        int resId = context.getResources().getIdentifier((isCurse ? "curse_category_" : "modrinth_category_") + category.getId().replace("-","_"),"string","com.tungsten.hmclpe");
        if (resId != 0 && context.getString(resId) != null) {
            c = context.getString(resId);
        }
        else {
            c = category.getId();
        }
        if (category.getSelf() instanceof CurseAddon.Category) {
            if (((CurseAddon.Category) category.getSelf()).getParentCategoryId() == rootId || ((CurseAddon.Category) category.getSelf()).getParentCategoryId() == 0) {
                viewHolder.checkedTextView.setText(c);
            }
            else {
                viewHolder.checkedTextView.setText("    " + c);
            }
        }
        else {
            viewHolder.checkedTextView.setText(c);
        }
        return convertView;
    }
}
