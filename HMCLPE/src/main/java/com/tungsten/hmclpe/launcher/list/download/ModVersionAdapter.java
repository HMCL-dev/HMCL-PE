package com.tungsten.hmclpe.launcher.list.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FolderChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.dialogs.EditDownloadNameDialog;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.uis.game.download.right.resource.DownloadResourceUI;
import com.tungsten.hmclpe.manifest.AppManifest;

import java.io.File;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;
import java.util.Locale;

public class ModVersionAdapter extends BaseAdapter {

    private Context context;
    private List<RemoteMod.Version> list;
    private DownloadResourceUI ui;

    private class ViewHolder{
        LinearLayout item;
        ImageView icon;
        TextView name;
        TextView type;
        TextView date;
        ImageButton select;
    }

    public ModVersionAdapter (Context context, List<RemoteMod.Version> list, DownloadResourceUI ui) {
        this.context = context;
        this.list = list;
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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_download_mod_version,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.icon = view.findViewById(R.id.mod_type_icon);
            viewHolder.name = view.findViewById(R.id.mod_name);
            viewHolder.type = view.findViewById(R.id.mod_type);
            viewHolder.date = view.findViewById(R.id.mod_date);
            viewHolder.select = view.findViewById(R.id.save_path);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (list.get(i).getVersionType() == RemoteMod.VersionType.Alpha) {
            viewHolder.icon.setBackground(context.getDrawable(R.drawable.ic_outline_alpha_black));
        }
        else if (list.get(i).getVersionType() == RemoteMod.VersionType.Beta) {
            viewHolder.icon.setBackground(context.getDrawable(R.drawable.ic_outline_beta_black));
        }
        else {
            viewHolder.icon.setBackground(context.getDrawable(R.drawable.ic_outline_release_black));
        }
        viewHolder.name.setText(list.get(i).getName());
        viewHolder.type.setText(list.get(i).getVersionType() == RemoteMod.VersionType.Release ? context.getString(R.string.download_resource_release) : context.getString(R.string.download_resource_beta));
        viewHolder.date.setText(FORMATTER.format(list.get(i).getDatePublished().toInstant()));
        viewHolder.select.setOnClickListener(view1 -> {
            ui.selectedVersion = list.get(i);
            Intent intent = new Intent(context, FolderChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.INITIAL_DIRECTORY, new File(AppManifest.DEFAULT_GAME_DIR).getAbsolutePath());
            ui.activity.startActivityForResult(intent, DownloadResourceUI.DOWNLOAD_RESOURCE_REQUEST);
        });
        viewHolder.item.setOnClickListener(view12 -> {
            ui.selectedVersion = list.get(i);
            if (ui.resourceType == 0 || ui.resourceType == 2) {
                EditDownloadNameDialog dialog = new EditDownloadNameDialog(context, ui, list.get(i), true, null);
                dialog.show();
            }
            if (ui.resourceType == 1) {

            }
            if (ui.resourceType == 3) {
                Intent intent = new Intent(context, FolderChooser.class);
                intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
                intent.putExtra(Constants.INITIAL_DIRECTORY, new File(AppManifest.DEFAULT_GAME_DIR).getAbsolutePath());
                ui.activity.startActivityForResult(intent, DownloadResourceUI.DOWNLOAD_RESOURCE_REQUEST);
            }
        });
        return view;
    }

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL).withLocale(Locale.getDefault()).withZone(ZoneId.systemDefault());

}
