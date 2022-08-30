package com.tungsten.hmclpe.launcher.dialogs.account;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.auth.offline.OfflineSkinSetting;
import com.tungsten.hmclpe.auth.offline.SkinJson;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.skin.GameCharacter;
import com.tungsten.hmclpe.skin.MinecraftSkinRenderer;
import com.tungsten.hmclpe.skin.SkinGLSurfaceView;
import com.tungsten.hmclpe.skin.utils.Avatar;
import com.tungsten.hmclpe.skin.utils.InvalidSkinException;
import com.tungsten.hmclpe.skin.utils.NormalizedSkin;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.io.NetworkUtils;
import com.tungsten.hmclpe.utils.string.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class SkinPreviewDialog implements View.OnClickListener {

    private static final int SELECT_SKIN_REQUEST = 9300;
    private static final int SELECT_CAPE_REQUEST = 9400;

    private LinearLayout dialog;
    private Button fakeBackground;

    private final Context context;
    private final MainActivity activity;
    private final Account account;
    private final OfflineSkinCallback callback;

    private LinearLayout skinParentView;

    private Button positive;
    private Button negative;

    private final MinecraftSkinRenderer renderer;

    private final Handler handler;
    private final Runnable startRunnable = new Runnable() {
        @Override
        public void run() {
            renderer.mCharacter.SetRunning(true);
            handler.postDelayed(stopRunnable,2000);
        }
    };
    private final Runnable stopRunnable = new Runnable() {
        @Override
        public void run() {
            renderer.mCharacter.SetRunning(false);
            handler.postDelayed(startRunnable,10000);
        }
    };

    private RadioButton defaultSkin;
    private RadioButton steveSkin;
    private RadioButton alexSkin;
    private RadioButton localSkin;
    private RadioButton littleSkin;
    private RadioButton blessingSkin;

    private LinearLayout localSkinLayout;
    private EditText editSkinPath;
    private EditText editCapePath;
    private ImageButton selectSkin;
    private ImageButton selectCape;

    private LinearLayout littleSkinLayout;

    private LinearLayout blessingSkinLayout;
    private EditText editServer;

    private TextView littleSkinUrl;

    private OfflineSkinSetting offlineSkinSetting;

    private static SkinPreviewDialog skinPreviewDialog;

    public SkinPreviewDialog(Context context, MainActivity activity, Account account,OfflineSkinCallback callback){
        this.context = context;
        this.activity = activity;
        this.account = account;
        this.callback = callback;
        handler = new Handler();
        renderer = new MinecraftSkinRenderer(context,R.drawable.skin_alex,true);
        skinPreviewDialog = this;
        init();
    }

    public interface OfflineSkinCallback{
        void onPositive(OfflineSkinSetting offlineSkinSetting);
    }

    public static SkinPreviewDialog getInstance () {
        return skinPreviewDialog;
    }

    public void show(){
        activity.dialogMode = true;

        dialog = activity.findViewById(R.id.dialog_offline_skin);
        fakeBackground = activity.findViewById(R.id.fake_dialog_background);

        dialog.setVisibility(View.VISIBLE);
        fakeBackground.setVisibility(View.VISIBLE);

        handler.postDelayed(startRunnable,10000);
    }

    public void dismiss(){
        activity.dialogMode = false;

        handler.removeCallbacks(startRunnable);
        handler.removeCallbacks(stopRunnable);

        skinParentView.removeAllViews();

        dialog.setVisibility(View.GONE);
        fakeBackground.setVisibility(View.GONE);

        skinPreviewDialog = null;
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init(){
        skinParentView = activity.findViewById(R.id.skin_parent_view);

        SkinGLSurfaceView skinGLSurfaceView = new SkinGLSurfaceView(context);
        skinGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        skinGLSurfaceView.getHolder().setFormat(PixelFormat.RGBA_8888);
        skinGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);
        skinGLSurfaceView.setZOrderOnTop(true);
        skinGLSurfaceView.setRenderer(renderer,5f);
        skinGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
        skinGLSurfaceView.setPreserveEGLContextOnPause(true);
        skinParentView.addView(skinGLSurfaceView);

        positive = activity.findViewById(R.id.edit_skin_positive);
        negative = activity.findViewById(R.id.cancel_edit_skin);

        littleSkinUrl = activity.findViewById(R.id.little_skin_url);
        littleSkinUrl.setOnClickListener(this);

        defaultSkin = activity.findViewById(R.id.check_skin_default);
        steveSkin = activity.findViewById(R.id.check_skin_steve);
        alexSkin = activity.findViewById(R.id.check_skin_alex);
        localSkin = activity.findViewById(R.id.check_skin_local);
        littleSkin = activity.findViewById(R.id.check_skin_little);
        blessingSkin = activity.findViewById(R.id.check_skin_blessing);

        defaultSkin.setOnClickListener(this);
        steveSkin.setOnClickListener(this);
        alexSkin.setOnClickListener(this);
        localSkin.setOnClickListener(this);
        littleSkin.setOnClickListener(this);
        blessingSkin.setOnClickListener(this);

        localSkinLayout = activity.findViewById(R.id.local_skin_layout);
        editSkinPath = activity.findViewById(R.id.edit_skin_path);
        selectSkin = activity.findViewById(R.id.select_skin);
        editCapePath = activity.findViewById(R.id.edit_cape_path);
        selectCape = activity.findViewById(R.id.select_cape);

        littleSkinLayout = activity.findViewById(R.id.little_skin_layout);

        blessingSkinLayout = activity.findViewById(R.id.blessing_skin_layout);
        editServer = activity.findViewById(R.id.edit_blessing_server);

        editSkinPath.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (offlineSkinSetting.type == 3) {
                    offlineSkinSetting.skinPath = editSkinPath.getText().toString();
                    Bitmap skin;
                    Bitmap cape;
                    if (new File(offlineSkinSetting.skinPath).exists()) {
                        skin = BitmapFactory.decodeFile(offlineSkinSetting.skinPath).getWidth() == 64 && (BitmapFactory.decodeFile(offlineSkinSetting.skinPath).getHeight() == 32 || BitmapFactory.decodeFile(offlineSkinSetting.skinPath).getHeight() == 64) ? BitmapFactory.decodeFile(offlineSkinSetting.skinPath) : Avatar.getBitmapFromRes(context,R.drawable.skin_alex);
                    }
                    else {
                        skin = Avatar.getBitmapFromRes(context,R.drawable.skin_alex);
                    }
                    if (new File(offlineSkinSetting.capePath).exists()) {
                        cape = (BitmapFactory.decodeFile(offlineSkinSetting.capePath).getWidth() == 64 && BitmapFactory.decodeFile(offlineSkinSetting.capePath).getHeight() == 32) ? BitmapFactory.decodeFile(offlineSkinSetting.capePath) : null;
                    }
                    else {
                        cape = null;
                    }
                    try {
                        NormalizedSkin normalizedSkin = new NormalizedSkin(skin);
                        renderer.mCharacter = new GameCharacter(normalizedSkin.isSlim());
                        renderer.updateTexture(normalizedSkin.isOldFormat() ? normalizedSkin.getNormalizedTexture() : normalizedSkin.getOriginalTexture(),cape);
                    } catch (InvalidSkinException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        editCapePath.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (offlineSkinSetting.type == 3) {
                    offlineSkinSetting.capePath = editCapePath.getText().toString();
                    Bitmap skin;
                    Bitmap cape;
                    if (new File(offlineSkinSetting.skinPath).exists()) {
                        skin = BitmapFactory.decodeFile(offlineSkinSetting.skinPath).getWidth() == 64 && (BitmapFactory.decodeFile(offlineSkinSetting.skinPath).getHeight() == 32 || BitmapFactory.decodeFile(offlineSkinSetting.skinPath).getHeight() == 64) ? BitmapFactory.decodeFile(offlineSkinSetting.skinPath) : Avatar.getBitmapFromRes(context,R.drawable.skin_alex);
                    }
                    else {
                        skin = Avatar.getBitmapFromRes(context,R.drawable.skin_alex);
                    }
                    if (new File(offlineSkinSetting.capePath).exists()) {
                        cape = (BitmapFactory.decodeFile(offlineSkinSetting.capePath).getWidth() == 64 && BitmapFactory.decodeFile(offlineSkinSetting.capePath).getHeight() == 32) ? BitmapFactory.decodeFile(offlineSkinSetting.capePath) : null;
                    }
                    else {
                        cape = null;
                    }
                    try {
                        NormalizedSkin normalizedSkin = new NormalizedSkin(skin);
                        renderer.mCharacter = new GameCharacter(normalizedSkin.isSlim());
                        renderer.updateTexture(normalizedSkin.isOldFormat() ? normalizedSkin.getNormalizedTexture() : normalizedSkin.getOriginalTexture(),cape);
                    } catch (InvalidSkinException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        selectSkin.setOnClickListener(this);
        selectCape.setOnClickListener(this);

        editServer.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                offlineSkinSetting.server = editServer.getText().toString();
                String cslApi;
                if (offlineSkinSetting.server.startsWith("http://")) {
                    cslApi = offlineSkinSetting.server.replace("http://","https://");
                }
                else {
                    cslApi = offlineSkinSetting.server;
                }
                URL u = null;
                try {
                    u = new URL(StringUtils.removeSuffix(cslApi, "/") + "/" + account.auth_player_name + ".json");
                    Log.e("cslApi",StringUtils.removeSuffix(cslApi, "/") + "/" + account.auth_player_name + ".json");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (u != null) {
                    new Thread(() -> {
                        try {
                            String resultText = NetworkUtils.doGet(NetworkUtils.toURL(StringUtils.removeSuffix(cslApi, "/") + "/" + account.auth_player_name + ".json"));
                            SkinJson result = JsonUtils.GSON.fromJson(resultText, SkinJson.class);
                            Bitmap skin;
                            Bitmap cape;
                            if (result != null && result.hasSkin()) {
                                if (result.getHash() == null) {
                                    skin = Avatar.getBitmapFromRes(context,R.drawable.skin_alex);
                                }
                                else {
                                    URL url = new URL(StringUtils.removeSuffix(cslApi, "/") + "/textures/" + result.getHash());
                                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                                    httpURLConnection.setDoInput(true);
                                    httpURLConnection.connect();
                                    InputStream inputStream = httpURLConnection.getInputStream();
                                    skin = BitmapFactory.decodeStream(inputStream);
                                }
                                if (result.getCapeHash() == null) {
                                    cape = null;
                                }
                                else {
                                    URL url = new URL(StringUtils.removeSuffix(cslApi, "/") + "/textures/" + result.getCapeHash());
                                    HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                                    httpURLConnection.setDoInput(true);
                                    httpURLConnection.connect();
                                    InputStream inputStream = httpURLConnection.getInputStream();
                                    cape = BitmapFactory.decodeStream(inputStream);
                                }
                                activity.runOnUiThread(() -> {
                                    if (offlineSkinSetting.type == 5) {
                                        try {
                                            NormalizedSkin normalizedSkin = new NormalizedSkin(skin);
                                            renderer.mCharacter = new GameCharacter(normalizedSkin.isSlim());
                                            renderer.updateTexture(normalizedSkin.isOldFormat() ? normalizedSkin.getNormalizedTexture() : normalizedSkin.getOriginalTexture(),cape);
                                        } catch (InvalidSkinException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }).start();
                }
            }
        });

        if (account.offlineSkinSetting == null) {
            offlineSkinSetting = new OfflineSkinSetting(context);
        }
        else {
            try {
                offlineSkinSetting = (OfflineSkinSetting) account.offlineSkinSetting.clone();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        renderer.updateTexture(Avatar.stringToBitmap(account.texture),null);

        switch (offlineSkinSetting.type) {
            case 0:
                switchToDefault();
                break;
            case 1:
                switchToSteve();
                break;
            case 2:
                switchToAlex();
                break;
            case 3:
                switchToLocal();
                break;
            case 4:
                switchToLittleSkin();
                break;
            case 5:
                switchToBlessingSkin();
                break;
        }

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == positive){
            callback.onPositive(offlineSkinSetting);
            dismiss();
        }
        if (v == negative){
            dismiss();
        }
        if (v == littleSkinUrl) {
            Uri uri = Uri.parse("https://littleskin.cn/");
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        if (v == defaultSkin) {
            switchToDefault();
        }
        if (v == steveSkin) {
            switchToSteve();
        }
        if (v == alexSkin) {
            switchToAlex();
        }
        if (v == localSkin) {
            switchToLocal();
        }
        if (v == littleSkin) {
            switchToLittleSkin();
        }
        if (v == blessingSkin) {
            switchToBlessingSkin();
        }
        if (v == selectSkin) {
            Intent intent = new Intent(context, FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "png");
            intent.putExtra(Constants.INITIAL_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath());
            activity.startActivityForResult(intent, SELECT_SKIN_REQUEST);
        }
        if (v == selectCape) {
            Intent intent = new Intent(context, FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "png");
            intent.putExtra(Constants.INITIAL_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath());
            activity.startActivityForResult(intent, SELECT_CAPE_REQUEST);
        }
    }

    private void switchToDefault(){
        defaultSkin.setChecked(true);
        steveSkin.setChecked(false);
        alexSkin.setChecked(false);
        localSkin.setChecked(false);
        littleSkin.setChecked(false);
        blessingSkin.setChecked(false);

        localSkinLayout.setVisibility(View.GONE);
        littleSkinLayout.setVisibility(View.GONE);
        blessingSkinLayout.setVisibility(View.GONE);

        renderer.mCharacter = new GameCharacter(true);
        renderer.updateTexture(Avatar.getBitmapFromRes(context,R.drawable.skin_alex),null);
        offlineSkinSetting.type = 0;
    }

    private void switchToSteve(){
        defaultSkin.setChecked(false);
        steveSkin.setChecked(true);
        alexSkin.setChecked(false);
        localSkin.setChecked(false);
        littleSkin.setChecked(false);
        blessingSkin.setChecked(false);

        localSkinLayout.setVisibility(View.GONE);
        littleSkinLayout.setVisibility(View.GONE);
        blessingSkinLayout.setVisibility(View.GONE);

        renderer.mCharacter = new GameCharacter(false);
        renderer.updateTexture(Avatar.getBitmapFromRes(context,R.drawable.skin_steve),null);
        offlineSkinSetting.type = 1;
    }

    private void switchToAlex(){
        defaultSkin.setChecked(false);
        steveSkin.setChecked(false);
        alexSkin.setChecked(true);
        localSkin.setChecked(false);
        littleSkin.setChecked(false);
        blessingSkin.setChecked(false);

        localSkinLayout.setVisibility(View.GONE);
        littleSkinLayout.setVisibility(View.GONE);
        blessingSkinLayout.setVisibility(View.GONE);

        renderer.mCharacter = new GameCharacter(true);
        renderer.updateTexture(Avatar.getBitmapFromRes(context,R.drawable.skin_alex),null);
        offlineSkinSetting.type = 2;
    }

    private void switchToLocal(){
        defaultSkin.setChecked(false);
        steveSkin.setChecked(false);
        alexSkin.setChecked(false);
        localSkin.setChecked(true);
        littleSkin.setChecked(false);
        blessingSkin.setChecked(false);

        localSkinLayout.setVisibility(View.VISIBLE);
        littleSkinLayout.setVisibility(View.GONE);
        blessingSkinLayout.setVisibility(View.GONE);

        offlineSkinSetting.type = 3;

        editSkinPath.setText(offlineSkinSetting.skinPath);
        editCapePath.setText(offlineSkinSetting.capePath);
    }

    private void switchToLittleSkin(){
        defaultSkin.setChecked(false);
        steveSkin.setChecked(false);
        alexSkin.setChecked(false);
        localSkin.setChecked(false);
        littleSkin.setChecked(true);
        blessingSkin.setChecked(false);

        localSkinLayout.setVisibility(View.GONE);
        littleSkinLayout.setVisibility(View.VISIBLE);
        blessingSkinLayout.setVisibility(View.GONE);

        offlineSkinSetting.type = 4;

        new Thread(() -> {
            try {
                String resultText = NetworkUtils.doGet(NetworkUtils.toURL("https://mcskin.littleservice.cn/" + account.auth_player_name + ".json"));
                SkinJson result = JsonUtils.GSON.fromJson(resultText, SkinJson.class);
                Bitmap skin;
                Bitmap cape;
                if (result != null && result.hasSkin()) {
                    if (result.getHash() == null) {
                        skin = Avatar.getBitmapFromRes(context,R.drawable.skin_alex);
                    }
                    else {
                        URL url = new URL("https://mcskin.littleservice.cn/textures/" + result.getHash());
                        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        skin = BitmapFactory.decodeStream(inputStream);
                    }
                    if (result.getCapeHash() == null) {
                        cape = null;
                    }
                    else {
                        URL url = new URL("https://mcskin.littleservice.cn/textures/" + result.getCapeHash());
                        HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        cape = BitmapFactory.decodeStream(inputStream);
                    }
                    activity.runOnUiThread(() -> {
                        if (offlineSkinSetting.type == 4) {
                            try {
                                NormalizedSkin normalizedSkin = new NormalizedSkin(skin);
                                renderer.mCharacter = new GameCharacter(normalizedSkin.isSlim());
                                renderer.updateTexture(normalizedSkin.isOldFormat() ? normalizedSkin.getNormalizedTexture() : normalizedSkin.getOriginalTexture(),cape);
                            } catch (InvalidSkinException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void switchToBlessingSkin(){
        defaultSkin.setChecked(false);
        steveSkin.setChecked(false);
        alexSkin.setChecked(false);
        localSkin.setChecked(false);
        littleSkin.setChecked(false);
        blessingSkin.setChecked(true);

        localSkinLayout.setVisibility(View.GONE);
        littleSkinLayout.setVisibility(View.GONE);
        blessingSkinLayout.setVisibility(View.VISIBLE);

        offlineSkinSetting.type = 5;

        editServer.setText(offlineSkinSetting.server);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == SELECT_SKIN_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = UriUtils.getRealPathFromUri_AboveApi19(context,uri);
            editSkinPath.setText(path);
        }
        if (requestCode == SELECT_CAPE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = UriUtils.getRealPathFromUri_AboveApi19(context,uri);
            editCapePath.setText(path);
        }
    }

    public void onPause(){
        ((SkinGLSurfaceView) skinParentView.getChildAt(0)).onPause();
    }

    public void onResume(){
        ((SkinGLSurfaceView) skinParentView.getChildAt(0)).onResume();
    }
}
