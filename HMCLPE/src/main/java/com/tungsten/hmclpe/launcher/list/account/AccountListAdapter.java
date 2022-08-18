package com.tungsten.hmclpe.launcher.list.account;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.auth.AuthInfo;
import com.tungsten.hmclpe.auth.AuthenticationException;
import com.tungsten.hmclpe.auth.authlibinjector.AuthlibInjectorServer;
import com.tungsten.hmclpe.auth.microsoft.Msa;
import com.tungsten.hmclpe.auth.offline.OfflineSkinSetting;
import com.tungsten.hmclpe.auth.yggdrasil.GameProfile;
import com.tungsten.hmclpe.auth.yggdrasil.Texture;
import com.tungsten.hmclpe.auth.yggdrasil.TextureType;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilService;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilSession;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.account.ReLoginDialog;
import com.tungsten.hmclpe.launcher.dialogs.account.SkinPreviewDialog;
import com.tungsten.hmclpe.launcher.uis.account.AccountUI;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.skin.utils.InvalidSkinException;
import com.tungsten.hmclpe.skin.utils.NormalizedSkin;
import com.tungsten.hmclpe.skin.utils.Avatar;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AccountListAdapter extends BaseAdapter {

    private Context context;
    private MainActivity activity;
    private ArrayList<Account> accounts;

    private int skinPosition;
    private ImageButton skinButton;
    private ProgressBar skinProgress;

    public AccountListAdapter (Context context, MainActivity activity, ArrayList<Account> accounts){
        this.context = context;
        this.activity = activity;
        this.accounts = accounts;
    }

    private class ViewHolder{
        RadioButton check;
        ImageView face;
        ImageView hat;
        TextView name;
        TextView type;
        ImageButton refresh;
        ImageButton skin;
        ImageButton delete;
        ProgressBar refreshProgress;
        ProgressBar uploadProgress;
    }

    @SuppressLint("HandlerLeak")
    public final Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {

            }
        }
    };

    Account newAccount;

    private AuthlibInjectorServer getServerFromUrl(String url){
        for (int i = 0;i < activity.uiManager.accountUI.serverList.size();i++){
            if (activity.uiManager.accountUI.serverList.get(i).getUrl().equals(url)){
                return activity.uiManager.accountUI.serverList.get(i);
            }
        }
        return null;
    }

    public void uploadSkin(String path) {
        Account account = accounts.get(skinPosition);
        boolean isSelected = account.email.equals(activity.publicGameSetting.account.email) && account.auth_player_name.equals(activity.publicGameSetting.account.auth_player_name) && account.auth_uuid.equals(activity.publicGameSetting.account.auth_uuid) && account.loginServer.equals(activity.publicGameSetting.account.loginServer);
        new Thread(() -> {
            handler.post(() -> {
                skinProgress.setVisibility(View.VISIBLE);
                skinButton.setVisibility(View.GONE);
            });
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            try {
                YggdrasilService yggdrasilService = Objects.requireNonNull(getServerFromUrl(account.loginServer)).getYggdrasilService();
                NormalizedSkin skin = new NormalizedSkin(bitmap);
                String model = skin.isSlim() ? "slim" : "";
                Log.e("upload model",model);
                yggdrasilService.uploadSkin(UUID.fromString(account.auth_uuid),account.auth_access_token,model,new File(path).toPath());
                handler.post(() -> {
                    account.texture = Avatar.bitmapToString(bitmap);
                    if (isSelected) {
                        activity.publicGameSetting.account = account;
                        GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                    }
                    activity.uiManager.accountUI.accounts.get(skinPosition).refresh(account);
                    GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                    activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                });
            }catch (AuthenticationException e){
                e.printStackTrace();
                handler.post(() -> {
                    Toast.makeText(context,context.getString(R.string.dialog_upload_skin_failed),Toast.LENGTH_SHORT).show();
                });
            }catch (InvalidSkinException e) {
                handler.post(() -> {
                    Toast.makeText(context,context.getString(R.string.dialog_upload_skin_invalid_skin),Toast.LENGTH_SHORT).show();
                });
            }
            handler.post(() -> {
                skinProgress.setVisibility(View.GONE);
                skinButton.setVisibility(View.VISIBLE);
            });
        }).start();
    }

    @Override
    public int getCount() {
        return accounts.size();
    }

    @Override
    public Object getItem(int position) {
        return accounts.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_account,null);
            viewHolder.check = convertView.findViewById(R.id.select_account);
            viewHolder.face = convertView.findViewById(R.id.skin_face);
            viewHolder.hat = convertView.findViewById(R.id.skin_hat);
            viewHolder.name = convertView.findViewById(R.id.name);
            viewHolder.type = convertView.findViewById(R.id.type);
            viewHolder.refresh = convertView.findViewById(R.id.refresh);
            viewHolder.skin = convertView.findViewById(R.id.skin);
            viewHolder.delete = convertView.findViewById(R.id.delete);
            viewHolder.refreshProgress = convertView.findViewById(R.id.refresh_account_progress);
            viewHolder.uploadProgress = convertView.findViewById(R.id.upload_skin_progress);
            activity.exteriorConfig.apply(viewHolder.check);
            convertView.setTag(viewHolder);
        }
        else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        Account account = accounts.get(position);
        boolean isSelected = account.email.equals(activity.publicGameSetting.account.email) && account.auth_player_name.equals(activity.publicGameSetting.account.auth_player_name) && account.auth_uuid.equals(activity.publicGameSetting.account.auth_uuid) && account.loginServer.equals(activity.publicGameSetting.account.loginServer);
        if (account.loginType == 1){
            viewHolder.name.setText(account.auth_player_name);
            viewHolder.type.setText(context.getString(R.string.item_account_type_offline));
            Avatar.setAvatar(account.texture, viewHolder.face, viewHolder.hat);
        }
        if (account.loginType == 2){
            viewHolder.name.setText(account.email + " - " +account.auth_player_name);
            viewHolder.type.setText(context.getString(R.string.item_account_type_mojang));
            Avatar.setAvatar(account.texture, viewHolder.face, viewHolder.hat);
        }
        if (account.loginType == 3){
            viewHolder.name.setText(account.auth_player_name);
            viewHolder.type.setText(context.getString(R.string.item_account_type_microsoft));
            Avatar.setAvatar(account.texture, viewHolder.face, viewHolder.hat);
        }
        if (account.loginType == 4){
            viewHolder.name.setText(account.email + " - " +account.auth_player_name);
            viewHolder.type.setText(context.getString(R.string.item_account_type_auth_lib) + ", " + context.getString(R.string.item_account_login_server) + " " + getServerFromUrl(account.loginServer).getName());
            Avatar.setAvatar(account.texture, viewHolder.face, viewHolder.hat);
        }
        if (account.loginType == 5){
            viewHolder.name.setText(account.email + " - " +account.auth_player_name);
            viewHolder.type.setText(context.getString(R.string.item_account_type_nide_8_auth) + ", " + context.getString(R.string.item_account_login_server) + " " + getServerFromUrl(account.loginServer).getName());
            Avatar.setAvatar(account.texture, viewHolder.face, viewHolder.hat);
        }
        viewHolder.check.setChecked(isSelected);
        viewHolder.check.setOnClickListener(v -> {
            activity.publicGameSetting.account = account;
            GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
            notifyDataSetChanged();
        });
        viewHolder.refresh.setOnClickListener(v -> {
            if (account.loginType == 3){
                new Thread(() -> {
                    handler.post(() -> {
                        viewHolder.refreshProgress.setVisibility(View.VISIBLE);
                        viewHolder.refresh.setVisibility(View.GONE);
                    });
                    try {
                        Msa msa = new Msa(true, account.refresh_token);
                        if (msa.doesOwnGame) {
                            Msa.MinecraftProfileResponse minecraftProfile = Msa.getMinecraftProfile(msa.tokenType, msa.mcToken);
                            Map<TextureType, Texture> map = Msa.getTextures(minecraftProfile).get();
                            Texture texture = map.get(TextureType.SKIN);
                            Bitmap skin;
                            if (texture == null) {
                                AssetManager manager = context.getAssets();
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
                            handler.post(() -> {
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
                                if (isSelected) {
                                    activity.publicGameSetting.account = a;
                                    GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                                }
                                activity.uiManager.accountUI.accounts.get(position).refresh(a);
                                GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                                activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                            });
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                        handler.post(() -> {
                            Toast.makeText(context, context.getString(R.string.account_refresh_exception_network), Toast.LENGTH_SHORT).show();
                        });
                    }
                    handler.post(() -> {
                        viewHolder.refreshProgress.setVisibility(View.GONE);
                        viewHolder.refresh.setVisibility(View.VISIBLE);
                    });
                }).start();
            }
            if (account.loginType == 4 || account.loginType == 5){
                new Thread(() -> {
                    handler.post(() -> {
                        viewHolder.refreshProgress.setVisibility(View.VISIBLE);
                        viewHolder.refresh.setVisibility(View.GONE);
                    });
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
                                        AssetManager manager = context.getAssets();
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
                                    handler.post(() -> {
                                        String skinTexture = Avatar.bitmapToString(skin);
                                        newAccount = new Account(account.loginType,
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
                                        if (isSelected) {
                                            activity.publicGameSetting.account = newAccount;
                                            GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                                        }
                                        activity.uiManager.accountUI.accounts.get(position).refresh(newAccount);
                                        GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                                        activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
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
                                AssetManager manager = context.getAssets();
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
                            handler.post(() -> {
                                String skinTexture = Avatar.bitmapToString(skin);
                                newAccount = new Account(account.loginType,
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
                                if (isSelected) {
                                    activity.publicGameSetting.account = newAccount;
                                    GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                                }
                                activity.uiManager.accountUI.accounts.get(position).refresh(newAccount);
                                GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                                activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                            });
                        }
                        handler.post(() -> {
                            viewHolder.refreshProgress.setVisibility(View.GONE);
                            viewHolder.refresh.setVisibility(View.VISIBLE);
                        });
                    }catch (AuthenticationException e){
                        e.printStackTrace();
                        handler.post(() -> {
                            ReLoginDialog dialog = new ReLoginDialog(context, account.email, Objects.requireNonNull(getServerFromUrl(account.loginServer)).getYggdrasilService(),account, new ReLoginDialog.ReloginCallback() {
                                @Override
                                public void onRelogin(Account account) {
                                    if (isSelected) {
                                        activity.publicGameSetting.account = account;
                                        GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                                    }
                                    activity.uiManager.accountUI.accounts.get(position).refresh(account);
                                    GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                                    activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                                    viewHolder.refreshProgress.setVisibility(View.GONE);
                                    viewHolder.refresh.setVisibility(View.VISIBLE);
                                }

                                @Override
                                public void onCancel() {
                                    viewHolder.refreshProgress.setVisibility(View.GONE);
                                    viewHolder.refresh.setVisibility(View.VISIBLE);
                                }
                            });
                            dialog.show();
                        });
                    }catch (IOException e) {
                        e.printStackTrace();
                        handler.post(() -> {
                            Toast.makeText(context, context.getString(R.string.account_refresh_exception_network), Toast.LENGTH_SHORT).show();
                        });
                    }
                }).start();
            }
        });
        if (account.loginType == 5) {
            ((View) viewHolder.skin.getParent()).setVisibility(View.GONE);
        }
        else {
            ((View) viewHolder.skin.getParent()).setVisibility(View.VISIBLE);
        }
        viewHolder.skin.setOnClickListener(v -> {
            if (account.loginType == 1){
                SkinPreviewDialog skinPreviewDialog = new SkinPreviewDialog(context, activity, account, offlineSkinSetting -> {
                    account.offlineSkinSetting = offlineSkinSetting;
                    if (isSelected) {
                        activity.publicGameSetting.account = account;
                        GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
                    }
                    activity.uiManager.accountUI.accounts.get(position).refresh(account);
                    GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
                    activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
                });
                skinPreviewDialog.show();
            }
            else if (account.loginType == 3) {
                Uri uri = Uri.parse("https://www.minecraft.net/zh-hans/msaprofile/mygames/editskin");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
            else if (account.loginType == 4) {
                skinPosition = position;
                skinButton = viewHolder.skin;
                skinProgress = viewHolder.uploadProgress;
                Intent intent = new Intent(context, FileChooser.class);
                intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
                intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "png");
                intent.putExtra(Constants.INITIAL_DIRECTORY, Environment.getExternalStorageDirectory().getAbsolutePath());
                activity.startActivityForResult(intent, AccountUI.SELECT_SKIN_REQUEST);
            }
        });
        viewHolder.delete.setOnClickListener(v -> {
            activity.uiManager.accountUI.accounts.remove(account);
            GsonUtils.saveAccounts(activity.uiManager.accountUI.accounts,AppManifest.ACCOUNT_DIR + "/accounts.json");
            if (activity.uiManager.accountUI.accounts.size() == 0){
                activity.publicGameSetting.account = new Account(0,"","","","","","","","","","","");
            }
            else if (isSelected){
                activity.publicGameSetting.account = accounts.get(0);
            }
            GsonUtils.savePublicGameSetting(activity.publicGameSetting, AppManifest.SETTING_DIR + "/public_game_setting.json");
            activity.uiManager.accountUI.accountListAdapter.notifyDataSetChanged();
        });
        return convertView;
    }
}
