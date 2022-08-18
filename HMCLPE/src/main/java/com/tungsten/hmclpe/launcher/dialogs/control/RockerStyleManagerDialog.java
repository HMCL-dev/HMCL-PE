package com.tungsten.hmclpe.launcher.dialogs.control;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.bean.rocker.RockerStyle;
import com.tungsten.hmclpe.launcher.list.local.controller.RockerStyleAdapter;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;

import java.util.ArrayList;

public class RockerStyleManagerDialog extends Dialog implements View.OnClickListener {

    private OnStyleListChangeListener onStyleListChangeListener;

    private ListView listView;

    private Button create;
    private Button positive;

    public RockerStyleManagerDialog(@NonNull Context context, OnStyleListChangeListener onStyleListChangeListener) {
        super(context);
        this.onStyleListChangeListener = onStyleListChangeListener;
        setContentView(R.layout.dialog_manage_rocker_style);
        setCancelable(false);
        init();
    }

    private void init(){
        listView = findViewById(R.id.rocker_style_list);

        create = findViewById(R.id.create_rocker_style);
        positive = findViewById(R.id.exit);
        create.setOnClickListener(this);
        positive.setOnClickListener(this);

        refreshStyleList();
    }

    public void refreshStyleList(){
        ArrayList<RockerStyle> styles = SettingUtils.getRockerStyleList();
        RockerStyleAdapter adapter = new RockerStyleAdapter(getContext(),styles,this);
        listView.setAdapter(adapter);
        onStyleListChangeListener.onStyleListChange();
    }

    @Override
    public void onClick(View view) {
        if (view == create){
            CreateRockerStyleDialog dialog = new CreateRockerStyleDialog(getContext(), SettingUtils.getRockerStyleList(), new CreateRockerStyleDialog.OnRockerStyleCreateListener() {
                @Override
                public void onRockerStyleCreate(RockerStyle rockerStyle) {
                    ArrayList<RockerStyle> styles = SettingUtils.getRockerStyleList();
                    styles.add(rockerStyle);
                    SettingUtils.saveRockerStyle(styles);
                    refreshStyleList();
                }
            });
            dialog.show();
        }
        if (view == positive){
            dismiss();
        }
    }

    public interface OnStyleListChangeListener{
        void onStyleListChange();
    }

}
