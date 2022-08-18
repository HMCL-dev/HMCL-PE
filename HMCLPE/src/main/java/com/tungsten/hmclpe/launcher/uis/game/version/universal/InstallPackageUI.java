package com.tungsten.hmclpe.launcher.uis.game.version.universal;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FileChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.mod.ManuallyCreatedModpackException;
import com.tungsten.hmclpe.launcher.mod.Modpack;
import com.tungsten.hmclpe.launcher.mod.ModpackHelper;
import com.tungsten.hmclpe.launcher.mod.UnsupportedModpackException;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.io.FileUtils;
import com.tungsten.hmclpe.utils.io.ZipTools;
import com.tungsten.hmclpe.utils.string.StringUtils;

import java.io.File;
import java.io.IOException;

public class InstallPackageUI extends BaseUI implements View.OnClickListener {

    public static final int SELECT_PACKAGE_REQUEST = 5700;

    public LinearLayout installPackageUI;

    private LinearLayout selectLayout;
    private LinearLayout installLayout;
    private ProgressBar progressBar;

    private LinearLayout installLocal;
    private LinearLayout installOnline;

    private TextView pathText;
    private TextView nameText;
    private TextView versionText;
    private TextView authorText;
    private EditText editName;

    private Button showDescription;
    private Button install;

    public Modpack modpack;

    public InstallPackageUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        installPackageUI = activity.findViewById(R.id.ui_install_package);

        selectLayout = activity.findViewById(R.id.select_package_layout);
        installLayout = activity.findViewById(R.id.install_package_layout);
        progressBar = activity.findViewById(R.id.loading_package_info_progress);

        installLocal = activity.findViewById(R.id.install_package_local);
        installOnline = activity.findViewById(R.id.install_package_online);
        installLocal.setOnClickListener(this);
        installOnline.setOnClickListener(this);

        pathText = activity.findViewById(R.id.package_path);
        nameText = activity.findViewById(R.id.package_name);
        versionText = activity.findViewById(R.id.package_version);
        authorText = activity.findViewById(R.id.package_author);
        editName = activity.findViewById(R.id.edit_package_name);

        showDescription = activity.findViewById(R.id.show_package_description);
        install = activity.findViewById(R.id.install_package);
        showDescription.setOnClickListener(this);
        install.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.install_package_ui_title),false,true);
        CustomAnimationUtils.showViewFromLeft(installPackageUI,activity,context,true);
        selectLayout.setVisibility(View.VISIBLE);
        installLayout.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(installPackageUI,activity,context,true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_PACKAGE_REQUEST && resultCode != Activity.RESULT_OK) {
            activity.backToLastUI();
        }
        if (requestCode == SELECT_PACKAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri uri = data.getData();
            String path = UriUtils.getRealPathFromUri_AboveApi19(context,uri);
            selectLayout.setVisibility(View.GONE);
            installLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            new Thread(() -> {
                try {
                    modpack = ModpackHelper.readModpackManifest(new File(path).toPath(), ZipTools.findSuitableEncoding(new File(path).toPath()));
                    activity.runOnUiThread(() -> {
                        selectLayout.setVisibility(View.GONE);
                        installLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        pathText.setText(path);
                        nameText.setText((modpack.getName() == null || StringUtils.isBlank(modpack.getName())) ? FileUtils.getNameWithoutExtension(new File(path)) : modpack.getName());
                        versionText.setText(modpack.getVersion() == null ? "" : modpack.getVersion());
                        authorText.setText(modpack.getAuthor() == null ? "" : modpack.getAuthor());
                        editName.setText((modpack.getName() == null || StringUtils.isBlank(modpack.getName())) ? FileUtils.getNameWithoutExtension(new File(path)) : modpack.getName());
                    });
                } catch (ManuallyCreatedModpackException e) {
                    e.printStackTrace();
                    modpack = null;
                    activity.runOnUiThread(() -> {
                        selectLayout.setVisibility(View.GONE);
                        installLayout.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        pathText.setText(path);
                        nameText.setText(new File(path).getName());
                        versionText.setText("");
                        authorText.setText("");
                        editName.setText(FileUtils.getNameWithoutExtension(new File(path)));
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.dialog_package_manual_warn_title));
                        builder.setMessage(context.getString(R.string.dialog_package_manual_warn_msg));
                        builder.setPositiveButton(context.getString(R.string.dialog_package_manual_warn_positive), null);
                        builder.setNegativeButton(context.getString(R.string.dialog_package_manual_warn_negative), (dialogInterface, i) -> {
                            activity.backToLastUI();
                        });
                        builder.create().show();
                    });
                } catch (UnsupportedModpackException | IOException e) {
                    e.printStackTrace();
                    modpack = null;
                    activity.runOnUiThread(() -> {
                        activity.backToLastUI();
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle(context.getString(R.string.dialog_package_not_support_title));
                        builder.setMessage(context.getString(R.string.dialog_package_not_support_msg));
                        builder.setPositiveButton(context.getString(R.string.dialog_package_not_support_exit), null);
                        builder.create().show();
                    });
                }
            }).start();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == installLocal) {
            Intent intent = new Intent(context, FileChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.ALLOWED_FILE_EXTENSIONS, "zip;mrpack");
            intent.putExtra(Constants.INITIAL_DIRECTORY, new File(Environment.getExternalStorageDirectory().getAbsolutePath()).getAbsolutePath());
            activity.startActivityForResult(intent, SELECT_PACKAGE_REQUEST);
        }
        if (view == installOnline) {

        }

        if (view == showDescription) {
            if (modpack != null && modpack.getDescription() != null && StringUtils.isNotBlank(modpack.getDescription())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(context.getString(R.string.dialog_package_description_title));
                CharSequence charSequence = Html.fromHtml(modpack.getDescription(), 0);
                builder.setMessage(charSequence);
                builder.setPositiveButton(context.getString(R.string.dialog_package_description_positive), null);
                builder.create().show();
            }
        }
        if (view == install) {

        }
    }
}
