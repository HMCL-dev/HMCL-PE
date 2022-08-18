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
import com.tungsten.hmclpe.launcher.setting.SettingUtils;
import com.tungsten.hmclpe.launcher.view.spinner.CategorySpinnerAdapter;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class DownloadModUI extends BaseUI implements View.OnClickListener, AdapterView.OnItemSelectedListener, TextWatcher, TextView.OnEditorActionListener {

    public LinearLayout downloadModUI;

    public String lastVersion;
    public String gameVersion;

    private Spinner gameSpinner;
    private Spinner downloadSourceSpinner;
    private EditText editName;
    private EditText editVersion;
    private Spinner versionSpinner;
    private Spinner typeSpinner;
    private Spinner sortSpinner;
    private Button search;

    private ArrayList<String> gameList;
    private ArrayAdapter<String> gameListAdapter;
    private ArrayList<String> sourceList;
    private ArrayAdapter<String> sourceListAdapter;
    private ArrayList<String> versionList;
    private ArrayAdapter<String> versionListAdapter;
    private ArrayList<RemoteModRepository.Category> categoryList;
    private CategorySpinnerAdapter categoryListAdapter;
    private ArrayList<String> sortList;
    private ArrayAdapter<String> sortListAdapter;

    private ListView modListView;
    private ArrayList<RemoteMod> modList;
    private DownloadResourceAdapter modListAdapter;
    private ProgressBar progressBar;
    private TextView refreshText;
    private boolean isSearching = false;

    private RemoteModRepository repository;

    public DownloadModUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    private class Repository extends LocalizedRemoteModRepository {

        @Override
        protected RemoteModRepository getBackedRemoteModRepository() {
            if (downloadSourceSpinner.getSelectedItemPosition() == 1) {
                return ModrinthRemoteModRepository.MODS;
            } else {
                return CurseForgeRemoteModRepository.MODS;
            }
        }

        @Override
        public Type getType() {
            return Type.MOD;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadModUI = activity.findViewById(R.id.ui_download_mod);

        gameSpinner = activity.findViewById(R.id.download_mod_arg_game);
        downloadSourceSpinner = activity.findViewById(R.id.download_mod_arg_source);
        editName = activity.findViewById(R.id.download_mod_arg_name);
        editVersion = activity.findViewById(R.id.edit_download_mod_arg_version);
        versionSpinner = activity.findViewById(R.id.download_mod_arg_version);
        typeSpinner = activity.findViewById(R.id.download_mod_arg_type);
        sortSpinner = activity.findViewById(R.id.download_mod_arg_sort);

        gameList = SettingUtils.getLocalVersionNames(activity.launcherSetting.gameFileDirectory);
        gameListAdapter = new ArrayAdapter<>(context,R.layout.item_spinner,gameList);
        gameSpinner.setAdapter(gameListAdapter);

        sourceList = new ArrayList<>();
        sourceList.add(context.getString(R.string.download_mod_source_curse_forge));
        sourceList.add(context.getString(R.string.download_mod_source_modrinth));
        sourceListAdapter = new ArrayAdapter<>(context, R.layout.item_spinner, sourceList);
        sourceListAdapter.setDropDownViewResource(R.layout.item_spinner_drop_down);
        downloadSourceSpinner.setAdapter(sourceListAdapter);

        versionList = new ArrayList<>();
        versionList.add("");
        versionList.addAll(Arrays.asList(DEFAULT_GAME_VERSIONS));
        versionListAdapter = new ArrayAdapter<>(context, R.layout.item_spinner, versionList);
        versionListAdapter.setDropDownViewResource(R.layout.item_spinner_drop_down);
        versionSpinner.setAdapter(versionListAdapter);

        categoryList = new ArrayList<>();
        categoryList.add(new RemoteModRepository.Category(CurseForgeRemoteModRepository.CATEGORY_ALL,"0",new ArrayList<>()));
        categoryListAdapter = new CategorySpinnerAdapter(context,categoryList,CurseForgeRemoteModRepository.SECTION_MOD);
        typeSpinner.setAdapter(categoryListAdapter);

        sortList = new ArrayList<>();
        sortList.add(context.getString(R.string.download_mod_sort_date));
        sortList.add(context.getString(R.string.download_mod_sort_heat));
        sortList.add(context.getString(R.string.download_mod_sort_recent));
        sortList.add(context.getString(R.string.download_mod_sort_name));
        sortList.add(context.getString(R.string.download_mod_sort_author));
        sortList.add(context.getString(R.string.download_mod_sort_downloads));
        sortList.add(context.getString(R.string.download_mod_sort_category));
        sortList.add(context.getString(R.string.download_mod_sort_game_version));
        sortListAdapter = new ArrayAdapter<>(context, R.layout.item_spinner, sortList);
        sortListAdapter.setDropDownViewResource(R.layout.item_spinner_drop_down);
        sortSpinner.setAdapter(sortListAdapter);

        gameSpinner.setOnItemSelectedListener(this);
        downloadSourceSpinner.setOnItemSelectedListener(this);
        versionSpinner.setOnItemSelectedListener(this);
        typeSpinner.setOnItemSelectedListener(this);
        sortSpinner.setOnItemSelectedListener(this);

        search = activity.findViewById(R.id.search_mod);

        search.setOnClickListener(this);

        editName.setOnEditorActionListener(this);
        editVersion.setOnEditorActionListener(this);
        editVersion.addTextChangedListener(this);

        repository = new Repository();

        modListView = activity.findViewById(R.id.download_mod_list);
        modList = new ArrayList<>();
        modListAdapter = new DownloadResourceAdapter(context,activity,repository,modList,0);
        modListView.setAdapter(modListAdapter);

        progressBar = activity.findViewById(R.id.loading_download_mod_list_progress);
        refreshText = activity.findViewById(R.id.refresh_mod_list);
        refreshText.setOnClickListener(this);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStart() {
        super.onStart();
        CustomAnimationUtils.showViewFromLeft(downloadModUI,activity,context,false);
        activity.uiManager.downloadUI.startDownloadModUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_white));
        init();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(downloadModUI,activity,context,false);
        if (activity.isLoaded){
            activity.uiManager.downloadUI.startDownloadModUI.setBackground(context.getResources().getDrawable(R.drawable.launcher_button_parent));
        }
    }

    @Override
    public void onClick(View v) {
        if (v == search){
            search();
        }
        if (v == refreshText) {
            search();
        }
    }

    private void init(){
        if (modList.size() == 0 && editName.getText().toString().equals("")){
            search();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == typeSpinner || parent == sortSpinner || parent == versionSpinner){
            search();
            if (parent == versionSpinner){
                editVersion.setText((String) parent.getItemAtPosition(position));
            }
        }
        if (parent == gameSpinner) {
            gameVersion = gameList.size() > 0 ? gameSpinner.getSelectedItem().toString() : null;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void refreshGameList() {
        boolean bool = false;
        if (!Objects.equals(lastVersion, activity.publicGameSetting.currentVersion)) {
            bool = true;
            lastVersion = activity.publicGameSetting.currentVersion;
        }
        gameList = SettingUtils.getLocalVersionNames(activity.launcherSetting.gameFileDirectory);
        gameListAdapter = new ArrayAdapter<>(context,R.layout.item_spinner,gameList);
        gameSpinner.setAdapter(gameListAdapter);
        if (gameList.size() > 0) {
            if (bool && activity.publicGameSetting.currentVersion != null && !activity.publicGameSetting.currentVersion.equals("")) {
                String currentVersion = activity.publicGameSetting.currentVersion.substring(activity.publicGameSetting.currentVersion.lastIndexOf("/") + 1);
                if (currentVersion.length() > 0 && gameList.contains(currentVersion)) {
                    gameSpinner.setSelection(gameListAdapter.getPosition(currentVersion));
                }
            }
            else if (!bool && gameVersion != null && gameList.contains(gameVersion)) {
                gameSpinner.setSelection(gameListAdapter.getPosition(gameVersion));
            }
            else {
                gameSpinner.setSelection(0);
            }
        }
        else {
            gameVersion = null;
        }
    }

    private void search(){
        if (!isSearching){
            new Thread(() -> {
                try {
                    searchHandler.sendEmptyMessage(0);
                    List<RemoteMod> list = repository.search(editVersion.getText().toString(), (RemoteModRepository.Category) categoryListAdapter.getItem(typeSpinner.getSelectedItemPosition()), 0, 50, editName.getText().toString(), RemoteMod.getSortTypeByPosition(sortSpinner.getSelectedItemPosition()), RemoteModRepository.SortOrder.DESC).collect(toList());
                    modList.clear();
                    modList.addAll(list);
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
                modListView.setVisibility(View.GONE);
            }
            if (msg.what == 1) {
                modListAdapter.notifyDataSetChanged();
                categoryListAdapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
                refreshText.setVisibility(View.GONE);
                modListView.setVisibility(View.VISIBLE);
                isSearching = false;
            }
            if (msg.what == 2) {
                progressBar.setVisibility(View.GONE);
                refreshText.setVisibility(View.VISIBLE);
                modListView.setVisibility(View.GONE);
                isSearching = false;
            }
        }
    };

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        search();
    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (v == editName || v == editVersion){
            search();
        }
        return false;
    }
}
