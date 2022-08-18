package com.tungsten.hmclpe.launcher.dialogs.control;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.list.local.controller.ControlPattern;
import com.tungsten.hmclpe.manifest.info.AppInfo;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;

import java.util.ArrayList;

public class CreateControlPatternDialog extends Dialog implements View.OnClickListener {

    private Activity activity;
    private OnPatternCreateListener onPatternCreateListener;

    private EditText editName;
    private EditText editAuthor;
    private EditText editVersion;
    private EditText editDescribe;

    private Button positive;
    private Button negative;

    public CreateControlPatternDialog(@NonNull Context context, Activity activity, OnPatternCreateListener onPatternCreateListener) {
        super(context);
        this.activity = activity;
        setContentView(R.layout.dialog_create_control_pattern);
        this.onPatternCreateListener = onPatternCreateListener;
        setCancelable(false);
        init();
    }

    private void init(){
        editName = findViewById(R.id.edit_pattern_name);
        editAuthor = findViewById(R.id.edit_pattern_author);
        editVersion = findViewById(R.id.edit_pattern_version);
        editDescribe = findViewById(R.id.edit_pattern_describe);

        positive = findViewById(R.id.create_pattern);
        negative = findViewById(R.id.exit);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == positive){
            ArrayList<ControlPattern> list = SettingUtils.getControlPatternList();
            ArrayList<String> names = new ArrayList<>();
            for (ControlPattern controlPattern : list){
                names.add(controlPattern.name);
            }
            boolean exist = names.contains(editName.getText().toString());
            if (editName.getText().toString().equals("") || editName.getText().toString().contains("/")){
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_create_control_pattern_warn),Toast.LENGTH_SHORT).show();
            }
            else if (exist){
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_create_control_pattern_warn_exist),Toast.LENGTH_SHORT).show();
            }
            else {
                ControlPattern controlPattern = new ControlPattern(editName.getText().toString(),
                        editAuthor.getText().toString(),
                        editVersion.getText().toString(),
                        editDescribe.getText().toString(),
                        AppInfo.CONTROL_VERSION_CODE);
                onPatternCreateListener.OnPatternCreate(controlPattern);
                dismiss();
            }
        }
        if (view == negative){
            dismiss();
        }
    }

    public interface OnPatternCreateListener{
        void OnPatternCreate(ControlPattern controlPattern);
    }
}
