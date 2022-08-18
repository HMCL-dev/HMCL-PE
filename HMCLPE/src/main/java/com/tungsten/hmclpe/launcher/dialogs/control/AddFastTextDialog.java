package com.tungsten.hmclpe.launcher.dialogs.control;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.setting.SettingUtils;

import java.util.ArrayList;

public class AddFastTextDialog extends Dialog implements View.OnClickListener {

    private OnFastTextAddListener onFastTextAddListener;

    private EditText editText;
    private Button positive;
    private Button negative;

    public AddFastTextDialog(@NonNull Context context,OnFastTextAddListener onFastTextAddListener) {
        super(context);
        this.onFastTextAddListener = onFastTextAddListener;
        setContentView(R.layout.dialog_add_fast_text);
        setCancelable(false);
        init();
    }

    private void init() {
        editText = findViewById(R.id.edit_fast_text);
        positive = findViewById(R.id.add_fast_text);
        negative = findViewById(R.id.exit);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            ArrayList<String> fastTexts = SettingUtils.getFastList();
            if (editText.getText().toString().equals("")) {
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_add_fast_text_empty),Toast.LENGTH_SHORT).show();
            }
            else if (fastTexts.contains(editText.getText().toString())) {
                Toast.makeText(getContext(),getContext().getString(R.string.dialog_add_fast_text_exist),Toast.LENGTH_SHORT).show();
            }
            else {
                onFastTextAddListener.onFastTextAdd(editText.getText().toString());
                dismiss();
            }
        }
        if (view == negative) {
            dismiss();
        }
    }

    public interface OnFastTextAddListener{
        void onFastTextAdd(String fastText);
    }
}
