package com.tungsten.hmclpe.launcher.list.info.contents;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.util.ArrayList;

public class ContentListAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    private ArrayList<ContentListBean> list;

    public ContentListAdapter (Context context,MainActivity activity,ArrayList<ContentListBean> list){
        this.context = context;
        this.list = list;
        this.activity = activity;
    }

    private class ViewHolder{
        LinearLayout switchContent;
        TextView name;
        TextView path;
        ImageButton delete;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_content_list,null);
            viewHolder.switchContent = convertView.findViewById(R.id.switch_content);
            viewHolder.name = convertView.findViewById(R.id.content_name);
            viewHolder.path = convertView.findViewById(R.id.content_path);
            viewHolder.delete = convertView.findViewById(R.id.delete_content);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        viewHolder.name.setText(list.get(position).name);
        viewHolder.path.setText(list.get(position).path);
        if (list.get(position).selected){
            viewHolder.switchContent.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        }
        else {
            viewHolder.switchContent.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
        viewHolder.switchContent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.launcherSetting.gameFileDirectory = list.get(position).path;
                GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
                for (int i = 0;i <list.size();i++){
                    list.get(i).selected = false;
                }
                list.get(position).selected = true;
                GsonUtils.saveContents(list, AppManifest.GAME_FILE_DIRECTORY_DIR + "/game_file_directories.json");
                new Thread(() -> {
                    activity.uiManager.versionListUI.refreshVersionList();
                }).start();
                notifyDataSetChanged();
            }
        });
        viewHolder.delete.setOnClickListener(v -> {
            boolean select = list.get(position).selected;
            list.remove(position);
            if (select && list.size() != 0){
                activity.launcherSetting.gameFileDirectory = list.get(0).path;
                GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
                list.get(0).selected = true;
                new Thread(() -> {
                    activity.uiManager.versionListUI.refreshVersionList();
                }).start();
            }
            if (list.size() == 0){
                list.add(new ContentListBean(context.getString(R.string.default_game_file_directory_list_pri),AppManifest.DEFAULT_GAME_DIR,true));
                list.add(new ContentListBean(context.getString(R.string.default_game_file_directory_list_sec),AppManifest.INNER_GAME_DIR,false));
                activity.launcherSetting.gameFileDirectory = list.get(0).path;
                GsonUtils.saveLauncherSetting(activity.launcherSetting,AppManifest.SETTING_DIR + "/launcher_setting.json");
                new Thread(() -> {
                    activity.uiManager.versionListUI.refreshVersionList();
                }).start();
            }
            GsonUtils.saveContents(list, AppManifest.GAME_FILE_DIRECTORY_DIR + "/game_file_directories.json");
            notifyDataSetChanged();
        });
        return convertView;
    }
}
