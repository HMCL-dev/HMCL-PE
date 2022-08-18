package com.tungsten.hmclpe.launcher.dialogs.control;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.leo618.zip.IZipCallback;
import com.leo618.zip.ZipManager;
import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.LoadingDialog;
import com.tungsten.hmclpe.launcher.list.local.controller.ControlPattern;
import com.tungsten.hmclpe.launcher.list.local.controller.ControlPatternListAdapter;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.utils.file.FileStringUtils;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;

import java.io.File;
import java.util.ArrayList;

public class ControllerManagerDialog extends Dialog implements View.OnClickListener {

    public static final int IMPORT_PATTERN_REQUEST_CODE = 3200;
    public static final int IMPORT_PATTERN_REQUEST_CODE_ISOLATED = 3900;

    private MainActivity activity;
    private boolean fullscreen;
    public String currentPattern;
    public OnPatternChangeListener onPatternChangeListener;
    private boolean isolate;

    private ListView patternList;
    private Button importPattern;
    private Button createNewPattern;
    private Button exit;

    public ControllerManagerDialog(@NonNull Context context, MainActivity activity, boolean fullscreen, String currentPattern, OnPatternChangeListener onPatternChangeListener,boolean isolate) {
        super(context);
        this.activity = activity;
        this.fullscreen = fullscreen;
        this.currentPattern = currentPattern;
        this.onPatternChangeListener = onPatternChangeListener;
        this.isolate = isolate;
        setContentView(R.layout.dialog_manage_controller);
        setCancelable(false);
        init();
    }

    private void init(){
        patternList = findViewById(R.id.control_pattern_list);
        importPattern = findViewById(R.id.import_pattern);
        createNewPattern = findViewById(R.id.new_pattern);
        exit = findViewById(R.id.exit);

        importPattern.setOnClickListener(this);
        createNewPattern.setOnClickListener(this);
        exit.setOnClickListener(this);

        loadList();
    }

    public void loadList(){
        ArrayList<ControlPattern> list = SettingUtils.getControlPatternList();
        ControlPatternListAdapter adapter = new ControlPatternListAdapter(getContext(),activity,this,list,currentPattern,fullscreen,isolate);
        patternList.setAdapter(adapter);
    }

    public void onResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            LoadingDialog dialog = new LoadingDialog(getContext());
            dialog.setLoadingText(getContext().getString(R.string.dialog_manage_controller_import_dialog));
            new Thread(() -> {
                activity.runOnUiThread(dialog::show);
                Uri uri = data.getData();
                String path = UriUtils.getRealPathFromUri_AboveApi19(getContext(),uri);
                String name = UriUtils.getRealPathFromUri_AboveApi19(getContext(),uri).substring(UriUtils.getRealPathFromUri_AboveApi19(getContext(),uri).lastIndexOf("/"));
                FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/import/");
                FileUtils.createDirectory(AppManifest.DEFAULT_CACHE_DIR + "/import/");
                FileUtils.copyFile(path,AppManifest.DEFAULT_CACHE_DIR + "/import/" + name);
                FileUtils.rename(AppManifest.DEFAULT_CACHE_DIR + "/import/" + name,name.substring(0,name.lastIndexOf(".")) + ".zip");
                ZipManager.unzip(AppManifest.DEFAULT_CACHE_DIR + "/import/" + name.substring(0, name.lastIndexOf(".")) + ".zip", AppManifest.DEFAULT_CACHE_DIR + "/import","HMCL-PE-Password", new IZipCallback() {
                    @Override
                    public void onStart() {

                    }

                    @Override
                    public void onProgress(int percentDone) {

                    }

                    @Override
                    public void onFinish(boolean success) {
                        if (success) {
                            String[] string = new File(AppManifest.DEFAULT_CACHE_DIR + "/import/").list();
                            String importName = "";
                            for (String str : string){
                                if (new File(AppManifest.DEFAULT_CACHE_DIR + "/import/" + str).isDirectory()) {
                                    importName = str;
                                    break;
                                }
                            }
                            if (new File(AppManifest.DEFAULT_CACHE_DIR + "/import/" + importName + "/info.json").exists()) {
                                String info = FileStringUtils.getStringFromFile(AppManifest.DEFAULT_CACHE_DIR + "/import/" + importName + "/info.json");
                                Gson gson = new Gson();
                                ControlPattern controlPattern = gson.fromJson(info, ControlPattern.class);
                                if (controlPattern.name != null && !controlPattern.name.equals("")) {
                                    ArrayList<String> names = new ArrayList<>();
                                    for (ControlPattern pattern : SettingUtils.getControlPatternList()) {
                                        names.add(pattern.name);
                                    }
                                    if (!names.contains(importName) && importName.equals(controlPattern.name)) {
                                        FileUtils.copyDirectory(AppManifest.DEFAULT_CACHE_DIR + "/import/" + importName,AppManifest.CONTROLLER_DIR + "/" + importName);
                                        FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/import/");
                                        loadList();
                                        activity.runOnUiThread(dialog::dismiss);
                                    }
                                    else if (!importName.equals(controlPattern.name)) {
                                        Toast.makeText(getContext(),getContext().getString(R.string.dialog_manage_controller_import_error),Toast.LENGTH_SHORT).show();
                                        FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/import/");
                                        activity.runOnUiThread(dialog::dismiss);
                                    }
                                    else {
                                        Toast.makeText(getContext(),getContext().getString(R.string.dialog_manage_controller_import_exist),Toast.LENGTH_SHORT).show();
                                        FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/import/");
                                        activity.runOnUiThread(dialog::dismiss);
                                    }
                                }
                                else {
                                    Toast.makeText(getContext(),getContext().getString(R.string.dialog_manage_controller_import_error),Toast.LENGTH_SHORT).show();
                                    FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/import/");
                                    activity.runOnUiThread(dialog::dismiss);
                                }
                            }
                            else {
                                Toast.makeText(getContext(),getContext().getString(R.string.dialog_manage_controller_import_error),Toast.LENGTH_SHORT).show();
                                FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/import/");
                                activity.runOnUiThread(dialog::dismiss);
                            }
                        }
                        else {
                            Toast.makeText(getContext(),getContext().getString(R.string.dialog_manage_controller_import_error),Toast.LENGTH_SHORT).show();
                            FileUtils.deleteDirectory(AppManifest.DEFAULT_CACHE_DIR + "/import/");
                            activity.runOnUiThread(dialog::dismiss);
                        }
                    }
                });
            }).start();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == importPattern){
            Intent intent = new Intent(getContext(), FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "hmclpe");
            intent.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getAbsolutePath());
            activity.startActivityForResult(intent, IMPORT_PATTERN_REQUEST_CODE);
        }
        if (view == createNewPattern){
            CreateControlPatternDialog dialog = new CreateControlPatternDialog(getContext(),activity, new CreateControlPatternDialog.OnPatternCreateListener() {
                @Override
                public void OnPatternCreate(ControlPattern controlPattern) {
                    FileUtils.createDirectory(AppManifest.CONTROLLER_DIR + "/" + controlPattern.name);
                    Gson gson = new Gson();
                    String string = gson.toJson(controlPattern);
                    FileStringUtils.writeFile(AppManifest.CONTROLLER_DIR + "/" + controlPattern.name + "/info.json",string);
                    loadList();
                }
            });
            dialog.show();
        }
        if (view == exit){
            dismiss();
        }
    }

    public interface OnPatternChangeListener{
        void onPatternChange(String pattern);
    }

}
