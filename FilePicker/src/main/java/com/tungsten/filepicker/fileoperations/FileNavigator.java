package com.tungsten.filepicker.fileoperations;

import com.tungsten.filepicker.Constants;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Set;

/**
 * Created by Aditya on 4/17/2017.
 */
public class FileNavigator {

    private static FileNavigator mNavigator;

    private File mCurrentNode = Constants.internalStorageRoot;
    private File mRootNode = Constants.internalStorageRoot;

    private Set<String> mAllowedFileExtensionFilter;

    public static FileNavigator getInstance() {
        if(mNavigator==null) {
            mNavigator = new FileNavigator();
        }
        return mNavigator;
    }

    public File [] getFilesInCurrentDirectory() {
       if(mAllowedFileExtensionFilter!=null) {
           FilenameFilter fileNameFilter = new FilenameFilter() {
               @Override
               public boolean accept(File dir, String name) {
                   File absolutePath = new File(dir, name);
                   if (absolutePath.isDirectory()) {
                       return  true;
                   }
                   String fileExtension = FilenameUtils.getExtension(name);
                   if(mAllowedFileExtensionFilter.contains(fileExtension)) {
                       return true;
                   }
                   return false;
               }
           };
           return mCurrentNode.listFiles(fileNameFilter);
       }
       return mCurrentNode.listFiles();
    }

    private FileNavigator() {
    }

    public File getmCurrentNode() {
        return mCurrentNode;
    }

    public void setmCurrentNode(File mCurrentNode) {
        if(mCurrentNode!=null)
            this.mCurrentNode = mCurrentNode;
    }

    public void setAllowedFileExtensionFilter(Set<String> allowedFileExtensions) {
        this.mAllowedFileExtensionFilter = allowedFileExtensions;
    }


    public File getmRootNode() {
        return mRootNode;
    }

}
