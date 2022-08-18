package com.tungsten.hmclpe.launcher.dialogs.control;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.view.KeyCodeView;

import java.util.ArrayList;

public class SelectKeycodeDialog extends Dialog implements KeyCodeView.OnKeyCodeChangeListener, View.OnClickListener {

    private ArrayList<Integer> list;
    private OnKeyCodesChangeListener onKeyCodesChangeListener;

    private Button positive;

    private LinearLayout keyboard;
    private LinearLayout mouse;

    public SelectKeycodeDialog(@NonNull Context context, ArrayList<Integer> list,OnKeyCodesChangeListener onKeyCodesChangeListener) {
        super(context);
        this.list = list;
        this.onKeyCodesChangeListener = onKeyCodesChangeListener;
        setContentView(R.layout.dialog_select_keycode);
        setCancelable(false);
        init();
    }

    private void init(){
        positive = findViewById(R.id.exit);
        positive.setOnClickListener(this);

        keyboard = findViewById(R.id.keyboard);
        mouse = findViewById(R.id.mouse);

        initializeAllButton(keyboard);
        initializeAllButton(mouse);
    }

    private void initializeAllButton(ViewGroup viewGroup){
        for (int i = 0;i < viewGroup.getChildCount();i++) {
            if (viewGroup.getChildAt(i) instanceof KeyCodeView) {
                ((KeyCodeView) viewGroup.getChildAt(i)).setOnKeyCodeChangeListener(this);
                ((KeyCodeView) viewGroup.getChildAt(i)).checkSelection(list);
            }
            else if (viewGroup.getChildAt(i) instanceof ViewGroup) {
                initializeAllButton((ViewGroup) viewGroup.getChildAt(i));
            }
        }
    }

    @Override
    public void onKeyCodeAdd(int keyCode) {
        list.add(keyCode);
    }

    @Override
    public void onKeyCodeRemove(int keyCode) {
        for (int i = 0;i < list.size();i++) {
            if (list.get(i) == keyCode) {
                list.remove(i);
            }
        }
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            onKeyCodesChangeListener.onKeyCodesChange(list);
            dismiss();
        }
    }

    public interface OnKeyCodesChangeListener{
        void onKeyCodesChange(ArrayList<Integer> list);
    }
}
