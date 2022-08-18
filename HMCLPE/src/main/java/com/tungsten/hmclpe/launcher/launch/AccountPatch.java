package com.tungsten.hmclpe.launcher.launch;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.auth.offline.LoadedSkin;
import com.tungsten.hmclpe.auth.offline.OfflineSkinSetting;
import com.tungsten.hmclpe.auth.offline.SkinJson;
import com.tungsten.hmclpe.auth.offline.Texture;
import com.tungsten.hmclpe.auth.offline.YggdrasilServer;
import com.tungsten.hmclpe.auth.yggdrasil.TextureModel;
import com.tungsten.hmclpe.skin.GameCharacter;
import com.tungsten.hmclpe.skin.utils.Avatar;
import com.tungsten.hmclpe.skin.utils.InvalidSkinException;
import com.tungsten.hmclpe.skin.utils.NormalizedSkin;
import com.tungsten.hmclpe.utils.gson.JsonUtils;
import com.tungsten.hmclpe.utils.gson.UUIDTypeAdapter;
import com.tungsten.hmclpe.utils.io.NetworkUtils;
import com.tungsten.hmclpe.utils.string.StringUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

public class AccountPatch {

    public static String[] getAccountArgs(Context context,Account account) {
        String authlibPath = context.getFilesDir().getAbsolutePath() + "/plugin/login/authlib-injector/authlib-injector.jar";
        switch (account.loginType) {
            case 1:
                if (account.offlineSkinSetting == null) {
                    return new String[0];
                }
                else {
                    YggdrasilServer server = new YggdrasilServer(0);
                    try {
                        server.start();
                        server.addCharacter(new YggdrasilServer.Character(UUIDTypeAdapter.fromString(account.auth_uuid), account.auth_player_name, getOfflineSkin(context,account.offlineSkinSetting,account.auth_player_name)));
                    } catch (IOException e) {
                        Log.e("yggdrasilServer",e.toString());
                        return new String[0];
                    }
                    return new String[] {
                            "-javaagent:" + authlibPath + "=http://localhost:" + server.getListeningPort(),
                            "-Dauthlibinjector.side=client"
                    };
                }
            case 4:
                return new String[] {
                        "-javaagent:" + authlibPath + "=" + account.loginServer,
                        "-Dauthlibinjector.side=client"
                };
            case 5:
                String nide8authPath = context.getFilesDir().getAbsolutePath() + "/plugin/login/nide8auth/nide8auth.jar";
                String serverId = account.loginServer.substring(account.loginServer.length() - 33,account.loginServer.length() - 1);
                return new String[] {
                        "-javaagent:" + nide8authPath + "=" + serverId,
                        "-Dnide8auth.client=true"
                };
            default:
                return new String[0];
        }
    }

    public static LoadedSkin getOfflineSkin(Context context,OfflineSkinSetting skinSetting,String name) {
        AssetManager manager = context.getAssets();
        InputStream alexInputStream;
        InputStream steveInputStream;
        Bitmap skin;
        Bitmap cape;
        try {
            alexInputStream = manager.open("img/alex.png");
            steveInputStream = manager.open("img/steve.png");
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        switch (skinSetting.type) {
            case 1:
                try {
                    return new LoadedSkin(TextureModel.STEVE, Texture.loadTexture(steveInputStream),null);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            case 2:
                try {
                    return new LoadedSkin(TextureModel.ALEX, Texture.loadTexture(alexInputStream),null);
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            case 3:
                if (new File(skinSetting.skinPath).exists()) {
                    skin = BitmapFactory.decodeFile(skinSetting.skinPath).getWidth() == 64 && (BitmapFactory.decodeFile(skinSetting.skinPath).getHeight() == 32 || BitmapFactory.decodeFile(skinSetting.skinPath).getHeight() == 64) ? BitmapFactory.decodeFile(skinSetting.skinPath) : Avatar.getBitmapFromRes(context, R.drawable.skin_alex);
                }
                else {
                    skin = Avatar.getBitmapFromRes(context,R.drawable.skin_alex);
                }
                if (new File(skinSetting.capePath).exists()) {
                    cape = (BitmapFactory.decodeFile(skinSetting.capePath).getWidth() == 64 && BitmapFactory.decodeFile(skinSetting.capePath).getHeight() == 32) ? BitmapFactory.decodeFile(skinSetting.capePath) : null;
                }
                else {
                    cape = null;
                }
                try {
                    NormalizedSkin normalizedSkin = new NormalizedSkin(skin);
                    return new LoadedSkin(normalizedSkin.isSlim() ? TextureModel.ALEX : TextureModel.STEVE,
                            Texture.loadTexture(bitmap2InputStream(normalizedSkin.isOldFormat() ? normalizedSkin.getNormalizedTexture() : normalizedSkin.getOriginalTexture())),
                            cape == null ? null : Texture.loadTexture(bitmap2InputStream(cape)));
                } catch (InvalidSkinException | IOException e) {
                    e.printStackTrace();
                    return null;
                }
            case 4:
            case 5:
                String cslApi = skinSetting.type == 4 ? "https://mcskin.littleservice.cn" : (skinSetting.server.startsWith("http://") ? skinSetting.server.replace("http://","https://") : skinSetting.server);
                URL u = null;
                try {
                    u = new URL(StringUtils.removeSuffix(cslApi, "/") + "/" + name + ".json");
                    Log.e("cslApi",StringUtils.removeSuffix(cslApi, "/") + "/" + name + ".json");
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    return null;
                }
                try {
                    String resultText = NetworkUtils.doGet(NetworkUtils.toURL(StringUtils.removeSuffix(cslApi, "/") + "/" + name + ".json"));
                    SkinJson result = JsonUtils.GSON.fromJson(resultText, SkinJson.class);
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
                        try {
                            NormalizedSkin normalizedSkin = new NormalizedSkin(skin);
                            return new LoadedSkin(normalizedSkin.isSlim() ? TextureModel.ALEX : TextureModel.STEVE,
                                    Texture.loadTexture(bitmap2InputStream(normalizedSkin.isOldFormat() ? normalizedSkin.getNormalizedTexture() : normalizedSkin.getOriginalTexture())),
                                    cape == null ? null : Texture.loadTexture(bitmap2InputStream(cape)));
                        } catch (InvalidSkinException | IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                    else {
                        return null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            default:
                return null;
        }
    }

    // 将Bitmap转换成InputStream
    public static InputStream bitmap2InputStream(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        InputStream is = new ByteArrayInputStream(baos.toByteArray());
        return is;
    }

}
