package com.tungsten.hmclpe.launcher.dialogs.hin2n;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.multiplayer.Hin2nService;
import com.tungsten.hmclpe.multiplayer.ServerType;

public class Hin2nInfoDialog extends Dialog implements View.OnClickListener {

    private String inviteCode;
    private String ipPort;

    private LinearLayout layout;
    private TextView inviteCodeText;
    private TextView ipPortText;
    private ImageButton copyInviteCode;
    private ImageButton copyIpPort;

    private Button exit;
    private Button positive;

    public Hin2nInfoDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.dialog_hin2n_community);
        setCancelable(false);
        init();
    }

    private void init() {
        inviteCode = Hin2nService.COMMUNITY_CODE;
        ipPort = Hin2nService.IP_PORT;

        layout = findViewById(R.id.ip_port_layout);
        inviteCodeText = findViewById(R.id.invite_code);
        ipPortText = findViewById(R.id.ip_port);
        copyInviteCode = findViewById(R.id.copy_invite_code);
        copyIpPort = findViewById(R.id.copy_ip_port);
        exit = findViewById(R.id.exit);
        positive = findViewById(R.id.positive);

        copyInviteCode.setOnClickListener(this);
        copyIpPort.setOnClickListener(this);
        exit.setOnClickListener(this);
        positive.setOnClickListener(this);

        inviteCodeText.setText(inviteCode);
        if (Hin2nService.SERVER_TYPE == ServerType.SERVER) {
            layout.setVisibility(View.VISIBLE);
            ipPortText.setText(ipPort);
        }
        else {
            layout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        if (view == copyInviteCode) {
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText(null, inviteCode);
            clip.setPrimaryClip(data);
            Toast.makeText(getContext(), getContext().getString(R.string.dialog_community_copy_success), Toast.LENGTH_SHORT).show();
        }
        if (view == copyIpPort) {
            ClipboardManager clip = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
            ClipData data = ClipData.newPlainText(null, ipPort);
            clip.setPrimaryClip(data);
            Toast.makeText(getContext(), getContext().getString(R.string.dialog_community_copy_success), Toast.LENGTH_SHORT).show();
        }
        if (view == exit) {
            Hin2nService.INSTANCE.stop(null);
            dismiss();
        }
        if (view == positive) {
            dismiss();
        }
    }
}
