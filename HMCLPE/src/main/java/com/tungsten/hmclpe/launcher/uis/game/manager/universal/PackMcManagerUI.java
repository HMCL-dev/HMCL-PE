package com.tungsten.hmclpe.launcher.uis.game.manager.universal;

import static com.tungsten.hmclpe.launcher.uis.universal.setting.right.launcher.ExteriorSettingUI.getThemeColor;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.game.World;
import com.tungsten.hmclpe.launcher.list.local.save.DatapackListAdapter;
import com.tungsten.hmclpe.launcher.mod.Datapack;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;

public class PackMcManagerUI extends BaseUI implements View.OnClickListener {

    public static final int ADD_DATAPACK_REQUEST = 3098;

    public LinearLayout packMcManagerUI;

    public World world;

    private LinearLayout toolbar;

    private LinearLayout refresh;
    private LinearLayout add;
    private LinearLayout delete;
    private LinearLayout enable;
    private LinearLayout disable;

    private ProgressBar progressBar;
    private ListView listView;
    private Datapack datapack;
    public ArrayList<Datapack.Pack> packs;

    public PackMcManagerUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        packMcManagerUI = activity.findViewById(R.id.ui_manage_datapack);

        toolbar = activity.findViewById(R.id.datapack_toolbar);

        refresh = activity.findViewById(R.id.refresh_datapack_list);
        add = activity.findViewById(R.id.add_datapack);
        delete = activity.findViewById(R.id.delete_datapack);
        enable = activity.findViewById(R.id.enable_datapack);
        disable = activity.findViewById(R.id.disable_datapack);
        refresh.setOnClickListener(this);
        add.setOnClickListener(this);
        delete.setOnClickListener(this);
        enable.setOnClickListener(this);
        disable.setOnClickListener(this);

        progressBar = activity.findViewById(R.id.load_datapacks_progress);
        listView = activity.findViewById(R.id.datapack_list);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.manage_datapack_ui_title).replace("%w",world.getWorldName()),activity.uiManager.uis.get(activity.uiManager.uis.size() - 2) != activity.uiManager.mainUI,false);
        CustomAnimationUtils.showViewFromLeft(packMcManagerUI,activity,context,true);
        init();
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(packMcManagerUI,activity,context,true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_DATAPACK_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = UriUtils.getRealPathFromUri_AboveApi19(context,uri);
            if (path != null) {
                File file = new File(path);
                listView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    installSingleDatapack(file);
                    datapack.loadFromDir();
                    activity.runOnUiThread(() -> {
                        packs = new ArrayList<>();
                        DatapackListAdapter adapter = new DatapackListAdapter(context,activity,datapack.getInfo(),PackMcManagerUI.this);
                        listView.setAdapter(adapter);
                        listView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    });
                }).start();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == refresh) {
            refresh();
        }
        if (view == add) {
            Intent intent = new Intent(context, FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "zip;Datapack");
            intent.putExtra(Constants.INITIAL_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath());
            activity.startActivityForResult(intent, ADD_DATAPACK_REQUEST);
        }
        if (view == delete) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(context.getString(R.string.dialog_delete_mod_title));
            builder.setMessage(context.getString(R.string.dialog_delete_mod_msg));
            builder.setPositiveButton(context.getString(R.string.dialog_delete_mod_positive), (dialogInterface, i) -> {
                listView.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    for (Datapack.Pack pack : packs) {
                        try {
                            datapack.deletePack(pack);
                        } catch (IOException e) {
                            activity.runOnUiThread(() -> {
                                Toast.makeText(context,e.toString(),Toast.LENGTH_SHORT).show();
                            });
                            e.printStackTrace();
                            break;
                        }
                    }
                    activity.runOnUiThread(() -> {
                        packs = new ArrayList<>();
                        DatapackListAdapter adapter = new DatapackListAdapter(context,activity,datapack.getInfo(),PackMcManagerUI.this);
                        listView.setAdapter(adapter);
                        listView.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    });
                }).start();
            });
            builder.setNegativeButton(context.getString(R.string.dialog_delete_mod_negative), (dialogInterface, i) -> {});
            builder.create().show();
        }
        if (view == enable) {
            for (Datapack.Pack pack : packs) {
                pack.setActive(true);
            }
            packs = new ArrayList<>();
            DatapackListAdapter adapter = new DatapackListAdapter(context,activity,datapack.getInfo(),PackMcManagerUI.this);
            listView.setAdapter(adapter);
        }
        if (view == disable) {
            for (Datapack.Pack pack : packs) {
                pack.setActive(false);
            }
            packs = new ArrayList<>();
            DatapackListAdapter adapter = new DatapackListAdapter(context,activity,datapack.getInfo(),PackMcManagerUI.this);
            listView.setAdapter(adapter);
        }
    }

    private void init() {
        int themeColor = Color.parseColor(getThemeColor(context,activity.launcherSetting.launcherTheme));
        float[] hsv = new float[3];
        Color.colorToHSV(themeColor, hsv);
        hsv[1] -= (1 - hsv[1]) * 0.3f;
        hsv[2] += (1 - hsv[2]) * 0.3f;
        toolbar.setBackgroundColor(Color.HSVToColor(hsv));
        refresh();
    }

    private void refresh() {
        packs = new ArrayList<>();
        datapack = new Datapack(world.getFile().resolve("datapacks"));
        listView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        new Thread(() -> {
            datapack.loadFromDir();
            activity.runOnUiThread(() -> {
                DatapackListAdapter adapter = new DatapackListAdapter(context,activity,datapack.getInfo(),PackMcManagerUI.this);
                listView.setAdapter(adapter);
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }

    private void installSingleDatapack(File datapack) {
        try {
            Datapack zip = new Datapack(datapack.toPath());
            zip.loadFromZip();
            zip.installTo(world.getFile());
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }
}
