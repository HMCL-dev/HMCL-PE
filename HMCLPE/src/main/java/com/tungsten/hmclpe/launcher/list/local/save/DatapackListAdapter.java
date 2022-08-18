package com.tungsten.hmclpe.launcher.list.local.save;

import static com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher.ExteriorSettingUI.getThemeColor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.mod.Datapack;
import com.tungsten.hmclpe.launcher.uis.game.manager.universal.PackMcManagerUI;

import java.util.ArrayList;

public class DatapackListAdapter extends BaseAdapter {

    private final Context context;
    private final MainActivity activity;
    private final ArrayList<Datapack.Pack> list;
    private final PackMcManagerUI ui;

    public DatapackListAdapter (Context context, MainActivity activity, ArrayList<Datapack.Pack> list, PackMcManagerUI ui) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        this.ui = ui;
    }

    private static class ViewHolder {
        LinearLayout item;
        CheckBox checkBox;
        TextView name;
        TextView info;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_datapack,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.checkBox = view.findViewById(R.id.check_enable);
            viewHolder.name = view.findViewById(R.id.name);
            viewHolder.info = view.findViewById(R.id.info);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        Datapack.Pack pack = list.get(i);
        viewHolder.item.setOnClickListener(view1 -> {
            if (!ui.packs.contains(pack)) {
                ui.packs.add(pack);
            }
            int themeColor = Color.parseColor(getThemeColor(context,activity.launcherSetting.launcherTheme));
            float[] hsv = new float[3];
            Color.colorToHSV(themeColor, hsv);
            hsv[1] -= (1 - hsv[1]) * 0.3f;
            hsv[2] += (1 - hsv[2]) * 0.3f;
            hsv[1] -= (1 - hsv[1]) * 0.3f;
            hsv[2] += (1 - hsv[2]) * 0.3f;
            Drawable drawable = context.getDrawable(R.drawable.launcher_view_selected);
            DrawableCompat.setTint(drawable, Color.HSVToColor(hsv));
            viewHolder.item.setBackground(drawable);
        });
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(pack.isActive());
        viewHolder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            pack.setActive(b);
        });
        viewHolder.name.setText(pack.getId());
        viewHolder.info.setText(pack.getDescription().toString());
        return view;
    }
}
