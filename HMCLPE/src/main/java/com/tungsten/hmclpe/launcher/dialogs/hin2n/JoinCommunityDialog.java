package com.tungsten.hmclpe.launcher.dialogs.hin2n;

import static android.app.Activity.RESULT_OK;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.MenuHelper;
import com.tungsten.hmclpe.launcher.uis.universal.multiplayer.MultiPlayerUI;
import com.tungsten.hmclpe.multiplayer.Hin2nService;
import com.tungsten.hmclpe.utils.string.StringUtils;

import wang.switchy.hin2n.model.EdgeStatus;

public class JoinCommunityDialog extends Dialog implements View.OnClickListener {

    public static JoinCommunityDialog INSTANCE;

    private final MenuHelper menuHelper;
    private final MultiPlayerUI multiPlayerUI;

    private EditText editText;
    public Button positive;
    public Button negative;

    public ProgressBar progressBar;

    public JoinCommunityDialog(@NonNull Context context, MenuHelper menuHelper, MultiPlayerUI multiPlayerUI) {
        super(context);
        INSTANCE = this;
        this.menuHelper = menuHelper;
        this.multiPlayerUI = multiPlayerUI;
        setContentView(R.layout.dialog_join_community);
        setCancelable(false);
        init();
    }

    public static JoinCommunityDialog getInstance() {
        return INSTANCE;
    }

    private void init() {
        editText = findViewById(R.id.invite_code);
        positive = findViewById(R.id.join);
        negative = findViewById(R.id.exit);
        progressBar = findViewById(R.id.progress);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            if (StringUtils.isNotBlank(editText.getText().toString())) {
                progressBar.setVisibility(View.VISIBLE);
                positive.setVisibility(View.GONE);
                negative.setEnabled(false);
                Hin2nService.COMMUNITY_CODE = editText.getText().toString();
                EdgeStatus.RunningStatus status = Hin2nService.INSTANCE == null ? EdgeStatus.RunningStatus.DISCONNECT : Hin2nService.INSTANCE.getCurrentStatus();
                if (Hin2nService.INSTANCE != null && status != EdgeStatus.RunningStatus.DISCONNECT && status != EdgeStatus.RunningStatus.FAILED) {
                    System.out.println("stop");
                    Hin2nService.INSTANCE.stop(null);
                }
                Intent vpnPrepareIntent = menuHelper != null ? VpnService.prepare(menuHelper.context) : VpnService.prepare(multiPlayerUI.context);
                if (vpnPrepareIntent != null) {
                    if (menuHelper != null) {
                        menuHelper.activity.startActivityForResult(vpnPrepareIntent, Hin2nService.VPN_REQUEST_CODE_JOIN);
                    }
                    else {
                        multiPlayerUI.activity.startActivityForResult(vpnPrepareIntent, Hin2nService.VPN_REQUEST_CODE_JOIN);
                    }
                } else {
                    if (menuHelper != null) {
                        menuHelper.onActivityResult(Hin2nService.VPN_REQUEST_CODE_JOIN, RESULT_OK, null);
                    }
                    else {
                        multiPlayerUI.onActivityResult(Hin2nService.VPN_REQUEST_CODE_JOIN, RESULT_OK, null);
                    }
                }
            }
            else {
                Toast.makeText(getContext(), getContext().getString(R.string.dialog_join_community_code_empty), Toast.LENGTH_SHORT).show();
            }
        }
        if (view == negative) {
            dismiss();
        }
    }
}
