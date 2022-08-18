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
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.material.tabs.TabLayout;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.auth.AuthInfo;
import com.tungsten.hmclpe.auth.AuthenticationException;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;
import com.tungsten.hmclpe.auth.microsoft.MicrosoftLoginActivity;
import com.tungsten.hmclpe.auth.microsoft.Msa;
import com.tungsten.hmclpe.auth.yggdrasil.GameProfile;
import com.tungsten.hmclpe.auth.yggdrasil.Texture;
import com.tungsten.hmclpe.auth.yggdrasil.TextureType;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilService;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilSession;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.account.server.AuthlibInjectorServerSpinnerAdapter;
import com.tungsten.hmclpe.launcher.setting.InitializeSetting;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.skin.utils.Avatar;
import com.tungsten.hmclpe.utils.gson.GsonUtils;
import com.tungsten.hmclpe.utils.gson.UUIDTypeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddAccountDialog extends Dialog implements View.OnClickListener, TabLayout.OnTabSelectedListener, AdapterView.OnItemSelectedListener {

    private MainActivity activity;
    private AddAccountCallback callback;

    private TabLayout tabLayout;

    private LinearLayout offlineLayout;
    private LinearLayout microsoftLayout;
    private LinearLayout externalLayout;

    private Button login;
    private Button cancel;
    private ProgressBar progressBar;

    private EditText editName;
    private EditText editUUID;
    private TextView purchaseLink;
    private LinearLayout showAdvanceSetting;
    private ImageView spinView;
    private LinearLayout editUUIDLayout;
    private LinearLayout hintLayout;

    private TextView accountSettingLink;
    private TextView helpLink;
    private TextView mPurchaseLink;

    private Spinner editServer;
    private TextView signUp;
    private ImageButton addServer;
    private EditText editEmail;
    private EditText editPassword;
    private AuthlibInjectorServerSpinnerAdapter serverListAdapter;
    private String signUpUrl;
    private AuthlibInjectorServer authlibInjectorServer;

    private Account account;

    public static final String NIDE_8_AUTH_SIGN_UP_PAGE = "https://login.mc-user.com:233/";

    public AddAccountDialog(@NonNull Context context,MainActivity activity,AddAccountCallback callback) {
        super(context);
        this.activity = activity;
        this.callback = callback;
        setContentView(R.layout.dialog_add_account);
        setCancelable(false);
        init();
    }

    private void init() {
        tabLayout = findViewById(R.id.add_account_tab);
        offlineLayout = findViewById(R.id.offline_layout);
        microsoftLayout = findViewById(R.id.microsoft_layout);
        externalLayout = findViewById(R.id.external_layout);
        tabLayout.addOnTabSelectedListener(this);
        tabLayout.selectTab(tabLayout.getTabAt(0));

        login = findViewById(R.id.login);
        cancel = findViewById(R.id.cancel_login);
        progressBar = findViewById(R.id.login_progress);
        login.setOnClickListener(this);
        cancel.setOnClickListener(this);

        editName = findViewById(R.id.edit_user_name);
        editUUID = findViewById(R.id.edit_uuid);

        purchaseLink = findViewById(R.id.purchase_link);
        purchaseLink.setMovementMethod(LinkMovementMethod.getInstance());
        showAdvanceSetting = findViewById(R.id.show_advance_setting);
        showAdvanceSetting.setOnClickListener(this);
        spinView = findViewById(R.id.spin_view);
        editUUIDLayout = findViewById(R.id.edit_uuid_layout);
        hintLayout = findViewById(R.id.hint_layout);

        accountSettingLink = findViewById(R.id.setting_link);
        helpLink = findViewById(R.id.help_link);
        mPurchaseLink = findViewById(R.id.m_purchase_link);
        accountSettingLink.setMovementMethod(LinkMovementMethod.getInstance());
        helpLink.setMovementMethod(LinkMovementMethod.getInstance());
        mPurchaseLink.setMovementMethod(LinkMovementMethod.getInstance());

        editServer = findViewById(R.id.edit_server);
        signUp = findViewById(R.id.sign_up);
        addServer = findViewById(R.id.add_server);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        signUp.setOnClickListener(this);
        addServer.setOnClickListener(this);
        ArrayList<AuthlibInjectorServer> authlibInjectorServers = InitializeSetting.initializeAuthlibInjectorServer(getContext());
        serverListAdapter = new AuthlibInjectorServerSpinnerAdapter(getContext(), authlibInjectorServers);
        editServer.setAdapter(serverListAdapter);
        editServer.setOnItemSelectedListener(this);
        if (authlibInjectorServers.size() == 0) {
            signUp.setVisibility(View.GONE);
        }
        else {
            editServer.setSelection(0);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == login) {
            if (offlineLayout.getVisibility() == View.VISIBLE) {
                if (editName.getText().toString().equals("")){
                    Toast.makeText(getContext(),getContext().getString(R.string.dialog_add_offline_account_empty_warn),Toast.LENGTH_SHORT).show();
                }
                else {
                    AssetManager manager = getContext().getAssets();
                    InputStream inputStream;
                    Bitmap bitmap;
                    String skinTexture = "";
                    try {
                        inputStream = manager.open("img/alex.png");
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        skinTexture = Avatar.bitmapToString(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Account account = new Account(1,
                            "",
                            "",
                            "mojang",
                            "0",
                            editName.getText().toString(),
                            editUUID.getText().toString().equals("") ? UUID.randomUUID().toString() : editUUID.getText().toString(),
                            UUIDTypeAdapter.fromUUID(UUID.randomUUID()),
                            "",
                            "",
                            "",
                            skinTexture);
                    callback.onAccountAdd(account);
                    this.dismiss();
                }
            }
            if (microsoftLayout.getVisibility() == View.VISIBLE) {
                Intent i = new Intent(getContext(), MicrosoftLoginActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("fullscreen",activity.launcherSetting.fullscreen);
                i.putExtras(bundle);
                activity.startActivityForResult(i,MicrosoftLoginActivity.AUTHENTICATE_MICROSOFT_REQUEST);
            }
            if (externalLayout.getVisibility() == View.VISIBLE) {
                if (authlibInjectorServer == null) {
                    Toast.makeText(getContext(), getContext().getString(R.string.dialog_add_authlib_injector_account_server_warn), Toast.LENGTH_SHORT).show();
                }
                else if (editEmail.getText().toString().equals("") || editPassword.getText().toString().equals("")){
                    Toast.makeText(getContext(), getContext().getString(R.string.dialog_add_authlib_injector_account_empty_warn), Toast.LENGTH_SHORT).show();
                }
                else {
                    String email = editEmail.getText().toString();
                    String password = editPassword.getText().toString();
                    boolean isNide = authlibInjectorServer.getUrl().startsWith(AddNide8AuthServerDialog.NIDE_8_AUTH_SERVER);
                    new Thread(() -> {
                        loginHandler.post(() -> {
                            progressBar.setVisibility(View.VISIBLE);
                            login.setVisibility(View.GONE);
                            cancel.setEnabled(false);
                            tabLayout.setEnabled(false);
                        });
                        YggdrasilService yggdrasilService = authlibInjectorServer.getYggdrasilService();
                        try {
                            YggdrasilSession yggdrasilSession = yggdrasilService.authenticate(email,password, UUID.randomUUID().toString());
                            if (yggdrasilSession.getAvailableProfiles().size() > 1) {
                                ArrayList<Bitmap> bitmaps = new ArrayList<>();
                                for (GameProfile gameProfile : yggdrasilSession.getAvailableProfiles()) {
                                    if (yggdrasilService.getCompleteGameProfile(gameProfile.getId()).isPresent() && YggdrasilService.getTextures(yggdrasilService.getCompleteGameProfile(gameProfile.getId()).get()).isPresent()) {
                                        Map<TextureType, Texture> map = YggdrasilService.getTextures(yggdrasilService.getCompleteGameProfile(gameProfile.getId()).get()).get();
                                        Texture texture = map.get(TextureType.SKIN);
                                        if (texture == null) {
                                            AssetManager manager = getContext().getAssets();
                                            InputStream inputStream;
                                            inputStream = manager.open("img/alex.png");
                                            Bitmap skin = BitmapFactory.decodeStream(inputStream);
                                            bitmaps.add(skin);
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
                                            Bitmap skin = BitmapFactory.decodeStream(inputStream);
                                            bitmaps.add(skin);
                                        }
                                    }
                                    else {
                                        AssetManager manager = getContext().getAssets();
                                        InputStream inputStream;
                                        inputStream = manager.open("img/alex.png");
                                        Bitmap skin = BitmapFactory.decodeStream(inputStream);
                                        bitmaps.add(skin);
                                    }
                                }
                                loginHandler.post(() -> {
                                    SelectProfileDialog dialog = new SelectProfileDialog(getContext(), yggdrasilService, yggdrasilSession, email, password, authlibInjectorServer.getUrl(), bitmaps, account -> {
                                        callback.onAccountAdd(account);
                                    }, isNide);
                                    dialog.show();
                                    dismiss();
                                });
                            }
                            else if (yggdrasilSession.getAvailableProfiles().size() == 1){
                                AuthInfo authInfo = yggdrasilSession.toAuthInfo();
                                Texture texture;
                                Bitmap skin;
                                if (yggdrasilService.getCompleteGameProfile(authInfo.getUUID()).isPresent() && YggdrasilService.getTextures(yggdrasilService.getCompleteGameProfile(authInfo.getUUID()).get()).isPresent()) {
                                    Map<TextureType, Texture> map = YggdrasilService.getTextures(yggdrasilService.getCompleteGameProfile(authInfo.getUUID()).get()).get();
                                    texture = map.get(TextureType.SKIN);
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
                                }
                                else {
                                    AssetManager manager = getContext().getAssets();
                                    InputStream inputStream;
                                    inputStream = manager.open("img/alex.png");
                                    skin = BitmapFactory.decodeStream(inputStream);
                                }
                                loginHandler.post(() -> {
                                    String skinTexture = Avatar.bitmapToString(skin);
                                    account = new Account(isNide ? 5 : 4,
                                            email,
                                            password,
                                            "mojang",
                                            "0",
                                            yggdrasilSession.getSelectedProfile().getName(),
                                            authInfo.getUUID().toString(),
                                            authInfo.getAccessToken(),
                                            yggdrasilSession.getClientToken(),
                                            "",
                                            authlibInjectorServer.getUrl(),
                                            skinTexture);
                                    callback.onAccountAdd(account);
                                    dismiss();
                                });
                            }
                            else {
                                loginHandler.post(() -> {
                                    Toast.makeText(getContext(), getContext().getString(R.string.dialog_add_authlib_injector_account_none), Toast.LENGTH_SHORT).show();
                                });
                            }
                        } catch (AuthenticationException | IOException e) {
                            e.printStackTrace();
                            loginHandler.post(() -> {
                                Toast.makeText(getContext(), getContext().getString(R.string.dialog_add_authlib_injector_account_failed), Toast.LENGTH_SHORT).show();
                            });
                        }
                        loginHandler.post(() -> {
                            progressBar.setVisibility(View.GONE);
                            login.setVisibility(View.VISIBLE);
                            cancel.setEnabled(true);
                            tabLayout.setEnabled(true);
                        });
                    }).start();
                }
            }
        }
        if (view == cancel) {
            dismiss();
            callback.onCancel();
        }

        if (view == showAdvanceSetting){
            if (editUUIDLayout.getVisibility() == View.GONE){
                editUUIDLayout.setVisibility(View.VISIBLE);
                hintLayout.setVisibility(View.VISIBLE);
                Animation animation = new RotateAnimation(0, 180, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(30);//设置动画持续时间
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatMode(Animation.REVERSE);//设置反方向执行
                animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                spinView.startAnimation(animation);
            }
            else {
                editUUIDLayout.setVisibility(View.GONE);
                hintLayout.setVisibility(View.GONE);
                Animation animation = new RotateAnimation(180, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                animation.setDuration(30);//设置动画持续时间
                animation.setInterpolator(new LinearInterpolator());
                animation.setRepeatMode(Animation.REVERSE);//设置反方向执行
                animation.setFillAfter(true);//动画执行完后是否停留在执行完的状态
                spinView.startAnimation(animation);
            }
        }

        if (view == signUp){
            Uri uri = Uri.parse(signUpUrl);
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
        if (view == addServer){
            SelectServerTypeDialog dialog = new SelectServerTypeDialog(getContext(), server -> {
                if (!activity.uiManager.accountUI.serverList.contains(server)){
                    activity.uiManager.accountUI.serverList.add(server);
                    activity.uiManager.accountUI.serverListAdapter.notifyDataSetChanged();
                    GsonUtils.saveServer(activity.uiManager.accountUI.serverList, AppManifest.ACCOUNT_DIR + "/authlib_injector_server.json");
                    ArrayList<AuthlibInjectorServer> authlibInjectorServers = InitializeSetting.initializeAuthlibInjectorServer(getContext());
                    serverListAdapter = new AuthlibInjectorServerSpinnerAdapter(getContext(), authlibInjectorServers);
                    editServer.setAdapter(serverListAdapter);
                }
            });
            dialog.show();
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
                    loginHandler.post(() -> {
                        progressBar.setVisibility(View.VISIBLE);
                        login.setVisibility(View.GONE);
                        cancel.setEnabled(false);
                        tabLayout.setEnabled(false);
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
                            loginHandler.post(new Runnable() {
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
                                    callback.onAccountAdd(account);
                                    dismiss();
                                }
                            });
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    loginHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        login.setVisibility(View.VISIBLE);
                        cancel.setEnabled(true);
                        tabLayout.setEnabled(true);
                    });
                }).start();
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == editServer){
            authlibInjectorServer = (AuthlibInjectorServer) serverListAdapter.getItem(i);
            signUpUrl = authlibInjectorServer.getLinks().get("register");
            if (authlibInjectorServer.getUrl().startsWith(AddNide8AuthServerDialog.NIDE_8_AUTH_SERVER)) {
                signUpUrl = NIDE_8_AUTH_SIGN_UP_PAGE + authlibInjectorServer.getUrl().substring(authlibInjectorServer.getUrl().length() - 33);
            }
            if (signUpUrl == null){
                signUp.setVisibility(View.GONE);
            }
            else {
                signUp.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @SuppressLint("HandlerLeak")
    public final Handler loginHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        if (Objects.requireNonNull(tab.getText()).toString().equals(getContext().getString(R.string.dialog_add_account_type_offline))) {
            offlineLayout.setVisibility(View.VISIBLE);
        }
        if (Objects.requireNonNull(tab.getText()).toString().equals(getContext().getString(R.string.dialog_add_account_type_microsoft))) {
            microsoftLayout.setVisibility(View.VISIBLE);
        }
        if (Objects.requireNonNull(tab.getText()).toString().equals(getContext().getString(R.string.dialog_add_account_type_external))) {
            externalLayout.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        if (Objects.requireNonNull(tab.getText()).toString().equals(getContext().getString(R.string.dialog_add_account_type_offline))) {
            offlineLayout.setVisibility(View.GONE);
        }
        if (Objects.requireNonNull(tab.getText()).toString().equals(getContext().getString(R.string.dialog_add_account_type_microsoft))) {
            microsoftLayout.setVisibility(View.GONE);
        }
        if (Objects.requireNonNull(tab.getText()).toString().equals(getContext().getString(R.string.dialog_add_account_type_external))) {
            externalLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        if (Objects.requireNonNull(tab.getText()).toString().equals(getContext().getString(R.string.dialog_add_account_type_offline))) {
            offlineLayout.setVisibility(View.VISIBLE);
        }
        if (Objects.requireNonNull(tab.getText()).toString().equals(getContext().getString(R.string.dialog_add_account_type_microsoft))) {
            microsoftLayout.setVisibility(View.VISIBLE);
        }
        if (Objects.requireNonNull(tab.getText()).toString().equals(getContext().getString(R.string.dialog_add_account_type_external))) {
            externalLayout.setVisibility(View.VISIBLE);
        }
    }

    public interface AddAccountCallback{
        void onAccountAdd(Account account);
        void onCancel();
    }

}
