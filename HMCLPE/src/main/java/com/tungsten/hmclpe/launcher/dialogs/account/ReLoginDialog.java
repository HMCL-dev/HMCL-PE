package com.tungsten.hmclpe.launcher.dialogs.account;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.auth.AuthInfo;
import com.tungsten.hmclpe.auth.AuthenticationException;
import com.tungsten.hmclpe.auth.yggdrasil.GameProfile;
import com.tungsten.hmclpe.auth.yggdrasil.Texture;
import com.tungsten.hmclpe.auth.yggdrasil.TextureType;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilService;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilSession;
import com.tungsten.hmclpe.skin.utils.Avatar;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.UUID;

public class ReLoginDialog extends Dialog implements View.OnClickListener {

    private String email;
    private YggdrasilService yggdrasilService;
    private Account account;
    private ReloginCallback callback;

    private TextView emailText;
    private EditText editPassword;
    private Button positive;
    private Button negative;
    private ProgressBar progressBar;

    public ReLoginDialog(@NonNull Context context,String email,YggdrasilService yggdrasilService,Account account,ReloginCallback callback) {
        super(context);
        this.email = email;
        this.yggdrasilService = yggdrasilService;
        this.account = account;
        this.callback = callback;
        setContentView(R.layout.dialog_relogin);
        setCancelable(false);
        init();
    }

    private void init(){
        emailText = findViewById(R.id.relogin_email);
        editPassword = findViewById(R.id.edit_password);
        positive = findViewById(R.id.relogin);
        negative = findViewById(R.id.cancel_relogin);
        progressBar = findViewById(R.id.login_progress);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        emailText.setText(email);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            String password = editPassword.getText().toString();
            new Thread(() -> {
                loginHandler.post(() -> {
                    progressBar.setVisibility(View.VISIBLE);
                    positive.setVisibility(View.GONE);
                    negative.setEnabled(false);
                });
                try {
                    YggdrasilSession yggdrasilSession = yggdrasilService.authenticate(email,password, UUID.randomUUID().toString());
                    if (yggdrasilSession.getAvailableProfiles().size() > 1) {
                        for (GameProfile gameProfile : yggdrasilSession.getAvailableProfiles()) {
                            if (gameProfile.getName().equals(account.auth_player_name)) {
                                Bitmap skin;
                                Map<TextureType, Texture> map = YggdrasilService.getTextures(yggdrasilService.getCompleteGameProfile(gameProfile.getId()).get()).get();
                                Texture texture = map.get(TextureType.SKIN);
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
                                loginHandler.post(() -> {
                                    String skinTexture = Avatar.bitmapToString(skin);
                                    Account newAccount = new Account(account.loginType,
                                            email,
                                            password,
                                            account.user_type,
                                            account.auth_session,
                                            gameProfile.getName(),
                                            gameProfile.getId().toString(),
                                            yggdrasilSession.getAccessToken(),
                                            yggdrasilSession.getClientToken(),
                                            account.refresh_token,
                                            account.loginServer,
                                            skinTexture);
                                    callback.onRelogin(newAccount);
                                    dismiss();
                                });
                                break;
                            }
                        }
                    }
                    else {
                        AuthInfo authInfo = yggdrasilSession.toAuthInfo();
                        Map<TextureType, Texture> map = YggdrasilService.getTextures(yggdrasilService.getCompleteGameProfile(authInfo.getUUID()).get()).get();
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
                        loginHandler.post(() -> {
                            String skinTexture = Avatar.bitmapToString(skin);
                            Account newAccount = new Account(4,
                                    email,
                                    password,
                                    account.user_type,
                                    account.auth_session,
                                    yggdrasilSession.getSelectedProfile().getName(),
                                    authInfo.getUUID().toString(),
                                    authInfo.getAccessToken(),
                                    yggdrasilSession.getClientToken(),
                                    account.refresh_token,
                                    account.loginServer,
                                    skinTexture);
                            callback.onRelogin(newAccount);
                            dismiss();
                        });
                    }
                } catch (AuthenticationException | IOException e) {
                    e.printStackTrace();
                    loginHandler.sendEmptyMessage(1);
                }
                loginHandler.post(() -> {
                    progressBar.setVisibility(View.GONE);
                    positive.setVisibility(View.VISIBLE);
                    negative.setEnabled(true);
                });
            }).start();
        }
        if (view == negative) {
            callback.onCancel();
            dismiss();
        }
    }

    @SuppressLint("HandlerLeak")
    public final Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                Toast.makeText(getContext(), getContext().getString(R.string.dialog_add_authlib_injector_account_failed), Toast.LENGTH_SHORT).show();
            }
        }
    };

    public interface ReloginCallback{
        void onRelogin(Account account);
        void onCancel();
    }
}
