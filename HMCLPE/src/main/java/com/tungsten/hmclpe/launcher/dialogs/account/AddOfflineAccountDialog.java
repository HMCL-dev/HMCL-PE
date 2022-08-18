package com.tungsten.hmclpe.launcher.dialogs.account;

import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.Account;
import com.tungsten.hmclpe.skin.utils.Avatar;
import com.tungsten.hmclpe.utils.gson.UUIDTypeAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.UUID;

public class AddOfflineAccountDialog extends Dialog implements View.OnClickListener {

    private Context context;
    private ArrayList<Account> accounts;
    private OnOfflineAccountAddListener onOfflineAccountAddListener;

    private EditText editName;
    private EditText editUUID;

    private TextView purchaseLink;

    private LinearLayout showAdvanceSetting;
    private ImageView spinView;
    private LinearLayout editUUIDLayout;
    private LinearLayout hintLayout;

    private Button login;
    private Button cancel;
    
    public AddOfflineAccountDialog(@NonNull Context context,ArrayList<Account> accounts,OnOfflineAccountAddListener onOfflineAccountAddListener) {
        super(context);
        this.context = context;
        this.accounts = accounts;
        this.onOfflineAccountAddListener = onOfflineAccountAddListener;
        setContentView(R.layout.dialog_add_offline_account);
        setCancelable(false);
        init();
    }
    
    private void init(){
        editName = findViewById(R.id.edit_user_name);
        editUUID = findViewById(R.id.edit_uuid);

        purchaseLink = findViewById(R.id.purchase_link);
        purchaseLink.setMovementMethod(LinkMovementMethod.getInstance());
        showAdvanceSetting = findViewById(R.id.show_advance_setting);
        showAdvanceSetting.setOnClickListener(this);
        spinView = findViewById(R.id.spin_view);
        editUUIDLayout = findViewById(R.id.edit_uuid_layout);
        hintLayout = findViewById(R.id.hint_layout);

        login = findViewById(R.id.login_offline);
        cancel = findViewById(R.id.cancel_login_offline);
        login.setOnClickListener(this);
        cancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == showAdvanceSetting){
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
        if (v == login){
            ArrayList<String> names = new ArrayList<>();
            for (Account account : accounts){
                if (account.loginType == 1){
                    names.add(account.auth_player_name);
                }
            }
            if (editName.getText().toString().equals("")){
                Toast.makeText(context,context.getString(R.string.dialog_add_offline_account_empty_warn),Toast.LENGTH_SHORT).show();
            }
            else if (names.contains(editName.getText().toString())){
                Toast.makeText(context,context.getString(R.string.dialog_add_offline_account_exist_warn),Toast.LENGTH_SHORT).show();
            }
            else {
                AssetManager manager = context.getAssets();
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
                onOfflineAccountAddListener.onPositive(account);
                this.dismiss();
            }
        }
        if (v == cancel){
            this.dismiss();
        }
    }

    public interface OnOfflineAccountAddListener{
        void onPositive(Account account);
    }
}
