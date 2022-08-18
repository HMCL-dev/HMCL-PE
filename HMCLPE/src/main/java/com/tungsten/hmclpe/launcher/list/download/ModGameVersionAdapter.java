package com.tungsten.hmclpe.launcher.list.download;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.uis.game.download.right.resource.DownloadResourceUI;
import com.tungsten.hmclpe.utils.SimpleMultimap;
import com.tungsten.hmclpe.utils.animation.HiddenAnimationUtils;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;
import com.tungsten.hmclpe.utils.versioning.VersionNumber;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModGameVersionAdapter extends BaseAdapter {

    private Context context;
    private List<String> list;
    private SimpleMultimap<String, RemoteMod.Version> versions;
    private int[] layoutHeights;
    private DownloadResourceUI ui;

    private class ViewHolder{
        LinearLayout item;
        ImageView show;
        TextView name;
        LinearLayout modListLayout;
        ListView modListView;
    }

    public ModGameVersionAdapter (Context context, SimpleMultimap<String, RemoteMod.Version> versions, DownloadResourceUI ui) {
        this.context = context;
        this.list = new ArrayList<>();
        list.addAll(versions.keys().stream()
                .sorted(VersionNumber.VERSION_COMPARATOR.reversed())
                .collect(Collectors.toList()));
        this.versions = versions;
        this.layoutHeights = new int[list.size()];
        this.ui = ui;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_download_mod_game_version,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.show = view.findViewById(R.id.show_game_version);
            viewHolder.name = view.findViewById(R.id.game_version);
            viewHolder.modListLayout = view.findViewById(R.id.mod_list_layout);
            viewHolder.modListView = view.findViewById(R.id.mod_list);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(list.get(i));
        ModVersionAdapter modVersionAdapter = new ModVersionAdapter(context, new ArrayList<>(versions.get(list.get(i))),ui);
        viewHolder.modListView.setAdapter(modVersionAdapter);
        layoutHeights[i] = getListViewHeight(viewHolder.modListView) + ConvertUtils.dip2px(context,24);
        viewHolder.item.setOnClickListener(view1 -> {
            ui.refreshVersionListHeight(viewHolder.modListLayout.getVisibility() == View.VISIBLE ? -layoutHeights[i] : layoutHeights[i]);
            HiddenAnimationUtils.newInstance(context,viewHolder.modListLayout,viewHolder.show,layoutHeights[i]).toggle();
        });
        return view;
    }

    public static int getListViewHeight(ListView listView) {
        int count = listView.getAdapter().getCount();
        View view = listView.getAdapter().getView(0,null,listView);
        view.measure(0, 0);
        return (view.getMeasuredHeight() * count) + (listView.getDividerHeight() * (count - 1));
    }
}
