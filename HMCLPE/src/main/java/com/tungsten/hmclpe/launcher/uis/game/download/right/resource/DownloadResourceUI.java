package com.tungsten.hmclpe.launcher.uis.game.download.right.resource;

import static com.tungsten.hmclpe.launcher.mod.ModManager.getMcbbsUrl;
import static com.tungsten.hmclpe.launcher.mod.ModManager.getMcmodUrl;
import static com.tungsten.hmclpe.utils.Lang.mapOf;
import static com.tungsten.hmclpe.utils.Pair.pair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.dialogs.EditDownloadNameDialog;
import com.tungsten.hmclpe.launcher.list.download.ModDependencyAdapter;
import com.tungsten.hmclpe.launcher.list.download.ModGameVersionAdapter;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.mod.RemoteModRepository;
import com.tungsten.hmclpe.launcher.mod.curse.CurseForgeRemoteModRepository;
import com.tungsten.hmclpe.utils.LocaleUtils;
import com.tungsten.hmclpe.utils.SimpleMultimap;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.io.NetworkUtils;
import com.tungsten.hmclpe.utils.string.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;

public class DownloadResourceUI extends BaseDownloadUI implements View.OnClickListener {

    private ImageView icon;
    private TextView name;
    private TextView type;
    private TextView description;
    private LinearLayout mcmod;
    private LinearLayout mcbbs;
    private LinearLayout curseForge;
    private LinearLayout modrinth;

    private ProgressBar progressBar;
    private TextView refreshText;
    private LinearLayout dependencyLayout;
    private ListView dependencyList;
    private ListView versionList;

    private ModGameVersionAdapter modGameVersionAdapter;
    private ModDependencyAdapter modDependencyAdapter;

    public RemoteMod.Version selectedVersion;

    public static final int DOWNLOAD_RESOURCE_REQUEST = 2700;

    public DownloadResourceUI(Context context, MainActivity activity, RemoteModRepository repository, RemoteMod bean, int resourceType) {
        super(context, activity, repository, bean, resourceType);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        icon = findViewById(R.id.resource_icon);
        name = findViewById(R.id.resource_name);
        type = findViewById(R.id.resource_type);
        description = findViewById(R.id.resource_description);
        mcmod = findViewById(R.id.mcmod_link);
        mcbbs = findViewById(R.id.mcbbs_link);
        curseForge = findViewById(R.id.curse_forge_link);
        modrinth = findViewById(R.id.modrinth_link);

        mcmod.setOnClickListener(this);
        mcbbs.setOnClickListener(this);
        curseForge.setOnClickListener(this);
        modrinth.setOnClickListener(this);

        progressBar = findViewById(R.id.mod_info_progress);
        refreshText = findViewById(R.id.mod_load_fail_text);
        dependencyLayout = findViewById(R.id.dependency_layout);
        dependencyList = findViewById(R.id.dependency_list);
        versionList = findViewById(R.id.mod_version_list);

        refreshText.setOnClickListener(this);

        new Thread(() -> {
            try {
                URL url = new URL(bean.getIconUrl());
                HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
                httpURLConnection.setDoInput(true);
                httpURLConnection.connect();
                InputStream inputStream = httpURLConnection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                activity.runOnUiThread(() -> {
                    icon.setImageBitmap(bitmap);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
        name.setText(bean.getTitle());
        if (LocaleUtils.isChinese(context)) {
            name.setText(modTranslation != null ? modTranslation.getDisplayName() : bean.getTitle());
        }
        StringBuilder categories = new StringBuilder();
        for (String category : bean.getCategories()) {
            boolean isCurse = bean.getPageUrl() != null && bean.getPageUrl().contains("curseforge");
            String c;
            int resId = context.getResources().getIdentifier((isCurse ? "curse_category_" : "modrinth_category_") + category.replace("-","_"),"string","com.tungsten.hmclpe");
            if (resId != 0 && context.getString(resId) != null) {
                c = context.getString(resId);
            }
            else {
                c = category;
            }
            categories.append(c).append("   ");
        }
        type.setText(categories.toString());
        description.setText(bean.getDescription());

        mcmod.setVisibility(resourceType == 0 ? View.VISIBLE : View.GONE);
        mcbbs.setVisibility(modTranslation != null && StringUtils.isNotBlank(modTranslation.getMcbbs()) ? View.VISIBLE : View.GONE);
        curseForge.setVisibility((bean.getPageUrl() != null && bean.getPageUrl().contains("curseforge")) ? View.VISIBLE : View.GONE);
        modrinth.setVisibility((bean.getPageUrl() != null && !bean.getPageUrl().contains("curseforge")) ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (isFirst) {
            refresh();
            isFirst = false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == DOWNLOAD_RESOURCE_REQUEST && resultCode == Activity.RESULT_OK && data != null && selectedVersion != null) {
            Uri uri = data.getData();
            String dir = UriUtils.getRealPathFromUri_AboveApi19(context,uri);
            EditDownloadNameDialog dialog = new EditDownloadNameDialog(context, this, selectedVersion, false, dir);
            dialog.show();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == mcmod) {
            Uri uri;
            if (modTranslation == null || StringUtils.isBlank(modTranslation.getMcmod())) {
                uri = Uri.parse(NetworkUtils.withQuery("https://search.mcmod.cn/s", mapOf(
                        pair("key", bean.getSlug()),
                        pair("site", "all"),
                        pair("filter", "0")
                )));
            }
            else {
                uri = Uri.parse(getMcmodUrl(modTranslation.getMcmod()));
            }
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        if (view == mcbbs) {
            Uri uri = Uri.parse(getMcbbsUrl(modTranslation.getMcbbs()));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        if (view == curseForge || view == modrinth) {
            Uri uri = Uri.parse(bean.getPageUrl());
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            context.startActivity(intent);
        }
        if (view == refreshText) {
            refresh();
        }
    }

    private SimpleMultimap<String, RemoteMod.Version> sortVersions(Stream<RemoteMod.Version> versions) {
        SimpleMultimap<String, RemoteMod.Version> classifiedVersions = new SimpleMultimap<>(HashMap::new, ArrayList::new);
        versions.forEach(version -> {
            for (String gameVersion : version.getGameVersions()) {
                classifiedVersions.put(gameVersion, version);
            }
        });

        for (String gameVersion : classifiedVersions.keys()) {
            List<RemoteMod.Version> versionList = (List<RemoteMod.Version>) classifiedVersions.get(gameVersion);
            versionList.sort(Comparator.comparing(RemoteMod.Version::getDatePublished).reversed());
        }
        return classifiedVersions;
    }

    public void refresh() {
        new Thread(() -> {
            activity.runOnUiThread(() -> {
                dependencyLayout.setVisibility(View.GONE);
                versionList.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                refreshText.setVisibility(View.GONE);
            });
            try {
                List<RemoteMod> dependencies = bean.getData().loadDependencies(repository);
                SimpleMultimap<String, RemoteMod.Version> versions = sortVersions(bean.getData().loadVersions(repository));
                if (dependencies.size() == 0) {
                    modGameVersionAdapter = new ModGameVersionAdapter(context,versions,this);
                    activity.runOnUiThread(() -> {
                        dependencyLayout.setVisibility(View.GONE);
                        versionList.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        refreshText.setVisibility(View.GONE);
                        versionList.setAdapter(modGameVersionAdapter);
                        reSetListViewHeight(versionList,getVersionListHeight(versionList) - versionList.getLayoutParams().height);
                    });
                }
                else {
                    modDependencyAdapter = new ModDependencyAdapter(context,activity,repository,dependencies);
                    modGameVersionAdapter = new ModGameVersionAdapter(context,versions,this);
                    activity.runOnUiThread(() -> {
                        dependencyLayout.setVisibility(View.VISIBLE);
                        versionList.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                        refreshText.setVisibility(View.GONE);
                        dependencyList.setAdapter(modDependencyAdapter);
                        versionList.setAdapter(modGameVersionAdapter);
                        reSetListViewHeight(dependencyList,getDependencyListHeight(dependencyList) - dependencyList.getLayoutParams().height);
                        reSetListViewHeight(versionList,getVersionListHeight(versionList) - versionList.getLayoutParams().height);
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(() -> {
                    dependencyLayout.setVisibility(View.GONE);
                    versionList.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    refreshText.setVisibility(View.VISIBLE);
                });
            }
        }).start();
    }

    public void refreshVersionListHeight(int change) {
        reSetListViewHeight(versionList,change);
    }

    public static int getVersionListHeight(ListView listView) {
        int count = listView.getAdapter().getCount();
        View view = listView.getAdapter().getView(0,null,listView);
        view.measure(0, 0);
        return (view.getMeasuredHeight() * count) + (listView.getDividerHeight() * (count - 1));
    }

    public static int getDependencyListHeight(ListView listView) {
        int count = listView.getAdapter().getCount();
        View view = listView.getAdapter().getView(0,null,listView);
        view.measure(0, 0);
        return (view.getMeasuredHeight() * count) + (listView.getDividerHeight() * (count - 1));
    }

    public static void reSetListViewHeight(ListView listView,int change) {
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height += change;
        listView.setLayoutParams(params);
    }
}
