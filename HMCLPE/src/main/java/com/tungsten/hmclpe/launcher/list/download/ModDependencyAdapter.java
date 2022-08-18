package com.tungsten.hmclpe.launcher.list.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.mod.RemoteModRepository;
import com.tungsten.hmclpe.launcher.mod.curse.CurseForgeRemoteModRepository;
import com.tungsten.hmclpe.launcher.uis.game.download.right.resource.DownloadResourceUI;
import com.tungsten.hmclpe.utils.LocaleUtils;
import com.tungsten.hmclpe.utils.string.ModTranslations;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class ModDependencyAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    private RemoteModRepository repository;
    private List<RemoteMod> list;

    public ModDependencyAdapter (Context context,MainActivity activity,RemoteModRepository repository,List<RemoteMod> list) {
        this.context = context;
        this.activity = activity;
        this.repository = repository;
        this.list = list;
    }

    private class ViewHolder{
        LinearLayout item;
        ImageView icon;
        TextView name;
        TextView categories;
        TextView introduction;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_download_mod_dependency,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.icon = view.findViewById(R.id.mod_icon);
            viewHolder.name = view.findViewById(R.id.mod_name);
            viewHolder.categories = view.findViewById(R.id.mod_categories);
            viewHolder.introduction = view.findViewById(R.id.mod_introduction);
            activity.exteriorConfig.apply(viewHolder.categories);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.icon.setImageDrawable(context.getDrawable(R.drawable.launcher_background_color_white));
        viewHolder.icon.setTag(i);
        new Thread(() -> {
            try {
                URL url = new URL(list.get(i).getIconUrl());
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap icon = BitmapFactory.decodeStream(inputStream);
                if (viewHolder.icon.getTag().equals(i)){
                    activity.runOnUiThread(() -> {
                        viewHolder.icon.setImageBitmap(icon);
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        StringBuilder categories = new StringBuilder();
        for (String category : list.get(i).getCategories()) {
            boolean isCurse = list.get(i).getPageUrl() != null && list.get(i).getPageUrl().contains("curseforge");
            String c;
            int resId = context.getResources().getIdentifier((isCurse ? "curse_category_" : "modrinth_category_") + category.replace("-","_"),"string","com.tungsten.hmclpe");
            if (resId != 0 && context.getString(resId) != null) {
                c = context.getString(resId);
            }
            else {
                c = category;
            }
            categories.append(c).append("   ");
        }
        viewHolder.categories.setText(categories.toString());
        ModTranslations modTranslations = ModTranslations.MOD;
        viewHolder.name.setText(list.get(i).getTitle());
        if (LocaleUtils.isChinese(context)) {
            viewHolder.name.setText(modTranslations.getModByCurseForgeId(list.get(i).getSlug()) == null ? list.get(i).getTitle() : modTranslations.getModByCurseForgeId(list.get(i).getSlug()).getDisplayName());
        }
        viewHolder.introduction.setText(list.get(i).getDescription());
        viewHolder.item.setOnClickListener(view1 -> {
            DownloadResourceUI downloadResourceUI = new DownloadResourceUI(context,activity,repository,list.get(i),0);
            activity.uiManager.switchMainUI(downloadResourceUI);
        });
        return view;
    }

}
