package com.tungsten.hmclpe.launcher.list.local.controller;

import android.content.Context;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.leo618.zip.IZipCallback;
import com.leo618.zip.ZipManager;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.ControlPatternActivity;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.LoadingDialog;
import com.tungsten.hmclpe.launcher.dialogs.control.ControllerManagerDialog;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.InitializeSetting;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.utils.animation.HiddenAnimationUtils;
import com.tungsten.hmclpe.utils.convert.ConvertUtils;
import com.tungsten.hmclpe.utils.file.AssetsUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;

import java.io.File;
import java.util.ArrayList;

public class ControlPatternListAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    private ControllerManagerDialog dialog;
    private ArrayList<ControlPattern> list;
    private String currentPattern;
    private boolean fullscreen;
    private boolean isolate;

    public ControlPatternListAdapter(Context context, MainActivity activity, ControllerManagerDialog dialog, ArrayList<ControlPattern> list, String currentPattern, boolean fullscreen,boolean isolate){
        this.context = context;
        this.activity = activity;
        this.dialog = dialog;
        this.list = list;
        this.currentPattern = currentPattern;
        this.fullscreen = fullscreen;
        this.isolate = isolate;
    }

    private static String getMimeType(String filePath) {
        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        String mime = "*/*";
        if (filePath != null) {
            try {
                mmr.setDataSource(filePath);
                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
            }
            catch (RuntimeException e) {
                return mime;
            }
        }
        return mime;
    }

    private class ViewHolder{
        LinearLayout item;
        LinearLayout info;
        RadioButton check;
        TextView name;
        TextView author;
        TextView version;
        TextView describe;
        ImageButton edit;
        ImageButton share;
        ImageButton delete;
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
            view = LayoutInflater.from(context).inflate(R.layout.item_control_pattern,null);
            viewHolder.item = view.findViewById(R.id.item);
            viewHolder.info = view.findViewById(R.id.pattern_info_layout);
            viewHolder.check = view.findViewById(R.id.check_current_pattern);
            viewHolder.name = view.findViewById(R.id.pattern_name);
            viewHolder.author = view.findViewById(R.id.author_text);
            viewHolder.version = view.findViewById(R.id.version_text);
            viewHolder.describe = view.findViewById(R.id.describe_text);
            viewHolder.edit = view.findViewById(R.id.edit_pattern);
            viewHolder.share = view.findViewById(R.id.share_pattern);
            viewHolder.delete = view.findViewById(R.id.delete_pattern);
            view.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder)view.getTag();
        }
        ControlPattern pattern = list.get(i);
        viewHolder.name.setText(pattern.name);
        viewHolder.author.setText(pattern.author);
        viewHolder.version.setText(pattern.versionName);
        viewHolder.describe.setText(pattern.describe);
        viewHolder.check.setChecked(pattern.name.equals(currentPattern));
        viewHolder.check.setOnClickListener(view14 -> {
            dialog.onPatternChangeListener.onPatternChange(pattern.name);
            dialog.currentPattern = pattern.name;
            currentPattern = pattern.name;
            notifyDataSetChanged();
        });
        viewHolder.item.setOnClickListener(view13 -> HiddenAnimationUtils.newInstance(context,viewHolder.info,null, ConvertUtils.dip2px(context,70)).toggle());
        viewHolder.edit.setOnClickListener(view12 -> {
            Intent intent = new Intent(context, ControlPatternActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("pattern", pattern.name);
            bundle.putString("initial", currentPattern);
            bundle.putBoolean("fullscreen",fullscreen);
            intent.putExtras(bundle);
            int code = isolate ? ControlPatternActivity.CONTROL_PATTERN_REQUEST_CODE_ISOLATE : ControlPatternActivity.CONTROL_PATTERN_REQUEST_CODE;
            activity.startActivityForResult(intent,code);
        });
        viewHolder.share.setOnClickListener(view1 -> {
            LoadingDialog dialog = new LoadingDialog(context);
            dialog.setLoadingText(context.getString(R.string.dialog_manage_controller_export_dialog));
            new Thread(() -> {
                activity.runOnUiThread(dialog::show);
                FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/export/");
                FileUtils.createDirectory(AppManifest.DEFAULT_CACHE_DIR + "/export/");
                ZipManager.zip(AppManifest.CONTROLLER_DIR + "/" + pattern.name, AppManifest.DEFAULT_CACHE_DIR + "/export/" + pattern.name + ".zip", "HMCL-PE-Password", new IZipCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onProgress(int percentDone) {

                    }

                    @Override
                    public void onFinish(boolean success) {
                        if (success) {
                            if (FileUtils.rename(AppManifest.DEFAULT_CACHE_DIR + "/export/" + pattern.name + ".zip",pattern.name + ".hmclpe")) {
                                Intent intent = new Intent(Intent.ACTION_SEND);
                                Uri uri = FileProvider.getUriForFile(context,context.getString(R.string.filebrowser_provider),new File(AppManifest.DEFAULT_CACHE_DIR + "/export/" + pattern.name + ".hmclpe"));
                                intent.setType(getMimeType(AppManifest.DEFAULT_CACHE_DIR + "/export/" + pattern.name + ".hmclpe"));
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                intent.addCategory(Intent.CATEGORY_DEFAULT);
                                activity.runOnUiThread(dialog::dismiss);
                                activity.startActivity(Intent.createChooser(intent,context.getString(R.string.dialog_manage_controller_export_dialog)));
                            }
                        }
                    }
                });
            }).start();
        });
        viewHolder.delete.setOnClickListener(view15 -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.dialog_delete_control_pattern_title));
            builder.setMessage(context.getString(R.string.dialog_delete_control_pattern_content));
            builder.setPositiveButton(context.getString(R.string.dialog_delete_control_pattern_positive), (dialogInterface, p) -> {
                if (currentPattern.equals(pattern.name)){
                    if (list.size() == 1){
                        FileUtils.deleteDirectory(AppManifest.CONTROLLER_DIR + "/" + pattern.name);
                        list.remove(i);
                        InitializeSetting.initializeControlPattern(activity, new AssetsUtils.FileOperateCallback() {
                            @Override
                            public void onSuccess() {
                                list = SettingUtils.getControlPatternList();
                                dialog.onPatternChangeListener.onPatternChange(list.get(0).name);
                                dialog.currentPattern = list.get(0).name;
                                currentPattern = list.get(0).name;
                                notifyDataSetChanged();
                            }

                            @Override
                            public void onFailed(String error) {

                            }
                        });
                    }
                    else {
                        FileUtils.deleteDirectory(AppManifest.CONTROLLER_DIR + "/" + pattern.name);
                        list.remove(i);
                        dialog.onPatternChangeListener.onPatternChange(list.get(0).name);
                        dialog.currentPattern = list.get(0).name;
                        currentPattern = list.get(0).name;
                        notifyDataSetChanged();
                    }
                }
                else {
                    FileUtils.deleteDirectory(AppManifest.CONTROLLER_DIR + "/" + pattern.name);
                    list.remove(i);
                    notifyDataSetChanged();
                }
            });
            builder.setNegativeButton(context.getString(R.string.dialog_delete_control_pattern_negative), (dialogInterface, i1) -> {

            });
            AlertDialog dialog = builder.create();
            dialog.show();
        });
        return view;
    }
}
