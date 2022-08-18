package com.tungsten.hmclpe.launcher.list.local.mod;

import static com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher.ExteriorSettingUI.getThemeColor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.core.graphics.drawable.DrawableCompat;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileBrowser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.ModInfoDialog;
import com.tungsten.hmclpe.launcher.mod.LocalModFile;
import com.tungsten.hmclpe.launcher.uis.game.manager.right.ModManagerUI;
import com.tungsten.hmclpe.utils.LocaleUtils;
import com.tungsten.hmclpe.utils.string.ModTranslations;
import com.tungsten.hmclpe.utils.string.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class LocalModListAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    private ArrayList<LocalModFile> list;
    private ModManagerUI ui;

    private static class ViewHolder {
        LinearLayout item;
        CheckBox checkBox;
        TextView name;
        TextView category;
        TextView info;
        ImageButton restore;
        ImageButton openFolder;
        ImageButton showInfo;
    }

    public LocalModListAdapter (Context context, MainActivity activity, ArrayList<LocalModFile> list, ModManagerUI ui) {
        this.context = context;
        this.activity = activity;
        this.list = list;
        this.ui = ui;
    }

    public ArrayList<LocalModFile> getList() {
        return list;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_local_mod,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.checkBox = view.findViewById(R.id.check_enable);
            viewHolder.name = view.findViewById(R.id.mod_name);
            viewHolder.category = view.findViewById(R.id.mod_category);
            viewHolder.info = view.findViewById(R.id.mod_info);
            viewHolder.restore = view.findViewById(R.id.restore);
            viewHolder.openFolder = view.findViewById(R.id.open_folder);
            viewHolder.showInfo = view.findViewById(R.id.show_mod_info);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) view.getTag();
        }
        LocalModFile localModFile = list.get(i);
        viewHolder.checkBox.setOnCheckedChangeListener(null);
        viewHolder.checkBox.setChecked(localModFile.isActive());
        viewHolder.name.setText(localModFile.getFileName());
        String displayName = ModTranslations.MOD.getModById(localModFile.getId()) == null ? "" : Objects.requireNonNull(ModTranslations.MOD.getModById(localModFile.getId())).getDisplayName();
        viewHolder.category.setText("");
        if (LocaleUtils.isChinese(context)) {
            viewHolder.category.setText(displayName);
        }
        String unknown = context.getString(R.string.mod_manager_ui_unknown_info);
        String name = StringUtils.isBlank(localModFile.getName()) ? unknown : localModFile.getName();
        String version = StringUtils.isBlank(localModFile.getVersion()) ? unknown : localModFile.getVersion();
        String author = StringUtils.isBlank(localModFile.getAuthors()) ? unknown : localModFile.getAuthors();
        viewHolder.info.setText(context.getString(R.string.mod_manager_ui_info).replace("%n",name).replace("%v",version).replace("%a",author));
        viewHolder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            try {
                localModFile.setActive(b);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        if (ui.selectedMods.contains(localModFile)) {
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
        }
        else {
            viewHolder.item.setBackground(context.getDrawable(R.drawable.launcher_button_white_blue));
        }
        viewHolder.item.setOnClickListener(view1 -> {
            if (ui.mainBar.getVisibility() == View.VISIBLE && ui.subBar.getVisibility() == View.GONE) {
                ui.mainBar.setVisibility(View.GONE);
                ui.subBar.setVisibility(View.VISIBLE);
            }
            if (!ui.selectedMods.contains(localModFile)) {
                ui.selectedMods.add(localModFile);
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
        viewHolder.restore.setVisibility(localModFile.getMod().getOldFiles().isEmpty() ? View.GONE : View.VISIBLE);
        viewHolder.restore.setOnClickListener(view14 -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.MenuStyle);
            @SuppressLint("RtlHardcoded") PopupMenu menu = new PopupMenu(wrapper, viewHolder.restore, Gravity.RIGHT);
            for (LocalModFile mod : localModFile.getMod().getOldFiles()) {
                menu.getMenu().add(mod.getVersion());
            }
            menu.setOnMenuItemClickListener(item -> {
                for (LocalModFile mod : localModFile.getMod().getOldFiles()) {
                    if (mod.getVersion().contentEquals(item.getTitle())) {
                        rollback(localModFile, mod);
                        return true;
                    }
                }
                return false;
            });
            menu.show();
        });
        viewHolder.openFolder.setOnClickListener(view12 -> {
            Intent intent = new Intent(context, FileBrowser.class);
            intent.putExtra(Constants.INITIAL_DIRECTORY, localModFile.getFile().getParent().toString());
            context.startActivity(intent);
        });
        viewHolder.showInfo.setOnClickListener(view13 -> {
            ModInfoDialog dialog = new ModInfoDialog(context,localModFile);
            dialog.show();
        });
        return view;
    }

    public void rollback(LocalModFile from, LocalModFile to) {
        try {
            ui.modManager.rollback(from, to);
            ui.refreshList();
        } catch (IOException ex) {
            Toast.makeText(context,ex.toString(),Toast.LENGTH_SHORT).show();
        }
    }
}
