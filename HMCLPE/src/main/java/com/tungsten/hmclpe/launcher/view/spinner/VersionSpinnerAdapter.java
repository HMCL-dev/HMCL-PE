package com.tungsten.hmclpe.launcher.view.spinner;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.list.local.game.GameListBean;
import com.tungsten.hmclpe.utils.file.DrawableUtils;

import java.io.File;
import java.util.ArrayList;

public class VersionSpinnerAdapter extends BaseAdapter {
    
    private Context context;
    private ArrayList<GameListBean> list;
    
    public VersionSpinnerAdapter (Context context, ArrayList<GameListBean> list) {
        this.context = context;
        this.list = list;
    }

    private class ViewHolder{
        ImageView icon;
        TextView name;
        TextView version;
    }
    
    public int getPosition(GameListBean gameListBean) {
        for (int i = 0;i < list.size();i++) {
            if (list.get(i).iconPath.equals(gameListBean.iconPath) && list.get(i).name.equals(gameListBean.name) && list.get(i).version.equals(gameListBean.version)) {
                return i;
            }
        }
        return 0;
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.spinner_item_local_version,null);
            viewHolder.icon = view.findViewById(R.id.icon);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.version = view.findViewById(R.id.version);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)view.getTag();
        }
        GameListBean bean = list.get(i);
        if (!bean.iconPath.equals("") && new File(bean.iconPath).exists()) {
            viewHolder.icon.setBackground(DrawableUtils.getDrawableFromFile(bean.iconPath));
        }
        else {
            if (!bean.version.contains(",")) {
                viewHolder.icon.setBackground(context.getDrawable(R.drawable.ic_grass));
            }
            else {
                viewHolder.icon.setBackground(context.getDrawable(R.drawable.ic_furnace));
            }
        }
        viewHolder.name.setText(bean.name);
        viewHolder.version.setText(bean.version);
        return view;
    }
}
