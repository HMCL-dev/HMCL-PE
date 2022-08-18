package com.leo618.zip;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.model.ZipParameters;
import net.lingala.zip4j.progress.ProgressMonitor;
import net.lingala.zip4j.util.Zip4jUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

/**
 * function:ZIP 压缩工具管理器
 *
 * <p>
 * Created by Leo on 2018/1/16.
 */
public final class ZipManager {
    private ZipManager() {}

    /**
     * 是否打印日志
     */
    public static void debug(boolean debug) {
        ZipLog.config(debug);
    }

    private static final int     WHAT_START    = 100;
    private static final int     WHAT_FINISH   = 101;
    private static final int     WHAT_PROGRESS = 102;
    private static       Handler mUIHandler    = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg == null) {
                return;
            }
            switch (msg.what) {
                case WHAT_START:
                    ((IZipCallback) msg.obj).onStart();
                    ZipLog.debug("onStart.");
                    break;
                case WHAT_PROGRESS:
                    ((IZipCallback) msg.obj).onProgress(msg.arg1);
                    ZipLog.debug("onProgress: percentDone=" + msg.arg1);
                    break;
                case WHAT_FINISH:
                    ((IZipCallback) msg.obj).onFinish(true);
                    ZipLog.debug("onFinish: success=true");
                    break;
            }
        }
    };

    /**
     * 压缩文件或者文件夹
     *
     * @param targetPath          被压缩的文件路径
     * @param destinationFilePath 压缩后生成的文件路径
     * @param callback            压缩进度回调
     */
    public static void zip(String targetPath, String destinationFilePath, IZipCallback callback) {
        zip(targetPath, destinationFilePath, "", callback);
    }

    /**
     * 压缩文件或者文件夹
     *
     * @param targetPath          被压缩的文件路径
     * @param destinationFilePath 压缩后生成的文件路径
     * @param password            压缩加密 密码
     * @param callback            压缩进度回调
     */
    public static void zip(String targetPath, String destinationFilePath, String password, IZipCallback callback) {
        if (!Zip4jUtil.isStringNotNullAndNotEmpty(targetPath) || !Zip4jUtil.isStringNotNullAndNotEmpty(destinationFilePath)) {
            if (callback != null) callback.onFinish(false);
            return;
        }
        ZipLog.debug("zip: targetPath=" + targetPath + " , destinationFilePath=" + destinationFilePath + " , password=" + password);
        try {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(8);
            parameters.setCompressionLevel(5);
            if (password.length() > 0) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(99);
                parameters.setAesKeyStrength(3);
                parameters.setPassword(password);
            }
            ZipFile zipFile = new ZipFile(destinationFilePath);
            zipFile.setRunInThread(true);
            File targetFile = new File(targetPath);
            if (targetFile.isDirectory()) {
                zipFile.addFolder(targetFile, parameters);
            } else {
                zipFile.addFile(targetFile, parameters);
            }
            timerMsg(callback, zipFile);
        } catch (Exception e) {
            if (callback != null) callback.onFinish(false);
            ZipLog.debug("zip: Exception=" + e.getMessage());
        }
    }

    /**
     * 压缩多个文件
     *
     * @param list                被压缩的文件集合
     * @param destinationFilePath 压缩后生成的文件路径
     * @param password            压缩 密码
     * @param callback            回调
     */
    public static void zip(ArrayList<File> list, String destinationFilePath, String password, final IZipCallback callback) {
        if (list == null || list.size() == 0 || !Zip4jUtil.isStringNotNullAndNotEmpty(destinationFilePath)) {
            if (callback != null) callback.onFinish(false);
            return;
        }
        ZipLog.debug("zip: list=" + list.toString() + " , destinationFilePath=" + destinationFilePath + " , password=" + password);
        try {
            ZipParameters parameters = new ZipParameters();
            parameters.setCompressionMethod(8);
            parameters.setCompressionLevel(5);
            if (password.length() > 0) {
                parameters.setEncryptFiles(true);
                parameters.setEncryptionMethod(99);
                parameters.setAesKeyStrength(3);
                parameters.setPassword(password);
            }
            ZipFile zipFile = new ZipFile(destinationFilePath);
            zipFile.setRunInThread(true);
            zipFile.addFiles(list, parameters);
            timerMsg(callback, zipFile);
        } catch (Exception e) {
            if (callback != null) callback.onFinish(false);
            ZipLog.debug("zip: Exception=" + e.getMessage());
        }
    }

    /**
     * 压缩多个文件
     *
     * @param list                被压缩的文件集合
     * @param destinationFilePath 压缩后生成的文件路径
     * @param callback            压缩进度回调
     */
    public static void zip(ArrayList<File> list, String destinationFilePath, IZipCallback callback) {
        zip(list, destinationFilePath, "", callback);
    }

    /**
     * 解压
     *
     * @param targetZipFilePath     待解压目标文件地址
     * @param destinationFolderPath 解压后文件夹地址
     * @param callback              回调
     */
    public static void unzip(String targetZipFilePath, String destinationFolderPath, IZipCallback callback) {
        unzip(targetZipFilePath, destinationFolderPath, "", callback);
    }

    /**
     * 解压
     *
     * @param targetZipFilePath     待解压目标文件地址
     * @param destinationFolderPath 解压后文件夹地址
     * @param password              解压密码
     * @param callback              回调
     */
    public static void unzip(String targetZipFilePath, String destinationFolderPath, String password, final IZipCallback callback) {
        if (!Zip4jUtil.isStringNotNullAndNotEmpty(targetZipFilePath) || !Zip4jUtil.isStringNotNullAndNotEmpty(destinationFolderPath)) {
            if (callback != null) callback.onFinish(false);
            return;
        }
        ZipLog.debug("unzip: targetZipFilePath=" + targetZipFilePath + " , destinationFolderPath=" + destinationFolderPath + " , password=" + password);
        try {
            ZipFile zipFile = new ZipFile(targetZipFilePath);
            if (zipFile.isEncrypted() && Zip4jUtil.isStringNotNullAndNotEmpty(password)) {
                zipFile.setPassword(password);
            }
            zipFile.setRunInThread(true);
            zipFile.extractAll(destinationFolderPath);
            timerMsg(callback, zipFile);
        } catch (Exception e) {
            if (callback != null) callback.onFinish(false);
            ZipLog.debug("unzip: Exception=" + e.getMessage());
        }
    }

    //Handler send msg
    private static void timerMsg(final IZipCallback callback, ZipFile zipFile) {
        if (callback == null) return;
        mUIHandler.obtainMessage(WHAT_START, callback).sendToTarget();
        final ProgressMonitor progressMonitor = zipFile.getProgressMonitor();
        final Timer           timer           = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                mUIHandler.obtainMessage(WHAT_PROGRESS, progressMonitor.getPercentDone(), 0, callback).sendToTarget();
                if (progressMonitor.getResult() == ProgressMonitor.RESULT_SUCCESS) {
                    mUIHandler.obtainMessage(WHAT_FINISH, callback).sendToTarget();
                    this.cancel();
                    timer.purge();
                }
            }
        }, 0, 300);
    }

}
