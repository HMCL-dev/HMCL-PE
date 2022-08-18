package com.tungsten.hmclpe.launcher.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.game.World;
import com.tungsten.hmclpe.launcher.uis.game.manager.right.WorldManagerUI;

import java.io.File;
import java.io.IOException;

public class EditWorldNameDialog extends Dialog implements View.OnClickListener {

    private World world;
    private String saveDir;
    private WorldManagerUI worldManagerUI;

    private EditText editText;
    private ProgressBar progressBar;
    private Button positive;
    private Button negative;

    public EditWorldNameDialog(@NonNull Context context, World world, String saveDir, WorldManagerUI worldManagerUI) {
        super(context);
        this.world = world;
        this.saveDir = saveDir;
        this.worldManagerUI = worldManagerUI;
        setContentView(R.layout.dialog_edit_world_name);
        setCancelable(false);
        init();
    }

    private void init() {
        editText = findViewById(R.id.world_name);
        progressBar = findViewById(R.id.progress);
        positive = findViewById(R.id.install);
        negative = findViewById(R.id.cancel);
        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        editText.setText(world.getWorldName());
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            if (!editText.getText().toString().equals("") && !editText.getText().toString().contains("/")) {
                Handler handler = new Handler();
                positive.setEnabled(false);
                negative.setEnabled(false);
                positive.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                new Thread(() -> {
                    try {
                        world.install(new File(saveDir).toPath(), editText.getText().toString());
                        handler.post(() -> {
                            dismiss();
                            worldManagerUI.refresh(worldManagerUI.versionName);
                        });
                    } catch (IOException e) {
                        handler.post(() -> {
                            Toast.makeText(getContext(),e.toString(),Toast.LENGTH_SHORT).show();
                        });
                        e.printStackTrace();
                    }
                    handler.post(() -> {
                        positive.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        positive.setEnabled(true);
                        negative.setEnabled(true);
                    });
                }).start();
            }
        }
        if (view == negative) {
            dismiss();
        }
    }
}
