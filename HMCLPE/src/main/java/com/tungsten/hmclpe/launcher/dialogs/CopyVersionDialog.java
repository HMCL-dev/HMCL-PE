package com.tungsten.hmclpe.launcher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.list.local.game.GameListBean;
import com.tungsten.hmclpe.launcher.setting.game.PrivateGameSetting;
import com.tungsten.hmclpe.utils.file.FileUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.io.File;
import java.util.ArrayList;

public class CopyVersionDialog extends Dialog implements View.OnClickListener {

    private ArrayList<GameListBean> list;
    private PrivateGameSetting privateGameSetting;
    private String gameDir;
    private String currentPath;
    private String currentName;
    private CopyVersionCallback callback;

    private EditText editName;
    private CheckBox checkBox;
    private Button positive;
    private Button negative;
    private ProgressBar progressBar;

    public CopyVersionDialog(@NonNull Context context, ArrayList<GameListBean> list, PrivateGameSetting privateGameSetting, String gameDir, String currentPath, String currentName, CopyVersionCallback callback) {
        super(context);
        this.list = list;
        this.privateGameSetting = privateGameSetting;
        this.gameDir = gameDir;
        this.currentPath = currentPath;
        this.currentName = currentName;
        this.callback = callback;
        setContentView(R.layout.dialog_copy_version);
        setCancelable(false);
        init();
    }

    private void init() {
        editName = findViewById(R.id.copy_version);
        checkBox = findViewById(R.id.check_copy_world);
        positive = findViewById(R.id.copy);
        negative = findViewById(R.id.cancel);
        progressBar = findViewById(R.id.copy_progress);

        editName.setText(currentName);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            if (!editName.getText().toString().equals("") && !editName.getText().toString().contains("/")) {
                ArrayList<String> names = new ArrayList<>();
                for (GameListBean bean : list) {
                    names.add(bean.name);
                }
                if (names.contains(editName.getText().toString())) {
                    Toast.makeText(getContext(), getContext().getString(R.string.dialog_copy_version_exist), Toast.LENGTH_SHORT).show();
                }
                else {
                    Handler handler = new Handler();
                    new Thread(() -> {
                        handler.post(() -> {
                            progressBar.setVisibility(View.VISIBLE);
                            positive.setVisibility(View.GONE);
                            negative.setEnabled(false);
                        });
                        FileUtils.createDirectory(currentPath + editName.getText().toString());
                        FileUtils.copyFile(currentPath + currentName + "/" + currentName + ".jar",currentPath + editName.getText().toString() + "/" + editName.getText().toString() + ".jar");
                        FileUtils.copyFile(currentPath + currentName + "/" + currentName + ".json",currentPath + editName.getText().toString() + "/" + editName.getText().toString() + ".json");
                        if (checkBox.isChecked() && new File(gameDir + "/saves").exists() && new File(gameDir + "/saves").isDirectory()) {
                            FileUtils.copyDirectory(gameDir + "/saves",currentPath + editName.getText().toString() + "/saves");
                        }
                        try {
                            PrivateGameSetting setting = (PrivateGameSetting) privateGameSetting.clone();
                            setting.enable = true;
                            setting.gameDirSetting.type = 1;
                            GsonUtils.savePrivateGameSetting(setting,currentPath + editName.getText().toString() + "/hmclpe.cfg");
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                        handler.post(() -> {
                            progressBar.setVisibility(View.GONE);
                            positive.setVisibility(View.VISIBLE);
                            negative.setEnabled(true);
                            callback.onFinish();
                            dismiss();
                        });
                    }).start();
                }
            }
        }
        if (view == negative) {
            dismiss();
        }
    }

    public interface CopyVersionCallback{
        void onFinish();
    }
}
