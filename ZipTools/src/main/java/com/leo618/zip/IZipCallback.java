package com.leo618.zip;

/**
 * function:压缩回调
 *
 * <p>
 * Created by Leo on 2018/1/16.
 */
public interface IZipCallback {
    /**
     * 开始
     */
    void onStart();

    /**
     * 进度回调
     *
     * @param percentDone 完成百分比
     */
    void onProgress(int percentDone);

    /**
     * 完成
     *
     * @param success 是否成功
     */
    void onFinish(boolean success);
}
