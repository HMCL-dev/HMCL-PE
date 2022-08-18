package com.tungsten.hmclpe.launcher.uis.game.download.right;

import static com.tungsten.hmclpe.launcher.mod.RemoteModRepository.DEFAULT_GAME_VERSIONS;
import static java.util.stream.Collectors.toList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.download.DownloadResourceAdapter;
import com.tungsten.hmclpe.launcher.mod.LocalizedRemoteModRepository;
import com.tungsten.hmclpe.launcher.mod.RemoteMod;
import com.tungsten.hmclpe.launcher.mod.RemoteModRepository;
import com.tungsten.hmclpe.launcher.mod.curse.CurseForgeRemoteModRepository;
import com.tungsten.hmclpe.launcher.mod.modrinth.ModrinthRemoteModRepository;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.launcher.view.spinner.CategorySpinnerAdapter;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DownloadPackageUI extends BaseUI implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextView.OnEditorActionListener, TextWatcher {

    public LinearLayout downloadPackageUI;

    private EditText editName;
    private EditText editVersion;
    private Spinner downloadSourceSpinner;
    private Spinner editVersionSpinner;
    private Spinner editCategory;
    private Spinner editSort;
    private Button installPackage;
    private Button search;

    private ArrayList<String> sourceList;
    private ArrayAdapter<String> sourceListAdapter;
    private ArrayList<String> sortList;
    private ArrayAdapter<String> sortListAdapter;
    private ArrayList<String> versionList;
    private ArrayAdapter<String> versionListAdapter;
    private ArrayList<RemoteModRepository.Category> categoryList;
    private CategorySpinnerAdapter categoryListAdapter;

    private boolean isSearching = false;

    private ProgressBar progressBar;
    private TextView refreshText;

    private ListView packageListView;
    private ArrayList<RemoteMod> packageList;
    private DownloadResourceAdapter downloadPackageListAdapter;

    private RemoteModRepository repository;

    public DownloadPackageUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    private class Repository extends LocalizedRemoteModRepository {

        @Override
        protected RemoteModRepository getBackedRemoteModRepository() {
            if (downloadSourceSpinner.getSelectedItemPosition() == 1) {
                return ModrinthRemoteModRepository.MODPACKS;
            } else {
                return CurseForgeRemoteModRepository.MODPACKS;
            }
        }

        @Override
        public Type getType() {
            return Type.MODPACK;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadPackageUI = activity.findViewById(R.id.ui_download_package);

        editName = activity.findViewById(R.id.download_package_arg_name);
        downloadSourceSpinner = activity.findViewById(R.id.download_package_arg_source);
        editVersion = activity.findViewById(R.id.edit_download_package_arg_version);
        editVersionSpinner = activity.findViewById(R.id.download_package_arg_version);
        editCategory = activity.findViewById(R.id.download_package_arg_type);
        editSort = activity.findViewById(R.id.download_package_arg_sort);

        installPackage = activity.findViewById(R.id.install_package_from_download_page);
        installPackage.setOnClickListener(this);
        search = activity.findViewById(R.id.search_package);
        search.setOnClickListener(this);

        sourceList = new ArrayList<>();
        sourceList.add(context.getString(R.string.download_mod_source_curse_forge));
        sourceList.add(context.getString(R.string.download_mod_source_modrinth));
        sourceListAdapter = new ArrayAdapter<>(context, R.layout.item_spinner, sourceList);
        sourceListAdapter.setDropDownViewResource(R.layout.item_spinner_drop_down);
        downloadSourceSpinner.setAdapter(sourceListAdapter);

        sortList = new ArrayList<>();
        sortList.add(context.getString(R.string.download_mod_sort_date));
        sortList.add(context.getString(R.string.download_mod_sort_heat));
        sortList.add(context.getString(R.string.download_mod_sort_recent));
        sortList.add(context.getString(R.string.download_mod_sort_name));
        sortList.add(context.getString(R.string.download_mod_sort_author));
        sortList.add(context.getString(R.string.download_mod_sort_downloads));
        sortList.add(context.getString(R.string.download_mod_sort_category));
        sortList.add(context.getString(R.string.download_mod_sort_game_version));
        sortListAdapter = new ArrayAdapter<String>(context,R.layout.item_spinner,sortList);
        sortListAdapter.setDropDownViewResource(R.layout.item_spinner_drop_down);
        editSort.setAdapter(sortListAdapter);

        versionList = new ArrayList<>();
        versionList.add("");
        versionList.addAll(Arrays.asList(DEFAULT_GAME_VERSIONS));
        versionListAdapter = new ArrayAdapter<String>(context,R.layout.item_spinner,versionList);
        versionListAdapter.setDropDownViewResource(R.layout.item_spinner_drop_down);
        editVersionSpinner.setAdapter(versionListAdapter);

        categoryList = new ArrayList<>();
        categoryList.add(new RemoteModRepository.Category(CurseForgeRemoteModRepository.CATEGORY_ALL, "0", new ArrayList<>()));
        categoryListAdapter = new CategorySpinnerAdapter(context,categoryList,CurseForgeRemoteModRepository.SECTION_MODPACK);
        editCategory.setAdapter(categoryListAdapter);

        downloadSourceSpinner.setOnItemSelectedListener(this);
        editVersionSpinner.setOnItemSelectedListener(this);
        editCategory.setOnItemSelectedListener(this);
        editSort.setOnItemSelectedListener(this);

        editName.setOnEditorActionListener(this);
        editVersion.setOnEditorActionListener(this);
        editVersion.addTextChangedListener(this);

        progressBar = activity.findViewById(R.id.loading_download_package_list_progress);
        refreshText = activity.findViewById(R.id.refresh_package_list);
        refreshText.setOnClickListener(this);

        repository = new Repository();

        packageListView = activity.findViewById(R.id.download_package_list);
        packageList = new ArrayList<>();
        downloadPackageListAdapter = new DownloadResourceAdapter(context,activity,repository,packageList,1);
        packageListView.setAdapter(downloadPackageListAdapter);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(downloadPackageUI,activity,context,false);
        activity.uiManager.downloadUI.startDownloadPackageUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        init();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(downloadPackageUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.downloadUI.startDownloadPackageUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    private void init(){
        if (packageList.size() == 0 && editName.getText().toString().equals("")){
            search();
        }
    }

    private void search(){
        if (!isSearching){
            new Thread(() -> {
                try {
                    searchHandler.sendEmptyMessage(0);
                    List<RemoteMod> list = repository.search(editVersion.getText().toString(), (RemoteModRepository.Category) categoryListAdapter.getItem(editCategory.getSelectedItemPosition()), 0, 50, editName.getText().toString(), RemoteMod.getSortTypeByPosition(editSort.getSelectedItemPosition()), RemoteModRepository.SortOrder.DESC).collect(toList());
                    packageList.clear();
                    packageList.addAll(list);
                    List<RemoteModRepository.Category> categories = repository.getCategories().collect(toList());
                    categoryList.clear();
                    categoryList.add(new RemoteModRepository.Category(downloadSourceSpinner.getSelectedItemPosition() == 0 ? CurseForgeRemoteModRepository.CATEGORY_ALL : ModrinthRemoteModRepository.CATEGORY_ALL, downloadSourceSpinner.getSelectedItemPosition() == 0 ? "0" : "all", new ArrayList<>()));
                    for (int i = 0;i < categories.size();i++) {
                        categoryList.add(categories.get(i));
                        categoryList.addAll(categories.get(i).getSubcategories());
                    }
                    searchHandler.sendEmptyMessage(1);
                } catch (Exception e) {
                    searchHandler.sendEmptyMessage(2);
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler searchHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0){
                isSearching = true;
                progressBar.setVisibility(View.VISIBLE);
                refreshText.setVisibility(View.GONE);
                packageListView.setVisibility(View.GONE);
            }
            if (msg.what == 1) {
                downloadPackageListAdapter.notifyDataSetChanged();
                categoryListAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                refreshText.setVisibility(View.GONE);
                packageListView.setVisibility(View.VISIBLE);
                isSearching = false;
            }
            if (msg.what == 2) {
                progressBar.setVisibility(View.GONE);
                refreshText.setVisibility(View.VISIBLE);
                packageListView.setVisibility(View.GONE);
                isSearching = false;
            }
        }
    };

    @Override
    public void onClick(View view) {
        if (view == installPackage) {
            activity.uiManager.switchMainUI(activity.uiManager.installPackageUI);
        }
        if (view == search){
            search();
        }
        if (view == refreshText) {
            search();
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        search();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView == editCategory || adapterView == editSort || adapterView == editVersionSpinner){
            search();
            if (adapterView == editVersionSpinner){
                editVersion.setText((String) adapterView.getItemAtPosition(i));
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (textView == editName || textView == editVersion){
            search();
        }
        return false;
    }
}
