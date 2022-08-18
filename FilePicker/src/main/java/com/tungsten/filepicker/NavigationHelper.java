package com.tungsten.filepicker;

import android.content.Context;
import android.os.Environment;

import com.tungsten.filepicker.fileoperations.FileNavigator;
import com.tungsten.filepicker.fileoperations.Operations;
import com.tungsten.filepicker.listeners.OnFileChangedListener;
import com.tungsten.filepicker.models.FileItem;
import com.tungsten.filepicker.utils.UIUtils;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.comparator.NameFileComparator;
import org.apache.commons.io.comparator.SizeFileComparator;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

/**
 * Created by Aditya on 4/18/2017.
 */
public class NavigationHelper {

    private FileNavigator mFileNavigator;
    private ArrayList<FileItem> mFiles = new ArrayList<FileItem>();
    private Context mContext;
    private List<OnFileChangedListener> mChangeDirectoryListeners;

    NavigationHelper(Context mContext) {
        this.mContext = mContext;
        this.mFileNavigator = FileNavigator.getInstance();
        this.mChangeDirectoryListeners = new ArrayList<>();
    }

    public void setAllowedFileExtensionFilter(Set<String> allowedFileExtensions) {
        mFileNavigator.setAllowedFileExtensionFilter(allowedFileExtensions);
    }

    public boolean navigateBack() {

        File parent = mFileNavigator.getmCurrentNode().getParentFile();
        if(parent==null || parent.compareTo(mFileNavigator.getmCurrentNode())==0 || Constants.externalStorageRoot==null || Constants.externalStorageRoot.compareTo(mFileNavigator.getmCurrentNode())==0 || Constants.internalStorageRoot.compareTo(mFileNavigator.getmCurrentNode())==0)
            return false;
        mFileNavigator.setmCurrentNode(parent);
        triggerFileChanged();
        return true;
    }

    public void navigateToInternalStorage() {
        mFileNavigator.setmCurrentNode(Constants.internalStorageRoot);
        triggerFileChanged();
    }

    public void navigateToExternalStorage() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            mFileNavigator.setmCurrentNode(Constants.externalStorageRoot);
        } else {
            UIUtils.ShowToast(mContext.getString(R.string.external_storage_error),mContext);
        }
        triggerFileChanged();
    }

    public void changeDirectory(File newDirectory) {
        if(newDirectory!=null && newDirectory.exists() &&  newDirectory.isDirectory()) {
            mFileNavigator.setmCurrentNode(newDirectory);
        }
        triggerFileChanged();
    }

    public ArrayList<FileItem> getFilesItemsInCurrentDirectory() {
        Operations op = Operations.getInstance(mContext);
        Constants.SORT_OPTIONS option = op.getmCurrentSortOption();
        Constants.FILTER_OPTIONS filterOption = op.getmCurrentFilterOption();
        if (mFileNavigator.getmCurrentNode() == null) mFileNavigator.setmCurrentNode(mFileNavigator.getmRootNode());
        File[] files = mFileNavigator.getFilesInCurrentDirectory();
        if (files != null) {
            mFiles.clear();
            Comparator<File> comparator = NameFileComparator.NAME_INSENSITIVE_COMPARATOR;
            switch(option) {
                case SIZE:
                    comparator = SizeFileComparator.SIZE_COMPARATOR;
                    break;
                case LAST_MODIFIED:
                    comparator = LastModifiedFileComparator.LASTMODIFIED_COMPARATOR;
                    break;
            }
            Arrays.sort(files,comparator);
            for (int i = 0; i < files.length; i++) {
                boolean addToFilter = true;
                switch(filterOption) {
                    case FILES:
                        addToFilter = !files[i].isDirectory();
                        break;
                    case FOLDER:
                        addToFilter = files[i].isDirectory();
                        break;
                }
                if (addToFilter && files[i].isDirectory()){
                    mFiles.add(new FileItem(files[i]));
                }
            }
            for (int i = 0; i < files.length; i++) {
                boolean addToFilter = true;
                switch(filterOption) {
                    case FILES:
                        addToFilter = !files[i].isDirectory();
                        break;
                    case FOLDER:
                        addToFilter = files[i].isDirectory();
                        break;
                }
                if (addToFilter && !files[i].isDirectory()){
                    mFiles.add(new FileItem(files[i]));
                }
            }
        }
        return mFiles;
    }

    public File getCurrentDirectory() {
        return mFileNavigator.getmCurrentNode();
    }

    public void triggerFileChanged() {
        for(int i=0;i< mChangeDirectoryListeners.size();i++) {
            mChangeDirectoryListeners.get(i).onFileChanged(getCurrentDirectory());
        }
    }

    public void setmChangeDirectoryListener(OnFileChangedListener mChangeDirectoryListener) {
        this.mChangeDirectoryListeners.add(mChangeDirectoryListener);
    }

}
