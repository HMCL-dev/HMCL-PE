package com.tungsten.hmclpe.launcher.dialogs.account;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilService;
import com.tungsten.hmclpe.auth.yggdrasil.YggdrasilSession;
import com.tungsten.hmclpe.launcher.list.account.ProfileListAdapter;

import java.util.ArrayList;

public class SelectProfileDialog extends Dialog implements View.OnClickListener {

    public YggdrasilService yggdrasilService;
    public YggdrasilSession yggdrasilSession;
    public String email;
    public String password;
    public String url;
    private ArrayList<Bitmap> bitmaps;
    private AddAuthlibInjectorAccountDialog.OnAuthlibInjectorAccountAddListener onAuthlibInjectorAccountAddListener;
    private boolean isNide;

    private ListView listView;
    private Button cancel;

    public SelectProfileDialog(@NonNull Context context, YggdrasilService yggdrasilService, YggdrasilSession yggdrasilSession, String email, String password, String url, ArrayList<Bitmap> bitmaps, AddAuthlibInjectorAccountDialog.OnAuthlibInjectorAccountAddListener onAuthlibInjectorAccountAddListener,boolean isNide) {
        super(context);
        setContentView(R.layout.dialog_select_profile);
        setCancelable(false);
        this.yggdrasilService = yggdrasilService;
        this.yggdrasilSession = yggdrasilSession;
        this.email = email;
        this.password = password;
        this.url = url;
        this.bitmaps = bitmaps;
        this.onAuthlibInjectorAccountAddListener = onAuthlibInjectorAccountAddListener;
        this.isNide = isNide;
        init();
    }

    private void init() {
        cancel = findViewById(R.id.exit);
        cancel.setOnClickListener(this);

        listView = findViewById(R.id.profile_list);
        refreshList();
    }

    private void refreshList(){
        ProfileListAdapter adapter = new ProfileListAdapter(getContext(),yggdrasilSession.getAvailableProfiles(),bitmaps,onAuthlibInjectorAccountAddListener,this,isNide);
        listView.setAdapter(adapter);
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.width = getMaxWidth(listView);
        listView.setLayoutParams(params);
    }

    private int getMaxWidth(ListView listView) {
        int maxWidth = 550;
        if (listView.getAdapter() == null) {
            return maxWidth;
        }
        int count = listView.getAdapter().getCount();
        View view = null;
        for (int i = 0; i < count; i++) {
            view = listView.getAdapter().getView(i, null, listView);
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            if (view.getMeasuredWidth() > maxWidth) {
                maxWidth = view.getMeasuredWidth();
            }
        }
        return maxWidth;
    }

    @Override
    public void onClick(View view) {
        if (view == cancel) {
            dismiss();
        }
    }
}
