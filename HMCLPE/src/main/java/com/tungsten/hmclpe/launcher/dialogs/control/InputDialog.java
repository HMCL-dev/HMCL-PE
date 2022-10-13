package com.tungsten.hmclpe.launcher.dialogs.control;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.InputBridge;
import com.tungsten.hmclpe.control.MenuHelper;
import com.tungsten.hmclpe.launcher.list.local.controller.FastTextAdapter;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;

import net.kdt.pojavlaunch.keyboard.LwjglGlfwKeycode;

import java.util.ArrayList;

public class InputDialog extends Dialog implements View.OnClickListener, TextWatcher {

    private MenuHelper menuHelper;

    private ListView listView;

    public EditText editText;

    private Button addText;
    private Button clearText;
    private Button send;
    private Button negative;

    public InputDialog(@NonNull Context context, MenuHelper menuHelper) {
        super(context);
        this.menuHelper = menuHelper;
        setContentView(R.layout.dialog_input);
        setCanceledOnTouchOutside(false);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        init();
    }

    private void init() {
        listView = findViewById(R.id.fast_text_list);

        editText = findViewById(R.id.edit_input_text);

        addText = findViewById(R.id.add_fast_text);
        clearText = findViewById(R.id.clear_input_text);
        send = findViewById(R.id.send_text);
        negative = findViewById(R.id.exit);

        addText.setOnClickListener(this);
        clearText.setOnClickListener(this);
        send.setOnClickListener(this);
        negative.setOnClickListener(this);

        editText.setText(">");
        editText.addTextChangedListener(this);

        refreshList();
    }

    private void refreshList(){
        FastTextAdapter adapter = new FastTextAdapter(getContext(),SettingUtils.getFastList(),this);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view == addText) {
            AddFastTextDialog dialog = new AddFastTextDialog(getContext(), fastText -> {
                ArrayList<String> list = SettingUtils.getFastList();
                list.add(fastText);
                SettingUtils.saveFastText(list);
                refreshList();
            });
            dialog.show();
        }
        if (view == clearText) {
            editText.setText(">");
        }
        if (view == send) {
            if (menuHelper.gameCursorMode == 0) {
                for(int i = 1; i < editText.getText().toString().length(); i++){
                    InputBridge.sendKeyChar(menuHelper.launcher,editText.getText().toString().charAt(i));
                }
                dismiss();
            }
            else {
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_T, true);
                InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_T, false);
                new Handler().postDelayed(() -> {
                    for(int i = 1; i < editText.getText().toString().length(); i++){
                        InputBridge.sendKeyChar(menuHelper.launcher,editText.getText().toString().charAt(i));
                    }
                    InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_ENTER, true);
                    InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_ENTER, false);
                    dismiss();
                },50);
            }
        }
        if (view == negative) {
            dismiss();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        String newText = editText.getText().toString();
        if (newText.length() < 1){
            InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_BACKSPACE, true);
            InputBridge.sendEvent(menuHelper.launcher, LwjglGlfwKeycode.GLFW_KEY_BACKSPACE, false);
            editText.setText(">");
            editText.setSelection(1);
        }
    }
}
