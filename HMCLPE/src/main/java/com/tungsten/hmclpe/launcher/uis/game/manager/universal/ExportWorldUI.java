package com.tungsten.hmclpe.launcher.uis.game.manager.universal;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FolderChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.LoadingDialog;
import com.tungsten.hmclpe.launcher.game.World;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;

import java.io.File;
import java.io.IOException;

public class ExportWorldUI extends BaseUI implements View.OnClickListener {

    public static final int EXPORT_WORLD_REQUEST = 3099;

    public World world;

    public LinearLayout exportWorldUI;

    private TextView exportPath;
    private ImageButton editPath;
    private TextView worldName;
    private TextView gameVersion;
    private Button export;

    public ExportWorldUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        exportWorldUI = activity.findViewById(R.id.ui_export_world);

        exportPath = activity.findViewById(R.id.export_world_path);
        editPath = activity.findViewById(R.id.edit_world_export_path);
        worldName = activity.findViewById(R.id.export_world_name);
        gameVersion = activity.findViewById(R.id.export_world_game_version);
        export = activity.findViewById(R.id.export_world);

        editPath.setOnClickListener(this);
        export.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.export_world_ui_title) + " " + world.getWorldName(),false,true);
        CustomAnimationUtils.showViewFromLeft(exportWorldUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(exportWorldUI,activity,context,true);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == EXPORT_WORLD_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            exportPath.setText(UriUtils.getRealPathFromUri_AboveApi19(context,uri) + "/" + world.getFileName() + ".zip");
        }
    }

    @Override
    public void onClick(View view) {
        if (view == editPath) {
            Intent selectIntent = new Intent(context, FolderChooser.class);
            selectIntent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            selectIntent.putExtra(Constants.INITIAL_DIRECTORY, new File(AppManifest.LAUNCHER_DIR).getAbsolutePath());
            activity.startActivityForResult(selectIntent, EXPORT_WORLD_REQUEST);
        }
        if (view == export) {
            if (!new File(exportPath.getText().toString()).exists()) {
                LoadingDialog dialog = new LoadingDialog(context);
                dialog.setLoadingText(context.getString(R.string.dialog_export_world_text));
                dialog.show();
                new Thread(() -> {
                    try {
                        world.export(exportPath.getText().toString().substring(0,exportPath.getText().toString().lastIndexOf("/")),exportPath.getText().toString().substring(exportPath.getText().toString().lastIndexOf("/") + 1));
                        activity.runOnUiThread(() -> {
                            dialog.dismiss();
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle(context.getString(R.string.dialog_export_world_title));
                            builder.setMessage(context.getString(R.string.dialog_export_world_msg));
                            builder.setPositiveButton(context.getString(R.string.dialog_export_world_positive), (dialogInterface, i) -> {
                                activity.backToLastUI();
                            });
                            builder.create().show();
                        });
                    } catch (IOException e) {
                        activity.runOnUiThread(() -> {
                            dialog.dismiss();
                            Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                        });
                        e.printStackTrace();
                    }
                }).start();
            }
            else {
                Toast.makeText(context,"file already exist!",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        exportPath.setText(AppManifest.LAUNCHER_DIR + "/" + world.getWorldName() + ".zip");
        worldName.setText(world.getWorldName());
        gameVersion.setText(world.getGameVersion() == null ? "" : world.getGameVersion());
    }
}
