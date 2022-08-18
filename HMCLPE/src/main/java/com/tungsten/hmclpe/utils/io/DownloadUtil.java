package com.tungsten.hmclpe.utils.io;

import static com.tungsten.hmclpe.task.DownloadTask.downloadFileMonitored;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.tungsten.hmclpe.launcher.list.install.DownloadTaskListBean;
import com.tungsten.hmclpe.task.DownloadTask;
import com.tungsten.hmclpe.utils.file.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class DownloadUtil {

    public static void downloadSingleFile(Context context,DownloadTaskListBean bean, DownloadTask.Feedback feedback) {
        ArrayList<DownloadTaskListBean> list = new ArrayList<>();
        list.add(bean);
        DownloadTask downloadTask = new DownloadTask(context,feedback);
        downloadTask.execute(list);
    }

    public static ArrayList<DownloadTaskListBean> downloadMultipleFiles(ArrayList<DownloadTaskListBean> list, int maxTask, AsyncTask task, Activity activity,DownloadMultipleFilesCallback callback) {
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(maxTask);
        ExecutorService threadPool = new ThreadPoolExecutor(maxTask,
                maxTask,
                0,
                TimeUnit.SECONDS,
                workQueue,
                new ThreadPoolExecutor.DiscardPolicy());
        ArrayList<DownloadTaskListBean> failedFile = new ArrayList<>();
        for (int j = 0; j < list.size(); j++) {
            DownloadTaskListBean bean = list.get(j);
            String url = bean.url;
            String path = bean.path;
            String sha1 = bean.sha1;
            threadPool.execute(() -> {
                if (!new File(path).exists() || (new File(path).exists() && !Objects.equals(FileUtils.getFileSha1(path), sha1))) {
                    int tryTimes = 5;
                    for (int i = 0; i < tryTimes; i++) {
                        if (task.isCancelled()) {
                            threadPool.shutdownNow();
                            return;
                        }
                        activity.runOnUiThread(() -> {
                            callback.onTaskStart(bean);
                        });
                        DownloadTask.DownloadFeedback fb = new DownloadTask.DownloadFeedback() {
                            @Override
                            public void updateProgress(long curr, long max) {
                                long progress = 100 * curr / max;
                                bean.progress = (int) progress;
                                activity.runOnUiThread(() -> {
                                    callback.onTaskProgress(bean);
                                });
                            }

                            @Override
                            public void updateSpeed(String speed) {

                            }
                        };
                        if (downloadFileMonitored(url, path, sha1, fb)) {
                            activity.runOnUiThread(() -> {
                                callback.onTaskFinish(bean);
                            });
                            break;
                        }
                        else {
                            if (i == tryTimes - 1) {
                                failedFile.add(bean);
                            }
                            activity.runOnUiThread(() -> {
                                callback.onTaskFinish(bean);
                            });
                        }
                    }
                }
            });
            while (!workQueue.isEmpty()) {
                ;
            }
        }
        threadPool.shutdown();
        try {
            threadPool.awaitTermination(1, TimeUnit.HOURS);
        } catch (InterruptedException e) {
            e.printStackTrace();
            callback.onFailed(e);
        }
        return failedFile;
    }

    public static boolean downloadFile(String url,String nameOutput,String sha1, DownloadTask.DownloadFeedback monitor) throws IOException {
        File nameOutputFile = new File(nameOutput);
        if (!nameOutputFile.exists()) {
            nameOutputFile.getParentFile().mkdirs();
        }
        else {
            if (!isRightFile(nameOutput,sha1)) {
                nameOutputFile.delete();
            }
        }
        URL downloadUrl = new URL(url);
        HttpURLConnection httpURLConnection = (HttpURLConnection)downloadUrl.openConnection();
        httpURLConnection.setDoInput(true);
        httpURLConnection.setConnectTimeout(8000);
        httpURLConnection.connect();
        InputStream inputStream = httpURLConnection.getInputStream();
        FileOutputStream fos = new FileOutputStream(nameOutputFile);
        int cur = 0;
        int oval = 0;
        long len = httpURLConnection.getContentLength();
        byte[] buf = new byte[65535];
        long lastTime = System.currentTimeMillis();
        long lastLen = 0;
        while ((cur = inputStream.read(buf)) != -1) {
            oval += cur;
            if (System.currentTimeMillis() - lastTime>=1000){
                lastLen = oval;
                lastTime = System.currentTimeMillis();
                if (monitor != null)
                    monitor.updateSpeed(formetFileSize(oval - lastLen) + "/s");
            }
            fos.write(buf, 0, cur);
            if (monitor != null)
                monitor.updateProgress(oval, len);
        }
        fos.close();
        inputStream.close();
        if (!isRightFile(nameOutput,sha1)) {
            nameOutputFile.delete();
            return false;
        }
        return true;
    }

    public static boolean isRightFile(String path,String sha1) {
        if (new File(path).exists()) {
            if (sha1 != null && !sha1.equals("")) {
                if (Objects.equals(FileUtils.getFileSha1(path), sha1)) {
                    return true;
                }
                else {
                    return false;
                }
            }
            else {
                return true;
            }
        }
        else {
            return false;
        }
    }

    public static String formetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "K";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "M";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "G";
        }
        return fileSizeString;
    }

    public interface DownloadMultipleFilesCallback{
        void onTaskStart(DownloadTaskListBean bean);
        void onTaskProgress(DownloadTaskListBean bean);
        void onTaskFinish(DownloadTaskListBean bean);
        void onFailed(Exception e);
    }

}
