package com.tungsten.hmclpe.launcher.dialogs;

import static com.tungsten.hmclpe.utils.Lang.mapOf;
import static com.tungsten.hmclpe.utils.Pair.pair;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.mod.LocalModFile;
import com.tungsten.hmclpe.launcher.mod.ModManager;
import com.tungsten.hmclpe.utils.io.NetworkUtils;
import com.tungsten.hmclpe.utils.io.ZipTools;
import com.tungsten.hmclpe.utils.string.ModTranslations;
import com.tungsten.hmclpe.utils.string.StringUtils;

import java.io.IOException;
import java.io.InputStream;

public class ModInfoDialog extends Dialog implements View.OnClickListener {

    private LocalModFile localModFile;

    private ImageView icon;
    private TextView name;
    private TextView version;
    private TextView fileName;
    private TextView description;
    private LinearLayout official;
    private LinearLayout mcbbs;
    private LinearLayout mcmod;
    private Button exit;

    private ModTranslations.Mod modTranslations;

    public ModInfoDialog(@NonNull Context context, LocalModFile localModFile) {
        super(context);
        this.localModFile = localModFile;
        setContentView(R.layout.dialog_local_mod_info);
        setCancelable(false);
        init();
    }

    private void init() {
        icon = findViewById(R.id.icon);
        name = findViewById(R.id.name);
        version = findViewById(R.id.version);
        fileName = findViewById(R.id.file_name);
        description = findViewById(R.id.description);
        official = findViewById(R.id.official_link);
        mcbbs = findViewById(R.id.mcbbs_link);
        mcmod = findViewById(R.id.mcmod_link);
        exit = findViewById(R.id.exit);

        String unknown = getContext().getString(R.string.mod_manager_ui_unknown_info);
        String mName = StringUtils.isBlank(localModFile.getName()) ? unknown : localModFile.getName();
        String mVersion = StringUtils.isBlank(localModFile.getVersion()) ? unknown : localModFile.getVersion();
        if (StringUtils.isNotBlank(localModFile.getLogoPath())) {
            try {
                InputStream inputStream = ZipTools.getFileInputStream(localModFile.getFile().toString(),localModFile.getLogoPath());
                if (inputStream != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                    icon.setBackground(new BitmapDrawable(bitmap));
                    inputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        name.setText(mName);
        version.setText(mVersion);
        fileName.setText(localModFile.getFile().toFile().getName());
        description.setText(localModFile.getDescription().toString());

        official.setVisibility(StringUtils.isNotBlank(localModFile.getUrl()) ? View.VISIBLE : View.GONE);
        modTranslations = ModTranslations.MOD.getModById(localModFile.getId());
        mcbbs.setVisibility(modTranslations != null && StringUtils.isNotBlank(modTranslations.getMcbbs()) ? View.VISIBLE : View.GONE);
        for (int i = 0;i < mcmod.getChildCount();i++) {
            if (mcmod.getChildAt(i) instanceof TextView) {
                if (modTranslations == null || StringUtils.isBlank(modTranslations.getMcmod())) {
                    ((TextView) mcmod.getChildAt(i)).setText(getContext().getString(R.string.mod_manager_ui_mcmod_search));
                }
                else {
                    ((TextView) mcmod.getChildAt(i)).setText(getContext().getString(R.string.mod_manager_ui_mcmod_page));
                }
                break;
            }
        }

        official.setOnClickListener(this);
        mcbbs.setOnClickListener(this);
        mcmod.setOnClickListener(this);
        exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == official) {
            Uri uri = Uri.parse(localModFile.getUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
        if (view == mcbbs) {
            Uri uri = Uri.parse(ModManager.getMcbbsUrl(modTranslations.getMcbbs()));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
        if (view == mcmod) {
            Uri uri;
            if (modTranslations == null || StringUtils.isBlank(modTranslations.getMcmod())) {
                uri = Uri.parse(NetworkUtils.withQuery("https://search.mcmod.cn/s", mapOf(
                        pair("key", localModFile.getName()),
                        pair("site", "all"),
                        pair("filter", "0")
                )));
            }
            else {
                uri = Uri.parse(ModManager.getMcmodUrl(modTranslations.getMcmod()));
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            getContext().startActivity(intent);
        }
        if (view == exit) {
            dismiss();
        }
    }
}
