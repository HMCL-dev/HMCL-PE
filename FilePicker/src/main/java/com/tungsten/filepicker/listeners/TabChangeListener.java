package com.tungsten.filepicker.listeners;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.widget.RadioGroup;

import androidx.annotation.IdRes;

import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.NavigationHelper;
import com.tungsten.filepicker.R;
import com.tungsten.filepicker.adapters.CustomAdapter;
import com.tungsten.filepicker.fileoperations.FileIO;
import com.tungsten.filepicker.fileoperations.Operations;
import com.tungsten.filepicker.interfaces.IContextSwitcher;
import com.tungsten.filepicker.models.FileItem;
import com.tungsten.filepicker.utils.UIUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Aditya on 4/18/2017.
 */
public class TabChangeListener implements OnTabSelectListener, OnTabReselectListener {

    private NavigationHelper mNavigationHelper;
    private CustomAdapter mAdapter;
    private Activity mActivity;
    private FileIO io;
    private IContextSwitcher mIContextSwitcher;
    private Constants.SELECTION_MODES selectionMode;
    private Constants.APP_MODE appMode;

    public TabChangeListener(Activity mActivity, NavigationHelper mNavigationHelper, CustomAdapter mAdapter, FileIO io, IContextSwitcher mContextSwtcher) {
        this.mNavigationHelper = mNavigationHelper;
        this.mActivity = mActivity;
        this.mAdapter = mAdapter;
        this.io = io;
        this.mIContextSwitcher = mContextSwtcher;
        this.selectionMode = Constants.SELECTION_MODES.SINGLE_SELECTION;
        this.appMode = Constants.APP_MODE.FILE_CHOOSER;
    }

    @Override
    public void onTabSelected(@IdRes int tabId) {
        handleTabChange(tabId);
    }

    @Override
    public void onTabReSelected(@IdRes int tabId) {
        handleTabChange(tabId);
    }

    private void handleTabChange(int tabId) {

            if (tabId == R.id.menu_back) {
                mNavigationHelper.navigateBack();
            }
            else if (tabId == R.id.menu_internal_storage) {
                mNavigationHelper.navigateToInternalStorage();
            }
            else if (tabId == R.id.menu_external_storage) {
                mNavigationHelper.navigateToExternalStorage();
            }
            else if (tabId == R.id.menu_refresh) {
                mNavigationHelper.triggerFileChanged();
            }
            else if (tabId == R.id.menu_filter) {
                UIUtils.showRadioButtonDialog(mActivity, mActivity.getResources().getStringArray(R.array.filter_options), mActivity.getString(R.string.filter_only), new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int position) {
                        Operations op = Operations.getInstance(mActivity);
                        if (op != null) {
                            op.setmCurrentFilterOption(Constants.FILTER_OPTIONS.values()[position]);
                        }
                        mNavigationHelper.triggerFileChanged();
                    }
                });
            }
            else if (tabId == R.id.menu_sort) {
                UIUtils.showRadioButtonDialog(mActivity, mActivity.getResources().getStringArray(R.array.sort_options), mActivity.getString(R.string.sort_by), new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup radioGroup, int position) {
                        Operations op = Operations.getInstance(mActivity);
                        if (op != null) {
                            op.setmCurrentSortOption(Constants.SORT_OPTIONS.values()[position]);
                        }
                        mNavigationHelper.triggerFileChanged();
                    }
                });
            }
            else if (tabId == R.id.menu_delete) {
                List<FileItem> selectedItems = mAdapter.getSelectedItems();
                if (io != null) {
                    io.deleteItems(selectedItems);
                    mIContextSwitcher.switchMode(Constants.CHOICE_MODE.SINGLE_CHOICE);
                }
            }
            else if (tabId == R.id.menu_copy) {
                Operations op = Operations.getInstance(mActivity);
                if (op != null) {
                    op.setOperation(Operations.FILE_OPERATIONS.COPY);
                    op.setSelectedFiles(mAdapter.getSelectedItems());
                    mIContextSwitcher.switchMode(Constants.CHOICE_MODE.SINGLE_CHOICE);
                }
            }
            else if (tabId == R.id.menu_cut) {
                Operations op = Operations.getInstance(mActivity);
                if (op != null) {
                    op.setOperation(Operations.FILE_OPERATIONS.CUT);
                    op.setSelectedFiles(mAdapter.getSelectedItems());
                    mIContextSwitcher.switchMode(Constants.CHOICE_MODE.SINGLE_CHOICE);
                }
            }
            else if (tabId == R.id.menu_chooseitems) {
                {
                    List<FileItem> selItems = getmAdapter().getSelectedItems();
                    ArrayList<Uri> chosenItems = new ArrayList<>();
                    boolean hasInvalidSelections = false;
                    for (int i = 0; i < selItems.size(); i++) {
                        if (getAppMode() == Constants.APP_MODE.FOLDER_CHOOSER) {
                            if (selItems.get(i).getFile().isDirectory()) {
                                chosenItems.add(Uri.fromFile(selItems.get(i).getFile()));
                            } else {
                                hasInvalidSelections = true;
                            }
                        } else {
                            chosenItems.add(Uri.fromFile(selItems.get(i).getFile()));
                        }
                    }
                    if (hasInvalidSelections) {
                        UIUtils.ShowToast(mActivity.getString(R.string.invalid_selections),mActivity);
                        mActivity.finish();
                    }

                    mIContextSwitcher.switchMode(Constants.CHOICE_MODE.SINGLE_CHOICE);
                    if(getSelectionMode() == Constants.SELECTION_MODES.SINGLE_SELECTION) {
                        if(chosenItems.size() == 1) {
                            Intent data = new Intent();
                            data.setData(chosenItems.get(0));
                            mActivity.setResult(Activity.RESULT_OK, data);
                            mActivity.finish();
                        } else {
                            UIUtils.ShowToast(mActivity.getString(R.string.selection_error_single),mActivity);
                        }
                    } else {
                        Intent data = new Intent();
                        data.putParcelableArrayListExtra(Constants.SELECTED_ITEMS, chosenItems);
                        mActivity.setResult(Activity.RESULT_OK, data);
                        mActivity.finish();
                    }
                }
            } else if (tabId == R.id.menu_select) {
                Uri fileUri = Uri.fromFile(mNavigationHelper.getCurrentDirectory());
                Intent data = new Intent();
                data.setData(fileUri);
                mActivity.setResult(Activity.RESULT_OK, data);
                mActivity.finish();
            }
    }

    public CustomAdapter getmAdapter() {
        return mAdapter;
    }

    public void setmAdapter(CustomAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    public Constants.SELECTION_MODES getSelectionMode() {
        return selectionMode;
    }

    public void setSelectionMode(Constants.SELECTION_MODES selectionMode) {
        this.selectionMode = selectionMode;
    }

    public Constants.APP_MODE getAppMode() {
        return appMode;
    }

    public void setAppMode(Constants.APP_MODE appMode) {
        this.appMode = appMode;
    }
}
