package com.tungsten.hmclpe.launcher.list.local.save;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileBrowser;
import com.tungsten.filepicker.FolderChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.game.World;
import com.tungsten.hmclpe.launcher.uis.game.manager.right.WorldManagerUI;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.versioning.VersionNumber;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class WorldListAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    private ArrayList<World> list;

    private static class ViewHolder {
        TextView name;
        TextView info;
        ImageButton more;
    }

    public WorldListAdapter (Context context, MainActivity activity, ArrayList<World> list) {
        this.context = context;
        this.activity = activity;
        this.list = list;
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

    @SuppressLint("NonConstantResourceId")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_save,null);
            viewHolder.name = view.findViewById(R.id.save_name);
            viewHolder.info = view.findViewById(R.id.save_info);
            viewHolder.more = view.findViewById(R.id.more_vert);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        World world = list.get(i);
        viewHolder.name.setText(world.getWorldName());
        String fileName = world.getFileName();
        String lastPlayTime = DateTimeFormatter.ofPattern(context.getString(R.string.time_pattern)).withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(world.getLastPlayed()));
        String gameVersion = world.getGameVersion() == null ? context.getString(R.string.world_manager_ui_unknown_game_version) : world.getGameVersion();
        viewHolder.info.setText(context.getString(R.string.world_manager_ui_info).replace("%f",fileName).replace("%t",lastPlayTime).replace("%v",gameVersion));
        viewHolder.more.setOnClickListener(view1 -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.MenuStyle);
            PopupMenu menu = new PopupMenu(wrapper, (View) viewHolder.more.getParent(), Gravity.END);
            menu.inflate(R.menu.world_menu);
            menu.setForceShowIcon(true);
            menu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()){
                    case R.id.manage_assets:
                        if (world.getGameVersion() == null || // old game will not write game version to level.dat
                                (VersionNumber.isIntVersionNumber(world.getGameVersion()) // we don't parse snapshot version
                                        && VersionNumber.asVersion(world.getGameVersion()).compareTo(VersionNumber.asVersion("1.13")) < 0)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(context.getString(R.string.dialog_manage_packmc_title));
                            builder.setMessage(context.getString(R.string.dialog_manage_packmc_msg));
                            builder.setPositiveButton(context.getString(R.string.dialog_manage_packmc_positive), null);
                            builder.create().show();
                        }
                        else {
                            activity.uiManager.packMcManagerUI.world = world;
                            activity.uiManager.switchMainUI(activity.uiManager.packMcManagerUI);
                        }
                        return true;
                    case R.id.export_world:
                        activity.uiManager.exportWorldUI.world = world;
                        activity.uiManager.switchMainUI(activity.uiManager.exportWorldUI);
                        return true;
                    case R.id.open_dir:
                        Intent intent = new Intent(context, FileBrowser.class);
                        intent.putExtra(Constants.INITIAL_DIRECTORY, world.getFile().toString());
                        context.startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            });
            menu.show();
        });
        return view;
    }

}
