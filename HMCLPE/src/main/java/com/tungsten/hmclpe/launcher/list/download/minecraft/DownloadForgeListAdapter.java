package com.tungsten.hmclpe.launcher.list.download.minecraft;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import com.tungsten.hmclpe.launcher.download.forge.ForgeVersion;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class DownloadForgeListAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    private ArrayList<ForgeVersion> versions;
    private boolean install;

    public DownloadForgeListAdapter(Context context,MainActivity activity,ArrayList<ForgeVersion> versions,boolean install){
        this.context = context;
        this.activity = activity;
        this.versions = versions;
        this.install = install;
    }

    private class ViewHolder{
        LinearLayout item;
        ImageView icon;
        TextView forgeId;
        TextView mcVersion;
        TextView releaseTime;
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_download_game_list,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.icon = view.findViewById(R.id.icon);
            viewHolder.forgeId = view.findViewById(R.id.id);
            viewHolder.mcVersion = view.findViewById(R.id.type);
            viewHolder.releaseTime = view.findViewById(R.id.release_time);
            activity.exteriorConfig.apply(viewHolder.mcVersion);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)view.getTag();
        }
        ForgeVersion version = versions.get(i);
        viewHolder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_forge));
        viewHolder.forgeId.setText(version.getVersion());
        viewHolder.mcVersion.setText(version.getGameVersion());
        viewHolder.releaseTime.setText(DateTimeFormatter.ofPattern(context.getString(R.string.time_pattern)).withZone(ZoneId.systemDefault()).format(Instant.parse(version.getModified())));
        viewHolder.item.setOnClickListener(v -> {
            if (install) {
                if (activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.forgeVersion != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.dialog_change_version_title));
                    builder.setMessage(context.getString(R.string.dialog_change_version_msg).replace("%s","Forge").replace("%v1",activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.forgeVersion).replace("%v2",version.getVersion()));
                    builder.setPositiveButton(context.getString(R.string.dialog_change_version_positive), (dialogInterface, i1) -> {
                        update(version);
                    });
                    builder.setNegativeButton(context.getString(R.string.dialog_change_version_negative), (dialogInterface, i12) -> {
                        activity.backToLastUI();
                    });
                    builder.create().show();
                }
                else {
                    update(version);
                }
            }
            else {
                activity.uiManager.installGameUI.forgeVersion = version;
                activity.backToLastUI();
            }
        });
        return view;
    }

    private void update(ForgeVersion forgeVersion) {
        GameUpdateDialog dialog = new GameUpdateDialog(context,activity,activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.versionName,activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.gameVersion,0,forgeVersion);
        dialog.show();
    }
}
