package com.tungsten.filepicker.fileoperations;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Handler;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.tungsten.filepicker.Constants;
import com.tungsten.filepicker.NavigationHelper;
import com.tungsten.filepicker.R;
import com.tungsten.filepicker.interfaces.IFuncPtr;
import com.tungsten.filepicker.models.FileItem;
import com.tungsten.filepicker.utils.UIUpdateHelper;
import com.tungsten.filepicker.utils.UIUtils;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Aditya on 4/15/2017.
 */
public class FileIO {

    ExecutorService executor;
    Handler mUIUpdateHandler;
    Context mContext;
    UIUpdateHelper mHelper;
    NavigationHelper mNavigationHelper;

    public FileIO(NavigationHelper mNavigationHelper,Handler mUIUpdateHandler, Context mContext) {
        this.mUIUpdateHandler = mUIUpdateHandler;
        this.mContext = mContext;
        this.mNavigationHelper = mNavigationHelper;
        mHelper = new UIUpdateHelper(mNavigationHelper, mContext);
        executor  = Executors.newFixedThreadPool(1);
    }

    public void createDirectory(final File path) {
        if(path.getParentFile()!=null && path.getParentFile().canWrite()) {
            executor.execute(() -> {
                try {
                    FileUtils.forceMkdir(path);
                    mUIUpdateHandler.post(mHelper.updateRunner());
                } catch (IOException e) {
                    e.printStackTrace();
                    mUIUpdateHandler.post(mHelper.errorRunner(mContext.getString(R.string.folder_creation_error)));
                }
            });
        }
    }

    public void deleteItems(final List<FileItem> selectedItems) {
        if(selectedItems!=null && selectedItems.size()>0) {
            AlertDialog confirmDialog = new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.delete_dialog_title))
                    .setMessage(mContext.getString(R.string.delete_dialog_message,selectedItems.size()))
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // continue with delete
                            final ProgressDialog progressDialog = new ProgressDialog(mContext);
                            progressDialog.setTitle(mContext.getString(R.string.delete_progress));
                            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                            progressDialog.setCancelable(false);
                            progressDialog.setProgress(0);
                            progressDialog.setMessage("");
                            progressDialog.show();

                            executor.execute(() -> {
                                int i = 0;
                                float TOTAL_ITEMS = selectedItems.size();
                                try {
                                    for (; i < selectedItems.size(); i++) {
                                        mUIUpdateHandler.post(mHelper.progressUpdater(progressDialog, (int)((i/TOTAL_ITEMS)*100), "File: "+selectedItems.get(i).getFile().getName()));
                                        if (selectedItems.get(i).getFile().isDirectory()) {
                                            removeDir(selectedItems.get(i).getFile());
                                        } else {
                                            FileUtils.forceDelete(selectedItems.get(i).getFile());
                                        }
                                    }
                                    mUIUpdateHandler.post(mHelper.toggleProgressBarVisibility(progressDialog));
                                    mUIUpdateHandler.post(mHelper.updateRunner());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    mUIUpdateHandler.post(mHelper.toggleProgressBarVisibility(progressDialog));
                                    mUIUpdateHandler.post(mHelper.errorRunner(mContext.getString(R.string.delete_error)));
                                }
                            });
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            UIUtils.ShowToast(mContext.getString(R.string.no_items_selected),mContext);
        }
    }

    public void pasteFiles(final File destination) {

        final Operations op = Operations.getInstance(mContext);
        final List<FileItem> selectedItems = op.getSelectedFiles();
        final Operations.FILE_OPERATIONS operation = op.getOperation();
        if(destination.canWrite()) {
            if (selectedItems != null && selectedItems.size() > 0) {
                final ProgressDialog progressDialog = new ProgressDialog(mContext);
                String title = mContext.getString(R.string.wait);
                progressDialog.setTitle(title);
                if (operation == Operations.FILE_OPERATIONS.COPY)
                    progressDialog.setTitle(mContext.getString(R.string.copying,title));
                else if (operation == Operations.FILE_OPERATIONS.CUT)
                    progressDialog.setTitle(mContext.getString(R.string.moving,title));

                progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                progressDialog.setCancelable(false);
                progressDialog.setMessage("");
                progressDialog.setProgress(0);
                progressDialog.show();
                executor.execute(() -> {
                    int i = 0;
                    float TOTAL_ITEMS = selectedItems.size();
                    try {
                        for (; i < selectedItems.size(); i++) {
                            mUIUpdateHandler.post(mHelper.progressUpdater(progressDialog, (int) ((i / TOTAL_ITEMS) * 100), "File: " + selectedItems.get(i).getFile().getName()));
                            if (selectedItems.get(i).getFile().isDirectory()) {
                                if (operation == Operations.FILE_OPERATIONS.CUT)
                                    FileUtils.moveDirectory(selectedItems.get(i).getFile(), new File(destination, selectedItems.get(i).getFile().getName()));
                                else if (operation == Operations.FILE_OPERATIONS.COPY)
                                    FileUtils.copyDirectory(selectedItems.get(i).getFile(), new File(destination, selectedItems.get(i).getFile().getName()));
                            } else {
                                if (operation == Operations.FILE_OPERATIONS.CUT)
                                    FileUtils.moveFile(selectedItems.get(i).getFile(), new File(destination, selectedItems.get(i).getFile().getName()));
                                else if (operation == Operations.FILE_OPERATIONS.COPY)
                                    FileUtils.copyFile(selectedItems.get(i).getFile(), new File(destination, selectedItems.get(i).getFile().getName()));
                            }
                        }
                        mUIUpdateHandler.post(op::resetOperation);
                        mUIUpdateHandler.post(mHelper.toggleProgressBarVisibility(progressDialog));
                        mUIUpdateHandler.post(mHelper.updateRunner());
                    } catch (IOException e) {
                        e.printStackTrace();
                        mUIUpdateHandler.post(mHelper.toggleProgressBarVisibility(progressDialog));
                        mUIUpdateHandler.post(mHelper.errorRunner(mContext.getString(R.string.pasting_error)));
                    }
                });
            } else {
                UIUtils.ShowToast(mContext.getString(R.string.no_items_selected), mContext);
            }
        } else {
            //UIUtils.ShowToast(mContext.getString(R.string.permission_error),mContext);
        }
    }

    public void renameFile(final FileItem fileItem) {
        UIUtils.showEditTextDialog(mContext, mContext.getString(R.string.rename_dialog_title), fileItem.getFile().getName() , val -> executor.execute(() -> {
            try {
                if(fileItem.getFile().isDirectory())
                    FileUtils.moveDirectory(fileItem.getFile(),new File(fileItem.getFile().getParentFile(), val.trim()));
                else
                    FileUtils.moveFile(fileItem.getFile(),new File(fileItem.getFile().getParentFile(), val.trim()));
                mUIUpdateHandler.post(mHelper.updateRunner());
            } catch (Exception e) {
                e.printStackTrace();
                mUIUpdateHandler.post(mHelper.errorRunner(mContext.getString(R.string.rename_error)));
            }
        }));
    }

    public void getProperties(List<FileItem> selectedItems) {

        StringBuilder msg = new StringBuilder();
        try {
            if(selectedItems.size()==1) {
                boolean isDirectory = false;
                File f = selectedItems.get(0).getFile().getCanonicalFile();
                isDirectory = (f.isDirectory());
                String type = isDirectory?mContext.getString(R.string.directory):mContext.getString(R.string.file);
                String size = FileUtils.byteCountToDisplaySize(isDirectory ? getDirSize(f):FileUtils.sizeOf(f));
                String lastModified = new SimpleDateFormat(Constants.DATE_FORMAT).format(selectedItems.get(0).getFile().lastModified());
                msg.append(mContext.getString(R.string.file_type,type));
                msg.append(mContext.getString(R.string.file_size,size));
                msg.append(mContext.getString(R.string.file_modified,lastModified));
                msg.append(mContext.getString(R.string.file_path,selectedItems.get(0).getFile().getAbsolutePath()));
            } else {
                long totalSize = 0;
                for(int i=0;i<selectedItems.size();i++) {
                    File f = selectedItems.get(0).getFile().getCanonicalFile();
                    boolean isDirectory = (f.isDirectory());
                    totalSize += isDirectory ? getDirSize(f):FileUtils.sizeOf(f);
                }
                msg.append(mContext.getString(R.string.file_type_plain)+" "+mContext.getString(R.string.file_type_multiple));
                msg.append(mContext.getString(R.string.file_size,FileUtils.byteCountToDisplaySize(totalSize)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.append(mContext.getString(R.string.property_error));
        }
        UIUtils.ShowMsg(msg.toString(),mContext.getString(R.string.properties_title),mContext);
    }

    public void shareMultipleFiles(List<FileItem> filesToBeShared){

        ArrayList<Uri> uris = new ArrayList<>();
        for(FileItem file: filesToBeShared){
            uris.add(FileProvider.getUriForFile(mContext,mContext.getString(R.string.filebrowser_provider),file.getFile()));
        }
        final Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
        intent.setType("*/*");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        PackageManager manager = mContext.getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);
        if (infos.size() > 0) {
            mContext.startActivity(Intent.createChooser(intent, mContext.getString(R.string.share)));
        } else {
            UIUtils.ShowToast(mContext.getString(R.string.sharing_no_app),mContext);
        }
    }

    private boolean removeDir(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                removeDir(file);
            }
        }
        return directoryToBeDeleted.delete();
    }

    private long getDirSize(File root) {
        if(root == null){
            return 0;
        }
        if(root.isFile()){
            return root.length();
        }
        try {
            if(isFileASymLink(root)){
                return 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }

        long length = 0;
        File[] files = root.listFiles();
        if(files == null){
            return 0;
        }
        for (File file : files) {
            length += getDirSize(file);
        }

        return length;
    }

    private static boolean isFileASymLink(File file) throws IOException {
        File canon;
        if (file.getParent() == null) {
            canon = file;
        } else {
            File canonDir = file.getParentFile().getCanonicalFile();
            canon = new File(canonDir, file.getName());
        }
        return !canon.getCanonicalFile().equals(canon.getAbsoluteFile());
    }
}
