package com.tungsten.hmclpe.launcher.list.download;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.mod.LocalModFile;
import com.tungsten.hmclpe.launcher.uis.game.manager.universal.ModUpdateUI;

import java.util.ArrayList;

public class ModUpdateListAdapter extends BaseAdapter {

    private final Context context;
    private final ModUpdateUI ui;
    private final ArrayList<LocalModFile.ModUpdate> list;

    public ModUpdateListAdapter (Context context, ModUpdateUI ui) {
        this.context = context;
        this.ui = ui;
        this.list = ui.modUpdates;
    }

    private static class ViewHolder {
        CheckBox checkBox;
        TextView file;
        TextView current;
        TextView target;
        TextView source;
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

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        final ViewHolder viewHolder;
        if (view == null){
            viewHolder = new ViewHolder();
            view = LayoutInflater.from(context).inflate(R.layout.item_mod_update,null);
            viewHolder.checkBox = view.findViewById(R.id.check_update);
            viewHolder.file = view.findViewById(R.id.file);
            viewHolder.current = view.findViewById(R.id.current);
            viewHolder.target = view.findViewById(R.id.target);
            viewHolder.source = view.findViewById(R.id.source);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        LocalModFile.ModUpdate modUpdate = list.get(i);
        viewHolder.checkBox.setChecked(ui.selectedMods.contains(modUpdate));
        viewHolder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                if (!ui.selectedMods.contains(modUpdate)) {
                    ui.selectedMods.add(modUpdate);
                }
            }
            else {
                ui.selectedMods.remove(modUpdate);
            }
        });
        viewHolder.file.setText(modUpdate.getLocalMod().getFileName());
        viewHolder.current.setText(modUpdate.getCurrentVersion().getVersion());
        viewHolder.target.setText(modUpdate.getCandidates().get(0).getVersion());
        viewHolder.source.setText("CurseForge");
        return view;
    }
}
