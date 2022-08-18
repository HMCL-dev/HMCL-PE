package com.tungsten.filepicker;

/**
 * Created by Aditya on 4/15/2017.
 */

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.roughike.bottombar.BottomBar;
import com.simplecityapps.recyclerview_fastscroll.interfaces.OnFastScrollStateChangeListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;
import com.tungsten.filepicker.adapters.CustomAdapter;
import com.tungsten.filepicker.adapters.CustomAdapterItemClickListener;
import com.tungsten.filepicker.fileoperations.FileIO;
import com.tungsten.filepicker.fileoperations.Operations;
import com.tungsten.filepicker.interfaces.IContextSwitcher;
import com.tungsten.filepicker.interfaces.IFuncPtr;
import com.tungsten.filepicker.listeners.OnFileChangedListener;
import com.tungsten.filepicker.listeners.SearchViewListener;
import com.tungsten.filepicker.listeners.TabChangeListener;
import com.tungsten.filepicker.models.FileItem;
import com.tungsten.filepicker.utils.AssortedUtils;
import com.tungsten.filepicker.utils.Permissions;
import com.tungsten.filepicker.utils.UIUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class FileBrowserWithCustomHandler extends AppCompatActivity implements OnFileChangedListener, IContextSwitcher {

    private Context mContext;

    private CustomAdapter mAdapter;
    private FastScrollRecyclerView.LayoutManager mLayoutManager;
    private FastScrollRecyclerView mFilesListView;

    private BottomBar mBottomView;
    private BottomBar mTopStorageView;
    private TabChangeListener mTabChangeListener;

    private TextView mCurrentPath;
    private NavigationHelper mNavigationHelper;
    private Operations op;
    private FileIO io;

    //Action Mode for filebrowser_toolbar
    private static ActionMode mActionMode;
    private static final int APP_PERMISSION_REQUEST = 0;

    private SearchView mSearchView;
    private MenuItem mSearchMenuItem;
    private SearchViewListener mSearchViewListener;
    private List<FileItem> mFileList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;

        // Initialize Stuff
        mNavigationHelper = new NavigationHelper(mContext);
        mNavigationHelper.setmChangeDirectoryListener(this);
        io = new FileIO(mNavigationHelper, new Handler(Looper.getMainLooper()), mContext);
        op = Operations.getInstance(mContext);

        //set file filter (i.e display files with the given extension)
        String filterFilesWithExtension = getIntent().getStringExtra(Constants.ALLOWED_FILE_EXTENSIONS);
        if(filterFilesWithExtension != null && !filterFilesWithExtension.isEmpty()) {
            Set<String> allowedFileExtensions = new HashSet<String>(Arrays.asList(filterFilesWithExtension.split(";")));
            mNavigationHelper.setAllowedFileExtensionFilter(allowedFileExtensions);
        }

        mFileList = mNavigationHelper.getFilesItemsInCurrentDirectory();

        loadUi();
    }

    @Override
    public void onBackPressed() {

        if (mAdapter.getChoiceMode() == Constants.CHOICE_MODE.MULTI_CHOICE) {
            switchMode(Constants.CHOICE_MODE.SINGLE_CHOICE);
            return;
        }

        if (!mNavigationHelper.navigateBack()) {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_default_menu, menu);
        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        mSearchMenuItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView)mSearchMenuItem.getActionView();
        // Assumes current activity is the searchable activity
        mSearchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        //searchView.setSubmitButtonEnabled(true);
        mSearchView.setOnQueryTextListener(mSearchViewListener);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.action_showfoldersizes) {
            if (AssortedUtils.GetPrefs(Constants.SHOW_FOLDER_SIZE, mContext).equalsIgnoreCase("true"))
                AssortedUtils.SavePrefs(Constants.SHOW_FOLDER_SIZE, "false", mContext);
            else
                AssortedUtils.SavePrefs(Constants.SHOW_FOLDER_SIZE, "true", mContext);
            onFileChanged(mNavigationHelper.getCurrentDirectory());
        }
        else if (item.getItemId() == R.id.action_newfolder) {
            UIUtils.showEditTextDialog(this, getString(R.string.new_folder), "", new IFuncPtr(){
                @Override
                public void execute(final String val) {
                    io.createDirectory(new File(mNavigationHelper.getCurrentDirectory(),val.trim()));
                }
            });
        }
        else if (item.getItemId() == R.id.action_paste) {
            if (op.getOperation() == Operations.FILE_OPERATIONS.NONE) {
                UIUtils.ShowToast(mContext.getString(R.string.no_operation_error), mContext);
            }
            if (op.getSelectedFiles() == null) {
                UIUtils.ShowToast(mContext.getString(R.string.no_files_paste), mContext);
            }
            io.pasteFiles(mNavigationHelper.getCurrentDirectory());
        }

        return false;

    }

    @Override
    public void onFileChanged(File updatedDirectory) {
        if(updatedDirectory!=null && updatedDirectory.exists() && updatedDirectory.isDirectory()) {
            mFileList = mNavigationHelper.getFilesItemsInCurrentDirectory();
            mCurrentPath.setText(updatedDirectory.getAbsolutePath());
            mAdapter.notifyDataSetChanged();
            mTopStorageView.getTabWithId(R.id.menu_internal_storage).setTitle(FileUtils.byteCountToDisplaySize(Constants.internalStorageRoot.getUsableSpace()) + "/" + FileUtils.byteCountToDisplaySize(Constants.internalStorageRoot.getTotalSpace()));
            if (Constants.externalStorageRoot != null)
                mTopStorageView.getTabWithId(R.id.menu_external_storage).setTitle(FileUtils.byteCountToDisplaySize(Constants.externalStorageRoot.getUsableSpace()) + "/" + FileUtils.byteCountToDisplaySize(Constants.externalStorageRoot.getTotalSpace()));
        }
    }

    private void loadUi() {
        setContentView(R.layout.filebrowser_activity_main);
        mCurrentPath = (TextView) findViewById(R.id.currentPath);

        mFilesListView = (FastScrollRecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new CustomAdapter(mFileList,mContext);
        mFilesListView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(mContext);
        mFilesListView.setLayoutManager(mLayoutManager);
        final CustomAdapterItemClickListener onItemClickListener = new CustomAdapterItemClickListener(mContext, mFilesListView, new CustomAdapterItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                // TODO Handle item click
                if (mAdapter.getChoiceMode()== Constants.CHOICE_MODE.SINGLE_CHOICE) {
                    File f = mAdapter.getItemAt(position).getFile();
                    if (f.isDirectory()) {
                        closeSearchView();
                        mNavigationHelper.changeDirectory(f);
                    } else {
                        Uri selectedFileUri = Uri.fromFile(f);
                        Intent i = new Intent(Constants.FILE_SELECTED_BROADCAST);
                        i.putExtra(Constants.BROADCAST_SELECTED_FILE, selectedFileUri);
                        Bundle extras = getIntent().getExtras();
                        if(extras!=null)
                            i.putExtras(extras);
                        sendBroadcast(i);
                    }
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {
                switchMode(Constants.CHOICE_MODE.MULTI_CHOICE);
                mAdapter.selectItem(position);
                mFilesListView.scrollToPosition(position);
            }
        });
        mFilesListView.addOnItemTouchListener(onItemClickListener);

        mFilesListView.setOnFastScrollStateChangeListener(new OnFastScrollStateChangeListener() {
            @Override
            public void onFastScrollStart() {
                onItemClickListener.setmFastScrolling(true);
            }

            @Override
            public void onFastScrollStop() {
                onItemClickListener.setmFastScrolling(false);
            }
        });

        mSearchViewListener = new SearchViewListener(mAdapter);

        Toolbar toolbar = findViewById(R.id.filebrowser_tool_bar);
        setSupportActionBar(toolbar);

        mBottomView = findViewById(R.id.bottom_navigation);
        mTopStorageView = findViewById(R.id.currPath_Nav);

        mTabChangeListener = new TabChangeListener(this, mNavigationHelper, mAdapter, io,this);

        mBottomView.setOnTabSelectListener(mTabChangeListener);
        mBottomView.setOnTabReselectListener(mTabChangeListener);

        mTopStorageView.setOnTabSelectListener(mTabChangeListener);
        mTopStorageView.setOnTabReselectListener(mTabChangeListener);

        mBottomView.getTabWithId(R.id.menu_none).setVisibility(View.GONE);
        mTopStorageView.getTabWithId(R.id.menu_none).setVisibility(View.GONE);

        onFileChanged(mNavigationHelper.getCurrentDirectory());

        //switch to initial directory if given
        String initialDirectory = getIntent().getStringExtra(Constants.INITIAL_DIRECTORY);
        if (initialDirectory != null && !initialDirectory.isEmpty() ) {
            File initDir = new File(initialDirectory);
            if (initDir.exists())
                mNavigationHelper.changeDirectory(initDir);
        }
    }

    public void switchMode(Constants.CHOICE_MODE mode) {
        if(mode == Constants.CHOICE_MODE.SINGLE_CHOICE) {
            if(mActionMode != null)
                mActionMode.finish();
        } else {
            if(mActionMode == null) {
                closeSearchView();
                ToolbarActionMode newToolBar = new ToolbarActionMode(this,this, mAdapter, Constants.APP_MODE.FILE_BROWSER, io);
                mActionMode = startSupportActionMode(newToolBar);
                mActionMode.setTitle(mContext.getString(R.string.select_multiple));
            }
        }
    }

    public void changeBottomNavMenu(Constants.CHOICE_MODE multiChoice) {
        if (multiChoice == Constants.CHOICE_MODE.SINGLE_CHOICE) {
            mBottomView.setItems(R.xml.bottom_nav_items);
            mBottomView.getTabWithId(R.id.menu_none).setVisibility(View.GONE);
            mTopStorageView.getTabWithId(R.id.menu_none).setVisibility(View.GONE);
        } else {
            mBottomView.setItems(R.xml.bottom_nav_items_multiselect);
            mBottomView.getTabWithId(R.id.menu_none).setVisibility(View.GONE);
            mTopStorageView.getTabWithId(R.id.menu_none).setVisibility(View.GONE);
        }
    }

    @Override
    public void setNullToActionMode() {
        if (mActionMode != null)
            mActionMode = null;
    }

    @Override
    public void reDrawFileList() {
        mFilesListView.setLayoutManager(null);
        mFilesListView.setAdapter(mAdapter);
        mFilesListView.setLayoutManager(mLayoutManager);
        mTabChangeListener.setmAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    private void closeSearchView() {
        if (mSearchView.isShown()) {
            mSearchView.setQuery("", false);
            mSearchMenuItem.collapseActionView();
            mSearchView.setIconified(true);
        }
    }
}
