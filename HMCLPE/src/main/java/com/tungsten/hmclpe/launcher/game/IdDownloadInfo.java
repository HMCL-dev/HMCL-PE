package com.tungsten.hmclpe.launcher.game;

import com.google.gson.annotations.SerializedName;

public class IdDownloadInfo extends DownloadInfo {

    @SerializedName("id")
    public String id;

    public IdDownloadInfo() {
        this("", "");
    }

    public IdDownloadInfo(String id, String url) {
        this(id, url, null);
    }

    public IdDownloadInfo(String id, String url, String sha1) {
        this(id, url, sha1, 0);
    }

    public IdDownloadInfo(String id, String url, String sha1, int size) {
        super(url, sha1, size);
        this.id = id;
    }

}