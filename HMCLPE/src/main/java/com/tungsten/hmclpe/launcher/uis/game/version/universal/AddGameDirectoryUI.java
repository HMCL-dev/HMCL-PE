package com.tungsten.hmclpe.launcher.uis.game.version.universal;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.FolderChooser;
import com.tungsten.hmclpe.R;
import com.tungsten.hmclpe.launcher.MainActivity;
import com.tungsten.hmclpe.launcher.list.info.contents.ContentListBean;
import com.tungsten.hmclpe.manifest.AppManifest;
import com.tungsten.hmclpe.launcher.uis.tools.BaseUI;
import com.tungsten.hmclpe.utils.animation.CustomAnimationUtils;
import com.tungsten.hmclpe.utils.file.UriUtils;
import com.tungsten.hmclpe.utils.gson.GsonUtils;

import java.io.File;

public class AddGameDirectoryUI extends BaseUI implements View.OnClickListener {

    public static final int PICK_GAME_FILE_FOLDER_REQUEST = 1001;

    public LinearLayout addGameDirUI;

    private EditText editName;
    private ImageButton editPath;
    private TextView selectedDir;
    private Button save;

    public AddGameDirectoryUI(Context context, MainActivity activity) {
        super(context, activity);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        addGameDirUI = activity.findViewById(R.id.ui_add_game_directory);

        editName = activity.findViewById(R.id.edit_content_name);

        editPath = activity.findViewById(R.id.edit_content_path);
        editPath.setOnClickListener(this);

        selectedDir = activity.findViewById(R.id.content_path);

        save = activity.findViewById(R.id.save_contents);
        save.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.showBarTitle(context.getResources().getString(R.string.add_game_dir_ui_title),activity.uiManager.uis.get(activity.uiManager.uis.size() - 2) != activity.uiManager.mainUI,false);
        CustomAnimationUtils.showViewFromLeft(addGameDirUI,activity,context,true);
        selectedDir.setText(AppManifest.DEFAULT_GAME_DIR);
    }

    @Override
    public void onStop() {
        super.onStop();
        CustomAnimationUtils.hideViewToLeft(addGameDirUI,activity,context,true);
    }

    @Override
    public void onClick(View v) {
        if (v == editPath){
            Intent intent = new Intent(context, FolderChooser.class);
            intent.putExtra(Constants.SELECTION_MODE, Constants.SELECTION_MODES.SINGLE_SELECTION.ordinal());
            intent.putExtra(Constants.INITIAL_DIRECTORY, new File(AppManifest.DEFAULT_GAME_DIR).getAbsolutePath());
            activity.startActivityForResult(intent, PICK_GAME_FILE_FOLDER_REQUEST);
        }
        if (v == save && !editName.getText().toString().equals("")){
            boolean exist = false;
            for (int i = 0;i < activity.uiManager.versionListUI.contentList.size();i++){
                if (activity.uiManager.versionListUI.contentList.get(i).name.equals(editName.getText().toString())){
                    exist = true;
                }
            }
            if (!exist){
                activity.uiManager.versionListUI.contentList.add(new ContentListBean(editName.getText().toString(),selectedDir.getText().toString(),false));
                GsonUtils.saveContents(activity.uiManager.versionListUI.contentList, AppManifest.GAME_FILE_DIRECTORY_DIR + "/game_file_directories.json");
                activity.backToLastUI();
            }
            else {
                Toast.makeText(context,context.getString(R.string.add_game_dir_ui_alert),Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_GAME_FILE_FOLDER_REQUEST && data != null) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                selectedDir.setText(UriUtils.getRealPathFromUri_AboveApi19(context,uri));
            }
        }
    }
}
