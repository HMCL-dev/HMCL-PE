package com.tungsten.filepicker;

import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;

import com.tungsten.filepicker.adapters.CustomAdapter;
import com.tungsten.filepicker.fileoperations.FileIO;
import com.tungsten.filepicker.interfaces.IContextSwitcher;
import com.tungsten.filepicker.models.FileItem;
import com.tungsten.filepicker.utils.UIUtils;

import java.util.List;

/**
 * Created by Aditya on 4/15/2017.
 */
public class ToolbarActionMode implements ActionMode.Callback{

    private CustomAdapter mAdapter;
    private IContextSwitcher mIContextSwitcher;
    private Constants.APP_MODE appMode;
    private Activity mActivity;
    private FileIO io;

    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;

    ToolbarActionMode(Activity mActivity, IContextSwitcher mIContextSwitcher, CustomAdapter mAdapter, Constants.APP_MODE mode, FileIO io) {
        this.mAdapter = mAdapter;
        this.mIContextSwitcher = mIContextSwitcher;
        this.appMode = mode;
        this.mActivity = mActivity;
        this.io = io;
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        if (this.appMode == Constants.APP_MODE.FILE_BROWSER)
            mode.getMenuInflater().inflate(R.menu.toolbar_multiselect_menu, menu);//Inflate the menu over action mode
        else if (this.appMode == Constants.APP_MODE.FILE_CHOOSER || this.appMode == Constants.APP_MODE.FOLDER_CHOOSER )
            mode.getMenuInflater().inflate(R.menu.toolbar_multiselect_menu_filechooser, menu);//Inflate the menu over action mode

        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        mAdapter.setChoiceMode(Constants.CHOICE_MODE.MULTI_CHOICE);
        mIContextSwitcher.changeBottomNavMenu(Constants.CHOICE_MODE.MULTI_CHOICE);
        mIContextSwitcher.reDrawFileList();
        return false;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        mAdapter.setChoiceMode(Constants.CHOICE_MODE.SINGLE_CHOICE);
        mIContextSwitcher.changeBottomNavMenu(Constants.CHOICE_MODE.SINGLE_CHOICE);
        mIContextSwitcher.reDrawFileList();
        mIContextSwitcher.setNullToActionMode();
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        List<FileItem> selectedItems = mAdapter.getSelectedItems();;
        if (item.getItemId() == R.id.action_properties) {
            if (io != null)
                io.getProperties(selectedItems);
            mode.finish();
        }
        else if (item.getItemId() == R.id.action_share) {
            if (io != null)
                io.shareMultipleFiles(selectedItems);
            mode.finish();//Finish action mode
        }
        else if (item.getItemId() == R.id.action_rename) {
            if (selectedItems.size() != 1) {
                UIUtils.ShowToast(mActivity.getString(R.string.selection_error_single), mActivity);
                return false;
            }
            if (!selectedItems.get(0).getFile().canWrite()) {
                //UIUtils.ShowToast(mActivity.getString(R.string.permission_error), mActivity);
                return false;
            }
            io.renameFile(selectedItems.get(0));
            mode.finish();//Finish action mode
        }
        else if (item.getItemId() == R.id.action_selectall) {
            mAdapter.selectAll();
        }
        else if (item.getItemId() == R.id.action_unselectall) {
            mAdapter.unSelectAll();
        }
        return false;
    }
}
