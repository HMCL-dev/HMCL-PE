package com.tungsten.hmclpe.launcher.list.download.minecraft;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.download.GameUpdateDialog;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DownloadFabricAPIListAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    private String mcVersion;
    private ArrayList<RemoteMod.Version> versions;
    private boolean install;

    public DownloadFabricAPIListAdapter(Context context,MainActivity activity,String mcVersion,ArrayList<RemoteMod.Version> versions,boolean install){
        this.context = context;
        this.activity = activity;
        this.mcVersion = mcVersion;
        this.versions = versions;
        this.install = install;
    }

    private class ViewHolder{
        LinearLayout item;
        ImageView icon;
        TextView fabricAPIId;
        TextView mcVersion;
        TextView time;
    }

    @Override
    public int getCount() {
        return versions.size();
    }

    @Override
    public Object getItem(int i) {
        return versions.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @SuppressLint({"UseCompatLoadingForDrawables"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_download_game_list,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.icon = view.findViewById(R.id.icon);
            viewHolder.fabricAPIId = view.findViewById(R.id.id);
            viewHolder.mcVersion = view.findViewById(R.id.type);
            viewHolder.time = view.findViewById(R.id.release_time);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)view.getTag();
        }
        RemoteMod.Version version = versions.get(i);
        viewHolder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_fabric));
        viewHolder.fabricAPIId.setText(version.getVersion());
        viewHolder.mcVersion.setText(mcVersion);
        viewHolder.time.setText(DateTimeFormatter.ofPattern(context.getString(R.string.time_pattern)).withZone(ZoneId.systemDefault()).format(version.getDatePublished().toInstant()));
        viewHolder.item.setOnClickListener(v -> {
            if (install) {
                GameUpdateDialog dialog = new GameUpdateDialog(context,activity,activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.versionName,activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.gameVersion,4,version);
                dialog.show();
            }
            else {
                activity.uiManager.installGameUI.fabricAPIVersion = version;
                activity.backToLastUI();
            }
        });
        return view;
    }
}
