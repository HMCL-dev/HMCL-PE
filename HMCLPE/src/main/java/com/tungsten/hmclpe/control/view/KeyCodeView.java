package com.tungsten.hmclpe.control.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.tungsten.hmclpe.R;

import java.util.ArrayList;

public class KeyCodeView extends androidx.appcompat.widget.AppCompatButton {

    private Integer keyCode;

    private OnKeyCodeChangeListener onKeyCodeChangeListener;

    private boolean selected = false;

    private long downTime;
    private float initialX;
    private float initialY;

    public KeyCodeView(@NonNull Context context) {
        super(context);
    }

    public KeyCodeView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        @SuppressLint("Recycle") TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.KeyCodeView);
        keyCode = typedArray.getInteger(R.styleable.KeyCodeView_keyCode,-1);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downTime = System.currentTimeMillis();
                initialX = event.getX();
                initialY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (Math.abs(event.getX() - initialX) <= 10 && Math.abs(event.getY() - initialY) <= 10 && System.currentTimeMillis() - downTime <= 200) {
                    if (selected) {
                        this.setBackground(getContext().getDrawable(R.drawable.launcher_button_normal));
                        if (onKeyCodeChangeListener != null){
                            onKeyCodeChangeListener.onKeyCodeRemove(keyCode);
                        }
                        selected = false;
                    }
                    else {
                        this.setBackground(getContext().getDrawable(R.drawable.launcher_button_selected));
                        if (onKeyCodeChangeListener != null){
                            onKeyCodeChangeListener.onKeyCodeAdd(keyCode);
                        }
                        selected = true;
                    }
                }
                break;
        }
        return true;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void checkSelection(ArrayList<Integer> list){
        if (list.contains(keyCode)) {
            this.setBackground(getContext().getDrawable(R.drawable.launcher_button_selected));
            selected = true;
        }
        else {
            this.setBackground(getContext().getDrawable(R.drawable.launcher_button_normal));
            selected = false;
        }
    }

    public void setOnKeyCodeChangeListener(OnKeyCodeChangeListener onKeyCodeChangeListener){
        this.onKeyCodeChangeListener = onKeyCodeChangeListener;
    }

    public interface OnKeyCodeChangeListener{
        void onKeyCodeAdd(int keyCode);
        void onKeyCodeRemove(int keyCode);
    }
}
