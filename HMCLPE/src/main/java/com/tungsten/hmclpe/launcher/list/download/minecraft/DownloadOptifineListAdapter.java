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
import com.tungsten.hmclpe.launcher.download.optifine.OptifineVersion;

import java.util.ArrayList;

public class DownloadOptifineListAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    private ArrayList<OptifineVersion> versions;
    private boolean install;

    public DownloadOptifineListAdapter(Context context,MainActivity activity,ArrayList<OptifineVersion> versions,boolean install){
        this.context = context;
        this.activity = activity;
        this.versions = versions;
        this.install = install;
    }

    private class ViewHolder{
        LinearLayout item;
        ImageView icon;
        TextView optifineId;
        TextView mcVersion;
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

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_download_game_list,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.icon = view.findViewById(R.id.icon);
            viewHolder.optifineId = view.findViewById(R.id.id);
            viewHolder.mcVersion = view.findViewById(R.id.release_time);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)view.getTag();
        }
        OptifineVersion version = versions.get(i);
        viewHolder.icon.setImageDrawable(context.getDrawable(R.drawable.ic_command));
        viewHolder.optifineId.setText(version.type + "_" + version.patch);
        viewHolder.mcVersion.setText(version.mcVersion);
        viewHolder.item.setOnClickListener(v -> {
            if (install) {
                if (activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.optifineVersion != null) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle(context.getString(R.string.dialog_change_version_title));
                    builder.setMessage(context.getString(R.string.dialog_change_version_msg).replace("%s","OptiFine").replace("%v1",activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.optifineVersion).replace("%v2",version.type + "_" + version.patch));
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
                activity.uiManager.installGameUI.optifineVersion = version;
                activity.backToLastUI();
            }
        });
        return view;
    }

    private void update(OptifineVersion optifineVersion) {
        GameUpdateDialog dialog = new GameUpdateDialog(context,activity,activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.versionName,activity.uiManager.gameManagerUI.gameManagerUIManager.autoInstallUI.gameVersion,2,optifineVersion);
        dialog.show();
    }
}
