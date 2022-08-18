package com.tungsten.hmclpe.launcher.dialogs.hin2n;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.VpnService;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.control.MenuHelper;
import com.tungsten.hmclpe.launcher.uis.universal.multiplayer.MultiPlayerUI;
import com.tungsten.hmclpe.multiplayer.Hin2nService;
import com.tungsten.hmclpe.multiplayer.LocalServerDetector;

import wang.switchy.hin2n.model.EdgeStatus;

public class CreateCommunityDialog extends Dialog implements View.OnClickListener {

    public static CreateCommunityDialog INSTANCE;

    private final MenuHelper menuHelper;
    private final MultiPlayerUI multiPlayerUI;
    private ProgressBar progressBar;

    private TextView nameText;
    private TextView portText;
    private TextView errorText;
    private Button positive;
    private Button negative;

    private LocalServerDetector lanServerDetectorThread;

    public CreateCommunityDialog(@NonNull Context context, MenuHelper menuHelper, MultiPlayerUI multiPlayerUI) {
        super(context);
        INSTANCE = this;
        this.menuHelper = menuHelper;
        this.multiPlayerUI = multiPlayerUI;
        setContentView(R.layout.dialog_create_community);
        setCancelable(false);
        init();
    }

    public static CreateCommunityDialog getInstance() {
        return INSTANCE;
    }

    @SuppressLint("SetTextI18n")
    private void init() {
        progressBar = findViewById(R.id.progress);

        nameText = findViewById(R.id.name);
        portText = findViewById(R.id.port);
        errorText = findViewById(R.id.error);

        positive = findViewById(R.id.create);
        negative = findViewById(R.id.exit);

        positive.setOnClickListener(this);
        negative.setOnClickListener(this);

        progressBar.setVisibility(View.VISIBLE);
        nameText.setText(getContext().getString(R.string.dialog_create_community_getting));
        portText.setText(getContext().getString(R.string.dialog_create_community_getting));
        errorText.setVisibility(View.GONE);
        positive.setEnabled(false);

        Handler handler = new Handler();
        lanServerDetectorThread = new LocalServerDetector(3);
        lanServerDetectorThread.onDetectedLanServer().register(event -> {
            handler.post(() -> {
                if (event.getLanServer() != null && event.getLanServer().isValid()) {
                    nameText.setText(event.getLanServer().getMotd());
                    portText.setText(event.getLanServer().getAd().toString());
                    positive.setEnabled(true);
                } else {
                    nameText.setText("");
                    portText.setText("");
                    errorText.setVisibility(View.VISIBLE);
                    positive.setEnabled(false);
                }
                progressBar.setVisibility(View.GONE);
            });
        });
        lanServerDetectorThread.start();
    }

    @Override
    public void onClick(View view) {
        if (view == positive) {
            Hin2nService.IP_PORT = "1.1.1.1:" + portText.getText().toString();
            EdgeStatus.RunningStatus status = Hin2nService.INSTANCE == null ? EdgeStatus.RunningStatus.DISCONNECT : Hin2nService.INSTANCE.getCurrentStatus();
            if (Hin2nService.INSTANCE != null && status != EdgeStatus.RunningStatus.DISCONNECT && status != EdgeStatus.RunningStatus.FAILED) {
                Hin2nService.INSTANCE.stop(null);
            }
            Intent vpnPrepareIntent = menuHelper != null ? VpnService.prepare(menuHelper.context) : VpnService.prepare(multiPlayerUI.context);
            if (vpnPrepareIntent != null) {
                if (menuHelper != null) {
                    menuHelper.activity.startActivityForResult(vpnPrepareIntent, Hin2nService.VPN_REQUEST_CODE_CREATE);
                }
                else {
                    multiPlayerUI.activity.startActivityForResult(vpnPrepareIntent, Hin2nService.VPN_REQUEST_CODE_CREATE);
                }
            } else {
                if (menuHelper != null) {
                    menuHelper.onActivityResult(Hin2nService.VPN_REQUEST_CODE_CREATE, RESULT_OK, null);
                }
                else {
                    multiPlayerUI.onActivityResult(Hin2nService.VPN_REQUEST_CODE_CREATE, RESULT_OK, null);
                }
            }
            dismiss();
        }
        if (view == negative) {
            lanServerDetectorThread.interrupt();
            dismiss();
        }
    }
}
