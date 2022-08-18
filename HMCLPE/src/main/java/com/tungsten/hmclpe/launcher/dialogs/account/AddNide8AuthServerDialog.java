package com.tungsten.hmclpe.launcher.dialogs.account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

import java.io.IOException;

public class AddNide8AuthServerDialog extends Dialog implements View.OnClickListener {

    private OnNide8AuthServerAddListener onNide8AuthServerAddListener;

    private LinearLayout layoutPri;
    private EditText editVerifyServer;
    private Button cancel;
    private Button next;

    private LinearLayout layoutSec;
    private TextView url;
    private TextView name;
    private Button cancelSec;
    private Button back;
    private Button positive;
    private ProgressBar progressBar;

    private AuthlibInjectorServer authlibInjectorServer;

    public static final String NIDE_8_AUTH_SERVER = "https://auth.mc-user.com:233/";

    public AddNide8AuthServerDialog(@NonNull Context context,OnNide8AuthServerAddListener onNide8AuthServerAddListener) {
        super(context);
        this.onNide8AuthServerAddListener = onNide8AuthServerAddListener;
        setContentView(R.layout.dialog_add_nide8auth_server);
        setCancelable(false);
        init();
    }

    private void init(){
        layoutPri = findViewById(R.id.add_verify_server_pri);
        editVerifyServer = findViewById(R.id.edit_verify_server);
        cancel = findViewById(R.id.cancel);
        next = findViewById(R.id.next);

        layoutSec = findViewById(R.id.add_verify_server_sec);
        url = findViewById(R.id.server_url);
        name = findViewById(R.id.server_name);
        cancelSec = findViewById(R.id.cancel_sec);
        back = findViewById(R.id.back);
        positive = findViewById(R.id.add_verify_server);
        progressBar = findViewById(R.id.verify_progress);

        cancel.setOnClickListener(this);
        next.setOnClickListener(this);

        cancelSec.setOnClickListener(this);
        back.setOnClickListener(this);
        positive.setOnClickListener(this);
    }

    private void next(AuthlibInjectorServer authlibInjectorServer){
        CustomAnimationUtils.hideViewToLeft(layoutPri,getContext(),false);
        CustomAnimationUtils.showViewFromRight(layoutSec,getContext(),true);
        url.setText(authlibInjectorServer.getUrl());
        name.setText(authlibInjectorServer.getName());
        this.authlibInjectorServer = authlibInjectorServer;
    }

    private void back(){
        CustomAnimationUtils.hideViewToRight(layoutSec,getContext(),false);
        CustomAnimationUtils.showViewFromLeft(layoutPri,getContext(),true);
    }

    @Override
    public void onClick(View view) {
        if (view == cancel || view == cancelSec){
            this.dismiss();
        }
        if (view == next){
            if (editVerifyServer.getText().toString().length() != 32){
                Toast.makeText(getContext(), getContext().getString(R.string.dialog_add_verify_server_nide_empty), Toast.LENGTH_SHORT).show();
            }
            else {
                new Thread(() -> {
                    loginHandler.post(() -> {
                        progressBar.setVisibility(View.VISIBLE);
                        next.setVisibility(View.GONE);
                        cancel.setEnabled(false);
                    });
                    try {
                        AuthlibInjectorServer authlibInjectorServer = AuthlibInjectorServer.locateServer(NIDE_8_AUTH_SERVER + editVerifyServer.getText().toString());
                        loginHandler.post(() -> next(authlibInjectorServer));
                    } catch (IOException e) {
                        e.printStackTrace();
                        loginHandler.post(() -> Toast.makeText(getContext(), getContext().getString(R.string.dialog_add_verify_server_nide_invalid), Toast.LENGTH_SHORT).show());
                    }
                    loginHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        next.setVisibility(View.VISIBLE);
                        cancel.setEnabled(true);
                    });
                }).start();
            }
        }
        if (view == back){
            back();
        }
        if (view == positive){
            if (authlibInjectorServer != null){
                onNide8AuthServerAddListener.onServerAdd(authlibInjectorServer);
                this.dismiss();
            }
        }
    }

    @SuppressLint("HandlerLeak")
    public final Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    public interface OnNide8AuthServerAddListener{
        void onServerAdd(AuthlibInjectorServer authlibInjectorServer);
    }

}
