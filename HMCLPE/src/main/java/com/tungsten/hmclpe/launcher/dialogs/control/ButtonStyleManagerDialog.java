package com.tungsten.hmclpe.launcher.dialogs.control;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.bean.button.ButtonStyle;
import com.tungsten.hmclpe.launcher.list.local.controller.ButtonStyleAdapter;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;

import java.util.ArrayList;

public class ButtonStyleManagerDialog extends Dialog implements View.OnClickListener {

    private OnStyleListChangeListener onStyleListChangeListener;

    private ListView listView;

    private Button create;
    private Button positive;

    public ButtonStyleManagerDialog(@NonNull Context context,OnStyleListChangeListener onStyleListChangeListener) {
        super(context);
        this.onStyleListChangeListener = onStyleListChangeListener;
        setContentView(R.layout.dialog_manage_button_style);
        setCancelable(false);
        init();
    }

    private void init(){
        listView = findViewById(R.id.button_style_list);

        create = findViewById(R.id.create_button_style);
        positive = findViewById(R.id.exit);
        create.setOnClickListener(this);
        positive.setOnClickListener(this);

        refreshStyleList();
    }

    public void refreshStyleList(){
        ArrayList<ButtonStyle> styles = SettingUtils.getButtonStyleList();
        ButtonStyleAdapter adapter = new ButtonStyleAdapter(getContext(),styles,this);
        listView.setAdapter(adapter);
        onStyleListChangeListener.onStyleListChange();
    }

    @Override
    public void onClick(View view) {
        if (view == create){
            CreateButtonStyleDialog dialog = new CreateButtonStyleDialog(getContext(), SettingUtils.getButtonStyleList(), new CreateButtonStyleDialog.OnButtonStyleCreateListener() {
                @Override
                public void onButtonStyleCreate(ButtonStyle buttonStyle) {
                    ArrayList<ButtonStyle> styles = SettingUtils.getButtonStyleList();
                    styles.add(buttonStyle);
                    SettingUtils.saveButtonStyle(styles);
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
