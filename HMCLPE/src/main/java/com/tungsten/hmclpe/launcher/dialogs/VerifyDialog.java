package com.tungsten.hmclpe.launcher.dialogs;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.VerifyInterface;
import com.tungsten.hmclpe.utils.DigestUtils;

public class VerifyDialog extends Dialog implements View.OnClickListener {

    private MainActivity activity;
    private SharedPreferences.Editor editor;
    private VerifyInterface verifyInterface;

    private String code;

    private TextView textView;
    private EditText editText;
    private Button obtainPermission;
    private Button cancel;
    private Button copy;
    private Button verify;

    public VerifyDialog(@NonNull Context context, MainActivity activity, SharedPreferences.Editor editor, VerifyInterface verifyInterface) {
        super(context);
        this.activity = activity;
        this.editor = editor;
        this.verifyInterface = verifyInterface;
        setContentView(R.layout.dialog_verify);
        setCancelable(false);
        init();
    }

    private void init() {
        code = DigestUtils.getDeviceCode(getContext());

        textView = findViewById(R.id.oaid_text);
        editText = findViewById(R.id.edit_verify_code);
        obtainPermission = findViewById(R.id.obtain_permission);
        cancel = findViewById(R.id.cancel);
        copy = findViewById(R.id.copy_oaid);
        verify = findViewById(R.id.verify);

        textView.setText(getContext().getString(R.string.dialog_verify_msg).replace("%s", code));
        obtainPermission.setOnClickListener(this);
        cancel.setOnClickListener(this);
        copy.setOnClickListener(this);
        verify.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == obtainPermission) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(getContext().getString(R.string.dialog_obtain_permission_title));
            builder.setMessage(getContext().getString(R.string.dialog_obtain_permission_msg));
            builder.setPositiveButton(getContext().getString(R.string.dialog_obtain_permission_positive), (dialogInterface, i) -> {
                Uri uri = Uri.parse("https://afdian.net/@tungs");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                getContext().startActivity(intent);
            });
            builder.setNegativeButton(getContext().getString(R.string.dialog_obtain_permission_negative), (dialogInterface, i) -> {});
            builder.create().show();
        }
        if (view == cancel) {
            verifyInterface.onCancel();
            dismiss();
        }
        if (view == copy) {
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText(null, code);
            clip.setPrimaryClip(data);
            Toast.makeText(getContext(), getContext().getString(R.string.dialog_verify_copy_success), Toast.LENGTH_SHORT).show();
        }
        if (view == verify) {
            if (activity.isValid(editText.getText().toString())){
                editor.putString("code",editText.getText().toString());
                editor.putBoolean("verified",true);
                editor.commit();
                Toast.makeText(getContext(), getContext().getString(R.string.dialog_verify_verify_success), Toast.LENGTH_SHORT).show();
                dismiss();
                verifyInterface.onSuccess();
            }
            else {
                Toast.makeText(getContext(), getContext().getString(R.string.dialog_verify_verify_fail), Toast.LENGTH_SHORT).show();
            }
        }
    }

}
