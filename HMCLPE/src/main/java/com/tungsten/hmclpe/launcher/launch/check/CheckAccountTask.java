package com.tungsten.hmclpe.launcher.launch.check;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.auth.AuthInfo;
import com.tungsten.hmclpe.auth.AuthenticationException;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;
import com.tungsten.hmclpe.auth.microsoft.Msa;
import com.tungsten.hmclpe.auth.yggdrasil.GameProfile;
import com.tungsten.hmclpe.auth.yggdrasil.Texture;
import com.tungsten.hmclpe.auth.yggdrasil.TextureType;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilService;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilSession;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.skin.utils.Avatar;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Objects;

public class CheckAccountTask extends AsyncTask<Account,Integer,Exception> {

    private final MainActivity activity;
    private final CheckAccountCallback callback;

    private boolean finish = false;

    public CheckAccountTask (MainActivity activity,CheckAccountCallback callback) {
        this.activity = activity;
        this.callback = callback;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callback.onStart();
    }

    @Override
    protected Exception doInBackground(Account... accounts) {
        Account account = accounts[0];
        switch (account.loginType) {
            case 1:
            case 2:
                finish = true;
                return null;
            case 3:
                try {
                    Msa msa = new Msa(true, account.refresh_token);
                    if (msa.doesOwnGame) {
                        Msa.MinecraftProfileResponse minecraftProfile = Msa.getMinecraftProfile(msa.tokenType, msa.mcToken);
                        Map<TextureType, Texture> map = Msa.getTextures(minecraftProfile).get();
                        Texture texture = map.get(TextureType.SKIN);
                        Bitmap skin;
                        if (texture == null) {
                            AssetManager manager = activity.getAssets();
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
                        activity.runOnUiThread(() -> {
                            String skinTexture = Avatar.bitmapToString(skin);
                            Account a = new Account(3,
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
                            for (int i = 0;i < activity.uiManager.accountUI.accounts.size();i++) {
                                Account ac = activity.uiManager.accountUI.accounts.get(i);
                                if (account.email.equals(ac.email) && account.auth_player_name.equals(ac.auth_player_name) && account.auth_uuid.equals(ac.auth_uuid) && account.loginServer.equals(ac.loginServer)) {
                                    activity.uiManager.accountUI.accounts.get(i).refresh(a);
                                    GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                                    break;
                                }
                            }
                            activity.publicGameSetting.account = a;
                            GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                            activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                            activity.uiManager.mainUI.refreshAccount();
                        });
                    }
                    finish = true;
                    return null;
                }
                catch (Exception e){
                    e.printStackTrace();
                    finish = true;
                    return e;
                }
            case 4:
            case 5:
                try {
                    //boolean isNide = account.loginType == 5;
                    YggdrasilService yggdrasilService = Objects.requireNonNull(getServerFromUrl(account.loginServer)).getYggdrasilService();
                    YggdrasilSession yggdrasilSession = yggdrasilService.refresh(account.auth_access_token, account.auth_client_token,null);
                    if (yggdrasilSession.getAvailableProfiles() != null && yggdrasilSession.getAvailableProfiles().size() > 1) {
                        for (GameProfile gameProfile : yggdrasilSession.getAvailableProfiles()) {
                            if (gameProfile.getName().equals(account.auth_player_name)) {
                                Bitmap skin;
                                Map<TextureType, Texture> map = YggdrasilService.getTextures(yggdrasilService.getCompleteGameProfile(gameProfile.getId()).get()).get();
                                Texture texture = map.get(TextureType.SKIN);
                                if (texture == null) {
                                    AssetManager manager = activity.getAssets();
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
                                activity.runOnUiThread(() -> {
                                    String skinTexture = Avatar.bitmapToString(skin);
                                    Account a = new Account(account.loginType,
                                            account.email,
                                            account.password,
                                            account.user_type,
                                            account.auth_session,
                                            gameProfile.getName(),
                                            gameProfile.getId().toString(),
                                            yggdrasilSession.getAccessToken(),
                                            yggdrasilSession.getClientToken(),
                                            account.refresh_token,
                                            account.loginServer,
                                            skinTexture);
                                    for (int i = 0;i < activity.uiManager.accountUI.accounts.size();i++) {
                                        Account ac = activity.uiManager.accountUI.accounts.get(i);
                                        if (account.email.equals(ac.email) && account.auth_player_name.equals(ac.auth_player_name) && account.auth_uuid.equals(ac.auth_uuid) && account.loginServer.equals(ac.loginServer)) {
                                            activity.uiManager.accountUI.accounts.get(i).refresh(a);
                                            GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                                            break;
                                        }
                                    }
                                    activity.publicGameSetting.account = a;
                                    GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                                    activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                                    activity.uiManager.mainUI.refreshAccount();
                                });
                                break;
                            }
                        }
                    }
                    else {
                        AuthInfo authInfo = yggdrasilSession.toAuthInfo();
                        Map<TextureType, Texture> map = null;
                        map = YggdrasilService.getTextures(yggdrasilService.getCompleteGameProfile(authInfo.getUUID()).get()).get();
                        Texture texture = map.get(TextureType.SKIN);
                        Bitmap skin;
                        if (texture == null) {
                            AssetManager manager = activity.getAssets();
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
                        activity.runOnUiThread(() -> {
                            String skinTexture = Avatar.bitmapToString(skin);
                            Account a = new Account(account.loginType,
                                    account.email,
                                    account.password,
                                    account.user_type,
                                    account.auth_session,
                                    authInfo.getUsername(),
                                    authInfo.getUUID().toString(),
                                    authInfo.getAccessToken(),
                                    yggdrasilSession.getClientToken(),
                                    account.refresh_token,
                                    account.loginServer,
                                    skinTexture);
                            for (int i = 0;i < activity.uiManager.accountUI.accounts.size();i++) {
                                Account ac = activity.uiManager.accountUI.accounts.get(i);
                                if (account.email.equals(ac.email) && account.auth_player_name.equals(ac.auth_player_name) && account.auth_uuid.equals(ac.auth_uuid) && account.loginServer.equals(ac.loginServer)) {
                                    activity.uiManager.accountUI.accounts.get(i).refresh(a);
                                    GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                                    break;
                                }
                            }
                            activity.publicGameSetting.account = a;
                            GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                            activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                            activity.uiManager.mainUI.refreshAccount();
                        });
                    }
                    finish = true;
                    return null;
                }catch (AuthenticationException e){
                    e.printStackTrace();
                    finish = false;
                    return null;
                }catch (IOException e) {
                    e.printStackTrace();
                    finish = true;
                    return e;
                }
            default:
                return new Exception(activity.getString(R.string.launch_check_dialog_exception_no_account));
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Exception e) {
        super.onPostExecute(e);
        callback.onFinish(e,finish);
    }

    private AuthlibInjectorServer getServerFromUrl(String url){
        for (int i = 0;i < activity.uiManager.accountUI.serverList.size();i++){
            if (activity.uiManager.accountUI.serverList.get(i).getUrl().equals(url)){
                return activity.uiManager.accountUI.serverList.get(i);
            }
        }
        return null;
    }

    public interface CheckAccountCallback{
        void onStart();
        void onFinish(Exception e,boolean finish);
    }
}
