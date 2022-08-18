package com.tungsten.hmclpe.launcher.dialogs.account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.auth.microsoft.MicrosoftLoginActivity;
import com.tungsten.hmclpe.auth.microsoft.Msa;
import com.tungsten.hmclpe.auth.yggdrasil.Texture;
import com.tungsten.hmclpe.auth.yggdrasil.TextureType;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.skin.utils.Avatar;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class AddMicrosoftAccountDialog extends Dialog implements View.OnClickListener {

    private MainActivity activity;
    private OnMicrosoftAccountAddListener onMicrosoftAccountAddListener;

    private TextView accountSettingLink;
    private TextView helpLink;
    private TextView purchaseLink;

    private Button login;
    private Button cancel;
    private ProgressBar progressBar;

    private Account account;

    public AddMicrosoftAccountDialog(@NonNull Context context, MainActivity activity,OnMicrosoftAccountAddListener onMicrosoftAccountAddListener) {
        super(context);
        this.activity = activity;
        this.onMicrosoftAccountAddListener = onMicrosoftAccountAddListener;
        setContentView(R.layout.dialog_add_microsoft_account);
        setCancelable(false);
        init();
    }

    private void init(){
        accountSettingLink = findViewById(R.id.setting_link);
        helpLink = findViewById(R.id.help_link);
        purchaseLink = findViewById(R.id.purchase_link);

        accountSettingLink.setMovementMethod(LinkMovementMethod.getInstance());
        helpLink.setMovementMethod(LinkMovementMethod.getInstance());
        purchaseLink.setMovementMethod(LinkMovementMethod.getInstance());

        login = findViewById(R.id.login_microsoft);
        cancel = findViewById(R.id.cancel_login_microsoft);
        progressBar = findViewById(R.id.login_progress);

        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == login){
            Intent i = new Intent(getContext(), MicrosoftLoginActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("fullscreen",activity.launcherSetting.fullscreen);
            i.putExtras(bundle);
            activity.startActivityForResult(i,MicrosoftLoginActivity.AUTHENTICATE_MICROSOFT_REQUEST);
        }
        if (v == cancel){
            this.dismiss();
        }
    }

    public void login(Intent intent){
        Uri data = null;
        if (intent != null){
            data = intent.getData();
        }
        if (data != null && data.getScheme().equals("ms-xal-00000000402b5328") && data.getHost().equals("auth")) {
            String error = data.getQueryParameter("error");
            String error_description = data.getQueryParameter("error_description");
            if (error != null) {
                if (!error_description.startsWith("The user has denied access to the scope requested by the client application")) {
                    Toast.makeText(getContext(), "Error: " + error + ": " + error_description, Toast.LENGTH_SHORT).show();
                }
            }
            else {
                String code = data.getQueryParameter("code");
                new Thread(() -> {
                    handler.post(() -> {
                        progressBar.setVisibility(View.VISIBLE);
                        login.setVisibility(View.GONE);
                        cancel.setEnabled(false);
                    });
                    try {
                        Msa msa = new Msa(false, code);
                        if (msa.doesOwnGame) {
                            Msa.MinecraftProfileResponse minecraftProfile = Msa.getMinecraftProfile(msa.tokenType, msa.mcToken);
                            Map<TextureType, Texture> map = Msa.getTextures(minecraftProfile).get();
                            Texture texture = map.get(TextureType.SKIN);
                            Bitmap skin;
                            if (texture == null) {
                                AssetManager manager = getContext().getAssets();
                                InputStream inputStream;
                                inputStream = manager.open("img/alex.png");
                                skin = BitmapFactory.decodeStream(inputStream);
                            }
                            else {
                                String u = texture.getUrl();
                                if (!u.startsWith("https")){
                                    u = u.replaceFirst("http","https");
                                }
                                URL url = new URL(u);
                                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                                httpURLConnection.setDoInput(true);
                                httpURLConnection.connect();
                                InputStream inputStream = httpURLConnection.getInputStream();
                                skin = BitmapFactory.decodeStream(inputStream);
                            }
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    String skinTexture = Avatar.bitmapToString(skin);
                                    account = new Account(3,
                                            "",
                                            "",
                                            "mojang",
                                            "0",
                                            msa.mcName,
                                            msa.mcUuid,
                                            msa.mcToken,
                                            "00000000-0000-0000-0000-000000000000",
                                            msa.msRefreshToken,
                                            "",
                                            skinTexture);
                                    onMicrosoftAccountAddListener.onPositive(account);
                                    dismiss();
                                }
                            });
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    handler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        cancel.setEnabled(true);
                    });
                }).start();
            }
        }
    }

    public interface OnMicrosoftAccountAddListener{
        void onPositive(Account account);
    }

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };
}
